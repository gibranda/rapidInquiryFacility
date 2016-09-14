/*
 * SQL statement name: 	create_geography_table.sql
 * Type:				Common SQL statement
 * Parameters:
 *						1: table; e.g. geography_cb_2014_us_county_500k
 *
 * Description:			Create geography table
 * Note:				%%%% becomes %% after substitution
 */
CREATE TABLE %1 (
       geography               VARCHAR(50)  NOT NULL,
       description             VARCHAR(250) NOT NULL,
       hierarchytable          VARCHAR(30)  NOT NULL,
       srid                    INTEGER      NULL DEFAULT 0,
       defaultcomparea         VARCHAR(30)  NULL,
       defaultstudyarea        VARCHAR(30)  NULL,
       CONSTRAINT %1_pk PRIMARY KEY(geography)
	)