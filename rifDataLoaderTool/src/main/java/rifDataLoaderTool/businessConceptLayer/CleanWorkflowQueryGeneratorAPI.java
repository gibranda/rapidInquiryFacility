package rifDataLoaderTool.businessConceptLayer;



/**
 * API of code generation classes that can support database calls related to cleaning
 * data.  Code generator classes will be developed for both Postgresql and Microsoft
 * SQL Server databases.
 *
 * <hr>
 * Copyright 2016 Imperial College London, developed by the Small Area
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

public interface CleanWorkflowQueryGeneratorAPI {

	public String generateSearchReplaceTableQuery(
		final DataSetConfiguration dataSetConfiguration);	
	public String generateDropSearchReplaceTableQuery(
		final DataSetConfiguration dataSetConfiguration);

	public String generateValidationTableQuery(
		final DataSetConfiguration dataSetConfiguration);
	public String generateDropValidationTableQuery(
		final DataSetConfiguration dataSetConfiguration);
	
	public String generateCastingTableQuery(
		final DataSetConfiguration dataSetConfiguration);
	public String generateDropCastingTableQuery(
		final DataSetConfiguration dataSetConfiguration);

	public String generateDeleteAuditsQuery(
		final DataSetConfiguration dataSetConfiguration);
	public String generateAuditChangesQuery(
		final DataSetConfiguration dataSetConfiguration);
	public String generateAuditErrorsQuery(
		final DataSetConfiguration dataSetConfiguration);
	public String generateAuditBlanksQuery(
		final DataSetConfiguration dataSetConfiguration);	
}


