package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;

public class ListUsers extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public ListUsers(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_isSecure.equals("false")) {
			throw new Exception("https connection required"); 
		}

		return getQuerier("self").executeSQLQuery("SELECT * FROM `router_user` WHERE `valid`='1'").toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "List users.";
	}

	/*---------------------------------------------------------------------*/
}
