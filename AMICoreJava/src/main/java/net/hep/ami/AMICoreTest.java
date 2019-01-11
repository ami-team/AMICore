package net.hep.ami;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;

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

		if(args.length != 18)
		{
			throw new Exception("Wrong number of arguments (expected 18):" + args.length);
		}

		// mysql args
		// "/Users/jfulach/.ami" "/" "https://ccami006.in2p3.fr:447" "admin" "insider" "ami@lpsc.in2p3.fr" "mPe1JQxaZiAfFaws" "ami_test_router" "" "jdbc:mysql://ccmysql.in2p3.fr:3306" "ami_test_router" "notthisone" "" "ami_test_database" "" "jdbc:mysql://ccmysql.in2p3.fr:3306" "ami_test_db" "notthisone"

		// postgres args
		// "/Users/jfulach/.ami" "/" "https://ccami006.in2p3.fr:446" "admin" "insider" "ami@lpsc.in2p3.fr" "mPe1JQxaZiAfFaws" "ami_test_router" "public" "jdbc:postgresql://ccpgsql.in2p3.fr:5432/ami_test_router" "ami_test_router" "notthisone" "" "ami_test_database" "public" "jdbc:postgresql://ccpgsql.in2p3.fr:5432/ami_test_database" "ami_test_database" "notthisone"

		// oracle args
		// "/Users/jfulach/.ami" "/" "https://ccami006.in2p3.fr:445" "admin" "insider" "ami@lpsc.in2p3.fr" "mPe1JQxaZiAfFaws" "AMI_TEST_ROUTER" "AMI_TEST_ROUTER" "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = ccdbora5v2.in2p3.fr)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST = ccdbora5v3.in2p3.fr)(PORT = 1521))) (CONNECT_DATA = (SERVICE_NAME = ccdev11g.in2p3.fr)))" "ami_test_router" "notthisone" "" "AMI_TEST_DATABASE" "AMI_TEST_DATABASE" "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = ccdbora5v2.in2p3.fr)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST = ccdbora5v3.in2p3.fr)(PORT = 1521))) (CONNECT_DATA = (SERVICE_NAME = ccdev11g.in2p3.fr)))" "ami_test_database" "notthisone"

		String router_reset = "on"; //on

		String configPath = args[0];
		System.out.println("configPath:" + configPath);
		String servletPath = args[1];

		String host = args[2];
		String admin_user = args[3];
		String admin_pass = args[4];
		String admin_email = args[5];
		String encryption_key = args[6];
		String router_catalog = args[7];
		String router_schema = args[8];
		if("N/A".equals(router_schema))
			router_schema = "";
		String router_url = args[9];
		String router_user = args[10];
		String router_pass = args[11];
		String class_path = args[12];
		if("N/A".equals(class_path))
			class_path = "";
		String test_catalog = args[13];
		String test_schema = args[14];
		if("N/A".equals(test_schema))
			test_schema = "";
		String test_url = args[15];
		String test_user = args[16];
		String test_pass = args[17];

		System.setProperty("ami.configfile", configPath + File.separator + "AMI.xml");

		/*-----------------------------------------------------------------*/
		/* BUILD CONFIG FILE                                               */
		/*-----------------------------------------------------------------*/
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();

		stringBuilder1.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
		              .append("\n")
		              .append("<properties>\n")
		              .append("  <property name=\"host\"><![CDATA[" + host + "]]></property>\n")
		              .append("  <property name=\"admin_user\"><![CDATA[" + admin_user + "]]></property>\n")
		              .append("  <property name=\"admin_pass\"><![CDATA[" + admin_pass + "]]></property>\n")
		              .append("  <property name=\"admin_email\"><![CDATA[" + admin_email + "]]></property>\n")
		              .append("  <property name=\"encryption_key\"><![CDATA[" + encryption_key + "]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"router_catalog\"><![CDATA[" + router_catalog + "]]></property>\n")
		              .append("  <property name=\"router_schema\"><![CDATA[" + router_schema + "]]></property>\n")
		              .append("  <property name=\"router_url\"><![CDATA[" + router_url + "]]></property>\n")
		              .append("  <property name=\"router_user\"><![CDATA[" + router_user + "]]></property>\n")
		              .append("  <property name=\"router_pass\"><![CDATA[" + router_pass + "]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"class_path\"><![CDATA[" + class_path + "]]></property>\n")
		              .append("</properties>\n")
		;

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* PATCH ROOT SERVLET                                          */
			/*-------------------------------------------------------------*/

			try(InputStream inputStream = new FileInputStream(servletPath + File.separator + "index.html"))
			{
				TextFile.read(stringBuilder2, inputStream);
			}

			/*-------------------------------------------------------------*/

			String stringContent2 = stringBuilder2.toString().replaceAll("endpoint_url\\s*:\\s*[\'\"][^\'\"]*[\'\"]", "endpoint_url: '" + host + "/AMI/FrontEnd'");

			/*-------------------------------------------------------------*/

			try(OutputStream outputStream = new FileOutputStream(servletPath + File.separator + "index.html"))
			{
				TextFile.write(outputStream, stringContent2);
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* SETUP SERVER CONFIG                                         */
			/*-------------------------------------------------------------*/
			//System.out.println("test " + router_catalog + " " + router_url + " " + router_user + " " + router_pass);
			Router db = new Router("self", router_catalog, router_url, router_user, router_pass);
			try
			{
				/*---------------------------------------------------------*/

				try(OutputStream outputStream = new FileOutputStream(configPath + File.separator + "AMI.xml"))
				{
					TextFile.write(outputStream, stringBuilder1);
				}

				ConfigSingleton.reload();

				/*---------------------------------------------------------*/

				if("on".equals(router_reset))
				{
					db.create();

					db.fill(router_schema);
				}

				/*---------------------------------------------------------*/

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
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		// update element (router_catalog self custom)
		try 
		{
			String selfCustom = "{\\\"router_authority\\\":{\\\"x\\\":790,\\\"y\\\":460,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_catalog\\\":{\\\"x\\\":270,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_catalog_extra\\\":{\\\"x\\\":530,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_command\\\":{\\\"x\\\":10,\\\"y\\\":480,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_command_role\\\":{\\\"x\\\":10,\\\"y\\\":360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_config\\\":{\\\"x\\\":10,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_converter\\\":{\\\"x\\\":10,\\\"y\\\":180,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_foreign_key\\\":{\\\"x\\\":790,\\\"y\\\":10,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_ipv4_blocks\\\":{\\\"x\\\":10,\\\"y\\\":740,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_ipv6_blocks\\\":{\\\"x\\\":530,\\\"y\\\":740,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_locations\\\":{\\\"x\\\":270,\\\"y\\\":755,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_role\\\":{\\\"x\\\":270,\\\"y\\\":360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_search_interface\\\":{\\\"x\\\":270,\\\"y\\\":480,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_short_url\\\":{\\\"x\\\":790,\\\"y\\\":250,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_user\\\":{\\\"x\\\":530,\\\"y\\\":480,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"router_user_role\\\":{\\\"x\\\":530,\\\"y\\\":360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"}}";
			String command = "UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -where=\"externalCatalog='self'\" -separator=\";\" -fields=\"custom\" -values=\"" + selfCustom + "\" -AMIUser=\"" + admin_user + "\" -AMIPass=\"" + admin_pass + "\"  ";
			System.out.println(CommandSingleton.executeCommand(command).replace(">", ">\n"));
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		
		// add element (test catalog)
		try 
		{
			String testCustom = "{\\\"DATASET\\\":{\\\"x\\\":260,\\\"y\\\":45,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_FILE_BRIDGE\\\":{\\\"x\\\":260,\\\"y\\\":255,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PARAM\\\":{\\\"x\\\":5,\\\"y\\\":0,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_TYPE\\\":{\\\"x\\\":605,\\\"y\\\":225,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE\\\":{\\\"x\\\":260,\\\"y\\\":450,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE_TYPE\\\":{\\\"x\\\":610,\\\"y\\\":450,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PROJECT\\\":{\\\"x\\\":600,\\\"y\\\":50,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"}}";
			String fields = "externalCatalog;internalCatalog;internalSchema;jdbcUrl;user;pass;custom";
			String values = "test;" + test_catalog + ";" + test_schema + ";" + test_url + ";" + test_user  + ";" + test_pass + ";" + testCustom;
			String command = "AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\" -AMIUser=\"" + admin_user + "\" -AMIPass=\"" + admin_pass + "\"  ";
			System.out.println(CommandSingleton.executeCommand(command).replace(">", ">\n"));
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		// catalog singleton reload (true)
		CatalogSingleton.reload(true);


		// new simple querier (test catalog)
		SimpleQuerier testDB = new SimpleQuerier("test");

		// execute sql create test database
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


		String command = "SearchQuery -AMIPass=\"insider\" -AMIUser=\"admin\" -catalog=\"self\" -entity=\"router_catalog\" -mql=\"SELECT externalCatalog, jdbcUrl\"";
		try
		{
			System.out.println(CommandSingleton.executeCommand(command).replace(">", ">\n"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		

		System.out.println("done");
		System.exit(0);
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