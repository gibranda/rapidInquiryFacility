package rifServices.restfulWebServices;


import javax.xml.bind.annotation.*;

import java.util.ArrayList;

/**
 *
 * <hr>
 * The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
 * that rapidly addresses epidemiological and public health questions using 
 * routinely collected health and population data and generates standardised 
 * rates and relative risks for any given health outcome, for specified age 
 * and year ranges, for any given geographical area.
 *
 * Copyright 2014 Imperial College London, developed by the Small Area
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

@XmlRootElement(name="investigation")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder= {
	"title",
	"healthTheme",
	"ndPair",
	"ageBands",
	"healthCodes",
	"yearRangeStart",
	"yearRangeEnd",
	"interval",
	"sex",
	"covariates"
	})
public class InvestigationProxy {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================

 	@XmlElement(required = true)	
	private String title;
 	@XmlElement(required = true)	
	private String healthTheme;
	private NumeratorDenominatorPairProxy ndPair;
	private ArrayList<AgeBandProxy> ageBands;
	private ArrayList<HealthCodeProxy> healthCodes;
	
 	@XmlElement(required = true)	
	private String yearRangeStart;
 	@XmlElement(required = true)	
	private String yearRangeEnd;
 	@XmlElement(required = true)
	private String interval;
 	@XmlElement(required = true)	
	private String sex;
	
	private ArrayList<CovariateProxy> covariates;
	
	// ==========================================
	// Section Construction
	// ==========================================

	public InvestigationProxy() {

	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHealthTheme() {
		return healthTheme;
	}

	public void setHealthTheme(String healthTheme) {
		this.healthTheme = healthTheme;
	}

	public NumeratorDenominatorPairProxy getNdPair() {
		return ndPair;
	}

	public void setNdPair(NumeratorDenominatorPairProxy ndPair) {
		this.ndPair = ndPair;
	}

	public ArrayList<AgeBandProxy> getAgeBands() {
		return ageBands;
	}

	public void setAgeBands(ArrayList<AgeBandProxy> ageBands) {
		this.ageBands = ageBands;
	}

	public ArrayList<HealthCodeProxy> getHealthCodes() {
		return healthCodes;
	}

	public void setHealthCodes(ArrayList<HealthCodeProxy> healthCodes) {
		this.healthCodes = healthCodes;
	}

	public String getYearRangeStart() {
		return yearRangeStart;
	}

	public void setYearRangeStart(String yearRangeStart) {
		this.yearRangeStart = yearRangeStart;
	}

	public String getYearRangeEnd() {
		return yearRangeEnd;
	}

	public void setYearRangeEnd(String yearRangeEnd) {
		this.yearRangeEnd = yearRangeEnd;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public ArrayList<CovariateProxy> getCovariates() {
		return covariates;
	}

	public void setCovariates(ArrayList<CovariateProxy> covariates) {
		this.covariates = covariates;
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
