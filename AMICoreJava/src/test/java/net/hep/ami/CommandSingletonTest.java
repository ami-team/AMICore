package net.hep.ami;

import java.io.*;
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
public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		Map<String, String> arguments = new HashMap<String, String>();

//		LogSingleton.root.error(LogSingleton.FATAL, "Hello World!");
//		LogSingleton.root.error("Hello World!");
//		LogSingleton.root.info("Hello World!");

		Querier querier = new SimpleQuerier("test");

//		System.out.println(querier.mqlToSQL("A", "DELETE WHERE `name` = 'foo' AND `kux` = 'bar'"));

//		System.out.println(querier.mqlToSQL("A", "SELECT `kux` WHERE `name` = 'foo' AND `kux` = 'bar'"));

		System.out.println(querier.mqlToSQL("A", "INSERT (`name`, `label`) VALUES ('foo', 'bar')"));

//		CommandSingleton.executeCommand("ResetPassword -amiLogin=\"jodier\"", true);

//		System.out.println(CommandSingleton.executeCommand("GetSessionInfo", true).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\";\" -fields=\"externalCatalog;internalCatalog;internalSchema;jdbcUrl;user;pass\" -values=\"test2;ami_test_database;;jdbc:mysql://ccmysql.in2p3.fr:3306;ami_test_db;olivet556_saumon\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_catalog\" -separator=\"§\" -fields=\"externalcatalog§internalcatalog§internalschema§jdbcurl§user§pass§custom§archived§created§createdby§modified§modifiedby\" -values=\"a§a§@NULL§a§a§a§{}§0§@CURRENT_TIMESTAMP§a§@CURRENT_TIMESTAMP§a\"", false).replace(">", ">\n"));

		//System.out.println(CommandSingleton.executeCommand("RemoveElements -catalog=\"test\" -entity=\"C\" -keyFields=\"id\" -keyValues=\"99\"", false).replace(">", ">\n"));

		//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"test\" -entity=\"C\" -mql=\"SELECT `*`\"", false).replace(">", ">\n"));

		//System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"test\" -entity=\"A\" -separator=\"§\" -fields=\"id§name§kux\" -values=\"-99§foo§bar\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_catalog_extra\" -separator=\"§\" -fields=\"catalog§entity§field§rank§ishidden§iscrypted§isprimary§iscreated§iscreatedby§ismodified§ismodifiedby§isstatable§isgroupable§description§weblinkscript§created§createdby§modified§modifiedby\" -values=\"mc16_001:production§dataset§logicalDatasetName§0§0§0§0§0§0§0§0§0§0§Logical Dataset Name§import net.hep.ami.jdbc.WebLink;  def webLink = new WebLink();  webLink.newLinkProperties().setLabel(\\\"Rucio\\\");  return webLink;§current_timestamp§§current_timestamp§\"", false).replace(">", ">\n"));

//		System.out.println(CommandSingleton.executeCommand("AddElement -catalog=\"self\" -entity=\"router_search_interface\" -fields=\"interface,json,archived,createdBy,modifiedBy\" -values=\"A,{},0,jodier,jodier\"", false).replace(">", ">\n"));

//		arguments.put("catalog", "mc16_001:production");
//		arguments.put("entity", "dataset");
//		//arguments.put("mql", "SELECT \"mc16_001:production\".\"DATASET\".\"GENFILTEFF\" AS \"field\", MIN(\"DATASET\".\"GENFILTEFF\") AS \"min\", MAX(\"DATASET\".\"GENFILTEFF\") AS \"max\", AVG(\"DATASET\".\"GENFILTEFF\") AS \"avg\", STDDEV(\"DATASET\".\"GENFILTEFF\") AS \"stddev\", COUNT(\"DATASET\".\"GENFILTEFF\") AS \"count\" WHERE \"mc16_001:production\".\"dataset\".\"AMIStatus\" = 'VALID'");
//		//arguments.put("mql", "SELECT \"mc16_001:production\".\"DATASET\".\"GENFILTEFF\" AS \"field\", MIN(\"DATASET\".\"GENFILTEFF\") AS \"min\", MAX(\"DATASET\".\"GENFILTEFF\") AS \"max\", AVG(\"DATASET\".\"GENFILTEFF\") AS \"avg\" WHERE \"mc16_001:production\".\"dataset\".\"AMIStatus\" = 'VALID'");
//		arguments.put("mql", "SELECT MIN(\"DATASET\".\"GENFILTEFF\") AS \"min\", MAX(\"DATASET\".\"GENFILTEFF\") AS \"max\", AVG(\"DATASET\".\"GENFILTEFF\") AS \"avg\", STDDEV(\"DATASET\".\"GENFILTEFF\") AS \"stddev\", COUNT(\"DATASET\".\"GENFILTEFF\") AS \"count\" WHERE \"mc16_001:production\".\"dataset\".\"AMIStatus\" = 'VALID'");
//		//arguments.put("mql", "SELECT MIN(\"DATASET\".\"GENFILTEFF\") AS \"min\", MAX(\"DATASET\".\"GENFILTEFF\") AS \"max\", AVG(\"DATASET\".\"GENFILTEFF\") AS \"avg\", COUNT(\"DATASET\".\"GENFILTEFF\") AS \"count\" WHERE \"mc16_001:production\".\"dataset\".\"AMIStatus\" = 'VALID'");
//		arguments.put("limit", "10");
//		arguments.put("offset", "0");
//		System.out.println(CommandSingleton.executeCommand("BrowseQuery", arguments, false).replace(">", ">\n"));

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

