package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

public class ListDrivers extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public ListDrivers(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_isSecure.equals("false")) {
			throw new Exception("https connection required"); 
		}

		return DriverSingleton.listDrivers();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "List drivers.";
	}

	/*---------------------------------------------------------------------*/
}
