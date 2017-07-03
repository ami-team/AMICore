package net.hep.ami;

import java.io.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.query.sql.Tokenizer;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;

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

		//System.out.println(CommandSingleton.executeCommand("GetSchemes", arguments).replace(">", ">\n"));

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
			SchemaSingleton.getDBSchemas();//.toString().replace(">", ">\n"));

			//System.out.println("done.");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
