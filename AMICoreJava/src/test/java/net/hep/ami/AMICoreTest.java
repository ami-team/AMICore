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
		boolean doCreateAndFill = true;
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

		if(!(test_catalog.toLowerCase().contains("test_") || true))
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
			else if(jdbcUrl.contains("jdbc:sqlite")) {
				path = "/sql/testDB-sqlite.sql";
			}
			else if(jdbcUrl.contains("jdbc:h2")) {
			path = "/sql/testDB-h2.sql";
			}
			else {
				throw new Exception("only `mysql`, `mariadb`, `oracle`, `postgresql`, `sqlite` and `h2` are supported");
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
										+ "\\\\\\\"defaultPrimaryField\\\\\\\": \\\\\\\"id\\\\\\\",\\\\\\\"more\\\\\\\": {\\\\\\\"summary\\\\\\\": ["
										+" {\\\\\\\"name\\\\\\\": \\\\\\\"number of files\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"FILE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"id\\\\\\\", \\\\\\\"constraints\\\\\\\": \\\\\\\"{`test`.`DATASET_FILE_BRIDGE`.`fileFK`}\\\\\\\", \\\\\\\"type\\\\\\\": 0},"
										+" {\\\\\\\"name\\\\\\\": \\\\\\\"total size\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"FILE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"size\\\\\\\", \\\\\\\"constraints\\\\\\\": \\\\\\\"{`test`.`DATASET_FILE_BRIDGE`.`fileFK`}\\\\\\\", \\\\\\\"type\\\\\\\": 1},"
										+" {\\\\\\\"name\\\\\\\": \\\\\\\"average size\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"FILE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"size\\\\\\\", \\\\\\\"constraints\\\\\\\": \\\\\\\"{`test`.`DATASET_FILE_BRIDGE`.`fileFK`}\\\\\\\", \\\\\\\"type\\\\\\\": 2},"
										+" {\\\\\\\"name\\\\\\\": \\\\\\\"sum dataset id\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"id\\\\\\\", \\\\\\\"type\\\\\\\": 1}"

										+"]},\\\\\\\"criteria\\\\\\\": ["



										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"valid\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"valid\\\\\\\", \\\\\\\"type\\\\\\\": 4, \\\\\\\"more\\\\\\\":{\\\\\\\"auto_open\\\\\\\": true, \\\\\\\"inclusive\\\\\\\": true, \\\\\\\"simple_search\\\\\\\": false,\\\\\\\"init_value\\\\\\\": \\\\\\\"VALID\\\\\\\", \\\\\\\"off\\\\\\\": [\\\\\\\"ALL\\\\\\\"], \\\\\\\"on\\\\\\\": \\\\\\\"ALL\\\\\\\"}}, "

										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"type\\\\\\\": 0, \\\\\\\"more\\\\\\\": {\\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": true, \\\\\\\"order\\\\\\\": \\\\\\\"ASC\\\\\\\"}}, "


										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"param1\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET_PARAM\\\\\\\", \\\\\\\"key_field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"stringValue\\\\\\\", \\\\\\\"type\\\\\\\": 7, \\\\\\\"more\\\\\\\": {\\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": true, \\\\\\\"order\\\\\\\": \\\\\\\"ASC\\\\\\\"}}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"param2\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET_PARAM\\\\\\\", \\\\\\\"key_field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"type\\\\\\\", \\\\\\\"type\\\\\\\": 9, \\\\\\\"more\\\\\\\": {\\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": true, \\\\\\\"order\\\\\\\": \\\\\\\"ASC\\\\\\\"}}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"param3\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET_PARAM\\\\\\\", \\\\\\\"key_field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"stringValue\\\\\\\", \\\\\\\"type\\\\\\\": 8, \\\\\\\"more\\\\\\\": {\\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": true, \\\\\\\"order\\\\\\\": \\\\\\\"ASC\\\\\\\"}}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"param4\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET_PARAM\\\\\\\", \\\\\\\"key_field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"type\\\\\\\", \\\\\\\"type\\\\\\\": 10, \\\\\\\"more\\\\\\\": {\\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": true, \\\\\\\"order\\\\\\\": \\\\\\\"ASC\\\\\\\"}}, "




										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"FILE.name\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"FILE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"type\\\\\\\": 1, \\\\\\\"more\\\\\\\": { \\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": false, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\", \\\\\\\"constraints\\\\\\\" : [\\\\\\\"\\\" + catalog + \\\".DATASET_FILE_BRIDGE.datasetFK\\\\\\\"]}}, "
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"FILE.size\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"FILE\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"size\\\\\\\", \\\\\\\"type\\\\\\\": 2, \\\\\\\"more\\\\\\\": { \\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": false, \\\\\\\"order\\\\\\\" : \\\\\\\"ASC\\\\\\\", \\\\\\\"constraints\\\\\\\" : [\\\\\\\"\\\" + catalog + \\\".DATASET_FILE_BRIDGE.datasetFK\\\\\\\"]}}, "

										//todo
										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"created\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"DATASET\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"created\\\\\\\", \\\\\\\"type\\\\\\\": 3, \\\\\\\"more\\\\\\\": {\\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": true, \\\\\\\"order\\\\\\\": \\\\\\\"ASC\\\\\\\"}}, "

										+ "{\\\\\\\"name\\\\\\\": \\\\\\\"PROJECT\\\\\\\", \\\\\\\"catalog\\\\\\\": \\\\\\\"\\\" + catalog + \\\"\\\\\\\", \\\\\\\"entity\\\\\\\": \\\\\\\"PROJECT\\\\\\\", \\\\\\\"field\\\\\\\": \\\\\\\"name\\\\\\\", \\\\\\\"type\\\\\\\": 0, \\\\\\\"more\\\\\\\": {\\\\\\\"auto_open\\\\\\\": false,\\\\\\\"simple_search\\\\\\\": true, \\\\\\\"order\\\\\\\": \\\\\\\"ASC\\\\\\\"}}"

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



			String imageBase64  = "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";

			String photoJson = "{\"hidden\":false,\"adminOnly\":false,\"crypted\":false,\"primary\":false,\"readable\":false,\"automatic\":false,\"created\":false,\"createdBy\":false,\"modified\":false,\"modifiedBy\":false,\"statable\":false,\"groupable\":false,\"displayable\":true,\"base64\":true,\"mime\":\"image/vnd.sealedmedia.softseal.jpg\",\"ctrl\":\"mediaviewer\",\"webLinkScript\":null}";

			try
			{
				//rank is 0 by default at insertion
				String fields = "catalog;entity;field;description;json";
				String jsonString = photoJson;
				String values = "test;DATASET_TYPE;photo;this is a test description;" + Utility.escapeJavaString(jsonString);
				command = "AddElement -catalog=\"self\" -entity=\"router_field\" -separator=\";\" -fields=\"" + fields + "\" -values=\"" + values + "\"";

				CommandSingleton.executeCommand(command, false);
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}


			try
			{
				System.out.println("init add dataset type");
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET_TYPE");
				arguments.put("separator", ";");
				arguments.put("fields", "name;PROJECT.name;description;photo");
				arguments.put("values", "A;AMI;This is a test;" + imageBase64);
				CommandSingleton.executeCommand("AddElement", arguments, false);
				System.out.println("done add dataset type");

			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			try
			{
				System.out.println("init add dataset type");
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET_TYPE");
				arguments.put("separator", ";");
				arguments.put("keyFields", "name;PROJECT.name");
				arguments.put("keyValues", "A_1;AMI");
				arguments.put("fields", "name;PROJECT.name;description;photo");
				arguments.put("values", "A_1;AMI;This is a test;" + imageBase64);
				CommandSingleton.executeCommand("AddUpdateElement", arguments, false);
				System.out.println("done add dataset type");

			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				testFail = true;
			}

			try
			{
				System.out.println("init add dataset type");
				arguments.clear();
				arguments.put("catalog", "test");
				arguments.put("entity", "DATASET_TYPE");
				arguments.put("separator", ";");
				arguments.put("keyFields", "name;PROJECT.name");
				arguments.put("keyValues", "A_1;AMI");
				arguments.put("fields", "name;PROJECT.name;description;photo");
				arguments.put("values", "A_1;AMI;This is a test update;" + imageBase64);
				CommandSingleton.executeCommand("AddUpdateElement", arguments, false);
				System.out.println("done add dataset type");

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
				arguments.put("fields", "name;PROJECT.name;description;photo");
				arguments.put("values", "B;AMI;This is a test;" + imageBase64);
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
		//

		//String commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -sql=\"SELECT * FROM `DATASET` WHERE `id` > 0  \" ";

		//String commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -sql=\"SELECT * FROM `DATASET` WHERE `id` > 0 and `created` < STR_TO_DATE('2019-10-28','%Y-%m-%d')  \" ";

		//String commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -sql=\"SELECT * FROM `DATASET` WHERE `id` > 0 and `created` > TIMESTAMP('2019-10-28','YYYY-MM-DD')  \" ";

		//String commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE `id` > 0 and `created` > TIMESTAMP('2017-10-28 13:01:01.001','YYYY-MM-DD HH24:MI:SSFF3')  \" ";
		//String commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE `id` > 0 and `created` > TIMESTAMP('2017-10-28 13:01:01','YYYY-MM-DD HH24:MI:SS')  \" ";
		String commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT * WHERE `id` > 0 and `created` > TIMESTAMP('2017-10-28 13:01:01.001','YYYY-MM-DD HH24:MI:SS.FF3')  \" ";

		System.out.println(commandTest);
		try
		{
			//CommandSingleton.executeCommand(commandTest, false);
			System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
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
		else if(jdbcUrl.contains("jdbc:sqlite")) {
			commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -raw=\"SELECT 1 AS AMI_TEST;\"";
		}
		else if(jdbcUrl.contains("jdbc:h2")) {
			commandTest = "SearchQuery -catalog=\"test\" -entity=\"DATASET\" -raw=\"SELECT 1 AS AMI_TEST;\"";
		}
		else {
			throw new Exception("only `mysql`, `mariadb`, `oracle`, `postgresql` and `sqlite` are supported");
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

		commandTest = "BrowseQuery -catalog=\"test\" -entity=\"DATASET\" -mql=\"SELECT COUNT(DISTINCT(FILE.name{DATASET_FILE_BRIDGE.datasetFK})) \"";

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

        commandTest = "BrowseQuery -catalog=\"te.*\" -entity=\"DATASET\" -mql=\"SELECT dataset.name\"";

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

		//commandTest ="UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -separator=\";\" -fields=\"json\" -values=\"{}\" -keyFields=\"id\" -keyValues=\"1\"";

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



		System.out.println("Testing GetJSONSchema command");

		/*-----------------------------------------------------------------*/

		//CommandSingleton.executeCommand("GetJSONSchema -catalog=\"test\"",false);

		System.out.println(CommandSingleton.executeCommand("GetJSONSchema -catalog=\"test\"",false).replace(">", ">\n"));



		commandTest = "BrowseQuery -catalog=\"test\" -entity=\"DATASET_TYPE\" -mql=\"SELECT * WHERE name='A' \"";

		System.out.println(commandTest);
		try
		{
			System.out.println(CommandSingleton.executeCommand(commandTest, false).replace(">", ">\n"));
			//CommandSingleton.executeCommand(commandTest, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			testFail = true;
		}


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

