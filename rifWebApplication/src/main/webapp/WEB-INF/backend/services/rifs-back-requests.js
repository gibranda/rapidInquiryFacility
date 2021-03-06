/**
 * The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
 * that rapidly addresses epidemiological and public health questions using 
 * routinely collected health and population data and generates standardised 
 * rates and relative risks for any given health outcome, for specified age 
 * and year ranges, for any given geographical area.
 *
 * Copyright 2016 Imperial College London, developed by the Small Area
 * Health Statistics Unit. The work of the Small Area Health Statistics Unit 
 * is funded by the Public Health England as part of the MRC-PHE Centre for 
 * Environment and Health. Funding for this project has also been received 
 * from the United States Centers for Disease Control and Prevention.  
 *
 * This file is part of the Rapid Inquiry Facility (RIF) project.
 * RIF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RIF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RIF. If not, see <http://www.gnu.org/licenses/>; or write 
 * to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
 * Boston, MA 02110-1301 USA
 
 * David Morley
 * @author dmorley
 */

/*
 * SERVICE for all requests to the middleware
 */
angular.module("RIF")
        .service('user', ['$http', 'studySubmissionURL', 'studyResultRetrievalURL', 'taxonomyServicesURL', 'DatabaseService',
            function ($http, studySubmissionURL, studyResultRetrievalURL, taxonomyServicesURL, DatabaseService) {

                var self = this;
                self.currentUser = "";

                //identify specific middleware calls in the interceptor
                var config = {
                    headers: {
                        "rifUser": "rif"
                    }
                };

                //submit a study               
                self.submitStudy = function (username, jsonObj) {
                    var blob = new Blob([JSON.stringify(jsonObj)], {
                        type: "text/plain"
                    });

                    var formData = new FormData();
                    formData.append("userID", username);
                    formData.append("fileField", blob, "submissionFile.txt");
                    formData.append("fileFormat", "JSON");

                    return $http.post(studySubmissionURL + DatabaseService.getDatabase() + "submitStudy/", formData, {
                        transformRequest: angular.identity,
                        headers: {'Content-Type': undefined}
                    });
                };

                //Note in the example URLs below either pg/ or ms/ needs to be added before the first paramter

                //login
                self.login = function (username, password) {
                    //http://localhost:8080/rifServices/studySubmission/login?userID=kgarwood&password=xyz
                    //[{"result":"User kgarwood logged in."}]
                    self.currentUser = username;
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'login?userID=' + username + '&password=' + password);
                };
                self.logout = function (username) {
                    //http://localhost:8080/rifServices/studySubmission/logout?userID=kgarwood
                    //[{"result":"User kgarwood logged out."}]
                    self.currentUser = "";
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'logout?userID=' + username);
                };
                self.isLoggedIn = function (username) {
                    //http://localhost:8080/rifServices/studySubmission/isLoggedIn?userID=kgarwood
                    //[{"result":"true"}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'isLoggedIn?userID=' + username);
                };
                self.getDatabaseType = function (username) {
                    //http://localhost:8080/rifServices/studySubmission/ms/getDatabaseType?userID=peter
                    //jdbc:sqlserver
                    return $http.get(studySubmissionURL + 'ms/' + 'getDatabaseType?userID=' + username);
                };
                //Taxonomy services              
                self.initialiseService = function () {
                    //http://localhost:8080/taxonomyServices/taxonomyServices/initialiseService
                    //true
                    return $http.get(taxonomyServicesURL + 'initialiseService', config);
                };
                self.getTaxonomyServiceProviders = function () {
                    //http://localhost:8080/taxonomyServices/taxonomyServices/getTaxonomyServiceProviders
                    //[{"identifier":"icd10","name":"ICD Taxonomy Service","description":"ICD 10 is a classification of diseases."}]
                    return $http.get(taxonomyServicesURL + 'getTaxonomyServiceProviders', config);
                };
                self.getMatchingTerms = function (taxonomy, text) {
                    //http://localhost:8080/taxonomyServices/taxonomyServices/getMatchingTerms?taxonomy_id=icd10&search_text=asthma&is_case_sensitive=false
                    return $http.get(taxonomyServicesURL + 'getMatchingTerms?taxonomy_id=' + taxonomy + '&search_text=' + text + '&is_case_sensitive=false', config);
                };
                //Project info
                self.getProjects = function (username) {
                    //http://localhost:8080/rifServices/studySubmission/getProjects?userID=kgarwood
                    //[{"name":"TEST","description":null}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getProjects?userID=' + username, config);
                };
                self.getProjectDescription = function (username, projectName) {
                    //http://localhost:8080/rifServices/studySubmission/getProjectDescription?userID=kgarwood&projectName=TEST
                    //[{"result":"Test Project. Will be disabled when in production."}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getProjectDescription?userID=' + username + '&projectName=' + projectName, config);
                };
                self.getGeographies = function (username) {
                    //http://localhost:8080/rifServices/studySubmission/getGeographies?userID=kgarwood
                    //[{"names":["EW01","SAHSU","UK91"]}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getGeographies?userID=' + username, config);
                };
                self.getHealthThemes = function (username, geography) {
                    //http://localhost:8080/rifServices/studySubmission/getHealthThemes?userID=kgarwood&geographyName=SAHSU
                    //[{"name":"SAHSULAND","description":"SAHSU land cancer incidence example data"}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getHealthThemes?userID=' + username + '&geographyName=' + geography, config);
                };
                self.getNumerator = function (username, geography, healthThemeDescription) {
                    //http://localhost:8080/rifServices/studySubmission/getNumerator?userID=kgarwood&geographyName=SAHSU&healthThemeDescription=SAHSU%20land%20cancer%20incidence%20example%20data
                    //[{"numeratorTableName":"SAHSULAND_CANCER","numeratorTableDescription":"Cancer cases in SAHSU land","denominatorTableName":"SAHSULAND_POP","denominatorTableDescription":"SAHSU land population"}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getNumerator?userID=' + username + '&geographyName=' + geography + "&healthThemeDescription=" + healthThemeDescription, config);
                };
                //Investigation parameters
                self.getYearRange = function (username, geography, numeratorTableName) {
                    //http://localhost:8080/rifServices/studySubmission/getYearRange?userID=kgarwood&geographyName=SAHSU&numeratorTableName=SAHSULAND_CANCER
                    //[{"lowerBound":"1989","upperBound":"1996"}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getYearRange?userID=' + username + '&geographyName=' + geography + '&numeratorTableName=' + numeratorTableName, config);
                };
                self.getSexes = function (username) {
                    //http://localhost:8080/rifServices/studySubmission/getSexes?userID=kgarwood
                    //[{"names":["Males","Females","Both"]}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getSexes?userID=' + username, config);
                };
                self.getCovariates = function (username, geography, geoLevel) {
                    //http://localhost:8080/rifServices/studySubmission/getCovariates?userID=kgarwood&geographyName=SAHSU&geoLevelToMapName=LEVEL4
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getCovariates?userID=' + username + '&geographyName=' + geography + '&geoLevelToMapName=' + geoLevel, config);
                };
                self.getAgeGroups = function (username, geography, numerator) {
                    //http://localhost:8080/rifServices/studySubmission/getAgeGroups?userID=kgarwood&geographyName=SAHSU&numeratorTableName=SAHSULAND_CANCER
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getAgeGroups?userID=' + username + '&geographyName=' + geography + '&numeratorTableName=' + numerator, config);
                };
                //geography info
                self.getDefaultGeoLevelSelectValue = function (username, geography) {
                    //http://localhost:8080/rifServices/studySubmission/getDefaultGeoLevelSelectValue?userID=kgarwood&geographyName=SAHSU
                    //[{"names":["LEVEL2"]}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getDefaultGeoLevelSelectValue?userID=' + username + '&geographyName=' + geography, config);
                };
                self.getGeoLevelViews = function (username, geography, geoLevelSelectName) {
                    //http://localhost:8080/rifServices/studySubmission/getGeoLevelViews?userID=kgarwood&geographyName=SAHSU&geoLevelSelectName=LEVEL2
                    //[{"names":["LEVEL2","LEVEL3","LEVEL4"]}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getGeoLevelViews?userID=' + username + '&geographyName=' + geography +
                            '&geoLevelSelectName=' + geoLevelSelectName, config);
                };
                self.getGeoLevelSelectValues = function (username, geography) {
                    //http://localhost:8080/rifServices/studySubmission/getGeoLevelSelectValues?userID=kgarwood&geographyName=SAHSU
                    //[{"names":["LEVEL1","LEVEL2","LEVEL3","LEVEL4"]}]
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getGeoLevelSelectValues?userID=' + username + '&geographyName=' + geography, config);
                };
                //statistical methods
                self.getAvailableCalculationMethods = function (username) {
                    //http://localhost:8080/rifServices/studySubmission/getAvailableCalculationMethods?userID=kgarwood
                    //[{"codeRoutineName":"car_r_procedure","prior":"Standard deviation","description":"Applies ...
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getAvailableCalculationMethods?userID=' + username, config);
                };
                //status
                self.getCurrentStatusAllStudies = function (username) {
                    //http://localhost:8080/rifServices/studyResultRetrieval/getCurrentStatusAllStudies?userID=kgarwood              
                    //{"smoothed_results_header":["study_id","study_name","study_description","study_state","message","date"]
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getCurrentStatusAllStudies?userID=' + username, config);
                };
                //results for viewer
                self.getSmoothedResults = function (username, studyID, sex) {
                    //http://localhost:8080/rifServices/studyResultRetrieval/getSmoothedResults?userID=kgarwood&studyID=1&sex=1
                    //{"smoothed_results_header":["area_id","band_id","genders","observed","...
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getSmoothedResults?userID=' + username + '&studyID=' + studyID + '&sex=' + sex, config);
                };
                self.getAllPopulationPyramidData = function (username, studyID, year) {
                    //http://localhost:8080/rifServices/studyResultRetrieval/getAllPopulationPyramidData?userID=kgarwood&studyID=1&year=1990
                    //{smoothed_results_header:{population_label,males,females}smoothed_results:{{population_la...
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getAllPopulationPyramidData?userID=' + username + '&studyID=' + studyID + '&year=' + year, config);
                };
                //get info on a completed study
                self.getYearsForStudy = function (username, studyID, leaflet) {
                    config.leaflet = leaflet;
                    //http://localhost:8080/rifServices/studyResultRetrieval/getYearsForStudy?userID=kgarwood&study_id=1
                    //{"years{":["1989","1990","1991","1992","1993","1994","1995","1996"]}
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getYearsForStudy?userID=' + username + '&study_id=' + studyID, config);
                };
                self.getSexesForStudy = function (username, studyID, leaflet) {
                    config.leaflet = leaflet;
                    //http://localhost:8080/rifServices/studyResultRetrieval/getSexesForStudy?userID=kgarwood&study_id=1
                    //[{"names":["Males","Females","Both"]}]
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getSexesForStudy?userID=' + username + '&study_id=' + studyID, config);
                };
                self.getGeographyAndLevelForStudy = function (username, studyID) {
                    //http://localhost:8080/rifServices/studyResultRetrieval/getGeographyAndLevelForStudy?userID=kgarwood&studyID=239
                    //[["SAHSU","LEVEL3"]]
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getGeographyAndLevelForStudy?userID=' + username + '&studyID=' + studyID);
                };
                //get the map tiles from Tile-Maker
                //returns a string not a promise, is resolved in Leaflet GridLayer
                self.getTileMakerTiles = function (username, geography, geoLevel) {
                    //'http://localhost:8080/rifServices/studyResultRetrieval/getTileMakerTiles?userID=kgarwood&geographyName=SAHSU&geoLevelSelectName=LEVEL2&zoomlevel={z}&x={x}&y={y}';
                    return (studyResultRetrievalURL + DatabaseService.getDatabase() + 'getTileMakerTiles?userID=' + username + '&geographyName=' + geography + '&geoLevelSelectName=' + geoLevel +
                            '&zoomlevel={z}&x={x}&y={y}');
                };
                //get 'global' geography for attribute table
                self.getTileMakerTilesAttributes = function (username, geography, geoLevel) {
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getTileMakerTiles?userID=' + username + '&geographyName=' + geography + '&geoLevelSelectName=' + geoLevel +
                            '&zoomlevel=1&x=0&y=0', config);
                };
                //get all the centroids for the current geolevel
                self.getTileMakerCentroids = function (username, geography, geoLevel) {
                    //http://localhost:8080/rifServices/studyResultRetrieval/pg/getTileMakerCentroids?userID=dwmorley&geographyName=SAHSULAND&geoLevelSelectName=SAHSU_GRD_LEVEL4
                    return $http.get(studyResultRetrievalURL + DatabaseService.getDatabase() + 'getTileMakerCentroids?userID=' + username + '&geographyName=' + geography + '&geoLevelSelectName=' + geoLevel);
                };
                //get details of a completed study
                //TODO: this method does not give complete information
                self.getStudySubmission = function (username, studyID) {
                    //http://localhost:8080/rifServices/studySubmission/getStudySubmission?userID=kgarwood&studyID=274
                    return $http.get(studySubmissionURL + DatabaseService.getDatabase() + 'getStudySubmission?userID=' + username + '&studyID=' + studyID);
                };
            }]);