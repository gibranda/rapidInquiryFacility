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
-- Rapid Enquiry Facility (RIF) - Common partitioning functions
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

DROP FUNCTION IF EXISTS rif40_sql_pkg._rif40_hash_partition_create(
	 	VARCHAR, VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER, VARCHAR[]);
DROP FUNCTION IF EXISTS rif40_sql_pkg._rif40_hash_partition_create(
	 	VARCHAR, VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER);
		
CREATE OR REPLACE FUNCTION rif40_sql_pkg._rif40_hash_partition_create(
	l_schema 	VARCHAR, 
	master_table 	VARCHAR, 
	partition_table VARCHAR, 
	l_column	VARCHAR, 
	l_value		INTEGER,
	num_partitions	INTEGER,
	l_table_list VARCHAR[])
RETURNS void
SECURITY DEFINER
AS $func$
/*
Function: 	_rif40_hash_partition_create()
Parameters:	Schema, master table, partition table, column, hash value, total partitions, list of tables in current partition build
Returns:	Nothing
Description:	Create hash partition schema.table_<value> on column <column> value <value>, inheriting from <mnaster table>.
		Comment columns

Runs as RIF40 (so can create partition tables)

Generates the following SQL to create a partition>
	
CREATE TABLE rif40_study_shares_p15 (
 CONSTRAINT rif40_study_shares_p15_ck CHECK (hash_partition_number = 15 /- bucket requested -/)
) INHERITS (rif40_study_shares);

Call rif40_sql_pkg._rif40_common_partition_create to:

* Add indexes, primary key
* Add foreign keys
* Add trigger, unique, check and exclusion constraints
* Validation triggers
* Add grants
* Table and column comments

 */
DECLARE
	ddl_stmt VARCHAR[];
--
BEGIN
--
-- Must be rif40 or have rif_user or rif_manager role
--
	IF USER != 'rif40' AND NOT rif40_sql_pkg.is_rif40_user_manager_or_schema() THEN
		PERFORM rif40_log_pkg.rif40_error(-20999, '_rif40_hash_partition_create', 'User % must be rif40 or have rif_user or rif_manager role', 
			USER::VARCHAR);
	END IF;
--
	PERFORM rif40_log_pkg.rif40_log('INFO', '_rif40_hash_partition_create', 
		'Create hash partition: % for hash value % (of %) on column: %; master: %.%', 
		partition_table::VARCHAR	/* Partition table */,
		l_value::VARCHAR		/* Partition hash value */,
		num_partitions::VARCHAR		/* Total partitions */,
		l_column::VARCHAR		/* Partition column */,
		l_schema::VARCHAR		/* Schema */, 
		master_table::VARCHAR		/* Master table inheriting from */);

--
-- Create partition table inheriting from master
--
--	IF l_value ~ '^[0-9]*.?[0-9]*$' THEN /* isnumeric */	
-- May need type specific _rif40_hash_bucket_check functions to avoid implicit cast which may break the equality checks in partition elimination
--
	ddl_stmt[1]:='CREATE TABLE '||quote_ident(l_schema)||'.'||quote_ident(partition_table)||' ('||E'\n'||
--
-- These don't work as the Postgres parser only supports partition range elimination by value
--
--		' CONSTRAINT '||quote_ident(partition_table||'_ck')||' CHECK ('''||l_value||''' = rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, '||num_partitions::Text||')::VARCHAR)'||E'\n'||
--		' CONSTRAINT '||quote_ident(partition_table||'_ck')||' CHECK ('||l_column||' = rif40_sql_pkg._rif40_hash_bucket_check('||quote_ident(l_column)||', '||num_partitions||' /* total buckets */, '||l_value||' /* bucket requested */))'||E'\n'||
		' CONSTRAINT '||quote_ident(partition_table||'_ck')||' CHECK (hash_partition_number = '||l_value||' /* bucket requested */)'||E'\n'||
		') INHERITS ('||quote_ident(master_table)||')';
--
-- Run
--
	PERFORM rif40_sql_pkg.rif40_ddl(ddl_stmt);
	ddl_stmt:=NULL;
--
-- Call rif40_sql_pkg._rif40_common_partition_create to:
-- * Add indexes, primary key
-- * Add foreign keys
-- * Add trigger, unique, check and exclusion constraints
-- * Validation triggers
-- * Add grants
-- * Table and column comments
--
	PERFORM rif40_sql_pkg._rif40_common_partition_create(l_schema, master_table, partition_table, l_column, l_value::VARCHAR, l_table_list);

END;
$func$ 
LANGUAGE plpgsql;

COMMENT ON FUNCTION rif40_sql_pkg._rif40_hash_partition_create(VARCHAR, VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER, VARCHAR[]) IS 'Function: 	_rif40_hash_partition_create()
Parameters:	Schema, master table, partition table, column, hash value, number of partitions, list of tables in current partition build
Returns:	Nothing
Description:	Create hash partition schema.table_<value> on column <column> value <value>, inheriting from <master table>.
		Comment columns

Runs as RIF40 (so can create partition tables)

Generates the following SQL to create a partition>
	
CREATE TABLE rif40_study_shares_p15 (
 CONSTRAINT rif40_study_shares_p15_ck CHECK (hash_partition_number = 15 /* bucket requested */)
) INHERITS (rif40_study_shares);

Call rif40_sql_pkg._rif40_common_partition_create to:

* Add indexes, primary key
* Add foreign keys
* Add trigger, unique, check and exclusion constraints
* Validation triggers
* Add grants
* Table and column comments';

--
-- Eof