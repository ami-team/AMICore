package net.hep.ami;

import java.util.HashMap;

public class CommandSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		HashMap<String, String> arguments = new HashMap<String, String>();

		try {
//			System.out.println(CommandSingleton.executeCommand("ListCommands", arguments));
//			System.out.println(CommandSingleton.executeCommand("ListConverters", arguments));
			System.out.println(CommandSingleton.executeCommand("RestartAMI", arguments));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
