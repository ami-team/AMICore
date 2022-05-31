package net.hep.ami;

import net.hep.ami.jdbc.Querier;
import net.hep.ami.jdbc.Router;
import net.hep.ami.jdbc.Row;
import net.hep.ami.jdbc.SimpleQuerier;
import net.hep.ami.jdbc.query.sql.Tokenizer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("all")
public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
//		System.out.println(ConfigSingleton.getConfigFileName());

//		Router querier = new Router();

//		querier.create();
//		querier.fill();
//		querier.commit();

//		System.out.println(querier.executeSQLQuery("N/A","SELECT AMI_TIMESTAMP('2020-01-10 12:52:04.1')").toStringBuilder().toString().replace(">", ">\n"));

//		System.out.println(querier.executeSQLQuery("N/A","SELECT AMI_TIMESTAMP('2020-01-10 12:52:04.1234')").toStringBuilder().toString().replace(">", ">\n"));

//		System.out.println(querier.executeSQLQuery("N/A","SELECT AMI_DATE('2020-01-10')").toStringBuilder().toString().replace(">", ">\n"));

//		System.out.println(querier.executeSQLQuery("N/A","SELECT AMI_TIME('12:52:04')").toStringBuilder().toString().replace(">", ">\n"));

//		CommandSingleton.executeCommand("AddUser -amiLogin=\"yyy\" -amiPassword=\"yyy\" -firstName=\"yyy\" -lastName=\"yyy\" -email=\"yyy\" -agree", false);

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\"§\" -fields=\"externalCatalog§internalCatalog§internalSchema§jdbcUrl§user§pass§json§description§archived\" -values=\"self2§router§@NULL§jdbc:mysql://localhost:3306/router?serverTimezone=UTC&useSSL=false§root§root§{\\\"router_authority\\\":{\\\"x\\\":250,\\\"y\\\":370,\\\"color\\\":\\\"#1494CC\\\"},\\\"router_catalog\\\":{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":\\\"#2BBB88\\\"},\\\"router_command\\\":{\\\"x\\\":0,\\\"y\\\":370,\\\"color\\\":\\\"#0066CC\\\"},\\\"router_command_role\\\":{\\\"x\\\":0,\\\"y\\\":270,\\\"color\\\":\\\"#0066CC\\\"},\\\"router_config\\\":{\\\"x\\\":750,\\\"y\\\":240,\\\"color\\\":\\\"#FF0000\\\"},\\\"router_converter\\\":{\\\"x\\\":750,\\\"y\\\":400,\\\"color\\\":\\\"#FF0000\\\"},\\\"router_dashboard\\\":{\\\"x\\\":0,\\\"y\\\":640,\\\"color\\\":\\\"#CCCC33\\\"},\\\"router_entity\\\":{\\\"x\\\":250,\\\"y\\\":0,\\\"color\\\":\\\"#2BBB88\\\"},\\\"router_field\\\":{\\\"x\\\":500,\\\"y\\\":0,\\\"color\\\":\\\"#2BBB88\\\"},\\\"router_foreign_key\\\":{\\\"x\\\":750,\\\"y\\\":0,\\\"color\\\":\\\"#2BBB88\\\"},\\\"router_ipv4_blocks\\\":{\\\"x\\\":0,\\\"y\\\":890,\\\"color\\\":\\\"#CCAC81\\\"},\\\"router_ipv6_blocks\\\":{\\\"x\\\":500,\\\"y\\\":890,\\\"color\\\":\\\"#CCAA88\\\"},\\\"router_locations\\\":{\\\"x\\\":250,\\\"y\\\":905,\\\"color\\\":\\\"#CCAA88\\\"},\\\"router_role\\\":{\\\"x\\\":250,\\\"y\\\":270,\\\"color\\\":\\\"#0066CC\\\"},\\\"router_search_interface\\\":{\\\"x\\\":500,\\\"y\\\":640,\\\"color\\\":\\\"#CCCC33\\\"},\\\"router_short_url\\\":{\\\"x\\\":250,\\\"y\\\":640,\\\"color\\\":\\\"#CCCC33\\\"},\\\"router_user\\\":{\\\"x\\\":500,\\\"y\\\":370,\\\"color\\\":\\\"#0066CC\\\"},\\\"router_user_role\\\":{\\\"x\\\":500,\\\"y\\\":270,\\\"color\\\":\\\"#0066CC\\\"}}§AMI configuration catalog§0\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("FindNewCommands", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("Echo -foo=\"foo\" -cached", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_search_interface\" -sql=\"SELECT `id`, `group`, `name`, `rank`, `json` FROM `router_search_interface` WHERE `archived` = 0 ORDER BY `rank` ASC, `group` ASC, `name`\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("FlushCommandCache", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("GetAMITagInfo -amiTag=\"f1068\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("PingNode -hostName=\"aiami02.cern.ch\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_role\" -sql=\"SELECT AMI_DATE('2019-10-14')\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_role\" -sql=\"SELECT @@GLOBAL.time_zone, @@SESSION.time_zone;\"", false).replace(">", ">\n"));

