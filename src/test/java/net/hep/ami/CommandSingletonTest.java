package net.hep.ami;

import java.util.*;

public class CommandSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		Map<String, String> arguments = new HashMap<String, String>();

		try {
//			arguments.put("role", "toto");
//			arguments.put("parent", "AMI_guest_role");
			System.out.println(CommandSingleton.executeCommand("FindCommands", arguments));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
