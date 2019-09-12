package net.hep.ami;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.junit.jupiter.api.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.parser.*;
//import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("all")
public class AMICoreTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		//System.setProperty("ami.conffile", "/Users/jodier/AMI_PostgreSQL.xml");

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
		int cptMax = 10;
		int cptMax2 = 5;
		boolean doCreateAndFill = false;
		String path;
		String test_catalog = ConfigSingleton.getProperty("test_catalog");
		String test_schema = ConfigSingleton.getProperty("test_schema");
		String test_url = ConfigSingleton.getProperty("test_url");
		String test_user = ConfigSingleton.getProperty("test_user");
		String test_pass = ConfigSingleton.getProperty("test_pass");
		String jdbcUrl = test_url;
		Map<String, String> arguments = new HashMap<String, String>();

		if(System.getProperty("ami.integration") == null)
		{
			System.out.println("skipping integration");

			return;
		}

		if(!test_catalog.toLowerCase().contains("test_"))
		{
			System.out.println("skipping integration not a test router");

			return;
		}



		System.out.println("Init tests"); 

		/*-----------------------------------------------------------------*/

		System.out.println("Setting up router database: " +  ConfigSingleton.getProperty("router_url"));

		/*-----------------------------------------------------------------*/


		if(doCreateAndFill)
		{
			try
			{
				/*-------------------------------------------------------------*/
				/* SETUP SERVER CONFIG                                         */
				/*-------------------------------------------------------------*/

				Router db = new Router();

				try
				{
					System.out.println("init create");
					db.create();

					db.fill(ConfigSingleton.getProperty("router_schema"));

					db.commitAndRelease();
					System.out.println("done create");
				}
				catch(Exception e)
				{
					db.rollbackAndRelease();

					System.out.println(e.getMessage());
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




			try 
			{
				String testCustom = "{\"DATASET\":{\"x\":280,\"y\":40,\"color\":\"#72DE4C\"},\"DATASET_FILE_BRIDGE\":{\"x\":280,\"y\":240,\"color\":\"#CBCC5A\"},\"DATASET_GRAPH\":{\"x\":20,\"y\":40,\"color\":\"#00CC52\"},\"DATASET_PARAM\":{\"x\":20,\"y\":210,\"color\":\"#00CC01\"},\"DATASET_TYPE\":{\"x\":540,\"y\":40,\"color\":\"#19CE57\"},\"FILE\":{\"x\":280,\"y\":410,\"color\":\"#D4E03F\"},\"FILE_TYPE\":{\"x\":540,\"y\":410,\"color\":\"#E5A44C\"},\"PROJECT\":{\"x\":540,\"y\":240,\"color\":\"#F5743B\"},\"FILE_VIEW\":{\"x\":20,\"y\":470,\"color\":\"#C3DB2E\"}}";
				String fields = "externalCatalog;internalCatalog;internalSchema;jdbcUrl;user;pass;json";
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

			SimpleQuerier testDB = new SimpleQuerier("test", "admin", "UTC", true, false);

			/*-----------------------------------------------------------------*/
			/* SELECT PROFILE                                                  */
			/*-----------------------------------------------------------------*/



			/**/ if(jdbcUrl.contains("jdbc:mysql")) {
				path = "/sql/testDB-mysql.sql";
				/*try {
					testDB.getStatement().execute("GRANT ALL ON " + test_catalog + ".* TO 'ami_test_router'@'%';");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}*/

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
				String command = "AddUser -amiLogin=\"demo\" -amiPassword=\"pipopipo\" -firstName=\"Jack\" -lastName=\"White\" -email=\"ami@lpsc.in2p3.fr\" -agree ";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			try 
			{
				String fields = "catalogxxxxxentityxxxxxfieldxxxxxdescriptionxxxxxjson";
				String params = "[\\\\\\\"BrowseQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"DATASET\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE PROJECT.name{test.DATASET.projectFK}='\\\" + row.getValue(\\\"test.PROJECT.name\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";
				String params2 = "[\\\\\\\"BrowseQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"DATASET\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT DATASET.NAME, DATASET.ID, PROJECT.NAME{test.DATASET.projectFK} AS `PROJECT.NAME` WHERE valid=1 AND PROJECT.name{test.DATASET.projectFK}='\\\" + row.getValue(\\\"test.PROJECT.name\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";
				String params3 = "[\\\\\\\"BrowseQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"DATASET\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT DATASET.NAME, DATASET.ID, DATASET.VALID, PROJECT.NAME{test.DATASET.projectFK} AS `PROJECT.NAME` WHERE valid=0 AND PROJECT.name{test.DATASET.projectFK}='\\\" + row.getValue(\\\"test.PROJECT.name\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";

				String webLinkScript = ""
										+"import net.hep.ami.jdbc.WebLink;"
										+"\\nwebLink = new WebLink();"
										+"\\nif(rowSet.isANameOrLabel(\\\"test.PROJECT.name\\\"))"
										+"\\n{"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"datasets\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"coffee\\\").setTitle(\\\"DATASET\\\");"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"valid\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params2+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"table\\\").setTitle(\\\"DATASET\\\");"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"invalid\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params3+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"table\\\").setTitle(\\\"DATASET\\\");"

										+"\\n	webLink.newLinkProperties().setLabel(\\\"test\\\").setCtrl(\\\"search\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\"\\\").setSettings(\\\"{\\\\\\\"name\\\\\\\" : \\\\\\\"Search dataset\\\\\\\",\\\\\\\"defaultCatalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\","
										+ "\\\\\\\"defaultEntity\\\\\\\": \\\\\\\"DATASET\\\\\\\","
										+ "\\\\\\\"defaultSelect\\\\\\\": \\\\\\\"name AS test, PROJECT.name{DATASET.projectFK} AS project, DATASET.id, DATASET.valid\\\\\\\","
										+ "\\\\\\\"defaultPrimaryField\\\\\\\": \\\\\\\"id\\\\\\\",\\\\\\\"criterias\\\\\\\": ["
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"valid\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"valid\\\\\\\", \\\\\\\"type\\\\\\\": 4, \\\\\\\"states\\\\\\\": {\\\\\\\"on\\\\\\\" : \\\\\\\"1\\\\\\\", \\\\\\\"off\\\\\\\": \\\\\\\"ALL\\\\\\\"}, \\\\\\\"select\\\\\\\": \\\\\\\"1\\\\\\\", \\\\\\\"inclusive\\\\\\\": true, \\\\\\\"auto_open\\\\\\\": true},"
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"type\\\\\\\": 0, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\"}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"param\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET_PARAM\\\\\\\", \\\\\\\"keyField\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"valueField\\\\\\\": \\\\\\\"type\\\\\\\", \\\\\\\"type\\\\\\\": 6, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\",\\\\\\\"mode\\\\\\\": \\\\\\\"advanced\\\\\\\"}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"FILE.name\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"FILE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"type\\\\\\\": 1, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\", \\\\\\\"constraints\\\\\\\" : [{\\\\\\\"operator\\\\\\\" : \\\\\\\"\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET_FILE_BRIDGE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"datasetFK\\\\\\\"}]}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"FILE.size\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"FILE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"size\\\\\\\", \\\\\\\"type\\\\\\\": 2, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\", \\\\\\\"constraints\\\\\\\" : [{\\\\\\\"operator\\\\\\\" : \\\\\\\"\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET_FILE_BRIDGE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"datasetFK\\\\\\\"}]}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"created\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"created\\\\\\\", \\\\\\\"type\\\\\\\": 3, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\"}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"PROJECT\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"PROJECT\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"type\\\\\\\": 0, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\", \\\\\\\"constraints\\\\\\\" : [{\\\\\\\"operator\\\\\\\" : \\\\\\\"\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"projectFK\\\\\\\"}] }"
										+ "]}\\\").setIcon(\\\"coffee\\\").setTitle(\\\"Search Datataset\\\");"

										+"\\n}"
										+"\\nif(rowSet.isANameOrLabel(\\\"test.PROJECT.id\\\"))"
										+"\\n{"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"id test\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"coffee\\\").setTitle(\\\"DATASET\\\");"
										+"\\n}"
										+"\\nreturn webLink;";

				String jsonString = "{\"readable\":true,\"groupable\":true,\"webLinkScript\":\"" + webLinkScript + "\"}";
				String values = "testxxxxxPROJECTxxxxxnamexxxxxthis is a test descritionxxxxx" + Utility.escapeJavaString(jsonString) +"";
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
				String fields = "catalogxxxxxentityxxxxxfieldxxxxxdescriptionxxxxxjson";
				String params = "[\\\\\\\"BrowseQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"FILE\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE DATASET.id{test.DATASET_FILE_BRIDGE.fileFK}='\\\" + row.getValue(\\\"test.DATASET.id\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";

				String webLinkScript = ""
										+"import net.hep.ami.jdbc.WebLink;"
										+"\\nwebLink = new WebLink();"
										+"\\nif(rowSet.isANameOrLabel(\\\"test.DATASET.id\\\"))"
										+"\\n{"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"files\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"coffee\\\").setTitle(\\\"FILE\\\").setClass(\\\"badge badge-info\\\");"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"files\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"coffee\\\").setTitle(\\\"FILE\\\").setClass(\\\"badge badge-success\\\");"
										+"\\n}"
										+"\\nif(rowSet.isANameOrLabel(\\\"test.DATASET.valid\\\"))"
										+"\\n{"
										+"\\n	switch(row.getValue(\\\"test.DATASET.valid\\\"))"
										+"\\n	{"
										+"\\n		case [\\\"1\\\"]:"
										+"\\n		webLink.setClass(\\\"badge badge-success w-100 d-block\\\"); "
										+"\\n		break;"
										+"\\n		case [\\\"0\\\"]:"
										+"\\n		webLink.setClass(\\\"badge badge-info w-100  d-block\\\"); "
										+"\\n		break;"
										+"\\n	}"
										+"\\n}"
										+"\\nreturn webLink;";
				String jsonString = "{\"readable\":true,\"groupable\":true,\"webLinkScript\":\"" + webLinkScript + "\"}";
				String values = "testxxxxxDATASETxxxxxnamexxxxxthis is a test descritionxxxxx" + Utility.escapeJavaString(jsonString) +"";
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
				String fields = "catalogxxxxxentityxxxxxfieldxxxxxdescriptionxxxxxjson";
				String params = "[\\\\\\\"BrowseQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"DATASET\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE FILE.id{test.DATASET_FILE_BRIDGE.datasetFK}='\\\" + row.getValue(\\\"test.FILE.id\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";

				String webLinkScript = ""
										+"import net.hep.ami.jdbc.WebLink;"
										+"\\nwebLink = new WebLink();"
										+"\\nif(rowSet.isANameOrLabel(\\\"test.FILE.id\\\"))"
										+"\\n{"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"datasets\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"coffee\\\").setTitle(\\\"DATASET\\\");"
										+"\\n}"
										+"\\nreturn webLink;";

				String jsonString = "{\"readable\":true,\"groupable\":true,\"webLinkScript\":\"" + webLinkScript + "\"}";
				String values = "testxxxxxFILExxxxxnamexxxxxthis is a test descritionxxxxx" + Utility.escapeJavaString(jsonString) +"";
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
				String fields = "catalogxxxxxentityxxxxxfieldxxxxxdescriptionxxxxxjson";
				String params = "[\\\\\\\"GetServerStatus\\\\\\\"]";
				params = "[\\\\\\\"BrowseQuery -catalog=\\\\\\\\\\\\\\\"\\\" + catalog + \\\"\\\\\\\\\\\\\\\" -entity=\\\\\\\\\\\\\\\"PROJECT\\\\\\\\\\\\\\\" -mql=\\\\\\\\\\\\\\\"SELECT * WHERE PROJECT.id='\\\" + row.getValue(\\\"test.DATASET.projectFK\\\") + \\\"'\\\\\\\\\\\\\\\"  \\\\\\\"]";
				String params2 = "[\\\\\\\"\\\" + catalog + \\\"\\\\\\\",\\\\\\\"PROJECT\\\\\\\",\\\\\\\"id\\\\\\\",\\\\\\\"\\\" + row.getValue(\\\"test.DATASET.projectFK\\\") + \\\"\\\\\\\"]";
				String webLinkScript = ""
										+"import net.hep.ami.jdbc.WebLink;"
										+"\\nimport net.hep.ami.jdbc.Querier;"
										+"\\nimport net.hep.ami.jdbc.SimpleQuerier;"
										+"\\nimport net.hep.ami.jdbc.Row;"
										+"\\nimport net.hep.ami.jdbc.reflexion.SchemaSingleton;"
										+"\\nString test = SchemaSingleton.getFieldNames(catalog,\\\"PROJECT\\\").toString();"
										+"\\nQuerier querier = new SimpleQuerier(catalog);"
										+"\\nString label = querier.executeSQLQuery(\\\"PROJECT\\\",\\\"SELECT `name` FROM `PROJECT` WHERE `id`='\\\" + row.getValue(\\\"test.DATASET.projectFK\\\") + \\\"'\\\").getAll().get(0).getValue(0);"
										+"\\nquerier.rollbackAndRelease();"
										+"\\nwebLink = new WebLink();"
										+"\\nif(rowSet.isANameOrLabel(\\\"test.DATASET.id\\\"))"
										+"\\n	{"
										+"\\n	webLink.newLinkProperties().setLabel(\\\"project table\\\").setCtrl(\\\"table\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"table\\\").setTitle(\\\"PROJECT\\\");"
										+"\\n	webLink.newLinkProperties().setLabel(label).setCtrl(\\\"elementInfo\\\").setLocation(WebLink.Location.CONTAINER).setParams(\\\""+params2+"\\\").setSettings(\\\"{}\\\").setIcon(\\\"arrows-alt\\\").setTitle(\\\"PROJECT\\\");"
										+"\\n	}"
										+"\\nreturn webLink;";
				String jsonString = "{\"readable\":false,\"groupable\":true,\"webLinkScript\":\"" + webLinkScript + "\"}";
				String values = "testxxxxxDATASETxxxxxprojectFKxxxxxthis is a test descritionxxxxx" + Utility.escapeJavaString(jsonString) +"";
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
				String fields = "catalogxxxxxentityxxxxxfieldxxxxxdescriptionxxxxxjson";
				String webLinkScript = ""
										+"import net.hep.ami.jdbc.WebLink;"
										+"\\nwebLink = new WebLink().setUnitName(\\\"Byte\\\").setUnitFactor(\\\"\\\").setUnitBase(\\\"1024\\\").setHumanReadable(\\\"true\\\");"
										+"\\nreturn webLink;";

				String jsonString = "{\"statable\":true,\"groupable\":false,\"webLinkScript\":\"" + webLinkScript + "\"}";
				String values = "testxxxxxFILExxxxxsizexxxxxthis is a test descritionxxxxx" + Utility.escapeJavaString(jsonString) +"";
				String command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\"xxxxx\" -fields=\"" + fields + "\" -values=\"" + values + "\"";
				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			/*
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
			 */

			try 
			{
				//rank is 0 by default at insertion
				String fields = "catalog;entity;field;description;json";
				String jsonString = "{\"groupable\":true}";
				String values = "test;DATASET;valid;this is a test description;" + Utility.escapeJavaString(jsonString);
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

				String fields = "catalog;entity;description;json";
				String jsonString = "{\"bridge\":true}";
				String values = "test;DATASET_FILE_BRIDGE;this is a test description;" + Utility.escapeJavaString(jsonString);
				String command = "AddElement -catalog=\"self\" -entity=\"router_entity\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			String[] testTables = {"PROJECT","DATASET","DATASET_FILE_BRIDGE","DATASET_PARAM","DATASET_GRAPH","DATASET_TYPE","FILE","FILE_TYPE"};
			for (int i = 0; i < testTables.length; i++) {
				try 
				{
					String fields = "catalog;entity;field;json";
					String jsonString = "{\"createdBy\":true}";
					String values = "test;" + testTables[i] +";createdBy;" + Utility.escapeJavaString(jsonString);
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
					String fields = "catalog;entity;field;json";
					String jsonString = "{\"modifiedBy\":true}";
					String values = "test;" + testTables[i] +";modifiedBy;" + Utility.escapeJavaString(jsonString);
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
					String fields = "catalog;entity;field;json";
					String jsonString = "{\"created\":true}";
					String values = "test;" + testTables[i] +";created;" + Utility.escapeJavaString(jsonString);
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
					String fields = "catalog;entity;field;json";
					String jsonString = "{\"modified\":true}";
					String values = "test;" + testTables[i] +";modified;" + Utility.escapeJavaString(jsonString);
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
				String fields = "catalog;entity;field;json";
				String jsonString = "{\"groupable\":true}";
				String values = "test;DATASET_PARAM;type;" + Utility.escapeJavaString(jsonString);
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
				String fields = "catalog;entity;field;json";
				String jsonString = "{\"primary\":true}";
				String values = "test;FILE_VIEW;id;" + Utility.escapeJavaString(jsonString);
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
			System.out.println("Adding datasets");
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
			System.out.println("Adding files");
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
			System.out.println("Adding files/datasets relations");

			command = "AddElement -catalog=\"test\" -entity=\"DATASET_FILE_BRIDGE\" -separator=\";\" -fields=\"fileFK;DATASET.NAME\" -values=\"3;dataset_0\"";
			//System.out.println(command);
			try
			{
				CommandSingleton.executeCommand(command, false);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			boolean testInter = false;
			for (int i = 0; i < cptMax; i++) {
				try
				{
					boolean localTestInter = testInter;

					for (int j = 0; j < cptMax2; j++) {
						arguments.clear();
						arguments.put("catalog", "test");
						arguments.put("entity", "DATASET_FILE_BRIDGE");
						arguments.put("separator", ";");
						//arguments.put("fields", "FILE.name;DATASET.name;FILE_TYPE.name{FILE.typeFK}");
						arguments.put("fields", "FILE.name;DATASET.name;FILE_TYPE.name");
						//arguments.put("values", "file_" + i + ";dataset_" + i +";TEXT");
						arguments.put("values", "file_" + j + ";dataset_" + i +";BINARY");
						//arguments.put("fields", "FILE.name;DATASET.name");
						//arguments.put("values", "file_" + i + ";dataset_" + i +"");
						if(localTestInter)
						{
							CommandSingleton.executeCommand("AddElement", arguments, false);
						}

						localTestInter = ! localTestInter;
					}
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					testFail = true;
				}

				testInter = !testInter;
			}
			/*-----------------------------------------------------------------*/
			System.out.println("Adding datasets/datasets relations");
			for (int i = 0; i < cptMax; i++) {
				try
				{
					for (int j = 0; j < cptMax2; j++) {
						arguments.clear();
						arguments.put("catalog", "test");
						arguments.put("entity", "DATASET_GRAPH");
						arguments.put("separator", ";");

						arguments.put("fields", "DATASET.name{sourceFK};DATASET.name{destinationFK};comment");
						arguments.put("values", "dataset_" + j + ";dataset_" + i +";a comment");

						CommandSingleton.executeCommand("AddElement", arguments, false);
					}
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					testFail = true;
				}
			}

			/*-----------------------------------------------------------------*/
			System.out.println("Adding datasets params");
			for (int i = 0; i < cptMax; i++) {
				try
				{
					for (int j = 0; j < 3; j++) {
						arguments.clear();
						arguments.put("catalog", "test");
						arguments.put("entity", "DATASET_PARAM");
						arguments.put("separator", ";");

						String tpmType ="stringValue";
						arguments.put("fields", "DATASET.name{datasetFK};name;type;" + tpmType);
						arguments.put("values", "dataset_" + i + ";param_" + j + "_" + tpmType +";"  + tpmType +";value_" + j + "_" + i);

						CommandSingleton.executeCommand("AddElement", arguments, false);
					}
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
					for (int j = 0; j < 3; j++) {
						arguments.clear();
						arguments.put("catalog", "test");
						arguments.put("entity", "DATASET_PARAM");
						arguments.put("separator", ";");

						String tpmType ="intValue";
						arguments.put("fields", "DATASET.name{datasetFK};name;type;" + tpmType);
						arguments.put("values", "dataset_" + i + ";param_" + j + "_" + tpmType +";"  + tpmType +";" + j);

						CommandSingleton.executeCommand("AddElement", arguments, false);
					}
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
					for (int j = 0; j < 3; j++) {
						arguments.clear();
						arguments.put("catalog", "test");
						arguments.put("entity", "DATASET_PARAM");
						arguments.put("separator", ";");

						String tpmType ="floatValue";
						
						String sep = ".";
						if(jdbcUrl.contains("jdbc:oracle")) {
							sep = ",";
						}
						arguments.put("fields", "DATASET.name{datasetFK};name;type;" + tpmType);
						arguments.put("values", "dataset_" + i + ";param_" + j + "_" + tpmType +";"  + tpmType +";" + j + sep + "999");
						CommandSingleton.executeCommand("AddElement", arguments, false);
					}
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
					for (int j = 0; j < 2; j++) {
						arguments.clear();
						arguments.put("catalog", "test");
						arguments.put("entity", "DATASET_PARAM");
						arguments.put("separator", ";");

						String tpmType ="booleanValue";
						arguments.put("fields", "DATASET.name{datasetFK};name;type;" + tpmType);
						arguments.put("values", "dataset_" + i + ";param_" + j + "_" + tpmType +";"  + tpmType +";" + j + "");

						CommandSingleton.executeCommand("AddElement", arguments, false);
					}
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					testFail = true;
				}
			}

			if(!jdbcUrl.contains("jdbc:oracle")) {
				for (int i = 0; i < cptMax; i++) {
					try
					{
						for (int j = 0; j < 1; j++) {
							arguments.clear();
							arguments.put("catalog", "test");
							arguments.put("entity", "DATASET_PARAM");
							arguments.put("separator", ";");

							String tpmType ="timestampValue";
							arguments.put("fields", "DATASET.name{datasetFK};name;type;" + tpmType);

							arguments.put("values", "dataset_" + i + ";param_" + j + "_" + tpmType +";"  + tpmType +";2010-04-04 11:19:52.0");

							CommandSingleton.executeCommand("AddElement", arguments, false);
						}
					}
					catch(Exception e)
					{
						System.out.println(e.getMessage());
						testFail = true;
					}
				}
			}

			/*-----------------------------------------------------------------*/
			System.out.println("Testing update commands");

			/*-----------------------------------------------------------------*/

			for (int i = 2; i < 5; i++) {
				try
				{
					arguments.clear();
					arguments.put("catalog", "test");
					arguments.put("entity", "DATASET_FILE_BRIDGE");
					arguments.put("separator", ";");
					//arguments.put("fields", "DATASET_FILE_BRIDGE.datasetFK");
					//arguments.put("values", (i + 1) + "" );
					//arguments.put("fields", "FILE.name{DATASET_FILE_BRIDGE.fileFK}");
					//arguments.put("values", "file_" + (cptMax-1));
					arguments.put("fields", "comment");
					arguments.put("values", "test_" + i);
					//arguments.put("keyFields", "FILE.name{DATASET_FILE_BRIDGE.fileFK};DATASET.name{DATASET_FILE_BRIDGE.datasetFK}");
					//arguments.put("keyValues", "file_" + i + ";dataset_" + i +"");
					if(i == 2)
						arguments.put("where", "FILE.name{DATASET_FILE_BRIDGE.fileFK}='file_" + i + "' AND DATASET.name{DATASET_FILE_BRIDGE.datasetFK}='dataset_" + i +"'");
					else 
					{
						arguments.put("keyFields", "FILE.name{DATASET_FILE_BRIDGE.fileFK};DATASET.name{DATASET_FILE_BRIDGE.datasetFK}");
						arguments.put("keyValues", "file_" + i + ";dataset_" + i +"");
					}
					//System.out.println("FILE.name{DATASET_FILE_BRIDGE.fileFK};DATASET.name{DATASET_FILE_BRIDGE.datasetFK}");
					//System.out.println(CommandSingleton.executeCommand("UpdateElements", arguments, false).replace(">", ">\n"));
					CommandSingleton.executeCommand("UpdateElements", arguments, false);
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
					arguments.put("entity", "DATASET_FILE_BRIDGE");
					arguments.put("separator", ";");
					arguments.put("fields", "comment;FILE.name{DATASET_FILE_BRIDGE.fileFK}");
					arguments.put("values", "test_x;file_5");
					arguments.put("keyFields", "FILE.name{DATASET_FILE_BRIDGE.fileFK};DATASET.name{DATASET_FILE_BRIDGE.datasetFK}");
					arguments.put("keyValues", "file_0;dataset_0");

					//System.out.println(CommandSingleton.executeCommand("UpdateElements", arguments, false).replace(">", ">\n"));
					CommandSingleton.executeCommand("UpdateElements", arguments, false);
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

			/*-----------------------------------------------------------------*/
			System.out.println("Testing remove commands");

			/*-----------------------------------------------------------------*/

			for (int i = 0; i < 1; i++) {
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
		}


		System.out.println("Testing select commands");

		/*-----------------------------------------------------------------*/

		String commandTest = "SearchQuery -catalog=\"test\" -entity=\"FILE_VIEW\" -mql=\"SELECT * WHERE id > 0 \" ";
		System.out.println(commandTest);
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
		System.out.println(commandTest);
		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE 'dataset_0'=DATASET.name ORDER BY test.DATASET.name\" ";
		System.out.println(commandTest);
		try
		{
			CommandSingleton.executeCommand(commandTest, false);
			//System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE FILE.name{DATASET_FILE_BRIDGE.datasetFK}='file_"+(cptMax-1)+"' ORDER BY DATASET.name\" ";
		System.out.println(commandTest);
		try
		{
			CommandSingleton.executeCommand(commandTest, false);
			//System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE FILE.name{DATASET_FILE_BRIDGE.datasetFK}='file_"+(cptMax-1)+"' AND PROJECT.name='AMI' AND DATASET_TYPE.name='A' ORDER BY DATASET.name\" ";
		System.out.println(commandTest);
		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [FILE.name{DATASET_FILE_BRIDGE.datasetFK}='file_"+(cptMax-1)+"'] AND [PROJECT.name='AMI' AND DATASET_TYPE.name='A'] ORDER BY DATASET.name\" ";
		System.out.println(commandTest);
		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [PROJECT.name='AMI2'] AND [PROJECT.name='AMI'] ORDER BY DATASET.name\"";
		System.out.println(commandTest);
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
		System.out.println(commandTest);
		try
		{
		CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [[PROJECT.name{DATASET.typeFK}='AMI'] AND [PROJECT.name{DATASET.projectFK}='AMI2']] OR [FILE.name{DATASET_FILE_BRIDGE.datasetFK}='file_"+(cptMax-1)+"'] ORDER BY DATASET.name, DATASET.id\" ";
		System.out.println(commandTest);
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
		System.out.println(commandTest);
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
		System.out.println(commandTest);
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
		System.out.println(commandTest);
		try
		{
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}
	
		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET_FILE_BRIDGE\" -mql=\"SELECT * WHERE FILE.name{DATASET_FILE_BRIDGE.fileFK}='file_2' AND DATASET.name{DATASET_FILE_BRIDGE.datasetFK}='dataset_2'\" ";
		System.out.println(commandTest);
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
	
		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET_FILE_BRIDGE\" -mql=\"SELECT * WHERE comment='test_2'\" ";
		System.out.println(commandTest);
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

		commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DATASET.NAME, DATASET.ID, DATASET.VALID, PROJECT.NAME{test.DATASET.projectFK} AS `test.PROJECT.name` WHERE valid=0 AND PROJECT.name{test.DATASET.projectFK}='AMI' LIMIT 20 OFFSET 0 \"";
		//commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DATASET.NAME, DATASET.ID, DATASET.VALID, PROJECT.NAME{test.DATASET.projectFK} WHERE valid=0 AND PROJECT.name{test.DATASET.projectFK}='AMI' \"";
	
		System.out.println(commandTest);
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
	
		commandTest = "BrowseQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT FILE.name{DATASET_FILE_BRIDGE.datasetFK} AS test \"";
		
		System.out.println(commandTest);
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

		commandTest = "SearchQuery -GUI -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE [`test`.`FILE`.`size`{ `test`.`DATASET_FILE_BRIDGE`.`datasetFK`} < 10 OR `test`.`FILE`.`size`{ `test`.`DATASET_FILE_BRIDGE`.`datasetFK`} > 50]\" -limit=\"10\" -offset=\"0\" ";
		
		System.out.println(commandTest);
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
		
		//commandTest = "SearchQuery -GUI -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT `test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}  LIMIT 10 OFFSET 0\" ";
		
		//commandTest = "SearchQuery -GUI -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT `test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`} WHERE [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_1']  ORDER BY `test`.`FILE`.`name` ASC LIMIT 10 OFFSET 0\" ";
		
		//commandTest = "SearchQuery -GUI -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT `test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`} WHERE [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_2']  ORDER BY `test`.`FILE`.`name` ASC LIMIT 10 OFFSET 0\" ";
		
		
		//commandTest = "SearchQuery -GUI -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT `test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`} WHERE ([`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_1'] OR [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_2'])  ORDER BY `test`.`FILE`.`name` ASC LIMIT 10 OFFSET 0\" ";
		
		commandTest = "SearchQuery -GUI -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT `test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`} WHERE [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_1'] OR [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_2']  ORDER BY `test`.`FILE`.`name` ASC LIMIT 10 OFFSET 0\" ";
		
		

		
		//commandTest = "SearchQuery -GUI -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT `test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`} WHERE [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_1'] OR [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_2'] OR [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_3'] OR [`test`.`FILE`.`name`{`test`.`DATASET_FILE_BRIDGE`.`datasetFK`}='file_4'] ORDER BY `test`.`FILE`.`name` ASC LIMIT 10 OFFSET 0\" ";
		
		System.out.println(commandTest);
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
		
		
		commandTest = "BrowseQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT FILE.name{DATASET_FILE_BRIDGE.datasetFK} as `name`\"";

		System.out.println(commandTest);
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

		commandTest = "BrowseQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT DISTINCT FILE.name, id as `name`\"";

		System.out.println(commandTest);
		try
		{
			//System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
			//CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		//commandTest = "BrowseQuery -catalog=\"test\" -entity=\"PROJECT\" -mql=\"SELECT PROJECT.name, created AS test ORDER BY PROJECT.name DESC\" -limit=\"10\" ";
		commandTest = "BrowseQuery -catalog=\"test\" -entity=\"FILE\" -mql=\"SELECT FILE.name, created AS test ORDER BY FILE.name DESC\" -limit=\"10\" ";

		commandTest ="UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -separator=\";\" -fields=\"json\" -values=\"{xwxwx}\" -keyFields=\"id\" -keyValues=\"1\"";

		System.out.println(commandTest);
		try
		{
			System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
			CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}

		/*-----------------------------------------------------------------*/


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



		/*
		String commandTestDev = "BrowseQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * \" -limit=\"10\"";
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

		/*-----------------------------------------------------------------*/

		String commandTestDev = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * where name not like '%yyy%' AND not name like '%xxxx'  AND NOT id BETWEEN 100 AND 200 ORDER BY CREATED DESC \" -limit=\"10\"";
		//AND (name REGEXP '[^a-z]')
		System.out.println(commandTestDev);
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
		/*
		try
		{
			ConverterSingleton.convert("AMIXmlToJson.xsl", stringReader, stringWriter);

			data = stringWriter.toString();
			//System.out.println(data);

			ObjectMapper m_mapper = new ObjectMapper();
			HashMap test = m_mapper.readValue(data, HashMap.class);
			for(Iterator iterator = test.keySet().iterator(); iterator.hasNext();)
			{
				String key = (String)iterator.next();
				HashMap value = (HashMap)test.get(key);
				//System.out.println(key +  " xxx " + ((ArrayList)value.get("rowset")).toString());
				ArrayList list = (ArrayList)value.get("rowset");
				for (int cpt = 0; cpt < list.size(); cpt++)
				{
					ArrayList fieldsList = (ArrayList)((HashMap)(((ArrayList)((HashMap)list.get(cpt)).get("row")).get(0))).get("field");

					for (int cpt2 = 0; cpt2 < fieldsList.size(); cpt2++) {
						HashMap field = (HashMap) fieldsList.get(cpt2);

							System.out.println("field  " + field.get("@name"));

					}

					//System.out.println("aaaa " + ((HashMap)(((ArrayList)((HashMap)list.get(cpt)).get("row")).get(0))).toString());

				}
			}
			System.out.println("test  " + test.toString());

		}
		catch(Exception e)
		{
			System.out.println("err  " + e.getMessage());
			data = XMLTemplates.error(
				e.getMessage()
			);

		}
		*/

		//System.out.println(data);



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

