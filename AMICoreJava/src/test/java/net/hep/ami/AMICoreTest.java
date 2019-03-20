package net.hep.ami;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

import javax.imageio.ImageIO;

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
		boolean testFail = false;

		if(System.getProperty("ami.integration") == null)
		{
			System.out.println("skipping integration");

			return;
		}

		/*
		String commandTestDev = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * \" -limit=\"10000\"";
		String data = "";
		try
		{
			data = CommandSingleton.executeCommand(commandTestDev, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		StringReader stringReader = new StringReader(data);
		StringWriter stringWriter = new StringWriter();
		try
		{
			ConverterSingleton.convert("AMIXmlToJson.xsl", stringReader, stringWriter);

			data = stringWriter.toString();
		}
		catch(Exception e)
		{
			data = XMLTemplates.error(
				e.getMessage()
			);

		}

		System.out.println(data);


		if(true)
		return;
		*/

		System.out.println("Init tests"); 

		/*-----------------------------------------------------------------*/

		System.out.println("Setting up router database: " +  ConfigSingleton.getProperty("router_url"));

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* SETUP SERVER CONFIG                                         */
			/*-------------------------------------------------------------*/

			Router db = new Router();

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
			testFail = true;
		}

		/*-----------------------------------------------------------------*/

		System.out.println("Setting up test database: " + ConfigSingleton.getProperty("test_url"));

		/*-----------------------------------------------------------------*/

		String test_catalog = ConfigSingleton.getProperty("test_catalog");
		String test_schema = ConfigSingleton.getProperty("test_schema");
		String test_url = ConfigSingleton.getProperty("test_url");
		String test_user = ConfigSingleton.getProperty("test_user");
		String test_pass = ConfigSingleton.getProperty("test_pass");
		int cptMax = 100000;

		try 
		{
			String testCustom = "{\"DATASET\":{\"x\":280,\"y\":55,\"color\":\"#72DE4C\"},\"DATASET_FILE_BRIDGE\":{\"x\":280,\"y\":240,\"color\":\"#CBCC5A\"},\"DATASET_PARAM\":{\"x\":20,\"y\":20,\"color\":\"#00CC01\"},\"DATASET_TYPE\":{\"x\":540,\"y\":55,\"color\":\"#19CE57\"},\"FILE\":{\"x\":280,\"y\":410,\"color\":\"#D4E03F\"},\"FILE_TYPE\":{\"x\":540,\"y\":410,\"color\":\"#E5A44C\"},\"PROJECT\":{\"x\":540,\"y\":240,\"color\":\"#F5743B\"},\"FILE_VIEW\":{\"x\":20,\"y\":410,\"color\":\"#C3DB2E\"}}";
			String fields = "externalCatalog;internalCatalog;internalSchema;jdbcUrl;user;pass;custom";
			String values = "test;" + test_catalog + ";" + test_schema + ";" + test_url + ";" + test_user  + ";" + test_pass + ";" + testCustom.replace("\"", "\\\"");

			String command = "AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			testFail = true;
		}

		/*-----------------------------------------------------------------*/

		CatalogSingleton.reload(true);

		/*-----------------------------------------------------------------*/

		SimpleQuerier testDB = new SimpleQuerier("test", "admin", true);

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
							//System.out.println("Query " + query.replace(";;", ""));
							testDB.getStatement().execute(query.replace(";;", ""));
						}
						catch(SQLException e)
						{
							System.out.println(e.getMessage());
							throw new SQLException(e.getMessage() + " for SQL query: " + query.replace(";;", ""), e);
						}

						query = "";
					}
				}
			}
		}

		try 
		{
			String fields = "catalogxxxxxentityxxxxxfieldxxxxxdescriptionxxxxxisReadablexxxxxwebLinkScriptxxxxxisGroupable";
			String params = "[\\\\\\\"SearchQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"DATASET\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE PROJECT.name{test.DATASET.projectFK}='\\\" + row.getValue(\\\"name\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";
			String params2 = "[\\\\\\\"SearchQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"DATASET\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE valid=1 AND PROJECT.name{test.DATASET.projectFK}='\\\" + row.getValue(\\\"name\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";
			String params3 = "[\\\\\\\"SearchQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"DATASET\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE valid=0 AND PROJECT.name{test.DATASET.projectFK}='\\\" + row.getValue(\\\"name\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";
			
			String webLinkScript = ""
									+"import net.hep.ami.jdbc.WebLink;"
									+"\\nwebLink = new WebLink();"
									+"\\nwebLink.newLinkProperties().setLabel(\\\"datasets\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"coffee\\\").setTitle(\\\"DATASET\\\");"
									+"\\nwebLink.newLinkProperties().setLabel(\\\"valid\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params2+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"table\\\").setTitle(\\\"DATASET\\\");"
									+"\\nwebLink.newLinkProperties().setLabel(\\\"invalid\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params3+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"table\\\").setTitle(\\\"DATASET\\\");"
									+"\\nreturn webLink;";
			String values = "testxxxxxPROJECTxxxxxnamexxxxxthis is a test descritionxxxxx1xxxxx"+webLinkScript+"xxxxx1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\"xxxxx\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		try 
		{
			String fields = "catalogxxxxxentityxxxxxfieldxxxxxdescriptionxxxxxwebLinkScriptxxxxxisGroupable";
			String params = "[\\\\\\\"GetServerStatus\\\\\\\"]";
			params = "[\\\\\\\"SearchQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"PROJECT\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE PROJECT.id='\\\" + row.getValue(\\\"projectFK\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";
			String params2 = "[\\\\\\\"\\\" + catalog + \\\"\\\\\\\",\\\\\\\"PROJECT\\\\\\\",\\\\\\\"id\\\\\\\",\\\\\\\"\\\" + row.getValue(\\\"projectFK\\\") + \\\"\\\\\\\"]";
			String webLinkScript = ""
									+"import net.hep.ami.jdbc.WebLink;"
									+"\\nimport net.hep.ami.jdbc.Querier;"
									+"\\nimport net.hep.ami.jdbc.SimpleQuerier;"
									+"\\nimport net.hep.ami.jdbc.Row;"
									+"\\nimport net.hep.ami.jdbc.reflexion.SchemaSingleton;"
									+"\\nString label = SchemaSingleton.getFieldNames(catalog,\\\"PROJECT\\\").toString();"
									+"\\n//try{ throw new java.lang.Exception(\\\"error test\\\");SimpleQuerier querier = new SimpleQuerier(catalog);}catch(java.lang.Exception e){label=e.getMessage();}"
									+"\\n//String label = querier.executeSQLQuery(\\\"SELECT name FROM PROJECT WHERE id='\\\" + row.getValue(\\\"projectFK\\\") + \\\"'\\\").getAll().get(0).getValue(0);"
									+"\\nwebLink = new WebLink();"
									+"\\nwebLink.newLinkProperties().setLabel(\\\"project table\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"table\\\").setTitle(\\\"PROJECT\\\");"
									+"\\nwebLink.newLinkProperties().setLabel(label).setCtrl(\\\"elementInfo\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params2+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"arrows-alt\\\").setTitle(\\\"PROJECT\\\");"
									+"\\nreturn webLink;";
			String values = "testxxxxxDATASETxxxxxprojectFKxxxxxthis is a test descriptionxxxxx"+webLinkScript+"xxxxx1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\"xxxxx\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			testFail = true;
		}


		try 
		{
			String fields = "catalog;entity;field;description;rank";
			String values = "test;DATASET;name;this is a test description;1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
		

		String[] testTables = {"PROJECT","DATASET","DATASET_FILE_BRIDGE","DATASET_PARAM","DATASET_TYPE","FILE","FILE_TYPE"};
		for (int i = 0; i < testTables.length; i++) {
			try 
			{
				String fields = "catalog;entity;field;isCreatedBy";
				String values = "test;" + testTables[i] +";createdBy;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			try 
			{
				String fields = "catalog;entity;field;isModifiedBy";
				String values = "test;" + testTables[i] +";modifiedBy;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				testFail = true;
			}
			try 
			{
				String fields = "catalog;entity;field;isCreated";
				String values = "test;" + testTables[i] +";created;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			try 
			{
				String fields = "catalog;entity;field;isModified";
				String values = "test;" + testTables[i] +";modified;1";
				String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

		}

		try 
		{
			String fields = "catalog;entity;field;isPrimary";
			String values = "test;FILE_VIEW;id;1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		try 
		{
			String fields = "catalog;entity;field;rank";
			String values = "test;FILE_VIEW;fileName;1";
			String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		try 
		{
			String fields = "catalog;entity;field;rank";
			String values = "test;FILE_VIEW;datasetName;2";
			String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
		try 
		{
			String fields = "catalog;entity;field;rank";
			String values = "test;FILE_VIEW;projectName;3";
			String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

			CommandSingleton.executeCommand(command, false);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			testFail = true;
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
			testFail = true;
		}

		testDB.commitAndRelease();

		/*-----------------------------------------------------------------*/

		System.out.println("Testing add commands");

		/*-----------------------------------------------------------------*/


		CatalogSingleton.reload(true);

		/*-----------------------------------------------------------------*/

		String command = "SearchQuery -catalog=\"self\" -entity=\"router_catalog\" -mql=\"SELECT externalCatalog, jdbcUrl WHERE externalCatalog LIKE '%%' \" ";

		try
		{
			CommandSingleton.executeCommand(command, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
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
			arguments.put("values", "AMI;This is an AMI demonstration project");
			CommandSingleton.executeCommand("AddElement", arguments, false);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "PROJECT");
			arguments.put("separator", ";");
			arguments.put("fields", "name;description");
			arguments.put("values", "AMI2;This is an other AMI demonstration project");
			CommandSingleton.executeCommand("AddElement", arguments, false);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
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
			CommandSingleton.executeCommand("AddElement", arguments, false);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "DATASET_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;PROJECT.name;description");
			arguments.put("values", "B;AMI;This is a test");
			CommandSingleton.executeCommand("AddElement", arguments, false);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		/*-----------------------------------------------------------------*/

		for (int i = 0; i < cptMax; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET");
				arguments.put("separator", ";");
				arguments.put("fields", "name;DATASET_TYPE.name;PROJECT.name");
				arguments.put("values", "dataset_" + i + ";A;AMI");
				CommandSingleton.executeCommand("AddElement", arguments, false);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}
		}

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "DATASET");
			arguments.put("separator", ";");
			arguments.put("fields", "name;DATASET_TYPE.name{DATASET.typeFK};PROJECT.name{DATASET.typeFK};PROJECT.name{DATASET.projectFK}");
			arguments.put("values", "test_multi_project_1;A;AMI;AMI2");
			CommandSingleton.executeCommand("AddElement", arguments, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
		
		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "DATASET");
			arguments.put("separator", ";");
			arguments.put("fields", "name;DATASET_TYPE.name{DATASET_TYPE.id};PROJECT.name{DATASET_TYPE.id};PROJECT.name{!DATASET_TYPE.id}");
			arguments.put("values", "test_multi_project_2;A;AMI;AMI2");
			CommandSingleton.executeCommand("AddElement", arguments, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
		/*-----------------------------------------------------------------*/

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "FILE_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;description;PROJECT.name");
			arguments.put("values", "TEXT;This is a test;AMI");
			CommandSingleton.executeCommand("AddElement", arguments, false);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "FILE_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;description;PROJECT.name");
			arguments.put("values", "BINARY;This is a test;AMI");
			CommandSingleton.executeCommand("AddElement", arguments, false);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		try
		{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "FILE_TYPE");
			arguments.put("separator", ";");
			arguments.put("fields", "name;description;PROJECT.name");
			arguments.put("values", "BINARY;This is a test;AMI2");
			CommandSingleton.executeCommand("AddElement", arguments, false);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
		
		/*-----------------------------------------------------------------*/

		for (int i = 0; i < cptMax; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "FILE");
				arguments.put("separator", ";");
				arguments.put("fields", "name;FILE_TYPE.name;PROJECT.name;size");
				arguments.put("values", "file_" + i + ";BINARY;AMI;"+(i*10));
				CommandSingleton.executeCommand("AddElement", arguments, false);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}
		}

		for (int i = 0; i < cptMax; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "FILE");
				arguments.put("separator", ";");
				arguments.put("fields", "name;FILE_TYPE.name;PROJECT.name");
				arguments.put("values", "file_ami2_" + i + ";BINARY;AMI2");
				CommandSingleton.executeCommand("AddElement", arguments, false);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}
		}

		for (int i = 0; i < cptMax; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET_FILE_BRIDGE");
				arguments.put("separator", ";");
				//arguments.put("fields", "FILE.name;DATASET.name;FILE_TYPE.name{FILE.typeFK}");
				arguments.put("fields", "FILE.name;DATASET.name;FILE_TYPE.name");
				//arguments.put("values", "file_" + i + ";dataset_" + i +";TEXT");
				arguments.put("values", "file_" + i + ";dataset_" + i +";BINARY");
				//arguments.put("fields", "FILE.name;DATASET.name");
				//arguments.put("values", "file_" + i + ";dataset_" + i +"");
				CommandSingleton.executeCommand("AddElement", arguments, false);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}
		}
		/*-----------------------------------------------------------------*/

		System.out.println("Testing select commands");

		/*-----------------------------------------------------------------*/

		String commandTest = "SearchQuery -catalog=\"test\" -entity=\"FILE_VIEW\" -mql=\"SELECT * WHERE id > 0 \" ";

		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"FILE_VIEW\" -mql=\"SELECT * \" ";

		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE 'test'=DATASET.name ORDER BY test.DATASET.name\" ";

		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE FILE.name='file_1' ORDER BY DATASET.name\" ";

		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE FILE.name='file_1' AND PROJECT.name='AMI' AND DATASET_TYPE.name='A' ORDER BY DATASET.name\" ";

		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [FILE.name='file_1'] AND [PROJECT.name='AMI' AND DATASET_TYPE.name='A'] ORDER BY DATASET.name\" ";

		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [PROJECT.name='AMI2'] AND [PROJECT.name='AMI'] ORDER BY DATASET.name\" ";

		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [PROJECT.name{DATASET.typeFK}='AMI'] AND [PROJECT.name{DATASET.projectFK}='AMI2'] ORDER BY DATASET.name\" ";

		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [[PROJECT.name{DATASET.typeFK}='AMI'] AND [PROJECT.name{DATASET.projectFK}='AMI2']] OR [FILE.name='file_1'] ORDER BY DATASET.name, DATASET.id\" ";

		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		//commandTest = "GetElementInfo -catalog=\"test\" -entity=\"DATASET\" -primaryFieldName=\"name\" -primaryFieldValue=\"test_multi_project_1\" -GUI=\"yes\" -expandedLinkedElements=\"\"";

		/**/ if(jdbcUrl.contains("jdbc:mysql")) {
			commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -raw=\"CALL AMI_TEST('dataset_1');\"";
		}
		else if(jdbcUrl.contains("jdbc:mariadb")) {
			commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -raw=\"CALL AMI_TEST('dataset_1');\"";
		}
		else if(jdbcUrl.contains("jdbc:oracle")) {
			commandTest = "GetSessionInfo";
		}
		else if(jdbcUrl.contains("jdbc:postgresql")) {
			commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -raw=\"SELECT AMI_TEST('dataset_1');\"";
		}
		else {
			throw new Exception("only `mysql`, `mariadb`, `oracle` and `postgresql` are supported");
		}
		try
		{
			//System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT COUNT(*) WHERE [[PROJECT.name{DATASET.typeFK}='AMI'] AND [PROJECT.name{DATASET.projectFK}='AMI2']] OR [FILE.name='file_1'] \" ";
		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DATASET.name,FILE.name WHERE FILE.name='file_1' ORDER BY DATASET.name,FILE.name\" ";

		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
		/*-----------------------------------------------------------------*/

		System.out.println("Testing update commands");

		/*-----------------------------------------------------------------*/

		for (int i = 0; i < cptMax-1; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET_FILE_BRIDGE");
				arguments.put("separator", ";");
				//arguments.put("fields", "DATASET_FILE_BRIDGE.datasetFK");
				//arguments.put("values", (i + 1) + "" );
				arguments.put("fields", "DATASET.name{DATASET_FILE_BRIDGE.datasetFK}");
				arguments.put("values", "dataset_" + (i + 1) );
				arguments.put("keyFields", "FILE.name;DATASET.name");
				arguments.put("keyValues", "file_" + i + ";dataset_" + i +"");
				//CommandSingleton.executeCommand("UpdateElements", arguments, false);
			}
			catch(Exception e)
			{
				System.out.println("xxx"+e.getMessage());
				testFail = true;
			}
		}

			try
			{
			arguments.clear();
			arguments.put("catalog", "test");
			arguments.put("entity", "DATASET");
			arguments.put("separator", ";");
			arguments.put("fields", "name;valid");
			arguments.put("values", "dataset_test_" + (cptMax-1) +";0");
			arguments.put("keyFields", "id");
			arguments.put("keyValues", "" + (cptMax-1));
			CommandSingleton.executeCommand("UpdateElements", arguments, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
/*
			BufferedImage bImage = ImageIO.read(new File("/Users/jfulach/Desktop/WAN/minus.png"));
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(bImage, "jpg", bos );
	        byte [] data = bos.toByteArray();
	        System.out.println(data.toString());
	        StringBuilder sb = new StringBuilder(data.length * 2);
	        for(byte b: data)
	           sb.append(String.format("%02x", b));
	        System.out.println(sb.toString());
*/

		/*-----------------------------------------------------------------*/

		System.out.println("Testing remove commands");

		/*-----------------------------------------------------------------*/

		for (int i = 0; i < cptMax/2; i++) {
			try
			{
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET_FILE_BRIDGE");
				arguments.put("separator", ";");
				arguments.put("keyFields", "FILE.name;DATASET.name;PROJECT.name");
				arguments.put("keyValues", "file_" + i + ";dataset_" + (i + 1) +";AMI");
				CommandSingleton.executeCommand("RemoveElements", arguments, false);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}
		}

		/*-----------------------------------------------------------------*/

		System.out.println("Testing GetServerStatus command");

		/*-----------------------------------------------------------------*/

		CommandSingleton.executeCommand("GetServerStatus",false);

		//System.out.println(CommandSingleton.executeCommand("GetServerStatus",false).replace(">", ">\n"));

		/*-----------------------------------------------------------------*/

		System.out.println("End of tests");

		if (testFail)
		{
			System.out.println("Some tests failed");
			System.exit(1);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}

