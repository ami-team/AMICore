package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;

public class ShowConfig extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public ShowConfig(Map<String, String> arguments, long transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_isSecure.equals("false")) {
			throw new Exception("https connection required"); 
		}

		return ConfigSingleton.showConfig();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Show configuration.";
	}

	/*---------------------------------------------------------------------*/
}
