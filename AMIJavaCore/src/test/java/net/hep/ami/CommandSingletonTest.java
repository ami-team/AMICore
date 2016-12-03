package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.*;

public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception
	{
		Map<String, String> arguments = new HashMap<String, String>();

//		LogSingleton.defaultLogger.debug("Hello World!");

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
			RouterBuilder rb = new RouterBuilder("self", "jdbc:mysql://localhost:3306/router_test", "root", "root");

			rb.create();
			rb.fill();

			rb.commitAndRelease();

//			System.out.println(SchemaSingleton.getCatalogNames());
//			System.out.println(SchemaSingleton.getDBSchemes().toString().replace(">", ">\n"));

			System.out.println("done.");
		}
		catch(Exception e)
		{
			System.out.println("::" + e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
