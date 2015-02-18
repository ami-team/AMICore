package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;

public class RestartAMI extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public RestartAMI(HashMap<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		StringBuilder result = new StringBuilder();

		LogSingleton.log(LogSingleton.LogLevel.INFO, ConfigSingleton.m_tomcatPath);

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Restart AMI.";
	}

	/*---------------------------------------------------------------------*/
}
