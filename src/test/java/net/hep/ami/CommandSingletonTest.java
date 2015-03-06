package net.hep.ami;

import java.util.*;

public class CommandSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		Map<String, String> arguments = new HashMap<String, String>();

		try {
			System.out.println(CommandSingleton.executeCommand("GetSessionInfo", arguments));
			System.out.println(CommandSingleton.executeCommand("GetSessionInfo", arguments));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
