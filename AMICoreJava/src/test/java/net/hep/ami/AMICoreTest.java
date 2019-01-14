package net.hep.ami;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

import org.junit.jupiter.api.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.jdbc.reflexion.*;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

@SuppressWarnings("all")
public class AMICoreTest
{
	/*---------------------------------------------------------------------*/

	@Test
	public void databaseTest() throws Exception
	{
		if(System.getProperty("ami.integration") == null)
		{
			System.out.println("skipping integration");

			return;
		}

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* SETUP SERVER CONFIG                                         */
			/*-------------------------------------------------------------*/

			Router db = new Router("self");

			try
			{
				db.create();

				db.fill(ConfigSingleton.getProperty("router_schema"));

				db.commitAndRelease();
			}
			catch(Exception e)
			{
				db.rollbackAndRelease();

				throw e;
			}

			/*-------------------------------------------------------------*/
			/* LOAD SERVER CONFIG                                          */
			/*-------------------------------------------------------------*/

			Router.reload(true);

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		try 
		{
			String selfCustom = "{\\\"router_authority\\\":{\\\"x\\\":790,\\\"y\\\":460,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_catalog\\\":{\\\"x\\\":270,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_catalog_extra\\\":{\\\"x\\\":530,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_command\\\":{\\\"x\\\":10,\\\"y\\\":480,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_command_role\\\":{\\\"x\\\":10,\\\"y\\\":360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_config\\\":{\\\"x\\\":10,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_converter\\\":{\\\"x\\\":10,\\\"y\\\":180,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_foreign_key\\\":{\\\"x\\\":790,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_ipv4_blocks\\\":{\\\"x\\\":10,\\\"y\\\":740,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_ipv6_blocks\\\":{\\\"x\\\":530,\\\"y\\\":740,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_locations\\\":{\\\"x\\\":270,\\\"y\\\":755,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_role\\\":{\\\"x\\\":270,\\\"y\\\":360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_search_interface\\\":{\\\"x\\\":270,\\\"y\\\":480,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_short_url\\\":{\\\"x\\\":790,\\\"y\\\":250,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_user\\\":{\\\"x\\\":530,\\\"y\\\":480,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_user_role\\\":{\\\"x\\\":530,\\\"y\\\":360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"}}";
			String command = "UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -where=\"externalCatalog='self'\" -separator=\";\" -fields=\"custom\" -values=\"" + selfCustom + "\"";

			System.out.println(CommandSingleton.executeCommand(command, false).replace(">", ">\n"));
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		String test_catalog = ConfigSingleton.getProperty("test_catalog");
		String test_schema = ConfigSingleton.getProperty("test_schema");
		String test_url = ConfigSingleton.getProperty("test_url");
		String test_user = ConfigSingleton.getProperty("test_user");
		String test_pass = ConfigSingleton.getProperty("test_pass");

