package net.hep.ami;

import java.util.*;

public class CommandSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		Map<String, String> arguments = new HashMap<String, String>();

		try {
			System.out.println(CommandSingleton.executeCommand("ListCommands", arguments));
			System.out.println(CommandSingleton.executeCommand("ListConverters", arguments));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
