/*
 * SQL statement name: 	create_tiles_table.sql
 * Type:				Common SQL statement
 * Parameters:
 *						1: table; e.g. t_tiles_cb_2014_us_county_500k
 *						2: JSON datatype (Postgres JSON, SQL server Text)
 *
 * Description:			Create tiles table
 * Note:				%%%% becomes %% after substitution
 */
CREATE TABLE %1 (
	geolevel_id			INTEGER			NOT NULL,
	zoomlevel			INTEGER			NOT NULL,
	x					INTEGER			NOT NULL, 
	y					INTEGER			NOT NULL,
	optimised_geojson	%2,
	optimised_topojson	%2,
	tile_id				VARCHAR(200)	NOT NULL
)