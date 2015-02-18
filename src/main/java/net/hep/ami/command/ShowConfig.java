package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;

public class ShowConfig extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public ShowConfig(HashMap<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		return ConfigSingleton.showConfig();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Show configuration.";
	}

	/*---------------------------------------------------------------------*/
}
