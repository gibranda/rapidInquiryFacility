package rifDataLoaderTool.businessConceptLayer;


import rifDataLoaderTool.businessConceptLayer.rifDataTypes.*;
import rifDataLoaderTool.system.RIFDataLoaderToolError;
import rifDataLoaderTool.system.RIFDataLoaderToolMessages;
import rifGenericLibrary.system.RIFServiceException;
import rifServices.util.FieldValidationUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.text.Collator;
/**
 * <p>
 * Describes the properties of a function that is used to convert a cleaned
 * data field to a field that is expected in the RIF schema.  The class was originally
 * developed to combine the values of a year and age field in a cleaned table so
 * that a new column <code>age_sex_group</code>, would appear in the converted table.  
 * </p>
 * 
 * <p>
 * When RIF managers are trying to import new denominator and numerator tables, their
 * original data sets may have separate columns for <code>sex</code> and <code>age</code>.
 * However, the RIF schema requires that these fields are combined into a single 
 * <code>age_sex_group</code>.
 * </p>
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

public class RIFConversionFunction {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	private String code;	
	private String schemaName;
	private String functionName;
	private ArrayList<String> formalParameterNames;
	
	//eg: func(cleanFieldA, cleanFieldB) AS convertFieldName
	private String convertFieldName;
	private HashMap<String, AbstractRIFDataType> dataTypeFromFormalParameterName;
	
	private ArrayList<DataSetFieldConfiguration> actualParameterValues;
	
	private boolean supportsOneToOneConversion;
	// ==========================================
	// Section Construction
	// ==========================================

	private RIFConversionFunction() {
		formalParameterNames = new ArrayList<String>();
		dataTypeFromFormalParameterName = new HashMap<String, AbstractRIFDataType>();
		convertFieldName = "";
		actualParameterValues = new ArrayList<DataSetFieldConfiguration>();
		supportsOneToOneConversion = true;
	}
	
	public static RIFConversionFunction newInstance() {
		RIFConversionFunction rifConversionFunction
			= new RIFConversionFunction();

		return rifConversionFunction;
	}
	
	public static RIFConversionFunction createCopy(
		final RIFConversionFunction originalFunction) {
		
		RIFConversionFunction cloneFunction
			= new RIFConversionFunction();
		cloneFunction.setCode(originalFunction.getCode());
		cloneFunction.setSchemaName(originalFunction.getSchemaName());
		cloneFunction.setFunctionName(originalFunction.getFunctionName());
		cloneFunction.setActualParameterValues(originalFunction.getActualParameterValues());
		cloneFunction.setConvertFieldName(originalFunction.getConvertFieldName());
		cloneFunction.setSupportsOneToOneConversion(originalFunction.supportsOneToOneConversion());
		ArrayList<DataSetFieldConfiguration> originalActualParameterValues
			= originalFunction.getActualParameterValues();
		ArrayList<DataSetFieldConfiguration> cloneActualParameterValues
			= DataSetFieldConfiguration.createCopy(originalActualParameterValues);
		cloneFunction.setActualParameterValues(cloneActualParameterValues);

		RIFDataTypeFactory rifDataTypeFactory = RIFDataTypeFactory.newInstance();
		HashMap<String, AbstractRIFDataType> originalParamatersFromNames
			= originalFunction.getDataTypesFromFormalParmaterNames();
		HashMap<String, AbstractRIFDataType> cloneParamatersFromNames
			= new HashMap<String, AbstractRIFDataType>();		
		ArrayList<String> originalKeys = new ArrayList<String>();
		originalKeys.addAll(originalParamatersFromNames.keySet());
		for (String originalKey : originalKeys) {
			AbstractRIFDataType originalDataType
				= originalParamatersFromNames.get(originalKey);
			AbstractRIFDataType cloneDataType
				= rifDataTypeFactory.getDataTypeFromCode(originalDataType.getIdentifier());
			cloneParamatersFromNames.put(originalKey, cloneDataType);
		}
		cloneFunction.setDataTypesFromFormalParameterNames(cloneParamatersFromNames);
		
		return cloneFunction;
	}
	
	public void defineFormalParameter(
		final String parameterName,
		final AbstractRIFDataType rifDataType) {
		
		formalParameterNames.add(parameterName);
		dataTypeFromFormalParameterName.put(
			parameterName, 
			rifDataType);		
	}

	public void addActualParameter(
		final DataSetFieldConfiguration dataSetFieldConfiguration) {
		
		actualParameterValues.add(dataSetFieldConfiguration);		
	}
	
	public String generateQueryFragment() {
		StringBuilder queryFragment = new StringBuilder();
		
		if (schemaName != null) {
			queryFragment.append(schemaName);
			queryFragment.append(".");
		}
		queryFragment.append(functionName);
		queryFragment.append("(");
		
		int numberOfFormalParameters = formalParameterNames.size();
		for (int i = 0; i < numberOfFormalParameters; i++) {
			if (i != 0) {
				queryFragment.append(",");
			}

			String currentFormalParameter
				= formalParameterNames.get(i);
			String cleanFieldName
				= getCleanFieldNameFromActualParameterName(currentFormalParameter);
			queryFragment.append(cleanFieldName);
		}
				
		queryFragment.append(") AS ");
		queryFragment.append(convertFieldName);
		
		
		return queryFragment.toString();
	}
	
	private String getCleanFieldNameFromActualParameterName(
		final String actualParameterName) {
			
		Collator collator = RIFDataLoaderToolMessages.getCollator();
		for (DataSetFieldConfiguration actualParameterValue : actualParameterValues) {
			String convertFieldName
				= actualParameterValue.getConvertFieldName();
			if (collator.equals(convertFieldName, actualParameterName)) {
				return actualParameterValue.getCleanFieldName();
			}
		}
		
		//we should have already called checkErrors() before generating query fragment
		//is called
		assert false;
		
		return null;
	}
	
	public void checkErrors() 
		throws RIFServiceException {
		
		ArrayList<String> errorMessages = new ArrayList<String>();
		
		FieldValidationUtility fieldValidationUtility
			= new FieldValidationUtility();

		if (fieldValidationUtility.isEmpty(code)) {
			String codeFieldLabel
				= RIFDataLoaderToolMessages.getMessage(
					"dataSetConfiguration.code.label");
			String errorMessage
				= RIFDataLoaderToolMessages.getMessage(
					"general.validation.emptyRequiredField",
					codeFieldLabel);
			errorMessages.add(errorMessage);	
		}
		
		if (fieldValidationUtility.isEmpty(schemaName)) {
			String schemaNameFieldLabel
				= RIFDataLoaderToolMessages.getMessage(
					"dataSetConfiguration.schemaName.label");
			String errorMessage
				= RIFDataLoaderToolMessages.getMessage(
					"general.validation.emptyRequiredField",
					schemaNameFieldLabel);
			errorMessages.add(errorMessage);	
		}
		
		if (fieldValidationUtility.isEmpty(functionName)) {
			String functionNameFieldLabel
				= RIFDataLoaderToolMessages.getMessage(
					"dataSetConfiguration.schemaName.label");
			String errorMessage
				= RIFDataLoaderToolMessages.getMessage(
					"general.validation.emptyRequiredField",
					functionNameFieldLabel);
			errorMessages.add(errorMessage);	
		}
		
		//check they have the same number of parameters
		if (formalParameterNames.size() != actualParameterValues.size()) {
			String errorMessage
				= RIFDataLoaderToolMessages.getMessage(
					"rifConversionFunction.error.unexpectedNumberOfParameters",
					functionName,
					String.valueOf(formalParameterNames.size()),
					String.valueOf(actualParameterValues.size()));
			errorMessages.add(errorMessage);
		}
		else {		
			Collator collator = RIFDataLoaderToolMessages.getCollator();
		
			for (int i = 0; i < formalParameterNames.size(); i++) {		
				String currentFormalParameterName
					= formalParameterNames.get(i);
				String formalParameterTypeIdentifier
					= dataTypeFromFormalParameterName.get(currentFormalParameterName).getIdentifier();

				DataSetFieldConfiguration matchingActualParameter = null;
				for (int j = 0; j < actualParameterValues.size(); j++) {
					DataSetFieldConfiguration currentActualParameter
						= actualParameterValues.get(j);
					String currentActualParameterName
						= currentActualParameter.getConvertFieldName();
					if (collator.equals(
						currentFormalParameterName, 
						currentActualParameterName)) {
						//match found
						matchingActualParameter = currentActualParameter;					
						break;
					}
				}

				if (matchingActualParameter == null) {
					String errorMessage
						= RIFDataLoaderToolMessages.getMessage(
							"rifConversionFunction.error.noMatchForFormalParameter",
							functionName,
							currentFormalParameterName);
					errorMessages.add(errorMessage);
				}
				else {
					//we found a matching actual parameter, but now we need to
					//ensure the data types match
					String actualParameterTypeIdentifier
						= matchingActualParameter.getRIFDataType().getIdentifier();
					
					if (collator.equals(
						formalParameterTypeIdentifier, 
						actualParameterTypeIdentifier) == false) {

						String errorMessage
							= RIFDataLoaderToolMessages.getMessage(
								"rifConversionFunction.error.incorrectParameterType",
								functionName,
								currentFormalParameterName,
								formalParameterTypeIdentifier,
								actualParameterTypeIdentifier);
						errorMessages.add(errorMessage);
					}				
				}
			}	
		}
	
		if (errorMessages.size() > 0) {			
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFDataLoaderToolError.IMPROPERLY_SET_CONVERSION_FUNCTION, 
					errorMessages);	
			throw rifServiceException;
		}
		
	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public String getConvertFieldName() {
		
		return convertFieldName;
	}
	
	public void setConvertFieldName(
		final String convertFieldName) {
		
		this.convertFieldName = convertFieldName;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(final String code) {
		
		this.code = code;
	}

	public ArrayList<String> getFormalParameterNames() {
		return formalParameterNames;
	}
	
	public void setFormalParameterNames(final ArrayList<String> formalParameterNames) {
		this.formalParameterNames = formalParameterNames;
	}
	
	public ArrayList<DataSetFieldConfiguration> getActualParameterValues() {
		return actualParameterValues;
	}
	
	public void setActualParameterValues(
		final ArrayList<DataSetFieldConfiguration> actualParameterValues) {
		
		this.actualParameterValues = actualParameterValues;
	}

	public boolean supportsOneToOneConversion() {
		return supportsOneToOneConversion;
	}
	
	public void setSupportsOneToOneConversion(
		final boolean supportsOneToOneConversion) {
		
		this.supportsOneToOneConversion = supportsOneToOneConversion;
	}

	public HashMap<String, AbstractRIFDataType> getDataTypesFromFormalParmaterNames() {
		return dataTypeFromFormalParameterName;
	}
	
	public void setDataTypesFromFormalParameterNames(
		final HashMap<String, AbstractRIFDataType> dataTypeFromFormalParameterName) {
		
		this.dataTypeFromFormalParameterName = dataTypeFromFormalParameterName;		
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

