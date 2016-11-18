package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.*;

public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		Map<String, String> arguments = new HashMap<String, String>();

		LogSingleton.defaultLogger.debug("Hello World!");

		CatalogSingleton.listCatalogs();

//		SchemaSingleton.readMetaData("self");

		//System.out.println(CommandSingleton.executeCommand("GetSchemes", arguments).replace(">", ">\n"));

		System.out.println("--------");

		//System.out.println(CommandSingleton.executeCommand("ListCatalogs", arguments).replace(">", ">\n"));

		System.out.println("--------");

		System.out.println("interface,entity,field,alias,type,rank,mask,defaultValue".split(",").length);

		String[] h = "users,router_user,lastname,,1,0,0,".split(",", -1);

		System.out.println(h.length);

		for(String x: h)
		{
			System.out.println("->" + x + "<-");
		}

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
