package net.hep.ami;

import java.io.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.structure.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.Command;
import net.hep.ami.utility.parser.JSON;

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
		arguments.put("commonName", "Jerome Odier");
		arguments.put("password", "????");
		arguments.put("validity", "10");
		System.out.println(CommandSingleton.executeCommand("GenerateCertificate", arguments).replace(">", ">\n"));
*/
		try
		{
			Router router = new Router("test", "router_test", "jdbc:mysql://localhost:3306/", "root", "root");

//			router.create();
//			router.fill();

			router.commitAndRelease();

//			System.out.println(CommandSingleton.executeCommand("UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -fields=\"jsonSerialization\" -values=\"{}\" -keyFields=\"externalCatalog\" -keyValues=\"self\"").replace(">", ">\n"));

			Router router2 = new Router();

			//System.out.println(router2.mqlToAST("router_user", "SELECT COUNT(router_user.`*`) WHERE router_user.`firstname`='Jérôme' AND valid=1"));
			//System.out.println(router2.mqlToAST("router_user", "SELECT router_user.*"));

			router2.commitAndRelease();
/*
			AutoJoinSingleton.AMIJoins joins;

			joins = new AutoJoinSingleton.AMIJoins();
			AutoJoinSingleton.resolveWithInnerJoins(joins, "self", "router_search_criteria", "router_search_interface.interface", "foo");
			System.out.println(joins.toSQL());

			joins = new AutoJoinSingleton.AMIJoins();
			AutoJoinSingleton.resolveWithInnerJoins(joins, "self", "router_search_interface", "router_search_criteria.alias", "foo");
			System.out.println(joins.toSQL());

			joins = new AutoJoinSingleton.AMIJoins();
			AutoJoinSingleton.resolveWithNestedSelect(joins, "self", "router_search_criteria", "router_search_interface.interface", "foo");
			System.out.println(joins.toSQL());

			joins = new AutoJoinSingleton.AMIJoins();
			AutoJoinSingleton.resolveWithNestedSelect(joins, "self", "router_search_interface", "router_search_criteria.alias", "foo");
			System.out.println(joins.toSQL());

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
			Islets islets0 = new Islets();

			islets0.getJoins(Islets.DUMMY, Islets.DUMMY)
			       .getQuery(Joins.DUMMY, Joins.DUMMY)
			       .addFromPart("DUAL")
			       .addWherePart("1==1")
			;

			System.out.println(islets0.toString());

			Islets islets1 = new Islets();

			QId qId1 = AutoJoinSingleton.resolveWithNestedSelect(
				islets1,
				"self",
				"router_ipv4_blocks",
				"continentCode",
				"EU"
			);

			QId qId2 = AutoJoinSingleton.resolveWithNestedSelect(
				islets1,
				"self",
				"router_ipv4_blocks",
				"router_locations.countryCode",
				"FR"
			);

			System.out.println(qId1);
			System.out.println(qId2);
			System.out.println(islets1.toString());

			Islets islets2 = new Islets();

			AutoJoinSingleton.resolveWithInnerJoins(
				islets2,
				"self",
				"router_ipv4_blocks",
				"continentCode",
				"EU"
			);

			AutoJoinSingleton.resolveWithInnerJoins(
				islets2,
				"self",
				"router_ipv4_blocks",
				"router_locations.countryCode",
				"FR"
			);

			System.out.println(qId1);
			System.out.println(qId2);
			System.out.println(islets2.toString());
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

			System.out.println(Command.parse("AddElement -foo=").arguments);

//			System.out.println(Command.parse("AddElement -foo=\"bar-bar").arguments);

//			System.out.println(Command.parse("AddElement-foo=\"bar-bar=\"f\\too\"").arguments);

//			System.out.println(Command.parse("AddElement -catalog=\"self\" -entity=\"router_ipv4_blocks\" -foo=\"bar\"").arguments);

//			System.out.println(Command.parse("UpdateElements -catalog=\"self\" -entity=\"router_catalog\" -separator=\"%\" -fields=\"custom\" -values=\"{\\\"APID\\\":{\\\"x\\\":25,\\\"y\\\":410,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"APID_BLOCK_BRIDGE\\\":{\\\"x\\\":20,\\\"y\\\":605,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK\\\":{\\\"x\\\":365,\\\"y\\\":635,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_CONTAINER\\\":{\\\"x\\\":290,\\\"y\\\":960,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_PARAM\\\":{\\\"x\\\":35,\\\"y\\\":910,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"BLOCK_TYPE\\\":{\\\"x\\\":30,\\\"y\\\":755,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"COMMAND\\\":{\\\"x\\\":410,\\\"y\\\":1180,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"COMMAND_PARAM\\\":{\\\"x\\\":85,\\\"y\\\":1295,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET\\\":{\\\"x\\\":1085,\\\"y\\\":855,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_BLOCK_ANNOTATION\\\":{\\\"x\\\":755,\\\"y\\\":985,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_COMMAND_BRIDGE\\\":{\\\"x\\\":735,\\\"y\\\":1195,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_CONTAINER\\\":{\\\"x\\\":1130,\\\"y\\\":460,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_FILE_BRIDGE\\\":{\\\"x\\\":1095,\\\"y\\\":1305,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_HIERARCHY\\\":{\\\"x\\\":1365,\\\"y\\\":980,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PARAM\\\":{\\\"x\\\":1355,\\\"y\\\":725,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PHASE_BRIDGE\\\":{\\\"x\\\":855,\\\"y\\\":265,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_PROCEDURE_BRIDGE\\\":{\\\"x\\\":765,\\\"y\\\":610,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"DATASET_TYPE\\\":{\\\"x\\\":745,\\\"y\\\":795,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"EXPERIMENT\\\":{\\\"x\\\":330,\\\"y\\\":0,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE\\\":{\\\"x\\\":745,\\\"y\\\":1360,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"FILE_PARAM\\\":{\\\"x\\\":745,\\\"y\\\":1535,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"INSTITUTE\\\":{\\\"x\\\":20,\\\"y\\\":15,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"INSTRUMENT\\\":{\\\"x\\\":330,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"LEVEL\\\":{\\\"x\\\":595,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"MODE\\\":{\\\"x\\\":320,\\\"y\\\":415,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PHASE\\\":{\\\"x\\\":630,\\\"y\\\":5,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PHYSICAL_FILE\\\":{\\\"x\\\":1055,\\\"y\\\":1495,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"PROCEDURE\\\":{\\\"x\\\":605,\\\"y\\\":420,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"},\\\"UNIT\\\":{\\\"x\\\":25,\\\"y\\\":215,\\\"topColor\\\":\\\"#0066CC\\\",\\\"bodyColor\\\":\\\"#FFFFFF\\\",\\\"strokeColor\\\":\\\"#0057AD\\\"}}\" -keyFields=\"externalCatalog\" -keyValues=\"radardb\"").arguments);

			//System.out.println("done.");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