//		CommandSingleton.executeCommand("RemoveElements -separator=\"|\" -catalog=\"self\" -entity=\"router_catalog_extra\" -keyFields=\"catalog|entity|field\" -keyValues=\"self|router_catalog|json\"", false);

//		CommandSingleton.executeCommand("AddElement -separator=\"|\" -catalog=\"self\" -entity=\"router_catalog_extra\" -fields=\"catalog|entity|field|rank|description|webLinkScript|isAdminOnly|isHidden|isCrypted|isPrimary|isCreated|isCreatedBy|isModified|isModifiedBy|isStatable|isGroupable\" -values=\"tasks|router_task|command|N/A|N/A|@NULL|0|1|0|0|0|0|0|0|0|0\"", false);

//		LogSingleton.root.error(LogSingleton.FATAL, "Hello World!");
//		LogSingleton.root.error("Hello World!");
//		LogSingleton.root.info("Hello World!");

//		CommandSingleton.executeCommand("RootH1I -catalog=\"self\" -entity=\"router_command\" -mql=\"SELECT `self`.`router_command`.`secured` WHERE 1=1\"", false);

//		CommandSingleton.executeCommand("RootH1I -catalog=\"self\" -entity=\"router_command\" -mql=\"SELECT `self`.`router_command`.`visible` WHERE 1=1\"", false);

//		CommandSingleton.executeCommand("GetElementInfo -catalog=\"self\" -entity=\"router_role\" -primaryFieldName=\"id\" -primaryFieldValue=\"1\"", false);

//		System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_command\" -mql=\"SELECT COUNT(`self`.`router_command`.`*`) WHERE `self`.`router_role`.`id`{`self`.`router_command_role`.`commandFK`, `self`.`router_command_role`.`roleFK`} = 1\"", false));

//		CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_catalog\" -mql=\"SELECT id\"", false);

//		CommandSingleton.executeCommand("AddElement -catalog=\"nika2:production\" -entity=\"scan\" -separator=\"§\" -fields=\"scanId§nbSubscans§startTime§endTime§source§type§tau225§receiver§azimuth§elevation§px§py§fx§fy§fz§comment\" -values=\"467465§1§2015-10-13 21:35:22§2015-10-13 21:37:05§Uranus§Lissajous§0.554055§NIKA§125.532975§46.424788§-14.800000§6.600000§0.000000§0.000000§-0.200000§focuslissold (the new one doesn' t work because the FOOFFSET parameter is not filled correctly)\"", false);

//		System.out.println(CommandSingleton.executeCommand("BrowseQuery -catalog=\"mc(12|16).*\" -entity=\"router_role\" -mql=\"SELECT `*`\"", false));

//		CommandSingleton.executeCommand("AddWidget -control=\"table\" -params=\"[\\\"BrowseQuery -catalog=\\\\\\\"self\\\\\\\" -entity=\\\\\\\"router_dashboard\\\\\\\" -mql=\\\\\\\"SELECT `*` WHERE \\'autoRefresh\\' = 0\\\\\\\"\\\"]\" -settings=\"{\\\"enableCache\\\":false,\\\"enableCount\\\":true,\\\"showToolBar\\\":true,\\\"showDetails\\\":true,\\\"showTools\\\":true,\\\"canEdit\\\":true,\\\"catalog\\\":\\\"self\\\",\\\"entity\\\":\\\"router_dashboard\\\",\\\"primaryField\\\":\\\"id\\\",\\\"rowset\\\":\\\"\\\",\\\"start\\\":1,\\\"stop\\\":20,\\\"orderBy\\\":\\\"\\\",\\\"orderWay\\\":\\\"\\\",\\\"maxCellLength\\\":64,\\\"card\\\":true}\"", false);

//		CommandSingleton.executeCommand("AddWidget -control=\"foo\" -params=\"[\\n]\" -settings=\"{}\"", false);

//		CommandSingleton.executeCommand("AddElement -separator=\"|\" -catalog=\"self\" -entity=\"router_field\" -fields=\"catalog|entity|field|rank|description\" -values=\"self|router_short_url|hash|0|h'k\"", false);

//		System.out.println(CommandSingleton.executeCommand("BrowseQuery -catalog=\"self\" -entity=\"router_role\" -mql=\"SELECT `*` WHERE 1=1\" -count", false));

		//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"mc16_001:production\" -entity=\"dataset\" -mql=\"SELECT * WHERE `mc16_001:production`.`dataset`.`AMIStatus` = \'VALID\'\" -limit=\"20\" -offset=\"0\" -count", false));
//		System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"mc16_001:production\" -entity=\"dataset\" -mql=\"SELECT * WHERE (`mc16_001:production`.`DATASET`.`AMISTATUS` = 'VALID') and (`mc16_001:production`.`DATASET`.`DATATYPE` = 'AOD') LIMIT 10 OFFSET 0\"", false));

