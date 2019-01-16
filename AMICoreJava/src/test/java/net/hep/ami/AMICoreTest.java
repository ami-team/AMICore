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

	public static void main(String[] args) throws Exception
	{
		System.setProperty("ami.conffile", "/Users/jodier/AMI_PostgreSQL.xml");

		System.setProperty("ami.integration", "");

		AMICoreTest test = new AMICoreTest();

		test.databaseTest();

		System.exit(0);
	}

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

		String test_catalog = ConfigSingleton.getProperty("test_catalog");
		String test_schema = ConfigSingleton.getProperty("test_schema");
		String test_url = ConfigSingleton.getProperty("test_url");
		String test_user = ConfigSingleton.getProperty("test_user");
		String test_pass = ConfigSingleton.getProperty("test_pass");

		try 
		{
			String testCustom = "{\"DATASET\":{\"x\":280,\"y\":55,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"DATASET_FILE_BRIDGE\":{\"x\":280,\"y\":240,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"DATASET_PARAM\":{\"x\":20,\"y\":20,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"DATASET_TYPE\":{\"x\":540,\"y\":230,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"FILE\":{\"x\":280,\"y\":410,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"FILE_TYPE\":{\"x\":540,\"y\":410,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"PROJECT\":{\"x\":540,\"y\":65,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"}}";

			String fields = "externalCatalog;internalCatalog;internalSchema;jdbcUrl;user;pass;custom";
			String values = "test;" + test_catalog + ";" + test_schema + ";" + test_url + ";" + test_user  + ";" + test_pass + ";" + testCustom.replace("\"", "\\\"");

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

		testDB.commitAndRelease();

		/*-----------------------------------------------------------------*/

		System.out.println("DONE 1");
		CatalogSingleton.reload(true);
		System.out.println("DONE 2");

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

		/*-----------------------------------------------------------------*/
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