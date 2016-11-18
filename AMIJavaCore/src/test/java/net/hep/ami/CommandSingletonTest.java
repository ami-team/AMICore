package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;

public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		@SuppressWarnings("unused")
		Map<String, String> arguments = new HashMap<String, String>();

		LogSingleton.defaultLogger.debug("Hello World!");

		CatalogSingleton.listCatalogs();

//		SchemaSingleton.readMetaData("self");

		//System.out.println(CommandSingleton.executeCommand("GetSchemes", arguments).replace(">", ">\n"));

		System.out.println("--------");

		//System.out.println(CommandSingleton.executeCommand("ListCatalogs", arguments).replace(">", ">\n"));

		System.out.println("--------");

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
		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