//		CommandSingleton.executeCommand("BrowseQuery -catalog=\"self\" -entity=\"router_role\" -mql=\"SELECT id, id AS toto, router_user.id AS id WHERE 1=1 ORDER BY id ASC LIMIT 1 OFFSET 2\"", false);

//		System.out.println(CommandSingleton.executeCommand("GetSessionInfo", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("UpdateElements -catalog=\"self\" -entity=\"router_role\" -separator=\"§\" -fields=\"role\" -values=\"123456\" -keyFields=\"id\" -keyValues=\"7\"", false).replace(">", ">\n"));

//		/*System.out.println(*/CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_command\" -mql=\"SELECT `self`.`router_command`.`id`, `self`.`router_role`.`id` WHERE `self`.`router_role`.`id`{`self`.`router_command_role`.`commandFK`, `self`.`router_command_role`.`roleFK`} = '1'\" -limit=\"20\" -offset=\"0\"", false).replace(">", ">\n")/*)*/;

//		System.out.println(CommandSingleton.executeCommand("BrowseQuery -catalog=\"self\" -entity=\"router_role\" -mql=\"SELECT `*` WHERE 1=1\"", false));

//		System.out.println(CommandSingleton.executeCommand("UpdateElements -catalog=\"test2\" -entity=\"DATASET_FILE_BRIDGE\" -separator=\";\" -fields=\"DATASET.name\" -values=\"dataset_\" -keyFields=\"FILE.name;DATASET.name\" -keyValues=\"file_;dataset_\"", false));

/*		CommandSingleton.executeCommand("RemoveElements -catalog=\"test2\" -entity=\"DATASET_FILE_BRIDGE\" -separator=\";\" -keyFields=\"FILE.name;DATASET.name;PROJECT.name\" -keyValues=\"file_;dataset_;AMI\"", false);

		CommandSingleton.executeCommand("AddElement -catalog=\"test2\" -entity=\"DATASET_FILE_BRIDGE\" -separator=\";\" -fields=\"FILE.name;DATASET.name;FILE_TYPE.name\" -values=\"file_;dataset_;BINARY\"", false);

		CommandSingleton.executeCommand("AddElement -catalog=\"test2\" -entity=\"DATASET_FILE_BRIDGE\" -separator=\";\" -fields=\"FILE.name;DATASET.name;FILE_TYPE.name\" -values=\"file_;dataset_;A\"", false);

		CommandSingleton.executeCommand("AddElement -catalog=\"test2\" -entity=\"DATASET\" -separator=\";\" -fields=\"name;DATASET_TYPE.name{DATASET.typeFK};PROJECT.name{DATASET.typeFK};PROJECT.name{DATASET.projectFK}\" -values=\"test_multi_project;A;AMI;AMI2\"", false);

		CommandSingleton.executeCommand("AddElement -catalog=\"test2\" -entity=\"DATASET\" -separator=\";\" -fields=\"name;DATASET_TYPE.name;PROJECT.name\" -values=\"hh;A;AMI\"", false);

		CommandSingleton.executeCommand("AddElement -catalog=\"test2\" -entity=\"DATASET_FILE_BRIDGE\" -separator=\";\" -fields=\"FILE.name;DATASET.name;PROJECT.name\" -values=\"file_;dataset_;AMI\"", false);

		CommandSingleton.executeCommand("UpdateElements -catalog=\"test2\" -entity=\"DATASET_FILE_BRIDGE\" -separator=\";\" -fields=\"DATASET.name;PROJECT.name\" -values=\"dataset_;AMI\" -keyFields=\"FILE.name;DATASET.name;PROJECT.name\" -keyValues=\"file_;dataset_;AMI\"", false);

		CommandSingleton.executeCommand("UpdateElements -catalog=\"test2\" -entity=\"DATASET\" -separator=\";\" -fields=\"name\" -values=\"dataset_test_9\" -keyFields=\"id\" -keyValues=\"10\"", false);

		CommandSingleton.executeCommand("AddElement -catalog=\"test2\" -entity=\"DATASET_TYPE\" -separator=\";\" -fields=\"name;PROJECT.name;description\" -values=\"A;AMI;This is a test\"", false);
*/
/*		SimpleQuerier querier1 = new SimpleQuerier("self", "admin", false);

		String mql;

		mql = "SELECT *";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		mql = "SELECT `router_command`.`command`, `router_role`.`role` WHERE `router_command`.`command` = 'GetUserInfo'";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_command", mql));
		System.out.println();

		mql = "SELECT `router_command`.`command`, `router_role`.`role` WHERE `router_command`.`command` = 'GetUserInfo'";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_role", mql));
		System.out.println();

		mql = "SELECT `router_user`.`AMIUser`, `router_role`.`role`, `router_role`.`description` WHERE `router_user`.`AMIUser` = 'jodier'";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		mql = "SELECT `router_user`.`AMIUser`, `router_role`.`role`, `router_role`.`description` WHERE `router_user`.`AMIUser` = 'jodier'";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_role", mql));
		System.out.println();

		mql = "SELECT `router_user`.`AMIUser`, `router_role`.`role` WHERE `router_user`.`AMIUser` = 'jodier'";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		mql = "SELECT `router_role`.`role` WHERE `router_role`.`role` = 'AMI_USER'";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		mql = "SELECT `router_user`.`AMIUser`, `router_role`.`role` WHERE (`router_role`.`role` = 'AMI_ADMIN') AND (`router_role`.`role` = 'AMI_USER')";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		mql = "SELECT `router_user`.`AMIUser`, `router_role`.`role` WHERE [`router_role`.`role` = 'AMI_ADMIN'] AND [`router_role`.`role` = 'AMI_USER']";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		mql = "INSERT (`roleFK`, `commandFK`) VALUES (1, 1)";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_command_role", mql));
		System.out.println();

		mql = "INSERT (`role`, `command`) VALUES ('AMI_ADMIN', 'GetUserInfo')";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_command_role", mql));
		System.out.println();

		mql = "INSERT (`role`, `AMIUser`) VALUES ('AMI_ADMIN', 'jodier')";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user_role", mql));
		System.out.println();

		mql = "SELECT `router_user`.`AMIUser`, `router_role`.`role`";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		mql = "SELECT `router_user`.`AMIUser`, `self`.`router_user_role2`.`userFK`, `router_role`.`role`";
		System.out.println(mql + "\n" + querier1.mqlToSQL("router_user", mql));
		System.out.println();

		querier1.rollbackAndRelease();
*/
/* CYCLE
		Querier querier1 = new SimpleQuerier("test", "admin", true);

		System.out.println(querier1.mqlToSQL("A", "SELECT id WHERE [`foo`{bFK} = 'FOO' AND `bar`{dFK} = 'BAR'] AND `qux` = 'QUX'"));

		System.out.println(querier1.mqlToSQL("A", "INSERT (`foo`{bFK}, `bar`{dFK}, `qux`) VALUES ('FOO', 'BAR', 'QUX')"));
*/

