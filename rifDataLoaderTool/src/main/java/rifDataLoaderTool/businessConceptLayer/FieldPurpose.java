package rifDataLoaderTool.businessConceptLayer;

import rifDataLoaderTool.system.RIFDataLoaderToolMessages;
import java.text.Collator;

/**
 *
 *
 * <hr>
 * Copyright 2015 Imperial College London, developed by the Small Area
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

public enum FieldPurpose {
	COVARIATE(
		"covariate", 
		"fieldPurpose.covariate.label"), 
	GEOGRAPHICAL_RESOLUTION(
		"geographical_resolution", 
		"fieldPurpose.geograpicalResolution.label"), 
	HEALTH_CODE(
		"health_code",
		"fieldPurpose.healthCode.label"),
	OTHER(
		"other",
		"fieldPurpose.other.label");
	
	private String code;
	private String propertyName;
	
	private FieldPurpose(
		final String code, 
		final String propertyName) {
		
		this.code = code;
		this.propertyName = propertyName;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		String name 
			= RIFDataLoaderToolMessages.getMessage(propertyName);
		return name;
	}
	
	public static FieldPurpose getValueFromCode(
		final String code) {
		
		Collator collator = RIFDataLoaderToolMessages.getCollator();
		if (collator.equals(code, COVARIATE.getCode())) {
			return COVARIATE;
		}
		else if (collator.equals(code, GEOGRAPHICAL_RESOLUTION.getCode())) {
			return GEOGRAPHICAL_RESOLUTION;
		}
		else if (collator.equals(code, HEALTH_CODE.getCode())) {
			return HEALTH_CODE;
		}
		else if (collator.equals(code, OTHER.getCode())) {
			return OTHER;
		}
		else {
			assert false;
			return null;
		}		
	}
	
	public static String[] getNames() {
		String[] results = new String[4];
		results[0] = COVARIATE.getName();
		results[1] = GEOGRAPHICAL_RESOLUTION.getName();
		results[2] = HEALTH_CODE.getName();
		results[3] = OTHER.getName();
		
		return results;
	}
	
	
	public static FieldPurpose getFieldPurposeFromName(final String name) {
		Collator collator = RIFDataLoaderToolMessages.getCollator();
		if (collator.equals(COVARIATE.getName(), name)) {
			return COVARIATE;
		}
		else if (collator.equals(GEOGRAPHICAL_RESOLUTION.getName(), name)) {
			return GEOGRAPHICAL_RESOLUTION;
		}
		else if (collator.equals(GEOGRAPHICAL_RESOLUTION.getName(), name)) {
			return GEOGRAPHICAL_RESOLUTION;
		}
		else if (collator.equals(HEALTH_CODE.getName(), name)) {
			return HEALTH_CODE;
		}
		else if (collator.equals(OTHER.getName(), name)) {
			return OTHER;
		}		
		else {
			assert false;
			return null;
		}

	}
	
	
}

