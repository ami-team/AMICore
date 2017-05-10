package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.reflexion.*;

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
			Router router = new Router("test", "jdbc:mysql://localhost:3306/router_test", "root", "root");

			//router.create();
			//router.fill();

			router.commitAndRelease();

			Map<String, List<String>> joins;
/*
			joins = new LinkedHashMap<>();
			AutoJoinSingleton.resolveWithInnerJoins(joins, "self", "router_search_criteria", "catalog", "foo");
			System.out.println(joins);
			System.out.println(AutoJoinSingleton.joinsToSQL(joins));
*/
			joins = new LinkedHashMap<>();
			AutoJoinSingleton.resolveWithInnerJoins(joins, "self", "router_search_interface", "router_search_criteria.alias", "foo");
			System.out.println(joins);
			System.out.println(AutoJoinSingleton.joinsToSQL(joins));

			//System.out.println(SchemaSingleton.getCatalogNames());
			//System.out.println(SchemaSingleton.getDBSchemes().toString().replace(">", ">\n"));

			//CatalogSingleton.reload();

			//System.out.println(SchemaSingleton.getTableNames("self"));

//			System.out.println(CommandSingleton.executeCommand("GetSessionInfo", arguments).replace(">", ">\n"));

//			System.out.println(ConnectionPoolSingleton.getStatus());

//			System.out.println("done.");
		}
		catch(Exception e)
		{
			System.out.println("CommandSingletonTest: " + e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
