#!/usr/bin/env node

// ************************************************************************
//
// GIT Header
//
// $Format:Git ID: (%h) %ci$
// $Id: 7ccec3471201c4da4d181af6faef06a362b29526 $
// Version hash: $Format:%H$
//
// Description:
//
// Rapid Enquiry Facility (RIF) - Node.js test harness
//
// Copyright:
//
// The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
// that rapidly addresses epidemiological and public health questions using 
// routinely collected health and population data and generates standardised 
// rates and relative risks for any given health outcome, for specified age 
// and year ranges, for any given geographical area.
//
// Copyright 2014 Imperial College London, developed by the Small Area
// Health Statistics Unit. The work of the Small Area Health Statistics Unit 
// is funded by the Public Health England as part of the MRC-PHE Centre for 
// Environment and Health. Funding for this project has also been received 
// from the Centers for Disease Control and Prevention.  
//
// This file is part of the Rapid Inquiry Facility (RIF) project.
// RIF is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RIF is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with RIF. If not, see <http://www.gnu.org/licenses/>; or write 
// to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
// Boston, MA 02110-1301 USA
//
// Author:
//
// Peter Hambly, SAHSU
//
// Usage: dbTileMaker [options]
//
// Version: 0.1
//
// RIF 4.0 Database test harness.
//
// Options:
//  -d, --debug     RIF database PL/pgsql debug level      [default: 0]
//  -D, --database  name of Postgres database              [default: PGDATABASE]
//  -U, --username  Postgres database username             [default: PGUSER]
//  -P, --port      Postgres database port                 [default: PGPORT]
//  -H, --hostname  hostname of Postgres database          [default: PGHOST]
//  --help          display this helpful message and exit  [default: false]
//
// E.g.
//
// node dbTileMaker.js -H wpea-rif1 -D sahsuland_dev -U pch -d 1
//
// Connects using Postgres native driver (not JDBC) as rif40.
//
// Uses:
//
// https://github.com/brianc/node-postgres
// https://github.com/substack/node-optimist
//
// See: Node Makefile for build instructions
//

const tileMaker = require('./lib/tileMaker');

/* 
 * Function: 	main()
 * Parameters: 	ARGV
 * Returns:		Nothing
 * Description: Create control database connecton, then run rif40_startup()
 *				Then _rif40_sql_test_log_setup() ...
 */	
function main() {
	
	var pg = null;
	var optimist  = require('optimist');
	
// Process Args using optimist
	var argv = optimist
    .usage("Usage: \033[1mdbTileMaker.\033[0m [options]\n\n"
+ "Version: 0.1\n\n")

    .options("D", {
      alias: "database",
      describe: "name of Postgres database",
	  type: "string",
      default: pg_default("PGDATABASE")
    })
    .options("U", {
      alias: "username",
      describe: "Postgres database username",
	  type: "string",
      default: pg_default("PGUSER") 
    })	
    .options("P", {
      alias: "port",
      describe: "Postgres database port",
	  type: "integer",
      default: pg_default("PGPORT") 
    })		
    .options("H", {
      alias: "hostname",
      describe: "hostname of Postgres database",
	  type: "string",
      default: pg_default("PGHOST")
    })

    .options("help", {
      describe: "display this helpful message and exit",
      type: "boolean",
      default: false
    })	
	 .check(function(argv) {
      if (argv.help) return;
    })
    .argv;

	if (argv.help) return optimist.showHelp();

//
// Load database module
// Will eventually support SQL server as well
//
	try {
		pg=require('pg');
	}
	catch(err) {
		console.error('1: Could not load postgres database module.', err);				
		process.exit(1);
	}
	
	// Create Postgres client;
	pg_db_connect(pg, argv["hostname"] , argv["database"], argv["username"], argv["port"]);
} /* End of main */

/* 
 * Function: 	pg_default()
 * Parameters: 	Postgres variable name
 * Returns:		Defaulted value
 * Description: Setup postgres defaults (i.e. use PGDATABASE etc from env or set sensible default
 *				Used by optimist
 */
function pg_default(p_var) {
	var p_def;
	
	if (p_var == "PGDATABASE") {
		p_def="sahsuland_dev";
	}
	else if (p_var == "PGHOST") {
		p_def="localhost";
	}
	else if (p_var == "PGUSER") {
		p_def=process.env["USERNAME"];
		if (p_def === undefined) {
			p_def=process.env["USER"];
			if (p_def === undefined) {
				p_def="<NO USER DEFINED>";
			}
		}
	}	
	else if (p_var == "PGPORT") {
		p_def=5432;
	}
	
	if (process.env[p_var]) { 
//		console.error(p_var + ": " + (process.env[p_var]||'Not defined'));
		return process.env[p_var];
	} 
	else { 
		return p_def;
	}
}
	
/* 
 * Function: 	pg_db_connect()
 * Parameters: 	Postgres PG package connection handle,
 *				database host, name, username, port
 * Returns:		Nothing
 * Description:	Connect to database, ...
 */
function pg_db_connect(p_pg, p_hostname, p_database, p_user, p_port) {
	
	var client1 = null; // Client 1: Master; hard to remove	

	var endCallBack = function endCallBack(err) {
		if (err) {
			console.error("Postgres error: " + err.message);
			process.exit(0);		
		}
		process.exit(1);	
	}
	
	var conString = 'postgres://' + p_user + '@' + p_hostname + ':' + p_port + '/' + p_database + '?application_name=dbTileMaker';

// Use PGHOST, native authentication (i.e. same as psql)
	client1 = new p_pg.Client(conString);
// Connect to Postgres database
	client1.connect(function(err) {
		if (err) {
			console.error('Could not connect to postgres client ' + p_num + ' using: ' + conString, err);
			if (p_hostname === 'localhost') {
				
// If host = localhost, use IPv6 numeric notation. This prevent ENOENT errors from getaddrinfo() in Windows
// when Wireless is disconnected. This is a Windows DNS issue. psql avoids this somehow.
// You do need entries for ::1 in pgpass			

				console.log('Attempt 2 (::1 instead of localhost) to connect to Postgres using: ' + conString);
				conString = 'postgres://' + p_user + '@' + '[::1]' + ':' + p_port + '/' + p_database + '?application_name=db_test_harness';
				client1 = new p_pg.Client(conString);
// Connect to Postgres database
				client1.connect(function(err) {
					if (err) {
						console.error('Could not connect [2nd attempt] to postgres client ' + p_num + ' using: ' + conString, err);
						process.exit(1);	
					}
					else {
// Call pgTileMaker()...
						tileMaker.pgTileMaker(client1, endCallBack);
					} // End of else connected OK 
				}); // End of connect				
				console.log('Connected to Postgres [2nd attempt] using: ' + conString);				
			}
		}
		else {			

// Call pgTileMaker()...
			tileMaker.pgTileMaker(client1, endCallBack);
		} // End of else connected OK 
	}); // End of connect		
	console.log('Connected to Postgres using: ' + conString);	

	// Notice message event processors
	client1.on('notice', function(msg) {
		  console.log('PG: %s', msg);
	});
}

main();

//
// Eof