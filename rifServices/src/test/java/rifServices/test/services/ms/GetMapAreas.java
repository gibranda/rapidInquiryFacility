package rifServices.test.services.ms;

import rifGenericLibrary.businessConceptLayer.User;
import rifGenericLibrary.system.RIFServiceException;
import rifGenericLibrary.system.RIFGenericLibraryError;
import rifServices.businessConceptLayer.GeoLevelArea;
import rifServices.businessConceptLayer.GeoLevelSelect;
import rifServices.businessConceptLayer.GeoLevelToMap;
import rifServices.businessConceptLayer.Geography;
import rifServices.businessConceptLayer.MapArea;
import rifServices.system.RIFServiceError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;


/**
 *
 * <hr>
 * The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
 * that rapidly addresses epidemiological and public health questions using 
 * routinely collected health and population data and generates standardised 
 * rates and relative risks for any given health outcome, for specified age 
 * and year ranges, for any given geographical area.
 *
 * Copyright 2017 Imperial College London, developed by the Small Area
 * Health Statistics Unit. The work of the Small Area Health Statistics Unit 
 * is funded by the Public Health England as part of the MRC-PHE Centre for 
 * Environment and Health. Funding for this project has also been received 
 * from the United States Centers for Disease Control and Prevention.  
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

public final class GetMapAreas 
	extends AbstractRIFServiceTestCase {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================

	// ==========================================
	// Section Construction
	// ==========================================

	public GetMapAreas() {

	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	// ==========================================
	// Section Errors and Validation
	// ==========================================	
	
	@Test
	public void getMapAreas_COMMON1() {
		
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			validGeoLevelArea.setName("Tirado");
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			ArrayList<MapArea> mapAreas
				= rifStudySubmissionService.getMapAreas(
					validUser, 
					validGeography,
					validGeoLevelSelect,
					validGeoLevelArea,
					validGeoLevelToMap);
			assertEquals(5, mapAreas.size());
		}
		catch(RIFServiceException rifServiceException) {
			fail();
		}

	}

	@Test
	public void getMapAreas_COMMON2() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			validGeoLevelArea.setName("Hambly");
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			ArrayList<MapArea> mapAreas
				= rifStudySubmissionService.getMapAreas(
					validUser, 
					validGeography,
					validGeoLevelSelect,
					validGeoLevelArea,
					validGeoLevelToMap);
			
			assertEquals(57, mapAreas.size());
		}
		catch(RIFServiceException rifServiceException) {
			fail();
		}
	}	
	
	@Test
	/**
	 * get a query where there are too many results
	 */
	public void getMapAreas_TOO_MANY_RESULTS1() {
		
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			validGeoLevelArea.setName("Clarke");
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, RIFServiceError.THRESHOLD_MAP_AREAS_PER_DISPLAY_EXCEEDED, 
				1);
			this.printErrors("TRTRT", rifServiceException);
		}
	}	

	
	
	@Test
	public void getMapAreas_NULL1() {
	
		try {
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				null, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.EMPTY_API_METHOD_PARAMETER,
				1);
		}
	}	
	
	@Test
	public void getMapAreas_EMPTY1() {
	
		try {
			User emptyUser = cloneEmptyUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				emptyUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFGenericLibraryError.INVALID_USER,
				1);
		}
	}	

	@Test
	public void getMapAreas_NULL2() {
	
		try {
			User validUser = cloneValidUser();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				null,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.EMPTY_API_METHOD_PARAMETER,
				1);
		}
	}	
	
	
	@Test
	public void getMapAreas_EMPTY2() {
	
		try {
			User validUser = cloneValidUser();
			Geography emptyGeography = cloneEmptyGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				emptyGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_GEOGRAPHY,
				1);
		}
	}	

	@Test
	public void getMapAreas_NULL3() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				null,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.EMPTY_API_METHOD_PARAMETER,
				1);
		}
	}
	
	
	@Test
	public void getMapAreas_EMPTY3() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect emptyGeoLevelSelect = cloneEmptyGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				emptyGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_GEOLEVEL_SELECT,
				1);
		}
	}

	@Test
	public void getMapAreas_NULL4() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				null,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.EMPTY_API_METHOD_PARAMETER,
				1);
		}
	}	
	
	@Test
	public void getMapAreas_EMPTY4() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea emptyGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				emptyGeoLevelArea,
				validGeoLevelToMap);
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_GEOLEVEL_AREA,
				1);
		}
	}	

	@Test
	public void getMapAreas_NULL5() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				null);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.EMPTY_API_METHOD_PARAMETER,
				1);
		}
	}	
	
	@Test
	public void getMapAreas_EMPTY5() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap emptyGeoLevelToMap = cloneEmptyGeoLevelToMap();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				emptyGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_GEOLEVEL_TO_MAP,
				1);
		}
	}	

	@Test
	public void getMapAreas_NONEXISTENT1() {
	
		try {
			User nonExistentUser = cloneNonExistentUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				nonExistentUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFGenericLibraryError.SECURITY_VIOLATION,
				1);
		}
	}	

	
	@Test
	public void getMapAreas_NONEXISTENT2() {
	
		try {
			User validUser = cloneValidUser();
			Geography nonExistentGeography = cloneNonExistentGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				nonExistentGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.NON_EXISTENT_GEOGRAPHY,
				1);
		}
	}	
	
	@Test
	public void getMapAreas_NONEXISTENT3() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect nonExistentGeoLevelSelect = cloneNonExistentGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				nonExistentGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.NON_EXISTENT_GEOLEVEL_SELECT_VALUE,
				1);
		}
	}

	
	@Test
	public void getMapAreas_NONEXISTENT4() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea nonExistentGeoLevelArea = cloneNonExistentGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				nonExistentGeoLevelArea,
				validGeoLevelToMap);
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.NON_EXISTENT_GEOLEVEL_AREA_VALUE,
				1);
		}
	}	

	
	@Test
	public void getMapAreas_NONEXISTENT5() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap nonExistentGeoLevelToMap = cloneNonExistentGeoLevelToMap();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				nonExistentGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.NON_EXISTENT_GEOLEVEL_TO_MAP_VALUE,
				1);
		}
	}	

	
	@Test
	public void getMapAreas_MALICIOUS1() {
	
		try {
			User maliciousUser = cloneMaliciousUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				maliciousUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFGenericLibraryError.SECURITY_VIOLATION,
				1);
		}
	}	

	@Test
	public void getMapAreas_MALICIOUS2() {
	
		try {
			User validUser = cloneValidUser();
			Geography maliciousGeography = cloneMaliciousGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				maliciousGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFGenericLibraryError.SECURITY_VIOLATION,
				1);
		}
	}	
	
	@Test
	public void getMapAreas_MALICIOUS3() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect maliciousGeoLevelSelect = cloneMaliciousGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();
			
			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				maliciousGeoLevelSelect,
				validGeoLevelArea,
				validGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFGenericLibraryError.SECURITY_VIOLATION,
				1);
		}
	}

	@Test
	public void getMapAreas_MALICIOUS4() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea maliciousGeoLevelArea = cloneMaliciousGeoLevelArea();
			GeoLevelToMap validGeoLevelToMap = cloneValidGeoLevelToMap();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				maliciousGeoLevelArea,
				validGeoLevelToMap);
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFGenericLibraryError.SECURITY_VIOLATION,
				1);
		}
	}	

	
	@Test
	public void getMapAreas_MALICIOUS5() {
	
		try {
			User validUser = cloneValidUser();
			Geography validGeography = cloneValidGeography();
			GeoLevelSelect validGeoLevelSelect = cloneValidGeoLevelSelect();
			GeoLevelArea validGeoLevelArea = cloneValidGeoLevelArea();
			GeoLevelToMap maliciousGeoLevelToMap = cloneMaliciousGeoLevelToMap();

			rifStudySubmissionService.getMapAreas(
				validUser, 
				validGeography,
				validGeoLevelSelect,
				validGeoLevelArea,
				maliciousGeoLevelToMap);
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFGenericLibraryError.SECURITY_VIOLATION,
				1);
		}
	}	

	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================
}
