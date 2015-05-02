package net.hep.ami.command.cloud;

import java.util.*;

import net.hep.ami.*;

public class Foo extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public Foo(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		StringBuilder result = new StringBuilder();

		/* TODO */

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "";
	}

	/*---------------------------------------------------------------------*/
}
