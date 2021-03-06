package rifDataLoaderTool.dataStorageLayer.ms;

import rifDataLoaderTool.system.*;
import rifDataLoaderTool.businessConceptLayer.*;
import rifGenericLibrary.businessConceptLayer.RIFResultTable;
import rifGenericLibrary.businessConceptLayer.User;
import rifGenericLibrary.system.RIFServiceException;
import rifGenericLibrary.util.RIFLogger;
import rifGenericLibrary.util.FieldValidationUtility;

import org.apache.commons.io.FileUtils;

import java.sql.*;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;

/**
 * Main implementation of the {@link rifDataLoaderTool.dataStorageLayer.pg.AbstractMSSQLDataLoaderService}.
 * Almost every method in this class has the following common steps:
 * <ol>
 * <li>
 * safely copy parameter values so that the middleware is not vulnerable to changes the client makes
 * to them while the method is executing.
 * </li>
 * <li>
 * if the rifManager making the request has been black listed because of a previous security incident, return
 * as soon as possible.
 * </li>
 * <li>
 * check for any null parameter values
 * </li>
 * <li>
 * check for any security violations that could occur, either in the rifManager parameter object,
 * or any text field value of any business object. If any security violation is detected, log it and
 * black list the rifManager until further notice.
 * </li>
 * <li>
 * audit the attempt to perform the method operation
 * </li>
 * <li>
 * obtain a pooled database connection
 * <li>
 * <li>
 * call a manager class, which is either contains the needed SQL queries or delegates to vendor-specific
 * SQL code generation classes.
 * </li>
 * <li>
 * reclaim the pooled database connection
 * </li>
 * <li>
 * log any failed attempt
 * </li>
 * </ol>
 *
 * <hr>
 * Copyright 2017 Imperial College London, developed by the Small Area
 * Health Statistics Unit. 
 *
 * <pre> 
 * This file is part of the Rapid Inquiry Facility (RIF) project.
 * RIF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * RIF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RIF.  If not, see <http://www.gnu.org/licenses/>.
 * </pre>
 *
 * <hr>
 * Kevin Garwood
 * @author kgarwood
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

abstract class AbstractMSSQLDataLoaderService 
	implements DataLoaderServiceAPI {
	
	// ==========================================
	// Section Constants
	// ==========================================

	
	// ==========================================
	// Section Properties
	// ==========================================
	//private DataLoaderToolConfiguration dataLoaderToolConfiguration;
	
	private MSSQLConnectionManager sqlConnectionManager;
		
	private MSSQLDatabaseSchemaInformationManager databaseSchemaInformationManager;
	private MSSQLDataSetManager dataSetManager;
	private MSSQLExtractWorkflowManager extractWorkflowManager;	
	private MSSQLChangeAuditManager changeAuditManager;
	private MSSQLCleanWorkflowManager cleanWorkflowManager;	
	private MSSQLCombineWorkflowManager combineWorkflowManager;
	private MSSQLSplitWorkflowManager splitWorkflowManager;
	private MSSQLConvertWorkflowManager convertWorkflowManager;
	private MSSQLOptimiseWorkflowManager optimiseWorkflowManager;
	private MSSQLCheckWorkflowManager checkWorkflowManager;
	private MSSQLReportManager reportManager;
	private MSSQLPublishWorkflowManager publishWorkflowManager;
		
	private ArrayList<DataSetConfiguration> dataSetConfigurations;
		
	// ==========================================
	// Section Construction
	// ==========================================

	public AbstractMSSQLDataLoaderService() {
					
		dataSetConfigurations 
			= new ArrayList<DataSetConfiguration>();
		
		databaseSchemaInformationManager
			= new MSSQLDatabaseSchemaInformationManager();
		
		dataSetManager = new MSSQLDataSetManager();
		extractWorkflowManager
			= new MSSQLExtractWorkflowManager(
				dataSetManager);
		
		changeAuditManager
			= new MSSQLChangeAuditManager();
		
		cleanWorkflowManager
			= new MSSQLCleanWorkflowManager(
				dataSetManager,
				changeAuditManager);
					
		convertWorkflowManager
			= new MSSQLConvertWorkflowManager();
		
		combineWorkflowManager
			= new MSSQLCombineWorkflowManager();
				
		splitWorkflowManager
			= new MSSQLSplitWorkflowManager();
		
		optimiseWorkflowManager
			= new MSSQLOptimiseWorkflowManager();
				
		checkWorkflowManager
			= new MSSQLCheckWorkflowManager(
				optimiseWorkflowManager);
		
		publishWorkflowManager
			= new MSSQLPublishWorkflowManager();
		
		reportManager
			= new MSSQLReportManager();
	}
	
	public void initialiseService(
		final DatabaseConnectionsConfiguration dbParameters) 
		throws RIFServiceException {

		String databaseDriverClassName
			= dbParameters.getDatabaseDriverClassName();
		dbParameters.setDatabaseDriverClassName(databaseDriverClassName);

		sqlConnectionManager
			= new MSSQLConnectionManager(
				dbParameters.getDatabasePasswordFilePath(),
				dbParameters.getDatabaseDriverClassName(),
				dbParameters.getDatabaseURL());
		
		sqlConnectionManager.initialiseConnectionQueue();
	}
	
	public void shutdownService() 
		throws RIFServiceException {

	}
	
	// ==========================================
	// Section Accessors and Mutators
	// ==========================================
	
	public void login(
		final String rifManagerID,
		final String password)
		throws RIFServiceException {
		
		sqlConnectionManager.login(
			rifManagerID, 
			password);		
	}
		
	public void logout(
		final User rifManager) 
		throws RIFServiceException {
		
		//sqlConnectionManager.logout(rifManager);
	}
	
	public void shutdown() throws RIFServiceException {
		sqlConnectionManager.deregisterAllUsers();
	}
	
	protected MSSQLDataSetManager getDataSetManager() {
		return dataSetManager;
	}
	
	protected MSSQLChangeAuditManager getChangeAuditManager() {
		return changeAuditManager;
	}
	
	protected MSSQLConnectionManager getSQLConnectionManger() {
		return sqlConnectionManager;
	}
	
	public ArrayList<DataSetConfiguration> getDataSetConfigurations(
		final User rifManager) 
		throws RIFServiceException {
		
		return dataSetConfigurations;
	}
		
	public ArrayList<DataSetConfiguration> getDataSetConfigurations(
		final User rifManager,
		final String searchPhrase) 
		throws RIFServiceException {
		
		
		return dataSetConfigurations;
	}
	
	public boolean dataSetConfigurationExists(
		final User rifManager,
		final DataSetConfiguration dataSetConfiguration) 
		throws RIFServiceException {

		return false;
	}

	public void deleteDataSetConfigurations(
		final User rifManager,
		final ArrayList<DataSetConfiguration> dataSetConfigurationsToDelete) 
		throws RIFServiceException {

		for (DataSetConfiguration dataSetConfigurationToDelete : dataSetConfigurationsToDelete) {
			dataSetConfigurations.remove(dataSetConfigurationToDelete);			
		}		
	}

	public void registerDataSetConfiguration(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration)
		throws RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

		try {
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"registerDataSetConfiguration",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"registerDataSetConfiguration",
				"dataSetConfiguration",
				dataSetConfiguration);
			
			//sortingOrder can be null, it just means that the
			//order will be ascending lower limit
		
			//Check for security violations
			validateUser(rifManager);
			dataSetConfiguration.checkSecurityViolations();

			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.registerDataSet",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);

			//Assign pooled connection
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(rifManager);
			
			dataSetManager.updateDataSetRegistration(
				connection,
				logFileWriter,
				dataSetConfiguration);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"registerDataSetConfiguration",
				rifServiceException);
		}		
	}
		
	public void setupConfiguration(
		final User _rifManager,
		final File exportDirectory,
		final DataSetConfiguration dataSetConfiguration)
		throws RIFServiceException {

		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		
		//Step 1: Create all the directories
		ensureTemporaryDirectoriesExist(
			rifManager, 
			exportDirectory,
			dataSetConfiguration); 		
		//Step 2: Copy original data file
		
		try {			
	
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);
	
			File sourceFile = new File(dataSetConfiguration.getFilePath());		
			String fileName = sourceFile.getName();
			Path sourcePath = sourceFile.toPath();

			StringBuilder destinationFilePath = new StringBuilder();
			destinationFilePath.append(dataSetExportDirectoryPath);
			destinationFilePath.append(File.separator);
			destinationFilePath.append(DataLoadingResultTheme.ARCHIVE_ORIGINAL_DATA.getSubDirectoryName());
			destinationFilePath.append(File.separator);
			destinationFilePath.append(fileName);
			File destinationFile = new File(destinationFilePath.toString());
			Path destinationPath = destinationFile.toPath();
		
			Files.copy(
				sourcePath, 
				destinationPath, 
				StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException ioException) {
			logException(
				rifManager,
				"registerDataSetConfiguration",
				ioException);			
			String errorMessage
				= RIFDataLoaderToolMessages.getMessage(
					"abstractDataLoaderService.error.unableToCopyOriginalData",
					dataSetConfiguration.getName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFDataLoaderToolError.UNABLE_TO_COPY_ORIGINAL_DATA, 
					errorMessage);
			throw rifServiceException;
		}
		
	}
	
	public void generateShapeFileScripts(final ArrayList<ShapeFile> shapeFiles)
		throws RIFServiceException {
		
	}

	public void addFileToDataSetResults(
		final User _rifManager,
		final Writer logWriter,
		final File exportDirectory,
		final File originalFile,
		final DataLoadingResultTheme rifDataLoadingResultTheme,
		final DataSetConfiguration dataSetConfiguration)
		throws RIFServiceException {
	
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
	
		if (originalFile == null) {
			return;
		}

		if (rifDataLoadingResultTheme == null) {
			return;
		}
		
		String dataSetExportDirectoryPath
			= generateDataSetExportDirectoryPath(
				exportDirectory,
				dataSetConfiguration);		
		
		try {
			
			StringBuilder auditTrailPath = new StringBuilder();
			auditTrailPath.append(dataSetExportDirectoryPath);
			auditTrailPath.append(File.separator);
			auditTrailPath.append(rifDataLoadingResultTheme.getSubDirectoryName());
			auditTrailPath.append(File.separator);
			auditTrailPath.append(originalFile.getName());
			File copyFile = new File(auditTrailPath.toString());
		
			FileUtils.copyFile(originalFile, copyFile);
				
		}
		catch(IOException ioException) {
			String errorMessage
				= RIFDataLoaderToolMessages.getMessage(
					"abstractDataLoaderService.error.unableToCopyWorkflowFile",
					dataSetConfiguration.getName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFDataLoaderToolError.UNABLE_TO_INCLUDE_WORKFLOW_IN_RESULTS, 
					errorMessage);
			throw rifServiceException;
		}
	}
	
	public void extractConfiguration(
		final User _rifManager,
		final Writer logFileWriter,	
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException,
		RIFServiceException {

		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
		RIFSchemaAreaPropertyManager schemaAreaPropertyManager
			= new RIFSchemaAreaPropertyManager();
		WorkflowValidator workFlowValidator 
			= new WorkflowValidator(schemaAreaPropertyManager);
		workFlowValidator.validateLoad(dataSetConfiguration);
		
		
		try {			
			//Check for empty parameters
			checkCommonParameters(
				"loadConfiguration",
				rifManager,
				dataSetConfiguration);
			
						
			//Check for security violations
			validateUser(rifManager);
			dataSetConfiguration.checkSecurityViolations();

			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.loadConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
						rifManager);

			
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);		
						
			extractWorkflowManager.extractConfiguration(
				connection, 
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);

			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"loadConfiguration",
				rifServiceException);
			throw rifServiceException;
		}		
	}
	
	public void addLoadTableData(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration,
		final String[][] tableData)
		throws RIFServiceException,
		RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
	
		try {

			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"addLoadTableData",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"addLoadTableData",
				"dataSetConfiguration",
				dataSetConfiguration);
			fieldValidationUtility.checkNullMethodParameter(
				"addLoadTableData",
				"tableData",
				tableData);
			
			//Check for security violations
			validateUser(rifManager);
			dataSetConfiguration.checkSecurityViolations();
			
			//@TODO: we obviously need something to check the array of Strings
			//for security violations
			//checkTableDataForSecurityViolations(tableData)


			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.addLoadTableData",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			extractWorkflowManager.addExtractTableData(
				connection, 
				logFileWriter,
				dataSetConfiguration, 
				tableData);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"addLoadTableData",
				rifServiceException);
		}		

	}

	public RIFResultTable getLoadTableData(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException,
		RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

				
		RIFResultTable results = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getLoadTableData",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"getLoadTableData",
				"dataSetConfiguration",
				dataSetConfiguration);
			

			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getLoadTableData",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
						
			results
				= extractWorkflowManager.getExtractTableData(
					connection, 
					logFileWriter,
					dataSetConfiguration);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			
			
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getLoadTableData",
				rifServiceException);
		}

		return results;
	}
		
	public void cleanConfiguration(
		final User _rifManager,
		final Writer logFileWriter,
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException,
		RIFServiceException {
				
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
		
		try {
			
			//Check for empty parameters
			checkCommonParameters(
				"cleanConfiguration",
				rifManager,
				dataSetConfiguration);
						
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.cleanConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);		
			
			cleanWorkflowManager.cleanConfiguration(
				connection, 
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);
						
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"cleanConfiguration",
				rifServiceException);
			throw rifServiceException;
		}		

	}
	
	public RIFResultTable getCleanedTableData(
		final User _rifManager,			
		final FileWriter logFileWriter,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException,
		RIFServiceException {

		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
				
		RIFResultTable result = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getCleanedTableData",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"getCleanedTableData",
				"dataSetConfiguration",
				dataSetConfiguration);
				
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getCleanedTableData",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
								
			result
				= cleanWorkflowManager.getCleanedTableData(
					connection, 
					logFileWriter,
					dataSetConfiguration);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getCleanedTableData",
				rifServiceException);
		}		
	
		return result;

	}

	public void combineConfiguration(
		final User _rifManager,
		final Writer logFileWriter,		
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException,
		RIFServiceException {
	
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
	
		try {
			
			//Check for empty parameters
			checkCommonParameters(
				"combineConfiguration",
				rifManager,
				dataSetConfiguration);
					
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.combineConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
		
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
			
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);		
			
			combineWorkflowManager.combineConfiguration(
				connection, 
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);	
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"combineConfiguration",
				rifServiceException);
			throw rifServiceException;
		}
	}
	
	public void splitConfiguration(
		final User _rifManager,
		final Writer logFileWriter,
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException,
		RIFServiceException {
	
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
	
		try {
			
			//Check for empty parameters
			checkCommonParameters(
				"splitConfiguration",
				rifManager,
				dataSetConfiguration);
					
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.splitConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
		
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
			
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);		
			
			splitWorkflowManager.splitConfiguration(
				connection, 
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);	
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"splitConfiguration",
				rifServiceException);
			throw rifServiceException;
		}
	}
	
	public void convertConfiguration(
		final User _rifManager,
		final Writer logFileWriter,
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration)
		throws RIFServiceException,
		RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
				
		try {
			
			//Check for empty parameters
			checkCommonParameters(
				"convertConfiguration",
				rifManager,
				dataSetConfiguration);
						
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.convertConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
			
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);
			convertWorkflowManager.convertConfiguration(
				connection, 
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"convertConfiguration",
				rifServiceException);
			throw rifServiceException;
		}		
	}

	public void optimiseConfiguration(
		final User _rifManager,
		final Writer logFileWriter,
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
		
		try {
			//Check for empty parameters
			checkCommonParameters(
				"optimiseConfiguration",
				rifManager,
				dataSetConfiguration);

			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage(
					"logging.optimiseConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);		

			optimiseWorkflowManager.optimiseConfiguration(
				connection, 
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"optimiseConfiguration",
				rifServiceException);
			throw rifServiceException;
		}	
	}


	public void checkConfiguration(
		final User _rifManager,
		final Writer logFileWriter,
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
		
		try {
			//Check for empty parameters
			checkCommonParameters(
				"checkConfiguration",
				rifManager,
				dataSetConfiguration);

			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.checkConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
		
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);			
			checkWorkflowManager.checkConfiguration(
				connection, 
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"checkConfiguration",
				rifServiceException);
			throw rifServiceException;
		}	
	}
	

	public void publishConfiguration(
		final User _rifManager,
		final Writer logFileWriter,
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
		
		try {
			//Check for empty parameters
			checkCommonParameters(
				"publishConfiguration",
				rifManager,
				dataSetConfiguration);

			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.publishConfiguration",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			
			String dataSetExportDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);			
			
			publishWorkflowManager.publishConfiguration(
				connection,
				logFileWriter,
				dataSetExportDirectoryPath,
				dataSetConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"publishConfiguration",
				rifServiceException);
			throw rifServiceException;
		}
		
		publishWorkflowManager.createZipArchiveFileAndCleanupTemporaryFiles(
			logFileWriter,
			exportDirectory.getAbsoluteFile(),
			dataSetConfiguration);		
		
	}

	public void generateResultReports(
		final User _rifManager,
		final Writer logFileWriter,		
		final File exportDirectory,
		final DataSetConfiguration _dataSetConfiguration) 
		throws RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);
		
		try {
			//Check for empty parameters
			checkCommonParameters(
				"generateResultReports",
				rifManager,
				dataSetConfiguration);

			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.generateResultReports",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			reportManager.writeResults(
				connection, 
				logFileWriter, 
				exportDirectory, 
				dataSetConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"generateResultReports",
				rifServiceException);
			throw rifServiceException;
		}	
	}
	
	
	
	
	public Integer getCleaningTotalBlankValues(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration)
		throws RIFServiceException,
		RIFServiceException {

		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

		Integer result = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getCleaningTotalBlankValues",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"getCleaningTotalBlankValues",
				"dataSetConfiguration",
				dataSetConfiguration);
			
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getCleaningTotalBlankValues",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
			result
				= cleanWorkflowManager.getCleaningTotalBlankValues(
					connection, 
					logFileWriter,
					dataSetConfiguration);
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			return result;
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getCleaningTotalBlankValues",
				rifServiceException);
		}		

		return result;
	}
		
	public Integer getCleaningTotalChangedValues(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration)
		throws RIFServiceException,
		RIFServiceException {
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

		Integer result = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getCleaningTotalChangedValues",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"getCleaningTotalChangedValues",
				"dataSetConfiguration",
				dataSetConfiguration);
			
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getCleaningTotalChangedValues",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
			result
				= cleanWorkflowManager.getCleaningTotalChangedValues(
					connection, 
					logFileWriter,
					dataSetConfiguration);
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			return result;
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getCleaningTotalBlankValues",
				rifServiceException);
		}		

		return result;
	}
		
	public Integer getCleaningTotalErrorValues(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration)
		throws RIFServiceException,
		RIFServiceException {

		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

		Integer result = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getCleaningTotalErrorValues",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"getCleaningTotalErrorValues",
				"dataSetConfiguration",
				dataSetConfiguration);
			
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getCleaningTotalErrorValues",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
			result
				= cleanWorkflowManager.getCleaningTotalErrorValues(
					connection, 
					logFileWriter,
					dataSetConfiguration);
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			return result;
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getCleaningTotalErrorValues",
				rifServiceException);
		}		

		return result;
	}
	
	public Boolean cleaningDetectedBlankValue(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration,
		final int rowNumber,
		final String targetBaseFieldName)
		throws RIFServiceException,
		RIFServiceException {

		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

		Boolean result = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedBlankValue",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedBlankValue",
				"dataSetConfiguration",
				dataSetConfiguration);

			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedBlankValue",
				"targetBaseFieldName",
				targetBaseFieldName);
					
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage(
					"logging.cleaningDetectedBlankValue",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName(),
					targetBaseFieldName, 
					String.valueOf(rowNumber));
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			result
				= cleanWorkflowManager.cleaningDetectedBlankValue(
					connection, 
					logFileWriter,
					dataSetConfiguration,
					rowNumber,
					targetBaseFieldName);
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			return result;
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"cleaningDetectedBlankValue",
				rifServiceException);
		}		

		return result;		
	}
	
	public Boolean cleaningDetectedChangedValue(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration,
		final int rowNumber,
		final String targetBaseFieldName)
		throws RIFServiceException,
		RIFServiceException {

		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

		Boolean result = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedChangedValue",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedChangedValue",
				"dataSetConfiguration",
				dataSetConfiguration);

			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedChangedValue",
				"targetBaseFieldName",
				targetBaseFieldName);
			
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.cleaningDetectedChangedValue",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetConfiguration.getDisplayName(),
					targetBaseFieldName);
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			result
				= cleanWorkflowManager.cleaningDetectedChangedValue(
					connection, 
					logFileWriter,
					dataSetConfiguration,
					rowNumber,
					targetBaseFieldName);
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			return result;
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"cleaningDetectedChangedValue",
				rifServiceException);
		}		

		return result;		

	}
	
	public Boolean cleaningDetectedErrorValue(
		final User _rifManager,
		final Writer logFileWriter,
		final DataSetConfiguration _dataSetConfiguration,
		final int rowNumber,
		final String targetBaseFieldName)
		throws RIFServiceException,
		RIFServiceException {
				
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return null;
		}
		DataSetConfiguration dataSetConfiguration
			= DataSetConfiguration.createCopy(_dataSetConfiguration);

		Boolean result = null;
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedErrorValue",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedErrorValue",
				"dataSetConfiguration",
				dataSetConfiguration);
			
			fieldValidationUtility.checkNullMethodParameter(
				"cleaningDetectedErrorValue",
				"targetBaseFieldName",
				targetBaseFieldName);
				
			
			//Audit attempt to do operation
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);

			result
				= cleanWorkflowManager.cleaningDetectedErrorValue(
					connection, 
					logFileWriter,
					dataSetConfiguration,
					rowNumber,
					targetBaseFieldName);
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
			return result;
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"cleaningDetectedErrorValue",
				rifServiceException);
		}		

		return result;
	}
	
	public String[][] getVarianceInFieldData(
		final User _rifManager,
		final DataSetFieldConfiguration _dataSetFieldConfiguration)
		throws RIFServiceException {
		
		
		String[][] results = new String[0][0];
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return results;
		}
		DataSetFieldConfiguration dataSetFieldConfiguration
			= DataSetFieldConfiguration.createCopy(_dataSetFieldConfiguration);
				
		try {
			
			//Check for empty parameters
			checkCommonParameters(
				"getVarianceInFieldData",
				rifManager,
				dataSetFieldConfiguration);
						
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getVarianceInFieldData",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					dataSetFieldConfiguration.getDisplayName());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
			
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
			
			results 
				= databaseSchemaInformationManager.getVarianceInFieldData(
				connection, 
				dataSetFieldConfiguration);
					
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"convertConfiguration",
				rifServiceException);
			throw rifServiceException;
		}		

				
		return results;
	}
	
	
	
	protected void ensureTemporaryDirectoriesExist(
		final User _rifManager,
		final File exportDirectory,
		final DataSetConfiguration dataSetConfiguration) 
		throws RIFServiceException {
				
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return;
		}
		
		String coreDataSetName = dataSetConfiguration.getName();
		try {			
			String mainScratchDirectoryPath
				= generateDataSetExportDirectoryPath(
					exportDirectory,
					dataSetConfiguration);
			
			File temporaryDirectory = new File(mainScratchDirectoryPath.toString());
			if (temporaryDirectory.exists() == false) {
				FileUtils.deleteDirectory(temporaryDirectory);
				Files.createDirectory(temporaryDirectory.toPath());
			}
			
		
			createSubDirectory(mainScratchDirectoryPath, DataLoadingResultTheme.ARCHIVE_ORIGINAL_DATA);
			createSubDirectory(mainScratchDirectoryPath, DataLoadingResultTheme.ARCHIVE_AUDIT_TRAIL);
			createSubDirectory(mainScratchDirectoryPath, DataLoadingResultTheme.ARCHIVE_RESULTS);
			createSubDirectory(mainScratchDirectoryPath, DataLoadingResultTheme.ARCHIVE_STAGES);
			createSubDirectory(mainScratchDirectoryPath, DataLoadingResultTheme.ARCHIVE_OTHER);
		}
		catch(IOException ioException) {
			logException(
				rifManager, 
				"ensureTemporaryDirectoriesExist", 
				ioException);
			String errorMessage
				= RIFDataLoaderToolMessages.getMessage(
					"abstractDataLoaderStepManager.error.unableToCreateTemporaryDirectories",
					coreDataSetName);
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFDataLoaderToolError.UNABLE_TO_CREATE_TEMPORARY_DIRECTORIES, 
					errorMessage);
			throw rifServiceException;
		}
		
	}
		
	private void createSubDirectory(
		final String mainScratchDirectoryPath,
		final DataLoadingResultTheme resultTheme) 
		throws IOException {
		
		StringBuilder subDirectoryPath = new StringBuilder();
		subDirectoryPath.append(mainScratchDirectoryPath);
		subDirectoryPath.append(File.separator);
		subDirectoryPath.append(resultTheme.getSubDirectoryName());
		
		File subDirectory = new File(subDirectoryPath.toString());
		if (subDirectory.exists() == false) {
			Files.createDirectory(subDirectory.toPath());
		}
		
	}
	
	private String generateDataSetExportDirectoryPath(
		final File exportDirectory,
		final DataSetConfiguration dataSetConfiguration) {
		
		String coreDataSetName = dataSetConfiguration.getName();
		StringBuilder dataSetExportDirectoryPath = new StringBuilder();
		dataSetExportDirectoryPath.append(exportDirectory.getAbsolutePath());
		dataSetExportDirectoryPath.append(File.separator);
		dataSetExportDirectoryPath.append(coreDataSetName);
		
		return dataSetExportDirectoryPath.toString();		
	}
	
	
	public String[] getCleaningFunctionNames(final User _rifManager) 
			throws RIFServiceException {

		
		String[] result = new String[0];
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return result;
		}
				
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getCleaningFunctionNames",
				"rifManager",
				rifManager);
			rifManager.checkSecurityViolations();
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getCleaningFunctionNames",
					rifManager.getUserID(),
					rifManager.getIPAddress());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
	
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
	
			result
				= databaseSchemaInformationManager.getValidationFunctionNames(connection);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getCleaningFunctionNames",
				rifServiceException);
			throw rifServiceException;
		}		

		return result;		
	}

	public String getDescriptionForCleaningFunction(
		final User _rifManager,
		final String cleaningFunctionName) 
		throws RIFServiceException {

		
		String result = "";
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return result;
		}
				
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getDescriptionForCleaningFunction",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"getDescriptionForCleaningFunction",
				"cleaningFunctionName",
				cleaningFunctionName);
			fieldValidationUtility.checkMaliciousMethodParameter(
				"getDescriptionForCleaningFunction", 
				"cleaningFunctionName", 
				cleaningFunctionName);			
			rifManager.checkSecurityViolations();
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getDescriptionForCleaningFunction",
					rifManager.getUserID(),
					rifManager.getIPAddress());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
	
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(rifManager);
	
			result
				= databaseSchemaInformationManager.getFunctionDescription(
					connection, 
					cleaningFunctionName);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getDescriptionForCleaningFunction",
				rifServiceException);
			throw rifServiceException;
		}		

		return result;		
	}
	
	public String[] getValidationFunctionNames(
		final User _rifManager) 
		throws RIFServiceException {

		
		String[] result = new String[0];
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return result;
		}
				
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getDescriptionForValidationFunction",
				"rifManager",
				rifManager);
			rifManager.checkSecurityViolations();
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getValidationFunctionNames",
					rifManager.getUserID(),
					rifManager.getIPAddress());
			rifLogger.info(
				getClass(),
				auditTrailMessage);
	
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
	
			result
				= databaseSchemaInformationManager.getValidationFunctionNames(
					connection);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getValidationFunctionNames",
				rifServiceException);
			throw rifServiceException;
		}		

		return result;		
	}

	
	public String getDescriptionForValidationFunction(
		final User _rifManager,
		final String validationFunctionName) 
		throws RIFServiceException {
		
		String result = "";
		
		//Defensively copy parameters and guard against blocked rifManagers
		User rifManager = User.createCopy(_rifManager);
		if (sqlConnectionManager.isUserBlocked(rifManager) == true) {
			return result;
		}
				
		try {
			
			//Check for empty parameters
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getDescriptionForValidationFunction",
				"rifManager",
				rifManager);
			fieldValidationUtility.checkNullMethodParameter(
				"getDescriptionForValidationFunction",
				"validationFunctionName",
				validationFunctionName);

			rifManager.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(
				"getDescriptionForValidationFunction", 
				"rifManager", 
				validationFunctionName);			
			
			//Audit attempt to do operation
			RIFLogger rifLogger = RIFLogger.getLogger();				
			String auditTrailMessage
				= RIFDataLoaderToolMessages.getMessage("logging.getDescriptionForValidationFunction",
					rifManager.getUserID(),
					rifManager.getIPAddress(),
					validationFunctionName);
			rifLogger.info(
				getClass(),
				auditTrailMessage);
	
			Connection connection 
				= sqlConnectionManager.assignPooledWriteConnection(
					rifManager);
	
			result
				= databaseSchemaInformationManager.getFunctionDescription(
					connection, 
					validationFunctionName);
			
			sqlConnectionManager.reclaimPooledWriteConnection(
				rifManager, 
				connection);
		}
		catch(RIFServiceException rifServiceException) {
			//Audit failure of operation
			logException(
				rifManager,
				"getDescriptionForValidationFunction",
				rifServiceException);
			throw rifServiceException;
		}		

		return result;		
	}

	// ==========================================
	// Section Errors and Validation
	// ==========================================

	private void checkEmptyUser(
		final User user)
		throws RIFServiceException {
		
		
	}
	
	private void checkCommonParameters(
		final String methodName,
		final User rifManager,
		final DataSetConfiguration dataSetConfiguration) 
		throws RIFServiceException {
	
		//Check for empty parameters
		FieldValidationUtility fieldValidationUtility
			= new FieldValidationUtility();
		fieldValidationUtility.checkNullMethodParameter(
			methodName,
			"rifManager",
			rifManager);
		fieldValidationUtility.checkNullMethodParameter(
			methodName,
			"dataSetConfiguration",
			dataSetConfiguration);
		
		rifManager.checkErrors();

		//validateUser(rifManager);
		dataSetConfiguration.checkSecurityViolations();
	}
	

	private void checkCommonParameters(
		final String methodName,
		final User rifManager,
		final DataSetFieldConfiguration dataSetFieldConfiguration) 
		throws RIFServiceException {
	
		//Check for empty parameters
		FieldValidationUtility fieldValidationUtility
			= new FieldValidationUtility();
		fieldValidationUtility.checkNullMethodParameter(
			methodName,
			"rifManager",
			rifManager);
		fieldValidationUtility.checkNullMethodParameter(
			methodName,
			"dataSetConfiguration",
			dataSetFieldConfiguration);
		
		rifManager.checkErrors();

		//validateUser(rifManager);
		dataSetFieldConfiguration.checkSecurityViolations();
	}
	
	//Audit failure of operation
	public void logException(
		User rifManager,
		String methodName,
		Exception exception) {
		
		
		if (exception instanceof RIFServiceException) {
			RIFServiceException rifServiceException
				= (RIFServiceException) exception;
			rifServiceException.printErrors();	
		}
		else {
			exception.printStackTrace(System.out);
		}
	}
	
	public void validateUser(
		final User rifManager) 
		throws RIFServiceException {

		//@TODO: harmonise this with the underlying AbstractRIFService call
	}
	
	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================

}


