package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;

public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		Map<String, String> arguments = new HashMap<String, String>();

		LogSingleton.defaultLogger.error("Hello");

		System.out.println(CatalogSingleton.listCatalogs().toString().replace(">", ">\n"));

		System.out.println(CommandSingleton.executeCommand("GetSessionInfo", arguments).replace(">", ">\n"));

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