//		System.out.println(querier1.executeMQLUpdate("router_catalog", "UPDATE (`pass`) VALUES ('root') WHERE `id` = '4'").getNbOfUpdatedRows());

//		System.out.println(querier1.mqlToSQL("DATASET_FILE_BRIDGE", "INSERT (`FILE`.`name`, `DATASET`.`name`, `PROJECT`.`name`) VALUES ('file_9', 'dataset_9', 'AMI')"));

//		querier1.executeMQLUpdate("DATASET_FILE_BRIDGE", "INSERT (`FILE`.`name`, `DATASET`.`name`, `PROJECT`.`name`) VALUES ('file_9', 'dataset_9', 'AMI')");

//		querier1.executeMQLUpdate("DATASET_FILE_BRIDGE", "INSERT (`PROJECT`.`name`) VALUES ('AMI')");

//		System.out.println(querier1.mqlToSQL("DATASET_FILE_BRIDGE", "DELETE WHERE `PROJECT`.`name` = 'AMI' AND `DATASET`.`name` = 'dataset_0' AND `FILE`.`name` = 'file_0'"));

//		Querier querier2 = new SimpleQuerier("test");

//		System.out.println(querier2.mqlToSQL("A", "DELETE WHERE `name` = 'foo' AND `kux` = 'bar'"));

//		System.out.println(querier2.mqlToSQL("A", "SELECT `kux` WHERE `name` = 'foo' AND `kux` = 'bar'"));

//		System.out.println(querier2.mqlToSQL("A", "INSERT (`name`, `label`) VALUES ('foo', 'bar')"));
//		System.out.println(querier2.mqlToSQL("A", "INSERT (`bFK`) VALUES (0)"));

//		CommandSingleton.executeCommand("ResetPassword -amiLogin=\"jodier\"", true);

//		System.out.println(CommandSingleton.executeCommand("GetSessionInfo", true).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\";\" -fields=\"externalCatalog;internalCatalog;internalSchema;jdbcUrl;user;pass\" -values=\"test2;ami_test_database;;jdbc:mysql://ccmysql.in2p3.fr:3306;ami_test_db;olivet556_saumon\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\"§\" -fields=\"externalcatalog§internalcatalog§internalschema§jdbcurl§user§pass§json§archived§created§createdby§modified§modifiedby\" -values=\"a§a§@NULL§a§a§a§{}§0§@CURRENT_TIMESTAMP§a§@CURRENT_TIMESTAMP§a\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("RemoveElements -catalog=\"test\" -entity=\"C\" -keyFields=\"id\" -keyValues=\"99\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"nika2:demo\" -entity=\"cluster\" -mql=\"SELECT DISTINCT `nika2:demo`.`campaign`.`name` WHERE `nika2:demo`.`cluster`.`Done` = '1' ORDER BY `nika2:demo`.`campaign`.`name` ASC\"", false).replace(">", ">\n"));

		//System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"test\" -entity=\"A\" -separator=\"§\" -fields=\"id§name§kux\" -values=\"-99§foo§bar\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\"§\" -fields=\"catalog§entity§field§rank§ishidden§iscrypted§isprimary§iscreated§iscreatedby§ismodified§ismodifiedby§isstatable§isgroupable§description§weblinkscript§created§createdby§modified§modifiedby\" -values=\"mc16_001:production§dataset§logicalDatasetName§0§0§0§0§0§0§0§0§0§0§Logical Dataset Name§import net.hep.ami.jdbc.WebLink;  def webLink = new WebLink();  webLink.newLinkProperties().setLabel(\\\"Rucio\\\");  return webLink;§current_timestamp§§current_timestamp§\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_search_interface\" -fields=\"interface,json,archived,createdBy,modifiedBy\" -values=\"A,{},0,jodier,jodier\"", false).replace(">", ">\n"));

