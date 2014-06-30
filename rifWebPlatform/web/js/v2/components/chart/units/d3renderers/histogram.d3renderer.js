RIF.chart.histogram.d3renderer = (function( opt, values ){
	
	/*
	 *	Bates distribution Histogram using d3.
	 *	Readaptation of http://bl.ocks.org/mbostock/3048450 
     *	 
	 */
	
	var id = opt.element,
		margin = opt.margin,
		width = opt.dimensions.width - margin.left - margin.right,
		height = opt.dimensions.height - margin.top - margin.bottom,
		field = opt.field;
	
	// Generate a Bates distribution of 10 random variables.
	var values = d3.range(1000).map(d3.random.bates(10));
	console.log(values);
	// A formatter for counts.
	var formatCount = d3.format(",.0f");

	var x = d3.scale.linear()
		.domain([0, 1])
		.range([0, width]);

	// Generate a histogram using twenty uniformly-spaced bins.
	//Could allows users to change this
	var data = d3.layout.histogram()
		.bins(x.ticks(20))
		(values);

	var y = d3.scale.linear()
		.domain([0, d3.max(data, function(d) { return d.y; })])
		.range([height, 0]);

	var xAxis = d3.svg.axis()
		.scale(x)
		.orient("bottom");

	var svg = d3.select("#distHisto").append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)		
	  .append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
			
	
	// A label for the current year.
	/*var title = svg.append("text")
		.attr("class", "title")
		.attr("dy", ".55em")		
			.attr("dx", opt.dimensions.width)
			.text("leukemia");*/
	
	var bar = svg.selectAll(".bar")
		.data(data)
	  .enter().append("g")
		.attr("class", "bar")
		.attr("transform", function(d) { return "translate(" + x(d.x) + "," + y(d.y) + ")"; });

	bar.append("rect")
		.attr("x", 1)
		.attr("width", x(data[0].dx) - 1)
		.attr("height", function(d) { return height - y(d.y); });

	bar.append("text")
		.attr("dy", ".75em")
		.attr("y", 6)
		.attr("x", x(data[0].dx) / 2)
		.attr("text-anchor", "middle")
		.text(function(d) { return formatCount(d.y); });

	svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0," + height + ")")
		.call(xAxis);
		

});