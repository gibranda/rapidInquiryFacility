package rifServices.dataStorageLayer.ms;

import rifGenericLibrary.businessConceptLayer.User;
import rifGenericLibrary.dataStorageLayer.SQLGeneralQueryFormatter;
import rifGenericLibrary.dataStorageLayer.ms.MSSQLFunctionCallerQueryFormatter;
import rifGenericLibrary.dataStorageLayer.ms.MSSQLQueryUtility;
import rifGenericLibrary.dataStorageLayer.ms.MSSQLSelectQueryFormatter;
import rifGenericLibrary.system.RIFServiceException;
import rifGenericLibrary.util.RIFLogger;
import rifServices.businessConceptLayer.AbstractRIFConcept.ValidationPolicy;
import rifServices.businessConceptLayer.AbstractGeographicalArea;
import rifServices.businessConceptLayer.GeoLevelArea;
import rifServices.businessConceptLayer.GeoLevelSelect;
import rifServices.businessConceptLayer.GeoLevelView;
import rifServices.businessConceptLayer.GeoLevelToMap;
import rifServices.businessConceptLayer.Geography;
import rifServices.businessConceptLayer.MapArea;
import rifServices.businessConceptLayer.MapAreaSummaryData;
import rifServices.businessConceptLayer.BoundaryRectangle;
import rifServices.system.RIFServiceError;
import rifServices.system.RIFServiceMessages;
import rifServices.system.RIFServiceStartupOptions;
import rifGenericLibrary.util.FieldValidationUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


/**
 *
 *
 * <hr>
 * The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
 * that rapidly addresses epidemiological and public health questions using 
 * routinely collected health and population data and generates standardised 
 * rates and relative risks for any given health outcome, for specified age 
 * and year ranges, for any given geographical area.
 *
 * <p>
 * Copyright 2017 Imperial College London, developed by the Small Area
 * Health Statistics Unit. The work of the Small Area Health Statistics Unit 
 * is funded by the Public Health England as part of the MRC-PHE Centre for 
 * Environment and Health. Funding for this project has also been received 
 * from the United States Centers for Disease Control and Prevention.  
 * </p>
 *
 * <pre> 
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
 * </pre>
 *
 * <hr>
 * Kevin Garwood
 * @author kgarwood
 * @version
 */

/*
 * Code Road Map:
 * --------------
 * Code is organised into the following sections.  Wherever possible, 
 * methods are classified based on an order of precedence described in 
 * parentheses (..).  For example, if you're trying to find a method 
 * 'getName(...)' that is both an interface method and an accessor 
 * method, the order tells you it should appear under interface.
 * 
 * Order of 
 * Precedence     Section
 * ==========     ======
 * (1)            Section Constants
 * (2)            Section Properties
 * (3)            Section Construction
 * (7)            Section Accessors and Mutators
 * (6)            Section Errors and Validation
 * (5)            Section Interfaces
 * (4)            Section Override
 *
 */