/*
		arguments.put("country", "FR");
		arguments.put("locality", "Grenoble");
		arguments.put("organization", "CNRS");
		arguments.put("organizationalUnit", "LPSC-AMI");
		arguments.put("commonName", "Fabian Lambert");
		arguments.put("password", "fofi1972");
		arguments.put("validity", "10");
		System.out.println(CommandSingleton.executeCommand("GenerateCertificate", arguments).replace(">", ">\n"));
*/
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

			System.out.println(Command.parse("UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -separator=\"%\" -fields=\"custom\" -values=\"{\\\"APID\\\":{\\\"x\\\":25,\\\"y\\\":410,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"APID_BLOCK_BRIDGE\\\":{\\\"x\\\":20,\\\"y\\\":605,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK\\\":{\\\"x\\\":365,\\\"y\\\":635,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_CONTAINER\\\":{\\\"x\\\":290,\\\"y\\\":960,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_PARAM\\\":{\\\"x\\\":35,\\\"y\\\":910,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_TYPE\\\":{\\\"x\\\":30,\\\"y\\\":755,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"COMMAND\\\":{\\\"x\\\":410,\\\"y\\\":1180,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"COMMAND_PARAM\\\":{\\\"x\\\":85,\\\"y\\\":1295,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET\\\":{\\\"x\\\":1085,\\\"y\\\":855,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_BLOCK_ANNOTATION\\\":{\\\"x\\\":755,\\\"y\\\":985,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_COMMAND_BRIDGE\\\":{\\\"x\\\":735,\\\"y\\\":1195,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_CONTAINER\\\":{\\\"x\\\":1130,\\\"y\\\":460,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_FILE_BRIDGE\\\":{\\\"x\\\":1095,\\\"y\\\":1305,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_HIERARCHY\\\":{\\\"x\\\":1365,\\\"y\\\":980,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PARAM\\\":{\\\"x\\\":1355,\\\"y\\\":725,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PHASE_BRIDGE\\\":{\\\"x\\\":855,\\\"y\\\":265,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PROCEDURE_BRIDGE\\\":{\\\"x\\\":765,\\\"y\\\":610,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_TYPE\\\":{\\\"x\\\":745,\\\"y\\\":795,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"EXPERIMENT\\\":{\\\"x\\\":330,\\\"y\\\":0,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE\\\":{\\\"x\\\":745,\\\"y\\\":1360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE_PARAM\\\":{\\\"x\\\":745,\\\"y\\\":1535,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"INSTITUTE\\\":{\\\"x\\\":20,\\\"y\\\":15,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"INSTRUMENT\\\":{\\\"x\\\":330,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"LEVEL\\\":{\\\"x\\\":595,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"MODE\\\":{\\\"x\\\":320,\\\"y\\\":415,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PHASE\\\":{\\\"x\\\":630,\\\"y\\\":5,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PHYSICAL_FILE\\\":{\\\"x\\\":1055,\\\"y\\\":1495,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PROCEDURE\\\":{\\\"x\\\":605,\\\"y\\\":420,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"UNIT\\\":{\\\"x\\\":25,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"}}\" -keyFields=\"externalCatalog\" -keyValues=\"radardb\"").arguments);
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
//			System.out.println(CommandSingleton.executeCommand("GetElementInfo -catalog=\"self\" -entity=\"router_user\" -primaryFieldName=\"id\" -primaryFieldValue=\"1\"").replace(">", ">\n"));

//			System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_user_role\" -mql=\"SELECT COUNT(*) WHERE `self`.`router_role`.`id` = '1'\"").replace(">", ">\n"));

//			System.out.println(CommandSingleton.executeCommand("FindNewCommands").replace(">", ">\n"));

//			System.out.println(new QId("A.B.C{D.E.#}"));
//			System.out.println(new QId("A.B.#{D.E.F,!G.H.*}"));

			//System.out.println("done.");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());

			//e.printStackTrace(System.out);
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
