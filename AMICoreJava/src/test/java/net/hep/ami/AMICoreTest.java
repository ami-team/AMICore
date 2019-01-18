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
			String testCustom = "{\"DATASET\":{\"x\":280,\"y\":55,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"DATASET_FILE_BRIDGE\":{\"x\":280,\"y\":240,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"DATASET_PARAM\":{\"x\":20,\"y\":20,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"DATASET_TYPE\":{\"x\":540,\"y\":230,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"FILE\":{\"x\":280,\"y\":410,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"FILE_TYPE\":{\"x\":540,\"y\":410,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"PROJECT\":{\"x\":540,\"y\":65,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"},\"FILE_VIEW\":{\"x\":20,\"y\":410,\"topColor\":\"#0066CC\",\"bodyColor\":\"#FFFFFF\",\"strokeColor\":\"#0057AD\"}}";

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

		try 
		{
			String fields = "catalog;entity;field;isPrimary";
			String values = "test;PROJECT;name;1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		try 
		{
			String fields = "catalog;entity;field;isPrimary";
			String values = "test;PROJECT;name;1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		String[] testTables = {"PROJECT","DATASET","DATASET_FILE_BRIDGE","DATASET_PARAM","DATASET_TYPE","FILE","FILE_TYPE"};
		for (int i = 0; i < testTables.length; i++) {
			try 
			{
				String fields = "catalog;entity;field;isCreatedBy";
				String values = "test;" + testTables[i] +";createdBy;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
			}

			try 
			{
				String fields = "catalog;entity;field;isModifiedBy";
				String values = "test;" + testTables[i] +";modifiedBy;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
			}
			try 
			{
				String fields = "catalog;entity;field;isCreated";
				String values = "test;" + testTables[i] +";created;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
			}

			try 
			{
				String fields = "catalog;entity;field;isModified";
				String values = "test;" + testTables[i] +";modified;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
			}

		}

		try 
		{
			String fields = "catalog;entity;field;isPrimary";
			String values = "test;FILE_VIEW;id;1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		try 
		{
			String fields = "name;fkCatalog;fkTable;fkColumn;pkCatalog;pkTable;pkColumn";
			String values = "fk1_FILE_VIEW;test;FILE_VIEW;id;test;DATASET_FILE_BRIDGE;id";
			String command = "AddElement -catalog=\"self\" -entity=\"router_foreign_key\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}

		testDB.commitAndRelease();

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

		Map<String, String> arguments = new HashMap<String, String>();

		/*-----------------------------------------------------------------*/

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "PROJECT");
			arguments.put("separator", ";");
			arguments.put("fields", "name;description");
			arguments.put("values", "AMI;This an AMI demonstration project");
			System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "DATASET_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;PROJECT.name;description");
			arguments.put("values", "A;AMI;This is a test");
			System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "DATASET_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;PROJECT.name;description");
			arguments.put("values", "B;AMI;This is a test");
			System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		int cptMax = 10;

		for (int i = 0; i < cptMax; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET");
				arguments.put("separator", ";");
				arguments.put("fields", "name;DATASET_TYPE.name;PROJECT.name");
				arguments.put("values", "dataset_" + i + ";A;AMI");
				System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}

		/*-----------------------------------------------------------------*/

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "FILE_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;description");
			arguments.put("values", "TEXT;This is a test");
			System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "FILE_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;description");
			arguments.put("values", "BINARY;This is a test");
			System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		for (int i = 0; i < cptMax; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "FILE");
				arguments.put("separator", ";");
				arguments.put("fields", "name;FILE_TYPE.name");
				arguments.put("values", "file_" + i + ";BINARY");
				System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}

		for (int i = 0; i < cptMax; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET_FILE_BRIDGE");
				arguments.put("separator", ";");
				//arguments.put("fields", "FILE.name;FILE.id;DATASET.name;PROJECT.name");
				//arguments.put("values", "file_" + i + ";" + i + ";dataset_" + i +";AMI");
				arguments.put("fields", "FILE.name;DATASET.name");
				arguments.put("values", "file_" + i + ";dataset_" + i +"");
				System.out.println(CommandSingleton.executeCommand("AddElement", arguments, false).replace(">", ">\n"));
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		/*-----------------------------------------------------------------*/

		String commandTest = "SearchQuery -AMIPass=\"insider\" -AMIUser=\"admin\" -catalog=\"test\" -entity=\"FILE_VIEW\" -mql=\"SELECT * WHERE id > 0 \" ";

		try
		{
			System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