final class MSSQLMapDataManager 
	extends MSSQLAbstractSQLManager {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	/** The rif service startup options. */
	private RIFServiceStartupOptions rifServiceStartupOptions;
	
	/** The sql rif context manager. */
	private MSSQLRIFContextManager sqlRIFContextManager;
	
	/** The point from map identifier. */
	private HashMap<String, Point> pointFromMapIdentifier;
	
	// ==========================================
	// Section Construction
	// ==========================================

	/**
	 * Instantiates a new SQL map data manager.
	 *
	 * @param rifServiceStartupOptions the rif service startup options
	 * @param sqlRIFContextManager the sql rif context manager
	 */
	public MSSQLMapDataManager(
		final RIFServiceStartupOptions rifServiceStartupOptions,
		final MSSQLRIFContextManager sqlRIFContextManager) {

		super(rifServiceStartupOptions.getRIFDatabaseProperties());
		this.rifServiceStartupOptions = rifServiceStartupOptions;
		this.sqlRIFContextManager = sqlRIFContextManager;
		
		pointFromMapIdentifier = new HashMap<String, Point>();
	}

	// ==========================================
	// Section Accessors and Mutators///////
	// ==========================================?//

	/**
	 * Gets the map area summary information.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @param mapAreas the map areas
	 * @return the map area summary information
	 * @throws RIFServiceException the RIF service exception
	 */
	public MapAreaSummaryData getMapAreaSummaryInformation (
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap,
		final ArrayList<MapArea> mapAreas) throws RIFServiceException {

		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			mapAreas);	
		
		//TODO: Add in operations to compute total area and total population
		MapAreaSummaryData result
			= MapAreaSummaryData.newInstance();
		result.setTotalViewAreas(mapAreas.size());
		
		return result;
	}
		
	/**
	 * Gets the summary data for extent areas.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @return the summary data for extent areas
	 * @throws RIFServiceException the RIF service exception
	 */
	public MapAreaSummaryData getSummaryDataForExtentAreas (
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap) 
		throws RIFServiceException {

		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			null);	

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Integer numberOfAreas = null;
		try {

		
			//Step I: Obtain the lookup table associated with the resolution
			//of geoLevelSelect (which is also the one used by geoLevelArea)
			String geoLevelSelectTableName
				= getGeoLevelLookupTableName(
					connection,
					geography,
					geoLevelSelect.getName());

			//Step II: Obtain the lookup table associated with the resolution
			//of geoLevelToMap
			String geoLevelToMapTableName
				= getGeoLevelLookupTableName(
					connection,
					geography,
					geoLevelToMap.getName());
		
			//Step III: Obtain the hierarchy table associated with this geography
			String hierarchyTableName
				=  getGeographyHierarchyTableName(connection, geography); 
				
			//Step IV: Assemble the query needed to extract the map areas
			//of resolution geoLevelToMap but restricted to only those 
			//having the specified geoLevelArea
		
		
			//SQLCountQueryFormatter queryFormatter = new SQLCountQueryFormatter();
			//queryFormatter.setCountField(countField);

			SQLGeneralQueryFormatter queryFormatter
				= new SQLGeneralQueryFormatter();
			configureQueryFormatterForDB(queryFormatter);
			queryFormatter.addPaddedQueryLine(0, "SELECT");
			queryFormatter.addQueryPhrase(1, "COUNT(");
			queryFormatter.addQueryPhrase(geoLevelToMapTableName);
			queryFormatter.addQueryPhrase(".");
			queryFormatter.addQueryPhrase(geoLevelToMap.getName());
			queryFormatter.addQueryPhrase(")");
			queryFormatter.padAndFinishLine();
		
			queryFormatter.addPaddedQueryLine(0, "FROM");
			queryFormatter.addQueryPhrase(1, geoLevelSelectTableName);
			queryFormatter.addQueryPhrase(geoLevelSelectTableName);
			queryFormatter.addQueryPhrase(",");
			queryFormatter.padAndFinishLine();		
			queryFormatter.addQueryPhrase(1, hierarchyTableName);		
			queryFormatter.addQueryPhrase(",");		
			queryFormatter.padAndFinishLine();		
			queryFormatter.addQueryPhrase(1, geoLevelToMapTableName);		
			queryFormatter.padAndFinishLine();		
		
			queryFormatter.addPaddedQueryLine(0, "WHERE");
			queryFormatter.addQueryPhrase(1, geoLevelToMapTableName);
			queryFormatter.addQueryPhrase(".");
			queryFormatter.addQueryPhrase(geoLevelToMap.getName());
			queryFormatter.addQueryPhrase("=");
			queryFormatter.addQueryPhrase(hierarchyTableName);		
			queryFormatter.addQueryPhrase(".");
			queryFormatter.addQueryPhrase(geoLevelToMap.getName());
			queryFormatter.addQueryPhrase(" AND");
			queryFormatter.padAndFinishLine();		
		
			queryFormatter.addQueryPhrase(1, geoLevelSelectTableName);		
			queryFormatter.addQueryPhrase(".");
			queryFormatter.addQueryPhrase(geoLevelSelect.getName());
			queryFormatter.addQueryPhrase("=");
			queryFormatter.addQueryPhrase(hierarchyTableName);		
			queryFormatter.addQueryPhrase(".");
			queryFormatter.addQueryPhrase(geoLevelSelect.getName());
			queryFormatter.addQueryPhrase(" AND");
			queryFormatter.padAndFinishLine();		

			queryFormatter.addQueryPhrase(1, geoLevelSelectTableName);		
			queryFormatter.addQueryPhrase(".");
			queryFormatter.addQueryPhrase("name");
			queryFormatter.addQueryPhrase("=?");
			queryFormatter.finishLine();
				
			statement 
				= createPreparedStatement(
					connection, 
					queryFormatter);
			statement.setString(1, geoLevelArea.getName());
			resultSet = statement.executeQuery();			
			resultSet.next();			
			numberOfAreas = resultSet.getInt(1);
			
			connection.commit();
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			MSSQLQueryUtility.rollback(connection);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetAreaCount",
					geography.getName(),
					geoLevelToMap.getName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				MSSQLMapDataManager.class, 
				errorMessage, 
				sqlException);
			
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);			
		}
		
		MapAreaSummaryData result = MapAreaSummaryData.newInstance();
		result.setTotalViewAreas(numberOfAreas);
		
		return result;		
	}
	
	//TOUR_ADD_METHOD-3
	/*
	 * The service method will delegate to a manager class, which is responsible
	 * for executing the SQL query.  By this point in the execution path, we
	 * assume that the parameter values are:
	 * <ul>
	 * <li>not null</li>
	 * <li>contain no malicious code</code>
	 * </ul>
	 */
	public String getMapAreasForBoundaryRectangle(
			final Connection connection,
			final Geography geography,
			final GeoLevelSelect geoLevelSelect,
			final BoundaryRectangle boundaryRectangle) 
			throws RIFServiceException {

		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			null,
			null,
			null);	
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		StringBuilder results = new StringBuilder();
		try {

			ValidationPolicy validationPolicy = getValidationPolicy();
			boundaryRectangle.checkErrors(validationPolicy);
		
			/*
			 * In many cases we're just calling a stored procedure in the database
			 * Some of these may be migrated into the middleware if it turns out that
			 * SQL Server and PostgreSQL cannot support them uniformly.  We may also
			 * choose to migrate them into the middleware if the function call produces
			 * too great a performance overhead.  In these cases, we would presumably 
			 * obtain better performance by running the code within the functions here
			 * 'inline'.
			 */
			SQLGeneralQueryFormatter queryFormatter
				= new SQLGeneralQueryFormatter();
			queryFormatter.addPaddedQueryLine(0, "SELECT");
			queryFormatter.addQueryPhrase("rif40_xml_pkg.rif40_getMapAreas(?,?,?,?,?,?)");
			queryFormatter.padAndFinishLine();		
			queryFormatter.addQueryPhrase(0, "LIMIT 4");
				
			logSQLQuery("getMapAreasForBoundaryRectangle", 
				queryFormatter, 
				geography.getName(),
				geoLevelSelect.getName(),
				String.valueOf( Float.valueOf(boundaryRectangle.getYMax())),
				String.valueOf( Float.valueOf(boundaryRectangle.getXMax())),
				String.valueOf( Float.valueOf(boundaryRectangle.getYMin())),
				String.valueOf( Float.valueOf(boundaryRectangle.getXMin())));
									
			statement 
				= createPreparedStatement(
					connection, 
					queryFormatter);
			statement.setString(1, geography.getName());
			statement.setString(2, geoLevelSelect.getName());
			statement.setFloat(3, Float.valueOf(boundaryRectangle.getYMax()));
			statement.setFloat(4, Float.valueOf(boundaryRectangle.getXMax()));
			statement.setFloat(5, Float.valueOf(boundaryRectangle.getYMin()));
			statement.setFloat(6, Float.valueOf(boundaryRectangle.getXMin()));
			
			resultSet = statement.executeQuery();
			connection.commit();

			while (resultSet.next()) {
				//the method returns a JSON string.  We're just concatenating them together
				results.append(resultSet.getString(1));
			}
			
			connection.commit();
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			MSSQLQueryUtility.rollback(connection);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetMapAreasForBoundaryRectangle",
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName(),
					boundaryRectangle.getDisplayName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				MSSQLMapDataManager.class, 
				errorMessage, 
				sqlException);
								
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);			
		}
		
		return results.toString();		
	}
	
	public ArrayList<MapArea> getAllRelevantMapAreas(
		final Connection connection,
		final Geography geography,
		final AbstractGeographicalArea geographicalArea)
		throws RIFServiceException {
		
		System.out.println("SQLMapDataManager getAllRelevantAreas!!!!!!!!!!!");
		ArrayList<MapArea> allRelevantMapAreas = new ArrayList<MapArea>();

		GeoLevelSelect geoLevelSelect
			= geographicalArea.getGeoLevelSelect();		
		GeoLevelToMap geoLevelToMap
			= geographicalArea.getGeoLevelToMap();
		
		
		ArrayList<MapArea> selectedMapAreas
			= geographicalArea.getMapAreas();
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			/*
			 * Step 1: Obtain the geography table; This maps the map identifier as it is known
			 * at the GeoLevelSelect level to the map identifier as it is known at the finer
			 * resolution of GeoLevelToMap
			 */
			//Obtain geography table eg: sahsuland_geography
			String mapAreaResolutionMappingTableName
				= getMapAreaResolutionMappingAreaTableName(
					connection,
					geography);

			String geoLevelToMapTableName
				= getGeoLevelLookupTableName(
					connection,
					geography,
					geoLevelToMap.getName());
			
			/*
			 * Example:
			 * 
			 * SELECT
			 *    gid,
			 *    level4
			 * FROM
			 *    mapAreaResolutionMappingTableName,  //eg: sahsuland_geography
			 *    geoLevelToMapTableName //eg: sahsuland_level4
			 * WHERE
			 *    level2='01.001' OR  //iteratively read in each map area provided by
			 *    level2='01.002' OR  //by client
			 *    level2='01.003' OR
			 *    ...
			 * 
			 * 
			 * 
			 * 
			 * 			
			 */
			SQLGeneralQueryFormatter queryFormatter = new SQLGeneralQueryFormatter();
			queryFormatter.addQueryLine(0, "SELECT DISTINCT");
			queryFormatter.addQueryPhrase(1, geoLevelToMapTableName);			
			queryFormatter.addQueryPhrase(".gid,");
			queryFormatter.addQueryPhrase(geoLevelToMapTableName);			
			queryFormatter.addQueryPhrase(".");			
			queryFormatter.addQueryPhrase(geoLevelToMap.getName());			
			queryFormatter.padAndFinishLine();			
			queryFormatter.addQueryLine(0, "FROM ");
			queryFormatter.addQueryPhrase(1, "rif_data." + mapAreaResolutionMappingTableName);
			queryFormatter.addQueryPhrase(",");
			queryFormatter.addQueryPhrase("rif_data." + geoLevelToMapTableName);
			queryFormatter.padAndFinishLine();			
			queryFormatter.addQueryLine(0, "WHERE");
			
			queryFormatter.addQueryPhrase(1, geoLevelToMapTableName);
			queryFormatter.addQueryPhrase(".");
			queryFormatter.addQueryPhrase(geoLevelToMap.getName());			
			queryFormatter.addQueryPhrase("=");
			queryFormatter.addQueryPhrase(mapAreaResolutionMappingTableName);
			queryFormatter.addQueryPhrase(".");			
			queryFormatter.addQueryPhrase(geoLevelToMap.getName());			
			
			
			int totalSelectedMapAreas = selectedMapAreas.size();
			if (totalSelectedMapAreas > 0) {

				queryFormatter.addQueryPhrase(" AND (");			
				queryFormatter.padAndFinishLine();
				
				String geoLevelSelectLevelName = geoLevelSelect.getName();
				//String geoLevelSelectLevelName = geoLevelToMap.getName();
			
				for (int i = 0 ; i < selectedMapAreas.size(); i++) {
					if (i != 0) {
						queryFormatter.padAndFinishLine();			
						queryFormatter.addQueryPhrase(1, " OR ");
					}
					
					queryFormatter.addQueryPhrase(mapAreaResolutionMappingTableName);					
					//queryFormatter.addQueryPhrase(geoLevelToMapTableName);					
					queryFormatter.addQueryPhrase(".");
					queryFormatter.addQueryPhrase(geoLevelSelectLevelName);
					queryFormatter.addQueryPhrase("=\'");
					queryFormatter.addQueryPhrase(selectedMapAreas.get(i).getIdentifier());
					queryFormatter.addQueryPhrase("'");
				}
				
				queryFormatter.addQueryPhrase(")");
			}
			
			queryFormatter.addQueryPhrase(";");
			
			logSQLQuery(
				"getAllRelevantMapAreas", 
				queryFormatter, 
				geography.getName(),
				geoLevelToMap.getName());
			
			statement
				= createPreparedStatement(
					connection, 
					queryFormatter);
			
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String identifier
					= resultSet.getString(1);
				String name
					= resultSet.getString(2);
				
				MapArea mapArea
					= MapArea.newInstance(
						identifier, 
						identifier, 
						name);
				allRelevantMapAreas.add(mapArea);
				
			}
		}
		catch(SQLException sqlException) {
			logException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToRetrievaAllRelevantMapAreas");
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.UNABLE_TO_RETRIEVE_ALL_RELEVANT_MAP_AREAS, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);
		}
	
		return allRelevantMapAreas;
	}
	
	private String getMapAreaResolutionMappingAreaTableName(
		final Connection connection,
		final Geography geography) 
		throws SQLException,
		RIFServiceException {
				
		String result = "";
				
		MSSQLSelectQueryFormatter queryFormatter = new MSSQLSelectQueryFormatter(false);
		queryFormatter.addSelectField("hierarchytable");
		queryFormatter.addFromTable("rif40.rif40_geographies");
		queryFormatter.addWhereParameter("geography");
		
		logSQLQuery(
			"getMapAreaResolutionMappingAreaTableName", 
			queryFormatter, 
			geography.getName());
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement 
				= createPreparedStatement(
					connection, 
					queryFormatter);
			statement.setString(1, geography.getName());
			resultSet = statement.executeQuery();
			
			resultSet.next();
			result = resultSet.getString(1);
		}
		finally {
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);
		}
		
		return result;
	}	
	
	/**
	 * Gets the map areas.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @return the map areas
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<MapArea> getMapAreas(
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap) 
		throws RIFServiceException {
		
		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			null);	

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<MapArea> results = new ArrayList<MapArea>();
		try {

			//Step I: Obtain the lookup table associated with the resolution
			//of geoLevelSelect (which is also the one used by geoLevelArea)
			String geoLevelSelectTableName
				= getGeoLevelLookupTableName(
					connection,
					geography,
					geoLevelSelect.getName());

			//Step II: Obtain the lookup table associated with the resolution
			//of geoLevelToMap
			String geoLevelToMapTableName
				= getGeoLevelLookupTableName(
					connection,
					geography,
					geoLevelToMap.getName());
		
			//Step III: Obtain the hierarchy table associated with this geography
			String hierarchyTableName
				=  getGeographyHierarchyTableName(connection, geography); 
		
				
			//Step IV: Assemble the query needed to extract the map areas
			//of resolution geoLevelToMap but restricted to only those 
			//having the specified geoLevelArea
		
			SQLGeneralQueryFormatter extractMapAreasQuery
				= new SQLGeneralQueryFormatter();
			configureQueryFormatterForDB(extractMapAreasQuery);
			
			extractMapAreasQuery.addPaddedQueryLine(0, "SELECT DISTINCT");
			extractMapAreasQuery.addQueryPhrase(1, geoLevelToMapTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
			extractMapAreasQuery.addQueryPhrase(",");
			extractMapAreasQuery.finishLine();
		
			extractMapAreasQuery.addQueryPhrase(1, geoLevelToMapTableName);
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase("name");
			extractMapAreasQuery.padAndFinishLine();
			extractMapAreasQuery.addPaddedQueryLine(0, "FROM");
			extractMapAreasQuery.addQueryPhrase(1, geoLevelSelectTableName);		
			extractMapAreasQuery.addQueryPhrase(",");
			extractMapAreasQuery.finishLine();		
			extractMapAreasQuery.addQueryPhrase(1, hierarchyTableName);		
			extractMapAreasQuery.addQueryPhrase(",");
			extractMapAreasQuery.finishLine();
			extractMapAreasQuery.addPaddedQueryLine(1, geoLevelToMapTableName);		

			extractMapAreasQuery.addPaddedQueryLine(0, "WHERE");
		
			extractMapAreasQuery.addQueryPhrase(1, geoLevelToMapTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
			extractMapAreasQuery.addQueryPhrase("=");
			extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
			extractMapAreasQuery.addQueryPhrase(" AND");
			extractMapAreasQuery.padAndFinishLine();
		
			extractMapAreasQuery.addQueryPhrase(1, geoLevelSelectTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
			extractMapAreasQuery.addQueryPhrase("=");
			extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
			extractMapAreasQuery.addQueryPhrase(" AND");
			extractMapAreasQuery.padAndFinishLine();
				
			extractMapAreasQuery.addQueryPhrase(1, geoLevelSelectTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase("name");
			extractMapAreasQuery.addQueryPhrase("=?");
		
			statement 
				= createPreparedStatement(connection, extractMapAreasQuery);
			statement.setString(1, geoLevelArea.getName());
			resultSet = statement.executeQuery();
			connection.commit();
			
			while (resultSet.next()) {
				MapArea mapArea = MapArea.newInstance();
				String identifier = resultSet.getString(1);
				if (identifier != null) {
					//KLG:  @TODO change this when db supports geographical identifier
					mapArea.setGeographicalIdentifier(identifier);
					mapArea.setIdentifier(identifier);					
				}

				String label = resultSet.getString(2);
				if (label != null) {
					mapArea.setLabel(label);
				}
				results.add(mapArea);
			}
			
			connection.commit();
			
			//@TODO: Ideally we'd like to put this first before we iterate
			//through all the results.  Normally I would use resultSet.last(),
			//followed by getRow() but this requires a scrollable result set.
			//For now we have this but perhaps in future we need a separate
			//method for indicating whether the count is too high
			checkNumberMapAreaResultsBelowThreshold(results.size());	
			
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version
			logSQLException(sqlException);
			MSSQLQueryUtility.rollback(connection);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetMapAreas",
					geography.getName(),
					geoLevelToMap.getName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				MSSQLMapDataManager.class, 
				errorMessage, 
				sqlException);
								
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);			
		}
		
		return results;		
	}
	
	

	/**
	 * Gets the map areas.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @param startIndex the start index
	 * @param endIndex the end index
	 * @return the map areas
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<MapArea> getMapAreas(
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap,
		final Integer startIndex,
		final Integer endIndex) 
		throws RIFServiceException {

		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			null);	
		
		FieldValidationUtility.hasDifferentNullity(startIndex, endIndex);
		
		if (startIndex > endIndex) {
			//start index cannot be greater than end index
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.mapAreaStartIndexMoreThanEndIndex",
					String.valueOf(startIndex),
					String.valueOf(endIndex));
			RIFServiceException rifServiceException
				 = new RIFServiceException(
					RIFServiceError.MAP_AREA_START_INDEX_MORE_THAN_END_INDEX, 
					errorMessage);
			throw rifServiceException;
		}

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<MapArea> results = new ArrayList<MapArea>();
		try {
		
			//Step I: Obtain the lookup table associated with the resolution
			//of geoLevelSelect (which is also the one used by geoLevelArea)
			String geoLevelSelectTableName
				= getGeoLevelLookupTableName(
					connection,
					geography,
					geoLevelSelect.getName());

			//Step II: Obtain the lookup table associated with the resolution
			//of geoLevelToMap
			String geoLevelToMapTableName
				= getGeoLevelLookupTableName(
					connection,
					geography,
					geoLevelToMap.getName());
		
			//Step III: Obtain the hierarchy table associated with this geography
			String hierarchyTableName
				=  getGeographyHierarchyTableName(connection, geography); 
		
				
			//Step IV: Assemble the query needed to extract the map areas
			//of resolution geoLevelToMap but restricted to only those 
			//having the specified geoLevelArea
		
			SQLGeneralQueryFormatter extractMapAreasQuery = new SQLGeneralQueryFormatter();
			configureQueryFormatterForDB(extractMapAreasQuery);
			//extractMapAreasQuery.addQueryPhrase("WITH ordered_results AS (");
			//StringBuilder extractMapAreasQuery = new StringBuilder();

			extractMapAreasQuery.addQueryLine(0, "WITH ordered_results AS ( ");
			extractMapAreasQuery.addQueryLine(1, "SELECT row_number() ");
			extractMapAreasQuery.addQueryPhrase(2, "OVER(ORDER BY ");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMapTableName);
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
			extractMapAreasQuery.addQueryPhrase(" ASC,");
			extractMapAreasQuery.finishLine();
		
			extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase("name");
			extractMapAreasQuery.addQueryPhrase(" ASC) AS row,");	
			extractMapAreasQuery.finishLine();

			extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
			extractMapAreasQuery.addQueryPhrase(" AS identifier,");
			extractMapAreasQuery.finishLine();
				
			extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase("name");
			extractMapAreasQuery.padAndFinishLine();
		
			extractMapAreasQuery.addQueryPhrase(1, "FROM");
			extractMapAreasQuery.padAndFinishLine();
			extractMapAreasQuery.addQueryPhrase(2, geoLevelSelectTableName);		
			extractMapAreasQuery.addQueryPhrase(",");
			extractMapAreasQuery.finishLine();
		
			extractMapAreasQuery.addQueryPhrase(2, hierarchyTableName);		
			extractMapAreasQuery.addQueryPhrase(",");
			extractMapAreasQuery.finishLine();
		
			extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
			extractMapAreasQuery.finishLine();
		
			extractMapAreasQuery.addQueryPhrase(1, "WHERE");
			extractMapAreasQuery.padAndFinishLine();

			extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
			extractMapAreasQuery.addQueryPhrase("=");
			extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
			extractMapAreasQuery.addQueryPhrase(" AND");
			extractMapAreasQuery.padAndFinishLine();
		
			extractMapAreasQuery.addQueryPhrase(2, geoLevelSelectTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
			extractMapAreasQuery.addQueryPhrase("=");
			extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
			extractMapAreasQuery.addQueryPhrase(" AND");
			extractMapAreasQuery.padAndFinishLine();
		
			extractMapAreasQuery.addQueryPhrase(2, geoLevelSelectTableName);		
			extractMapAreasQuery.addQueryPhrase(".");
			extractMapAreasQuery.addQueryPhrase("name");
			extractMapAreasQuery.addQueryPhrase("=?");
			extractMapAreasQuery.addQueryPhrase(")");
			extractMapAreasQuery.padAndFinishLine();
		
			extractMapAreasQuery.addQueryPhrase(0, "SELECT DISTINCT");
			extractMapAreasQuery.padAndFinishLine();
		
			extractMapAreasQuery.addQueryLine(1, "identifier,");
			extractMapAreasQuery.addQueryPhrase(1, "name");
			extractMapAreasQuery.padAndFinishLine();

			extractMapAreasQuery.addQueryPhrase(0, "FROM");
			extractMapAreasQuery.padAndFinishLine();
			extractMapAreasQuery.addQueryPhrase(1, "ordered_results");
			extractMapAreasQuery.padAndFinishLine();

			extractMapAreasQuery.addQueryPhrase(0, "WHERE");
			extractMapAreasQuery.padAndFinishLine();

			extractMapAreasQuery.addQueryPhrase(1, "row >= ? AND");
			extractMapAreasQuery.padAndFinishLine();
			extractMapAreasQuery.addQueryPhrase(1, "row <= ?");
			
			logSQLQuery(
				"getMapAreas",
				extractMapAreasQuery,
				geoLevelArea.getName(),
				String.valueOf(startIndex),
				String.valueOf(endIndex));
		
			statement 
				= createPreparedStatement(connection, extractMapAreasQuery);
			statement.setString(1, geoLevelArea.getName());
			statement.setInt(2, startIndex);
			statement.setInt(3, endIndex);
			resultSet = statement.executeQuery();
			connection.commit();

			while (resultSet.next()) {
				MapArea mapArea = MapArea.newInstance();
				String identifier = resultSet.getString(1);
				if (identifier != null) {
					//KLG: Later on, database should return these as separate things
					mapArea.setIdentifier(identifier);
					mapArea.setGeographicalIdentifier(identifier);
				}
				String label = resultSet.getString(2);
				if (label != null) {
					mapArea.setLabel(label);
				}
				results.add(mapArea);
			}	
			
			connection.commit();
			
			//@TODO: Ideally we'd like to put this first before we iterate
			//through all the results.  Normally I would use resultSet.last(),
			//followed by getRow() but this requires a scrollable result set.
			//For now we have this but perhaps in future we need a separate
			//method for indicating whether the count is too high
			checkNumberMapAreaResultsBelowThreshold(results.size());	
			
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			MSSQLQueryUtility.rollback(connection);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetMapAreas",
					geography.getName(),
					geoLevelToMap.getName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				MSSQLMapDataManager.class, 
				errorMessage, 
				sqlException);
								
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);			
		}
		
		return results;		
	}
	
	
	/**
	 * Gets the geo level lookup table name.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param resolutionLevel the resolution level
	 * @return the geo level lookup table name
	 * @throws RIFServiceException the RIF service exception
	 */
	private String getGeoLevelLookupTableName( 
		final Connection connection,
		final Geography geography,
		final String resolutionLevel) 
		throws SQLException,
		RIFServiceException { 

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String result = null;
		try {
		
			MSSQLSelectQueryFormatter queryFormatter 
				= new MSSQLSelectQueryFormatter(false);
			configureQueryFormatterForDB(queryFormatter);
			queryFormatter.addSelectField("lookup_table");
			queryFormatter.addFromTable("rif40.rif40_geolevels");
			queryFormatter.addWhereParameter("geography");
			queryFormatter.addWhereParameter("geolevel_name");
		
			logSQLQuery(
				"getGeoLevelLookupTableName",
				queryFormatter,
				geography.getName(),
				resolutionLevel);
		
			statement 
				= createPreparedStatement(
					connection, 
					queryFormatter);
			statement.setString(1, geography.getName());
			statement.setString(2, resolutionLevel);
			resultSet = statement.executeQuery();
			connection.commit();
			
			if (resultSet.next() == false) {
				//this method assumes that geoLevelSelect is valid
				//Therefore, it must be associated with a lookup table
				assert false;
			}		
			
			result
				= useAppropariateTableNameCase(resultSet.getString(1));
		}
		finally {
			//Cleanup database resources			
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);		
		}
		
		return result;
	}
	
	/**
	 * Gets the geography hierarchy table name.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @return the geography hierarchy table name
	 * @throws RIFServiceException the RIF service exception
	 */
	private String getGeographyHierarchyTableName( 
		final Connection connection,
		final Geography geography) 
		throws SQLException,
		RIFServiceException { 

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String result = null;
		try {
					
			MSSQLSelectQueryFormatter queryFormatter 
				= new MSSQLSelectQueryFormatter(false);
			configureQueryFormatterForDB(queryFormatter);
			queryFormatter.addSelectField("hierarchytable");
			queryFormatter.addFromTable("rif40.rif40_geographies");
			queryFormatter.addWhereParameter("geography");

			logSQLQuery(
				"getGeographyHierarchyTableName",
				queryFormatter,
				geography.getName());
		
			statement 
				= createPreparedStatement(
					connection, 
					queryFormatter);
			statement.setString(1, geography.getName());
			resultSet = statement.executeQuery();
			connection.commit();
			if (resultSet.next() == false) {
				//this method assumes that geoLevelSelect is valid
				//Therefore, it must be associated with a lookup table
				assert false;
			}			
			result
				= useAppropariateTableNameCase(resultSet.getString(1));			
		}
		finally {
			//Cleanup database resources			
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);		
		}
			
		return result;
	}
	
	/**
	 * Gets the image.
	 *
	 * @param connection the connection
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelView the geo level view
	 * @param mapAreas the map areas
	 * @return the image
	 * @throws RIFServiceException the RIF service exception
	 */
	public BufferedImage getImage(
		final Connection connection,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelView geoLevelView,
		final ArrayList<MapArea> mapAreas) 
		throws RIFServiceException {
		
		BufferedImage result = null;
		try {
			File serverSideCacheDirectory
				= rifServiceStartupOptions.getServerSideCacheDirectory();
			StringBuilder imageFilePath
				= new StringBuilder();
			imageFilePath.append(serverSideCacheDirectory.getAbsolutePath());
			imageFilePath.append(File.separator);
			imageFilePath.append("mappamundilge.JPG");
			File imageFile = new File(imageFilePath.toString());			
			result = ImageIO.read(imageFile);
			for (MapArea mapArea : mapAreas) {
				drawMapArea(result, mapArea);				
			}
			//now colour dots	
		}
		catch(IOException ioException) {
			String errorMessage
				= RIFServiceMessages.getMessage("io.error.problemsReadingMapImageData");
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		return result;
	}
	
	/**
	 * Draw map area.
	 *
	 * @param bufferedImage the buffered image
	 * @param mapArea the map area
	 */
	private void drawMapArea(
		final BufferedImage bufferedImage,
		final MapArea mapArea) {
		
		Point pointForMapArea 
			= pointFromMapIdentifier.get(mapArea.getIdentifier());
		if (pointForMapArea == null) {			
			int imageWidth = bufferedImage.getWidth();
			int imageHeight = bufferedImage.getHeight();

			//assume that dimension of the map will always be at least 10
			//in each direction
			int randomX = (int) (Math.random() * (imageWidth - 10));
			int randomY = (int) (Math.random() * (imageHeight - 10));
			pointForMapArea = new Point(randomX, randomY);
		}
				
		Graphics2D graphics2D 
			= (Graphics2D) bufferedImage.getGraphics();
		graphics2D.setColor(Color.RED);
		graphics2D.fillOval(
			(int) pointForMapArea.getX(), 
			(int) pointForMapArea.getY(), 
			20, 
			20);
	}
	
	public String getGeometry(
		final Connection connection,
		final User user,
		final Geography geography,	
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelView geoLevelView,
		final ArrayList<MapArea> mapAreas) throws RIFServiceException {

		
		ValidationPolicy validationPolicy = getValidationPolicy();
		
		user.checkErrors();
		geography.checkErrors(validationPolicy);
		geoLevelSelect.checkErrors(validationPolicy);
		geoLevelView.checkErrors(validationPolicy);
		
		if (mapAreas.isEmpty()) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.noMapAreasSpecified");
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.NO_MAP_AREAS_SPECIFIED, 
					errorMessage);
			throw rifServiceException;
		}
		
		for (MapArea mapArea : mapAreas) {
			mapArea.checkErrors(validationPolicy);
		}

		String geographyName = geography.getName();
		String geoLevelSelectName = geoLevelSelect.getName();

		sqlRIFContextManager.checkGeographyExists(
			connection, 
			geographyName);
		sqlRIFContextManager.checkGeoLevelSelectExists(
			connection,
			geographyName,
			geoLevelSelectName);
		sqlRIFContextManager.checkGeoLevelToMapOrViewValueExists(
			connection, 
			geographyName, 
			geoLevelSelectName, 
			geoLevelView.getName(), 
			false);
		
		checkAreasExist(
			connection,
			geography.getName(),
			geoLevelSelect.getName(),
			mapAreas);
						
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String result = "";
		try {		
			MSSQLFunctionCallerQueryFormatter queryFormatter
				= new MSSQLFunctionCallerQueryFormatter(false);
			configureQueryFormatterForDB(queryFormatter);
			queryFormatter.setDatabaseSchemaName("rif40_xml_pkg");
			queryFormatter.setFunctionName("rif40_get_geojson_as_js");
			queryFormatter.setNumberOfFunctionParameters(5);

			//@TODO: the DB query needs to be altered to support an array of 
			//map areas
			String[] mapAreaIdentifiers = MapArea.getMapAreaIdentifierList(mapAreas);

			logSQLQuery(
				"getGeometry",
				queryFormatter,
				geography.getName(),
				geoLevelView.getName(),
				geoLevelSelect.getName(),
				mapAreaIdentifiers[0],
				String.valueOf(false));				
		
			statement 
				= createPreparedStatement(connection, queryFormatter);
			statement.setString(1, geography.getName());
			statement.setString(2, geoLevelView.getName());
			statement.setString(3, geoLevelSelect.getName());
			statement.setString(4, mapAreaIdentifiers[0]);
			statement.setBoolean(5, false);		
			resultSet = statement.executeQuery();
			connection.commit();
			resultSet.next();
			result = resultSet.getString(1);
			
			connection.commit();
		}
		catch(SQLException sqlException) {
			logSQLException(sqlException);
			MSSQLQueryUtility.rollback(connection);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetGeometry",
					geography.getName(),
					geoLevelSelect.getName(),
					geoLevelView.getName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);			
		}
		
		return result;
	}
	
	
	// ==========================================
	// Section Errors and Validation
	// ==========================================

	private void checkNumberMapAreaResultsBelowThreshold(
		final int numberOfMapAreasRetrieved) 
		throws RIFServiceException {

		int maximumMapAreasAllowedForSingleDisplay
			= rifServiceStartupOptions.getMaximumMapAreasAllowedForSingleDisplay();
		if (numberOfMapAreasRetrieved > maximumMapAreasAllowedForSingleDisplay) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.thresholdMapAreasPerDisplayExceeded",
					String.valueOf(numberOfMapAreasRetrieved),
					String.valueOf(maximumMapAreasAllowedForSingleDisplay));
			
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.THRESHOLD_MAP_AREAS_PER_DISPLAY_EXCEEDED, 
					errorMessage);
			throw rifServiceException;
		}
	}
	
	public void checkAreasExist(
		final Connection connection,
		final String geographyName,
		final String geoLevelName,
		final ArrayList<MapArea> mapAreas) 
		throws RIFServiceException {

		/*
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			//find the correct table corresponding to the geography
			//eg: SAHSULAND_GEOGRAPHY
			String geographyTableName
				= getGeographyTableName(
					connection, 
					geographyName);

			SQLRecordExistsQueryFormatter queryFormatter
				= new SQLRecordExistsQueryFormatter();
			configureQueryFormatterForDB(queryFormatter);
			queryFormatter.setFromTable(geographyTableName);
			queryFormatter.setLookupKeyFieldName(geoLevelName);
			
			logSQLQuery(
				"checkAreasExist -- example",
				queryFormatter,
				mapAreas.get(0).getIdentifier());
			
			statement 
				= createPreparedStatement(connection, queryFormatter);
			ArrayList<String> errorMessages = new ArrayList<String>();
			for (MapArea mapArea : mapAreas) {
				statement.setString(1, mapArea.getIdentifier());
				resultSet = statement.executeQuery();
				
				if (resultSet.next() == false) {
					//no result, which means identifier was not found
					String errorMessage
						= RIFServiceMessages.getMessage(
							"sqlMapDataManager.error.nonExistentMapArea",
							mapArea.getIdentifier(),
							geographyName,
							geoLevelName);
					errorMessages.add(errorMessage);
				}
			}	
			
			connection.commit();
			
			if (errorMessages.size() > 0) {
				RIFServiceException rifServiceException
					= new RIFServiceException(
						RIFServiceError.NON_EXISTENT_MAP_AREA,
						errorMessages);
				throw rifServiceException;
			}
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			SQLQueryUtility.rollback(connection);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToCheckMapAreasExists",
					geographyName,
					geoLevelName);
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);
		}
		
		*/
	}

	/**
	 * Assumes that geography exists
	 * @param connection
	 * @param geography
	 * @return
	 * @throws SQLException
	 */
	private String getGeographyTableName(
		final Connection connection,
		final String geographyName) 
		throws SQLException,
		RIFServiceException {

		MSSQLSelectQueryFormatter queryFormatter
			= new MSSQLSelectQueryFormatter(false);
		configureQueryFormatterForDB(queryFormatter);
		queryFormatter.addSelectField("hierarchytable");
		queryFormatter.addFromTable("rif40.rif40_geographies");
		queryFormatter.addWhereParameter("geography");

		logSQLQuery(
			"getGeographyTableName",
			queryFormatter,
			geographyName);
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement 
				= createPreparedStatement(
					connection, 
					queryFormatter);
			statement.setString(1, geographyName);
			
			resultSet = statement.executeQuery();
			connection.commit();
			resultSet.next();
						
			return resultSet.getString(1);			
		}
		finally {
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);
		}		
	}

	public BoundaryRectangle getGeoLevelFullExtent(
		final Connection connection,
		final User user,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect)
		throws RIFServiceException {
		
		//Validate parameters
		ValidationPolicy validationPolicy = getValidationPolicy();		
		geography.checkErrors(validationPolicy);
		geoLevelSelect.checkErrors(validationPolicy);
		sqlRIFContextManager.checkGeographyExists(
			connection, 
			geography.getName());
		sqlRIFContextManager.checkGeoLevelSelectExists(
			connection, 
			geography.getName(), 
			geoLevelSelect.getName());
					
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {

			//Create query
			MSSQLFunctionCallerQueryFormatter queryFormatter
				= new MSSQLFunctionCallerQueryFormatter(false);
			configureQueryFormatterForDB(queryFormatter);
			queryFormatter.setDatabaseSchemaName("rif40_xml_pkg");
			queryFormatter.setFunctionName("rif40_getgeolevelfullextent");
			queryFormatter.setNumberOfFunctionParameters(2);		
		
			logSQLQuery(
				"getGeoLevelFullExtent",
				queryFormatter,
				geography.getName(),
				geoLevelSelect.getName());
		
			//Execute query and generate results
			statement
				= createPreparedStatement(
					connection, 
					queryFormatter);
			statement.setString(1, geography.getName());
			statement.setString(2, geoLevelSelect.getName());				
			resultSet = statement.executeQuery();
			
			//Assume there is at least one row result
			resultSet.next();
			
			double yMax = resultSet.getDouble(1);
			double xMax = resultSet.getDouble(2);
			double yMin = resultSet.getDouble(3);			
			double xMin = resultSet.getDouble(4);
			
			BoundaryRectangle result
				= BoundaryRectangle.newInstance(
					String.valueOf(xMin),
					String.valueOf(yMin),
					String.valueOf(xMax),
					String.valueOf(yMax));
			
			connection.commit();
			
			return result;
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.unableToGetBoundsForGeoLevel",
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources
			MSSQLQueryUtility.close(statement);
			MSSQLQueryUtility.close(resultSet);
		}		
	}


	public BoundaryRectangle getGeographyFullExtent(
		final Connection connection,
		final User user,
		final Geography geography)
		throws RIFServiceException {
		
		//Validate parameters
		ValidationPolicy validationPolicy = getValidationPolicy();
		geography.checkErrors(validationPolicy);
		sqlRIFContextManager.checkGeographyExists(
			connection, 
			geography.getName());
					
		PreparedStatement findLowestResolutionStatement = null;
		ResultSet findLowestResolutionResultSet = null;
		PreparedStatement getMapAreaIDStatement = null;
		ResultSet getMapAreaIDResultSet = null;

		PreparedStatement getBoundsForGeographyStatement = null;
		ResultSet getBoundsForGeographyResultSet = null;		
		
		try {
				
			//Determine the geo level name and look up table for the lowest resolution
			//of the geography
			SQLGeneralQueryFormatter findLowestResolutionQueryFormatter
				= new SQLGeneralQueryFormatter();
			findLowestResolutionQueryFormatter.addQueryLine(0, "SELECT");
			findLowestResolutionQueryFormatter.addQueryLine(1, "geolevel_name,");
			findLowestResolutionQueryFormatter.addQueryLine(1, "LOWER(lookup_table) AS lookup_table");
			findLowestResolutionQueryFormatter.addQueryLine(0, "FROM");
			findLowestResolutionQueryFormatter.addQueryLine(1, "rif40.rif40_geolevels");
			findLowestResolutionQueryFormatter.addQueryLine(0, "WHERE");
			
			findLowestResolutionQueryFormatter.addQueryLine(1, "geography=? AND ");
			findLowestResolutionQueryFormatter.addQueryLine(1, "geolevel_id=1");
			
			logSQLQuery(
				"findLowestResolutionQueryFormatter",
				findLowestResolutionQueryFormatter,
				geography.getName());
		
			//Execute query and generate results
			findLowestResolutionStatement
				= createPreparedStatement(
					connection, 
					findLowestResolutionQueryFormatter);
			findLowestResolutionStatement.setString(1, geography.getName());				
			findLowestResolutionResultSet = findLowestResolutionStatement.executeQuery();
			
			//Assume there is at least one row result
			findLowestResolutionResultSet.next();
			
			String geoLevelName = findLowestResolutionResultSet.getString(1);
			String lookupTable = findLowestResolutionResultSet.getString(2);
			
			
			//Obtain the map area id
			MSSQLSelectQueryFormatter getMapAreaIDQueryFormatter = new MSSQLSelectQueryFormatter(false);
			getMapAreaIDQueryFormatter.addSelectField(geoLevelName);
			getMapAreaIDQueryFormatter.addFromTable(lookupTable);

			logSQLQuery(
				"getMapAreaIDQueryFormatter",
				getMapAreaIDQueryFormatter);
			
			getMapAreaIDStatement 
				= createPreparedStatement(
					connection, 
					getMapAreaIDQueryFormatter);
			getMapAreaIDResultSet
				= getMapAreaIDStatement.executeQuery();
			getMapAreaIDResultSet.next();			
			String mapAreaID
				= getMapAreaIDResultSet.getString(1);
			
			
			
			//now call the getGeoLevelBoundsForArea function 
			MSSQLFunctionCallerQueryFormatter getBoundsForGeographyQueryFormatter
				= new MSSQLFunctionCallerQueryFormatter(false);
			getBoundsForGeographyQueryFormatter.addSelectField("y_max");
			getBoundsForGeographyQueryFormatter.addSelectField("x_max");
			getBoundsForGeographyQueryFormatter.addSelectField("y_min");
			getBoundsForGeographyQueryFormatter.addSelectField("x_min");
			getBoundsForGeographyQueryFormatter.setDatabaseSchemaName("rif40_xml_pkg");
			getBoundsForGeographyQueryFormatter.setFunctionName("rif40_getGeoLevelBoundsForArea");
			getBoundsForGeographyQueryFormatter.setNumberOfFunctionParameters(3);
			
			getBoundsForGeographyStatement
				= createPreparedStatement(
					connection, 
					getBoundsForGeographyQueryFormatter);
					
			getBoundsForGeographyStatement.setString(1, geography.getName());
			getBoundsForGeographyStatement.setString(2, geoLevelName);
			getBoundsForGeographyStatement.setString(3, mapAreaID);
						
			logSQLQuery(
				"getBoundsForGeographyQueryFormatter",
				getBoundsForGeographyQueryFormatter,
				geography.getName(),
				geoLevelName,
				mapAreaID);
			
			getBoundsForGeographyResultSet
				= getBoundsForGeographyStatement.executeQuery();
			getBoundsForGeographyResultSet.next();
			
			double yMax = getBoundsForGeographyResultSet.getDouble(1);
			double xMax = getBoundsForGeographyResultSet.getDouble(2);
			double yMin = getBoundsForGeographyResultSet.getDouble(3);			
			double xMin = getBoundsForGeographyResultSet.getDouble(4);
			
			BoundaryRectangle result
				= BoundaryRectangle.newInstance(
					String.valueOf(xMin),
					String.valueOf(yMin),
					String.valueOf(xMax),
					String.valueOf(yMax));
			
			connection.commit();
			
			return result;
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.unableToGetBoundsForGeography",
					geography.getDisplayName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources
			MSSQLQueryUtility.close(findLowestResolutionStatement);						
			MSSQLQueryUtility.close(findLowestResolutionResultSet);
			
			MSSQLQueryUtility.close(getMapAreaIDStatement);
			MSSQLQueryUtility.close(getMapAreaIDResultSet);
			
			MSSQLQueryUtility.close(getBoundsForGeographyStatement);
			MSSQLQueryUtility.close(getBoundsForGeographyResultSet);
			
		}		
	}
	
	/**
	 * This is a convenience method that tries to centralise
	 * much of the validation code that is used in the other methods
	 * From the point of view of the SQLMapDataManager, its caller,
	 * an implementation of a RIFJobSubmissionAPI service, has already
	 * checked whether the parameters are null.  Therefore, the optional
	 * checks for null in this method are merely meant to let the
	 * convenience routine validate parameters if they're relevant.
	 * For example, some
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @param mapAreas the map areas
	 * @throws RIFServiceException the RIF service exception
	 */
	private void validateCommonMethodParameters(
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap,
		final ArrayList<MapArea> mapAreas) 
		throws RIFServiceException {
		
		//Validate parameters
		ValidationPolicy validationPolicy = getValidationPolicy();
		if (geography != null) {
			geography.checkErrors(validationPolicy);
			sqlRIFContextManager.checkGeographyExists(
				connection, 
				geography.getName());			
		}

		if (geoLevelSelect != null) {
			geoLevelSelect.checkErrors(validationPolicy);
			sqlRIFContextManager.checkGeoLevelSelectExists(
				connection, 
				geography.getName(), 
				geoLevelSelect.getName());
			
		}

		if (geoLevelArea != null) {
			geoLevelArea.checkErrors(validationPolicy);	
			sqlRIFContextManager.checkGeoLevelAreaExists(
				connection,
				geography.getName(),
				geoLevelSelect.getName(),
				geoLevelArea.getName());
		}
		
		if (geoLevelToMap != null) {
			geoLevelToMap.checkErrors(validationPolicy);			
			sqlRIFContextManager.checkGeoLevelToMapOrViewValueExists(
				connection, 
				geography.getName(), 
				geoLevelSelect.getName(), 
				geoLevelToMap.getName(),
				true);
		}
	}
	
	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================
}
