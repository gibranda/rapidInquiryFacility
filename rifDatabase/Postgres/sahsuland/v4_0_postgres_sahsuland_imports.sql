--
-- THIS SCRIPT IS NO LONGER AUTOGENERATED 
--
-- ************************************************************************
-- *
-- * DO NOT EDIT THIS SCRIPT OR MODIFY THE RIF SCHEMA - USE ALTER SCRIPTS
-- *
-- ************************************************************************
--
-- ************************************************************************
--
-- GIT Header
--
-- $Format:Git ID: (%h) %ci$
-- $Id$
-- Version hash: $Format:%H$
--
-- Description:
--
-- Rapid Enquiry Facility (RIF) - INSERT sahsuland data
--
-- Copyright:
--
-- The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
-- that rapidly addresses epidemiological and public health questions using 
-- routinely collected health and population data and generates standardised 
-- rates and relative risks for any given health outcome, for specified age 
-- and year ranges, for any given geographical area.
--
-- Copyright 2014 Imperial College London, developed by the Small Area
-- Health Statistics Unit. The work of the Small Area Health Statistics Unit 
-- is funded by the Public Health England as part of the MRC-PHE Centre for 
-- Environment and Health. Funding for this project has also been received 
-- from the Centers for Disease Control and Prevention.  
--
-- This file is part of the Rapid Inquiry Facility (RIF) project.
-- RIF is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Lesser General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- RIF is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
-- GNU Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public License
-- along with RIF. If not, see <http://www.gnu.org/licenses/>; or write 
-- to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
-- Boston, MA 02110-1301 USA
--
-- Author:
--
-- Peter Hambly, SAHSU
--
\set ECHO all
\set ON_ERROR_STOP ON
\timing

--
-- Check user is rif40
--
DO LANGUAGE plpgsql $$
BEGIN
	IF user = 'rif40' THEN
		RAISE INFO 'User check: %', user;	
	ELSE
		RAISE EXCEPTION 'C20900: User check failed: % is not rif40', user;	
	END IF;
END;
$$;

--
-- Check database is sahsuland_dev
--
DO LANGUAGE plpgsql $$
BEGIN
	IF current_database() = 'sahsuland_dev' THEN
		RAISE INFO 'Database check: %', current_database();	
	ELSE
		RAISE EXCEPTION 'C20901: Database check failed: % is not sahsuland_dev', current_database();	
	END IF;
END;
$$;

\COPY t_rif40_num_denom FROM '../sahsuland/data/t_rif40_num_denom.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_dual FROM '../sahsuland/data/rif40_dual.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY t_rif40_projects FROM '../sahsuland/data/t_rif40_projects.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_version FROM '../sahsuland/data/rif40_version.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_error_messages FROM '../sahsuland/data/rif40_error_messages.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\dS+ rif40_outcomes
\COPY rif40_outcomes(outcome_type, outcome_description, current_version, current_sub_version, previous_version, previous_sub_version) FROM '../sahsuland/data/rif40_outcomes.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_icd9 FROM '../sahsuland/data/rif40_icd9.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_icd10 FROM '../sahsuland/data/rif40_icd10.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_opcs4 FROM '../sahsuland/data/rif40_opcs4.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_icd_o_3 FROM '../sahsuland/data/rif40_icd_o_3.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_a_and_e FROM '../sahsuland/data/rif40_a_and_e.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_population_world FROM '../sahsuland/data/rif40_population_world.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_population_europe FROM '../sahsuland/data/rif40_population_europe.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_population_us FROM '../sahsuland/data/rif40_population_us.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_reference_tables FROM '../sahsuland/data/rif40_reference_tables.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY sahsuland_pop FROM '../sahsuland/data/sahsuland_pop.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY sahsuland_cancer FROM '../sahsuland/data/sahsuland_cancer.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY sahsuland_geography FROM '../sahsuland/data/sahsuland_geography.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY sahsuland_covariates_level3 FROM '../sahsuland/data/sahsuland_covariates_level3.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY sahsuland_covariates_level4 FROM '../sahsuland/data/sahsuland_covariates_level4.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_age_group_names FROM '../sahsuland/data/rif40_age_group_names.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_outcome_groups FROM '../sahsuland/data/rif40_outcome_groups.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_predefined_groups FROM '../sahsuland/data/rif40_predefined_groups.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY rif40_age_groups FROM '../sahsuland/data/rif40_age_groups.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\COPY t_rif40_fdw_tables FROM '../sahsuland/data/t_rif40_fdw_tables.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');

\dS+ rif40_columns
\COPY rif40_columns(table_or_view_name_hide, column_name_hide, nullable, oracle_data_type, comments) FROM '../sahsuland/data/rif40_columns.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\dS+ rif40_tables_and_views
\COPY rif40_tables_and_views(class, table_or_view, table_or_view_name_hide, comments) FROM '../sahsuland/data/rif40_tables_and_views.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');
\dS+ rif40_triggers
\COPY rif40_triggers FROM '../sahsuland/data/rif40_triggers.csv' WITH (FORMAT csv, QUOTE '"', ESCAPE '\');

--
-- Eof
