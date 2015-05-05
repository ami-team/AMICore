package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;

public class CommandSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		Map<String, String> arguments = new HashMap<String, String>();

		try {
			/*System.out.println(*/CatalogSingleton.listCatalogs()/*)*/;

			System.out.println(CommandSingleton.executeCommand("FindCommands", arguments));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
