package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;

public class CommandSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		Map<String, String> arguments = new HashMap<String, String>();

		try {
			/*System.out.println(*/CatalogSingleton.listCatalogs()/*)*/;

			//System.out.println(CommandSingleton.executeCommand("CloudListServers", arguments));

			arguments.put("catalog", "self");
			arguments.put("glite", "SELECT `router_command`.* WHERE (`router_command`.`command`='GetSessionInfo')");
			System.out.println(CommandSingleton.executeCommand("BrowseQuery", arguments));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