		try 
		{
			String testCustom = "{\\\"DATASET\\\":{\\\"x\\\":260,\\\"y\\\":45,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_FILE_BRIDGE\\\":{\\\"x\\\":260,\\\"y\\\":255,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PARAM\\\":{\\\"x\\\":5,\\\"y\\\":0,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_TYPE\\\":{\\\"x\\\":605,\\\"y\\\":225,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE\\\":{\\\"x\\\":260,\\\"y\\\":450,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE_TYPE\\\":{\\\"x\\\":610,\\\"y\\\":450,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PROJECT\\\":{\\\"x\\\":600,\\\"y\\\":50,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"}}";
			String fields = "externalCatalog;internalCatalog;internalSchema;jdbcUrl;user;pass;custom";
			String values = "test;" + test_catalog + ";" + test_schema + ";" + test_url + ";" + test_user  + ";" + test_pass + ";" + testCustom;
			String command = "AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			System.out.println(CommandSingleton.executeCommand(command, false).replace(">", ">\n"));
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		CatalogSingleton.reload(true);

		/*-----------------------------------------------------------------*/

		SimpleQuerier testDB = new SimpleQuerier("test");

		/*-----------------------------------------------------------------*/
		/* SELECT PROFILE                                                  */
		/*-----------------------------------------------------------------*/

		String path;

		String jdbcUrl = test_url;

		/**/ if(jdbcUrl.contains("jdbc:mysql")) {
			path = "/sql/testDB-mysql.sql";
		}
		else if(jdbcUrl.contains("jdbc:mariadb")) {
			path = "/sql/testDB-mysql.sql";
		}
		else if(jdbcUrl.contains("jdbc:oracle")) {
			path = "/sql/testDB-oracle.sql";
		}
		else if(jdbcUrl.contains("jdbc:postgresql")) {
			path = "/sql/testDB-postgresql.sql";
		}
		else {
			throw new Exception("only `mysql`, `mariadb`, `oracle` and `postgresql` are supported");
		}
		/*-----------------------------------------------------------------*/
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = Router.class.getResourceAsStream(path);

		/*-----------------------------------------------------------------*/
		/* EXECUTE SQL QUERIES                                             */
		/*-----------------------------------------------------------------*/

		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)))
		{
			String line = "";
			String query = "";

			while((line = bufferedReader.readLine()) != null)
			{
				line = line.trim();

				if(line.isEmpty() == false
				   &&
				   line.startsWith("-") == false
				 ) {
					query += line + " ";

					if(line.endsWith(";;"))
					{
						LogSingleton.root.info(query);

						try
						{
							System.out.println("Query " + query.replace(";;", ""));
							testDB.getStatement().execute(query.replace(";;", ""));
							//testDB.commit();
						}
						catch(SQLException e)
						{
							throw new SQLException(e.getMessage() + " for SQL query: " + query.replace(";;", ""), e);
						}

						query = "";
					}
				}
			}
		}

		// catalog singleton reload (true)
		CatalogSingleton.reload(true);
		/*-----------------------------------------------------------------*/


		String command = "SearchQuery -AMIPass=\"insider\" -AMIUser=\"admin\" -catalog=\"self\" -entity=\"router_catalog\" -mql=\"SELECT externalCatalog, jdbcUrl WHERE externalCatalog LIKE '%%' \" ";

		try
		{
			System.out.println(CommandSingleton.executeCommand(command, false).replace(">", ">\n"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/
}


/*

Map<String, String> arguments = new HashMap<String, String>();

-----------------------------------------------------------------------------

arguments.put("catalog", "test");
arguments.put("entity", "PROJECT");
arguments.put("name", "AMI");
arguments.put("description", "This an AMI demonstration project");
System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

-----------------------------------------------------------------------------

arguments.put("catalog", "test");
arguments.put("entity", "PROJECT");
arguments.put("name", "AMI_2");
arguments.put("description", "This an other AMI demonstration project");
System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

arguments.put("catalog", "test");
arguments.put("entity", "PROJECT");
arguments.put("name", "AMI_2");
System.out.println(CommandSingleton.executeCommand("RemoveElement", arguments, false).replace(">", ">\n"));
-----------------------------------------------------------------------------

arguments.put("catalog", "test");
arguments.put("entity", "FILE_TYPE");
arguments.put("name", "TEXT");
arguments.put("description", "This is a file type");
System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

arguments.put("catalog", "test");
arguments.put("entity", "FILE_TYPE");
arguments.put("name", "BINARY");
arguments.put("description", "This is an other file type");
System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

-----------------------------------------------------------------------------

arguments.put("catalog", "test");
arguments.put("entity", "DATASET_TYPE");
arguments.put("name", "A");
arguments.put("project.name", "AMI");
arguments.put("description", "This is a dataset type for project AMI");
System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

arguments.put("catalog", "test");
arguments.put("entity", "DATASET_TYPE");
arguments.put("name", "B");
arguments.put("project.name", "AMI");
arguments.put("description", "This is an other dataset type for project AMI");
System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

-----------------------------------------------------------------------------

*/