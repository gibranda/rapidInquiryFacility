
package rifDataLoaderTool.fileFormats;

import rifDataLoaderTool.businessConceptLayer.RIFDataTypeFactory;
import rifDataLoaderTool.businessConceptLayer.RIFDataType;
import rifGenericLibrary.system.RIFServiceException;
import rifServices.system.RIFServiceError;
import rifServices.system.RIFServiceMessages;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.ArrayList;

/**
 *
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
 * Copyright 2014 Imperial College London, developed by the Small Area
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


public final class RIFDataLoaderSettingsReader {
/*	
	public static void main(String[] arguments) {
		
		File inputFile
			= new File("C://rif_scratch//test_data_settings_modified.xml");
		try {
			RIFDataLoaderSettingsReader reader
				= new RIFDataLoaderSettingsReader();
			reader.readFile(inputFile);
			RIFDataTypeFactory rifDataTypeFactoryA
				= reader.getRIFDataTypeFactory();
			
			ArrayList<RIFDataType> dataTypesA
				= rifDataTypeFactoryA.getRegisteredDataTypes();
			RIFDataTypeFactory dataTypeFactoryB
				= RIFDataTypeFactory.newInstance();
			dataTypeFactoryB.populateFactoryWithBuiltInTypes();
			
			System.out.println("===================================================");
			for (RIFDataType dataTypeA : dataTypesA) {
				dataTypeFactoryB.registerCustomDataType(dataTypeA, true);
			}

			ArrayList<RIFDataType> modifiedTypes
				= dataTypeFactoryB.getRegisteredDataTypes();
			for (RIFDataType modifiedType : modifiedTypes) {
				modifiedType.printFields();				
			}
		}
		catch(Exception exception) {
			exception.printStackTrace(System.out);
		}
		
		
	}
*/	
// ==========================================
// Section Constants
// ==========================================

// ==========================================
// Section Properties
// ==========================================
	private RIFDataLoaderConfigurationHandler rifDataLoaderConfigurationHandler;
	
// ==========================================
// Section Construction
// ==========================================
    /**
     * Instantiates a new RIF job submission xml reader.
     */
	public RIFDataLoaderSettingsReader() {
		rifDataLoaderConfigurationHandler
			= new RIFDataLoaderConfigurationHandler();
    }

// ==========================================
// Section Accessors and Mutators
// ==========================================
    
	/**
 * Read file.
 *
 * @param rifSubmissionFile the rif submission file
 * @throws RIFServiceException the RIF service exception
 */
	public void readFile(
		final File rifSubmissionFile) 
		throws RIFServiceException {

		try {			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(rifSubmissionFile, rifDataLoaderConfigurationHandler);
		}
		catch(Exception exception) {
			exception.printStackTrace(System.out);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"io.error.problemReadingFile",
					rifSubmissionFile.getName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.XML_FILE_PARSING_PROBLEM, 
					errorMessage);
			throw rifServiceException;
		}
	}
	
	public void readFileFromString (
		final String rifStudySubmissionAsXML) 
		throws RIFServiceException {

		try {
			StringReader stringReader = new StringReader(rifStudySubmissionAsXML);
			InputSource inputSource = new InputSource(stringReader);
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inputSource, rifDataLoaderConfigurationHandler);
		}
		catch(Exception exception) {
			exception.printStackTrace(System.out);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"io.error.problemReadingFile",
					"blah");
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.XML_FILE_PARSING_PROBLEM, 
					errorMessage);
			throw rifServiceException;
		}
	}			

	public void readFile(
		final InputStream inputStream) 
		throws RIFServiceException {

		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(
				inputStream, 
				rifDataLoaderConfigurationHandler);
		}
		catch(Exception exception) {
			exception.printStackTrace(System.out);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"io.error.problemReadingFile",
					"blah");
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.XML_FILE_PARSING_PROBLEM, 
					errorMessage);
			throw rifServiceException;
		}
	}			
	
	public RIFDataTypeFactory getRIFDataTypeFactory() {
		return rifDataLoaderConfigurationHandler.getRIFDataTypeFactory();
	}
	

	
// ==========================================
// Section Errors and Validation
// ==========================================

// ==========================================
// Section Interfaces
// ==========================================

// ==========================================
// Section Override
// ==========================================

}