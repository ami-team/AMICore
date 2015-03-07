package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;

public class ListCommands extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public ListCommands(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		return CommandSingleton.listCommands();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "List commands.";
	}

	/*---------------------------------------------------------------------*/
}
