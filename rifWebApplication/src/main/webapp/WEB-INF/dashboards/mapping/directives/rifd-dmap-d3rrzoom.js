/* 
 * D3 directive for RR chart rr-zoom
 * http://bl.ocks.org/mbostock/34f08d5e11952a80609169b7917d4172
 */

/* global d3, Infinity */

angular.module("RIF")
        .directive('rrZoom', function ($rootScope, MappingStateService) { //rr-zoom
            var directiveDefinitionObject = {
                restrict: 'E',
                replace: false,
                scope: {
                    data: '=chartData',
                    opt: '=options',
                    width: '=width',
                    height: '=height'
                },
                link: function (scope, element, attrs) {

                    //listener for global key event
                    var keyListener;
                    element.on('$destroy', function () {
                        if (!angular.isUndefined(keyListener)) {
                            keyListener();
                        }
                    });

                    scope.$watch(function () {
                        if (angular.isUndefined(scope.data)) {
                            return;
                        } else {
                            scope.renderBase();
                        }
                    });

                    scope.renderBase = function () {
                        var margin = {top: 30, right: 20, bottom: 30, left: 60};
                        var xHeight = scope.height - margin.top - (margin.bottom + scope.height * 0.3);
                        var xHeight2 = scope.height - margin.top - (margin.bottom + scope.height * 0.7);
                        var xWidth = scope.width - margin.left - margin.right - 4;
                        var orderField = scope.opt.x_field;
                        var lineField = scope.opt.risk_field;
                        var lowField = scope.opt.cl_field;
                        var highField = scope.opt.cu_field;
                        var dataLength = scope.data.length;
                        var labelField = scope.opt.label_field;
                        var panel = scope.opt.panel;

                        var x = d3.scaleLinear().range([0, xWidth]);
                        var x2 = d3.scaleLinear().range([0, xWidth]);

                        x.domain(d3.extent(scope.data, function (d) {
                            return d[orderField];
                        }));

                        x2.domain(x.domain());

                        var y = d3.scaleLinear()
                                .domain([d3.min(scope.data, function (d) {
                                        return d[ lowField ];
                                    }), d3.max(scope.data, function (d) {
                                        return d[ highField ];
                                    })])
                                .range([xHeight, 0]);

                        var y2 = d3.scaleLinear().range([xHeight2, 0]);

                        y2.domain(y.domain());

                        var xAxis = d3.axisBottom().scale(x).ticks(0);
                        var xAxis2 = d3.axisBottom().scale(x2);
                        var yAxis = d3.axisLeft().scale(y);

                        var brush = d3.brushX()
                                .extent([[0, 0], [xWidth, xHeight2]])
                                .on("start brush", brushed);

                        var zoom = d3.zoom()
                                .scaleExtent([1, Infinity])
                                .translateExtent([[0, 0], [xWidth, xHeight]])
                                .extent([[0, 0], [xWidth, xHeight]]);

                        var area = d3.area()
                                .x(function (d) {
                                    return x(d[ orderField ]);
                                })
                                .y0(function (d) {
                                    return y(d[ lowField ]);
                                })
                                .y1(function (d) {
                                    return y(d[ highField ]);
                                });
                        var line = d3.line()
                                .x(function (d) {
                                    return x(d[ orderField ]);
                                })
                                .y(function (d) {
                                    return y(d[ lineField ]);
                                });
                        var line2 = d3.line()
                                .x(function (d) {
                                    return x2(d[ orderField ]);
                                })
                                .y(function (d) {
                                    return y2(d[ lineField ]);
                                });
                        var area2 = d3.area()
                                .x(function (d) {
                                    return x2(d[ orderField ]);
                                })
                                .y0(function (d) {
                                    return y2(d[ lowField ]);
                                })
                                .y1(function (d) {
                                    return y2(d[ highField ]);
                                });
                        var refLine = d3.line()
                                .x(function (d) {
                                    return x(d[ orderField ]);
                                })
                                .y(function (d) {
                                    return y(1);
                                });

                        d3.select("#rrchart" + panel).remove();

                        //graph canvas
                        var svg = d3.select(element[0]).append("svg")
                                .attr("width", scope.width)
                                .attr("height", scope.height)
                                .attr("id", "rrchart" + panel);

                        var focus = svg.append("g")
                                .attr("class", "focus")
                                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                        var context = svg.append("g")
                                .attr("class", "context")
                                .attr("transform", "translate(" + margin.left + "," + (xHeight + margin.bottom + margin.top) + ")");

                        //clip-path clashes on multiple instance so needs unique id                    
                        focus.append("clipPath")
                                .attr("id", "plot-clip" + panel)
                                .append("rect")
                                .attr("width", xWidth)
                                .attr("height", xHeight);

                        focus.append("path")
                                .datum(scope.data)
                                .attr("class", "area")
                                .attr("clip-path", "url(#plot-clip" + panel + ")")
                                .attr("d", area);

                        focus.append("path")
                                .datum(scope.data)
                                .attr("class", "line")
                                .attr("id", "lineRisk")
                                .attr("clip-path", "url(#plot-clip" + panel + ")")
                                .attr("d", line);

                        focus.append("path")
                                .datum(scope.data)
                                .attr("class", "line")
                                .attr("id", "refLine")
                                .attr("clip-path", "url(#plot-clip" + panel + ")")
                                .attr("d", refLine);

                        focus.append("g")
                                .attr("class", "axis x")
                                .attr("transform", "translate(0," + xHeight + ")")
                                .call(xAxis);

                        focus.append("g")
                                .attr("class", "y axis")
                                .call(yAxis);

                        context.append("path")
                                .datum(scope.data)
                                .attr("class", "area")
                                .attr("d", area2);

                        context.append("path")
                                .datum(scope.data)
                                .attr("class", "line")
                                .attr("id", "lineRisk2")
                                .attr("d", line2);

                        context.append("g")
                                .attr("transform", "translate(0," + xHeight2 + ")")
                                .call(xAxis2);

                        //set brush handle ends
                        var startLoc = scope.opt.zoomStart;
                        var endLoc = scope.opt.zoomEnd;
                        if (startLoc === null) {
                            startLoc = x2.range()[0];
                        }
                        if (endLoc === null) {
                            endLoc = x2.range()[1];
                        }

                        var gBrush = context.append("g")
                                .attr("class", "brush")
                                .call(brush)
                                .call(brush.move, [scope.opt.zoomStart, scope.opt.zoomEnd].map(x2, x2.invert));

                        svg.append("rect")
                                .attr("class", "zoom")
                                .attr("width", xWidth)
                                .attr("height", xHeight)
                                .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
                                .on("click", function (d) {
                                    scope.$parent.mapInFocus = panel;
                                    var xy = d3.mouse(this);
                                    scope.clickXPos = snapToBounds(xy[0]);
                                    broadcastAreaUpdate({xpos: scope.clickXPos, map: true});
                                })
                                .call(zoom);

                        function broadcastAreaUpdate(data) {
                            //get selected from x_order
                            for (var i = 0; i < dataLength; i++) {
                                if (scope.data[i].x_order === data.xpos) {
                                    selected = scope.data[i];
                                    MappingStateService.getState().selected[panel] = scope.data[i];
                                    break;
                                }
                            }
                            $rootScope.$broadcast('syncMapping2Events', {selected: selected, mapID: panel, map: data.map});
                        }

                        function snapToBounds(mouseX) {
                            var val = Math.round(x.invert(mouseX));
                            return (val < 0) ? 0 : (val > dataLength) ? dataLength - 1 : val;
                        }

                        var highlighter = focus.append("line")
                                .attr("x1", 0)
                                .attr("y1", 0)
                                .attr("x2", 0)
                                .attr("y2", xHeight)
                                .attr("height", xHeight)
                                .attr("class", "bivariateHiglighter")
                                .attr("id", "bivariateHiglighter1" + panel);

                        var highlighter2 = context.append("line")
                                .attr("x1", 0)
                                .attr("y1", 0)
                                .attr("x2", 0)
                                .attr("y2", xHeight2)
                                .attr("height", xHeight2)
                                .attr("class", "bivariateHiglighter")
                                .attr("id", "bivariateHiglighter2" + panel);

                        focus.append("text")
                                .attr("transform", "translate(70,20)")
                                .attr("id", "labelLineBivariate")
                                .text(labelField);

                        var currentFigures = focus.append("text")
                                .attr("transform", "translate(70,40)")
                                .attr("class", "currentFiguresLineBivariate")
                                .attr("id", "currentFiguresLineBivariate" + panel)
                                .text("");

                        //the drop reference line
                        var selected = MappingStateService.getState().selected[panel];
                        if (selected !== null) {
                            focus.select("#bivariateHiglighter1" + panel).attr("transform", "translate(" + x(selected.x_order) + "," + 0 + ")");
                            context.select("#bivariateHiglighter2" + panel).attr("transform", "translate(" + x2(selected.x_order) + "," + 0 + ")");
                            focus.select("#currentFiguresLineBivariate" + panel).text(selected.rr.toFixed(3) + " (" + selected.cl.toFixed(2) + " - " + selected.ul.toFixed(2) + ")");
                        }

                        function brushed() {
                            if (d3.event.sourceEvent && d3.event.sourceEvent.type === "zoom") {
                                return; // ignore brush-by-zoom
                            }
                            var s = d3.event.selection || x2.range();
                            var si = s.map(x2.invert, x2);
                            x.domain(si);

                            focus.select(".area").attr("d", area);
                            focus.select(".line").attr("d", line);
                            focus.select(".axis--x").call(xAxis);
                            svg.select(".zoom").call(zoom.transform, d3.zoomIdentity
                                    .scale(xWidth / (s[1] - s[0]))
                                    .translate(-s[0], 0));

                            //ignore if brush has not changed
                            if (MappingStateService.getState().brushStartLoc[panel] === si[0] &&
                                    MappingStateService.getState().brushEndLoc[panel] === si[1]) {
                                return;
                            } else {
                                //remember brush locations
                                scope.$parent[scope.opt.this].zoomStart = si[0];
                                scope.$parent[scope.opt.this].zoomEnd = si[1];
                                MappingStateService.getState().brushStartLoc[panel] = si[0];
                                MappingStateService.getState().brushEndLoc[panel] = si[1];
                                broadcastAreaUpdate({xpos: scope.clickXPos, map: true});
                            }
                        }

                        //add dropLine on map select events
                        scope.$on('rrDropLineRedraw', function (event, data, container) {
                            //get selected from area_id
                            if (panel === container) {
                                for (var i = 0; i < dataLength; i++) {
                                    if (scope.data[i].gid === data) {
                                        selected = scope.data[i];
                                        scope.clickXPos = scope.data[i].x_order;
                                        MappingStateService.getState().selected[container] = scope.data[i];
                                        break;
                                    }
                                }
                                context.select("#bivariateHiglighter2" + panel).attr("transform", "translate(" + x2(selected.x_order) + "," + 0 + ")");
                                focus.select("#currentFiguresLineBivariate" + panel).text(selected.rr.toFixed(3) + " (" + selected.cl.toFixed(2) + " - " + selected.ul.toFixed(2) + ")");
                                //is highlighter out of x range?
                                if (selected.x_order >= x.domain()[0] && selected.x_order <= x.domain()[1]) {
                                    focus.select("#bivariateHiglighter1" + panel).attr("transform", "translate(" + x(selected.x_order) + "," + 0 + ")");
                                    highlighter.style("stroke", "#EEA9B8");
                                } else {
                                    highlighter.style("stroke", "transparent");
                                }
                            }
                        });

                        //handle left, right key events
                        if (angular.isUndefined(scope.$$listeners.rrKeyEvent)) {
                            keyListener = scope.$on('rrKeyEvent', function (event, up, keyCode, container) {
                                if (panel === container) {
                                    if (keyCode === 37) { //left minus
                                        if (!up & scope.clickXPos - 1 > 0) {
                                            scope.clickXPos--;
                                        }
                                    } else if (!up & keyCode === 39) { //right plus
                                        if (scope.clickXPos + 1 <= dataLength) {
                                            scope.clickXPos++;
                                        }
                                    }
                                    //only update map on keyup, always update dropLine
                                    broadcastAreaUpdate({xpos: scope.clickXPos, map: up});
                                }
                            });
                        }
                    };
                }
            };
            return directiveDefinitionObject;
        });