package net.hep.ami;

import java.util.*;

public class CommandSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		Map<String, String> arguments = new HashMap<String, String>();

		try {
			arguments.put("catalog", "self");
			arguments.put("entity", "router_search_criteria");
			arguments.put("separator", ",");
			arguments.put("keyFields", "interface");//field
			arguments.put("keyValues", "user");//AMIPass
//			arguments.put("keyFields", "field");
//			arguments.put("keyValues", "AMIPass");
//			arguments.put("keyFields", "");
//			arguments.put("keyValues", "");
			arguments.put("fields", "field");
			arguments.put("values", "AMIPass2");
			System.out.println(CommandSingleton.executeCommand("UpdateElements", arguments));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