/*		arguments.put("catalog", "mc15_001:production");
		arguments.put("entity", "dataset");
		arguments.put("mql", "SELECT * WHERE `mc15_001:production`.`dataset`.`logicalDatasetName` =  'mc15_13TeV.428001.ParticleGun_single_piplus_logE0p2to2000.evgen.EVNT.e3501'");
		arguments.put("limit", "10");
		arguments.put("offset", "0");
		System.out.println(CommandSingleton.executeCommand("BrowseQuery", arguments, false).replace(">", ">\n"));
*/
//		arguments.put("catalog", "mc16_001:production");
//		arguments.put("entity", "dataset");
//		arguments.put("mql", "SELECT COUNT(identifier) AS nb WHERE `mc16_001:production`.`dataset`.`AMIStatus` = 'VALID'");
//		System.out.println(CommandSingleton.executeCommand("SearchQuery", arguments, false).replace(">", ">\n"));

		//Router router = new Router("self");
//		RowSet rowSet = router.executeMQLQuery("router_locations", "SELECT continentCode, countryCode WHERE (router_ipv4_blocks.network = '1.0.0.0/24' AND router_ipv4_blocks.network = '1.0.1.0/24')");
//		System.out.println(rowSet.getMQL());
//		System.out.println(rowSet.getSQL());

		//System.out.println(router.mqlToSQL("router_locations", "SELECT continentCode, countryCode, router_ipv4_blocks.network WHERE router_ipv4_blocks.network = '1.0.0.0/24' AND router_ipv4_blocks.network = '1.0.1.0/24'").replace("WHERE", "WHERE\n"));
		//System.out.println();
		//System.out.println(router.mqlToSQL("router_locations", "SELECT continentCode, countryCode, router_ipv4_blocks.network WHERE (router_ipv4_blocks.network = '1.0.0.0/24' AND router_ipv4_blocks.network = '1.0.1.0/24')").replace("WHERE", "WHERE\n"));
		//System.out.println();
		//System.out.println(router.mqlToSQL("router_locations", "SELECT continentCode, countryCode, router_ipv4_blocks.network WHERE [router_ipv4_blocks.network = '1.0.0.0/24' AND router_ipv4_blocks.network = '1.0.1.0/24']").replace("WHERE", "WHERE\n"));

		//System.out.println(CommandSingleton.executeCommand("GetSchemas", arguments).replace(">", ">\n"));

		//System.out.println("--------");

		//System.out.println(CommandSingleton.executeCommand("ListCatalogs", arguments).replace(">", ">\n"));

		//System.out.println("--------");

		//System.out.println(CommandSingleton.executeCommand("ListDrivers", arguments).replace(">", ">\n"));

		try
		{
//			Router router = new Router("test", "ami_router", "jdbc:postgresql://localhost:2432/ami_router", "radardb-ami-lpsc", "Pci62Emxt65zcZY84UO7");
//			Router router = new Router("test", "router_test", "jdbc:mysql://localhost:3306/", "root", "root");
//			Router router = new Router("test");

//			router.create();
//			router.fill();

//			router.commitAndRelease();

//			System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"tasks\" -entity=\"router_task\" -fields=\"name,command,description,oneShot,priority,timeStep,serverName\" -values=\"example,echo 'Hello World!',Example,0,0,1000,test\"").replace(">", ">\n"));

//			System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -sql=\"SELECT (SELECT COUNT(*) FROM `router_config`) AS `nb1`, (SELECT COUNT(*) FROM `router_role`) AS `nb2`, (SELECT COUNT(*) FROM `router_command`) AS `nb3`, (SELECT COUNT(*) FROM `router_user`) AS `nb4`, (SELECT COUNT(*) FROM `router_catalog`) AS `nb5`\"", false).replace(">", ">\n"));


//			System.out.println(CommandSingleton.executeCommand("FindNewCommands").replace(">", ">\n"));

//			System.out.println(CommandSingleton.executeCommand("UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -fields=\"jsonSerialization\" -values=\"{}\" -keyFields=\"externalCatalog\" -keyValues=\"self\"").replace(">", ">\n"));

//			Router router2 = new Router();

			//System.out.println(router2.mqlToAST("router_user", "SELECT COUNT(router_user.`*`) WHERE router_user.`firstname`='Jérôme' AND valid=1"));
			//System.out.println(router2.mqlToAST("router_user", "SELECT router_user.*"));

//			router2.commitAndRelease();
/*

			System.out.println("::" + SecuritySingleton.encrypt("") + "::");
			System.out.println("::" + SecuritySingleton.decrypt("") + "::");
*/
			//System.out.println(SchemaSingleton.getCatalogNames());
			//System.out.println(SchemaSingleton.getDBSchemes().toString().replace(">", ">\n"));

			//CatalogSingleton.reload();

			//System.out.println(SchemaSingleton.getTableNames("self"));

//			System.out.println(CommandSingleton.executeCommand("GetSessionInfo", arguments).replace(">", ">\n"));

//			System.out.println("SELECT 1 FROM " + SchemaSingleton.externalCatalogToInternalCatalog_noException("self"));

			//System.out.println(ConnectionPoolSingleton.getStatus());

			//System.out.println(MQLToAST.parse("SELECT `foo`.`bar`", "toto"));

			//System.out.println(MQLToAST.parse("SELECT `foo`.* WHERE `foo`.`bar`='kux' OR `foo`.`bar`=-777", "toto"));

/*			Exception e = new Exception("foo");

			Exception f = new Exception("bar");

			f.initCause(e);

			f.printStackTrace();
			System.out.println(f.getMessage());
 */
/*			LogSingleton.root.error("hello!", (Exception) null);

			LogSingleton.root.error("hello!", e);

			LogSingleton.root.error("hello {}!", "world", e);

			LogSingleton.root.error("hello {}, {}!", "world1", "world2", e);

			LogSingleton.root.error("class '{}' doesn't extend 'AbstractCommand'", "foo");
*/
			//System.out.println(CommandSingleton.executeCommand("GetFieldInfo -catalog=\"self\" -entity=\"router_user\"").replace(">", ">\n"));

			//System.out.println(Tokenizer.tokenize("SELECT `A`.`B`.`C` FROM `A`.`B`"));
/*
			System.out.println();
			System.out.println("\n-> " + router.mqlToSQL("t2", "SELECT qux, fred WHERE bar='a' AND fred='b'"));
*/
			//System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_ipv4_blocks\" -fields=\"network,router_locations.continentCode,router_locations.countryCode\" -values=\"foo,EU,FR\"").replace(">", ">\n"));

			//System.out.println(CommandSingleton.executeCommand("RemoveElements -catalog=\"self\" -entity=\"router_ipv4_blocks\" -keyFields=\"network,router_locations.continentCode,router_locations.countryCode\" -keyValues=\"foo,EU,FR\"").replace(">", ">\n"));

			//System.out.println(CommandSingleton.executeCommand("UpdateElements -catalog=\"self\" -entity=\"router_ipv4_blocks\" -fields=\"network,router_locations.continentCode,router_locations.countryCode\" -values=\"foo,EU,FR\"").replace(">", ">\n"));
/*
			System.out.println(router2.mqlToSQL("router_ipv4_blocks", "SELECT countryCode, router_command.command WHERE id=10"));

			System.out.println(router2.mqlToSQL("router_ipv4_blocks", "SELECT 1 WHERE router_command.command=10"));
*/
/*
			System.out.println(router2.mqlToSQL("router_command", "SELECT 1"));

			System.out.println(router2.mqlToSQL("router_command", "SELECT *"));

			System.out.println(router2.mqlToSQL("router_command", "SELECT count(*)"));

			System.out.println(router2.mqlToSQL("router_command", "SELECT command WHERE id=?"));

			System.out.println(router2.mqlToSQL("router_command", "SELECT command WHERE id=10"));

			System.out.println(router2.mqlToSQL("router_command", "SELECT command WHERE 10=id"));

			System.out.println(router2.mqlToSQL("router_command", "SELECT command WHERE class LIKE '%net.hep%'"));

			System.out.println(router2.mqlToSQL("router_ipv4_blocks", "SELECT network, continentCode, countryCode, router_command.command WHERE continentCode = countryCode AND countryCode=10"));
*/
//			System.out.println(router2.mqlToSQL("router_ipv4_blocks", "SELECT router_ipv4_blocks.id, network, continentCode, countryCode, router_command.command WHERE id=10"));
/*
			System.out.println(Command.parse("AddElement -foo=\"\"").arguments);

			System.out.println(Command.parse("AddElement -foo=\"bar\"-bar").arguments);

			System.out.println(Command.parse("AddElement-foo=\"bar\"-bar=\"f\\too\"").arguments);

			System.out.println(Command.parse("AddElement -catalog=\"self\" -entity=\"router_ipv4_blocks\" -foo=\"bar\"").arguments);

			System.out.println(Command.parse("UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -separator=\"%\" -fields=\"json\" -values=\"{\\\"APID\\\":{\\\"x\\\":25,\\\"y\\\":410,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"APID_BLOCK_BRIDGE\\\":{\\\"x\\\":20,\\\"y\\\":605,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK\\\":{\\\"x\\\":365,\\\"y\\\":635,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_CONTAINER\\\":{\\\"x\\\":290,\\\"y\\\":960,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_PARAM\\\":{\\\"x\\\":35,\\\"y\\\":910,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_TYPE\\\":{\\\"x\\\":30,\\\"y\\\":755,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"COMMAND\\\":{\\\"x\\\":410,\\\"y\\\":1180,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"COMMAND_PARAM\\\":{\\\"x\\\":85,\\\"y\\\":1295,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET\\\":{\\\"x\\\":1085,\\\"y\\\":855,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_BLOCK_ANNOTATION\\\":{\\\"x\\\":755,\\\"y\\\":985,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_COMMAND_BRIDGE\\\":{\\\"x\\\":735,\\\"y\\\":1195,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_CONTAINER\\\":{\\\"x\\\":1130,\\\"y\\\":460,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_FILE_BRIDGE\\\":{\\\"x\\\":1095,\\\"y\\\":1305,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_HIERARCHY\\\":{\\\"x\\\":1365,\\\"y\\\":980,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PARAM\\\":{\\\"x\\\":1355,\\\"y\\\":725,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PHASE_BRIDGE\\\":{\\\"x\\\":855,\\\"y\\\":265,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PROCEDURE_BRIDGE\\\":{\\\"x\\\":765,\\\"y\\\":610,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_TYPE\\\":{\\\"x\\\":745,\\\"y\\\":795,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"EXPERIMENT\\\":{\\\"x\\\":330,\\\"y\\\":0,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE\\\":{\\\"x\\\":745,\\\"y\\\":1360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE_PARAM\\\":{\\\"x\\\":745,\\\"y\\\":1535,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"INSTITUTE\\\":{\\\"x\\\":20,\\\"y\\\":15,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"INSTRUMENT\\\":{\\\"x\\\":330,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"LEVEL\\\":{\\\"x\\\":595,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"MODE\\\":{\\\"x\\\":320,\\\"y\\\":415,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PHASE\\\":{\\\"x\\\":630,\\\"y\\\":5,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PHYSICAL_FILE\\\":{\\\"x\\\":1055,\\\"y\\\":1495,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PROCEDURE\\\":{\\\"x\\\":605,\\\"y\\\":420,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"UNIT\\\":{\\\"x\\\":25,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"}}\" -keyFields=\"externalCatalog\" -keyValues=\"radardb\"").arguments);
*/
//			System.out.println(Pattern.compile(".*(?:INT|FLOAT|DOUBLE|DECIMAL|NUMERIC).*", Pattern.CASE_INSENSITIVE).matcher("INTEGER").matches());

//			System.out.println(CommandSingleton.executeCommand("GetElementInfo -catalog=\"mc16_001:production\" -entity=\"dataset\" -primaryFieldName=\"identifier\" -primaryFieldValue=\"286894\"", false));

//			System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"radardb\" -entity=\"UNIT\" -separator=\";\" -fields=\"NAME;FULLNAME;INSTRUMENT.NAME;EXPERIMENT.NAME\" -values=\"testUnitForExperiment;test unit;testInstrument;ROSETTA\""));

//			System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"radardb\" -entity=\"UNIT\" -separator=\";\" -fields=\"NAME;FULLNAME;EXPERIMENT.NAME\" -values=\"testUnitForExperiment;test unit;ROSETTA\""));

//			System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"radardb\" -entity=\"UNIT\" -separator=\";\" -fields=\"NAME;FULLNAME;INSTRUMENT.NAME\" -values=\"testUnitForExperiment;test unit;testInstrument\""));
/*
			PathList pathList2 = AutoJoinSingleton.resolve(
				"test",
				"t1",
				"foo6"
			);

			System.out.println(pathList2.getQId());
			System.out.println(pathList2.getPaths());
			System.out.println();
*/
//			System.out.println(new SimpleQuerier("test").mqlToSQL("t8", "SELECT foo8 + 1 WHERE foo10 = 'bar'"));
//			System.out.println(new SimpleQuerier("test").mqlToSQL("t8", "SELECT foo8 + 1 WHERE foo9 + foo10 = 'bar'"));
//			System.out.println();

/*			System.out.println(new SimpleQuerier("test").mqlToSQL("t8", "SELECT foo8 + 1 WHERE foo9 + 1 = 'foo' AND foo9 * 2 = 'bar'")); // AND foo9 IN ('bar', 'foo')
			System.out.println();

			System.out.println(new SimpleQuerier("test").mqlToSQL("t1", "SELECT foo7 WHERE foo1 = 'foo'"));
			System.out.println();
*/
/*
			QId pathQId = new QId("A.B{!toto.yy}", QId.FLAG_FIELD, QId.FLAG_ENTITY);
			System.out.println("------------");

			QId qId = new QId("yy.#", QId.FLAG_FIELD);
			System.out.println("------------");

			System.out.println(pathQId);

			System.out.println(qId);

			System.out.println(pathQId.getConstraints().get(0).matches(qId) != pathQId.getConstraints().get(0).getExclusion());
*/
//			System.out.println(CommandSingleton.executeCommand("GetElementInfo -catalog=\"mc16_001:production\" -entity=\"DATASET\" -primaryFieldName=\"IDENTIFIER\" -primaryFieldValue=\"257898\"", false).replace(">", ">\n"));

//			System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_user_role\" -mql=\"SELECT COUNT(*) WHERE `self`.`router_role`.`id` = '1'\"").replace(">", ">\n"));

//			System.out.println(CommandSingleton.executeCommand("FindNewCommands").replace(">", ">\n"));

//			System.out.println(new QId("A.B.C{D.E.#}"));
//			System.out.println(new QId("A.B.#{D.E.F,!G.H.*}"));

/*			System.out.println(CommandSingleton.executeCommand("GetSessionInfo -detachCert -amiLogin=\"admin\" -amiPassword=\"insider\"", false).replace(">", ">\n"));

			System.out.println(System.getProperty("os.name").startsWith("Windows"));

			System.out.println(System.getProperty("java.io.tmpdir"));*/


			//System.out.println("done.");

			//System.out.println(CommandSingleton.executeCommand("AnalyzeQuery -xql=\"SELECT JSON_PATHS(AUXILIARYPARAMS,'$') WHERE TAGNAME LIKE 'z%'\"", false).replace(">", ">\n"));

			//System.out.println(CommandSingleton.executeCommand("PingNode -hostName=\"ccami021.in2p3.fr\"", false).replace(">", ">\n"));


			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"AMITags2021:production\" -entity=\"T_TAGS\"  -mql=\"SELECT 5, JSON_PATHS(AUXILIARYPARAMS,'$'), 6 \"", false).replace(">", ">\n"));

			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"AMITags2021:production\" -entity=\"T_TAGS\"  -mql=\"SELECT JSON_VALUES(AUXILIARYPARAMS,'$.test.bar') WHERE TAGNAME LIKE 'z%'\"", false).replace(">", ">\n"));

			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"nika2:production\" -entity=\"CLUSTER\"  -mql=\"SELECT JSON_PATHS(ancillaryData,'$') WHERE shortName LIKE '%'\"", false).replace(">", ">\n"));
			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"nika2:production\" -entity=\"CLUSTER\"  -mql=\"SELECT JSON_VALUES(ancillaryData,'$.test') WHERE shortName LIKE '%'\"", false).replace(">", ">\n"));

			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"DatasetWB:production\" -entity=\"WB_HASHTAGS\"  -mql=\"SELECT DISTINCT \\\"DATASETFK\\\" AS \\\"id\\\", \\\"DATASETCATALOG\\\" AS \\\"catalog\\\", \\\"DATASETNAME\\\" AS \\\"ldn\\\" WHERE \\\"SCOPE\\\" = 'PMGL1' AND \\\"NAME\\\" = 'Exotics'\"", false).replace(">", ">\n"));

			/*Querier querier = new Router();

			@NotNull List<Row> rows = querier.executeSQLQuery("router_catalog", "SELECT * FROM \"router_catalog\" WHERE \"jdbcUrl\" LIKE '%in2p3%' AND \"internalCatalog\" LIKE 'ATLAS_AMI%'").getAll();

			for(Row row : rows)
			{
				System.out.println(
						"UPDATE \"router_catalog\" SET " +
								"\"internalCatalog\" = '" + row.getValue("internalCatalog") + "_W', " +
								"\"internalSchema\" = '" + row.getValue("internalSchema") + "_W', " +
								"\"jdbcUrl\" = 'the new url', " +
								"\"user\" = '" + SecuritySingleton.encrypt(row.getValue("internalSchema") + "_W") + "' " +
						"WHERE \"id\" = " + row.getValue("id") + ";"
				);
			}*/

			//System.out.println(CommandSingleton.executeCommand("GetResettableAMITags", true).replace(">", ">\n"));

			//System.out.println(CommandSingleton.executeCommand("GetTmpPass -amiLogin=\"ami\"", false).replace(">", ">\n"));

			//CommandSingleton.executeCommand("ChangePassword -amiLogin=\"ami\" -amiPasswordOld=\"c4eb529786520afd86bd955fae759ccc\" -amiPasswordNew=\"ami\"", false);
			//System.out.println(CommandSingleton.executeCommand("ChangePassword -amiLogin=\"ami\" -amiPasswordOld=\"c4eb529786520afd86bd955fae759ccc\" -amiPasswordNew=\"ami\"", false).replace(">", ">\n"));

//			String hash = SecuritySingleton.bcryptEncode("Hello");

//			System.out.println(hash);

//			SecuritySingleton.checkBCrypt("Hello", hash);

			System.out.println(SecuritySingleton.setupOIDC("1ebb0532-7e02-4003-8038-4d4190fd7664", "https://atlas-auth.web.cern.ch/.well-known/openid-configuration"));

			System.out.println(SecuritySingleton.setupOIDC("amiwf", "https://auth.cern.ch/auth/realms/cern/.well-known/openid-configuration"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());

			//e.printStackTrace(System.out);
		}

		System.out.println("bye.");

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
