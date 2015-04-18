package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public class Decrypt extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_string;

	/*---------------------------------------------------------------------*/

	public Decrypt(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_string = arguments.get("string");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_string == null) {
			throw new Exception("invalid usage");
		}

		if(m_isSecure.equals("false")) {
			throw new Exception("https connection required"); 
		}

		return new StringBuilder("<info><![CDATA[" + Cryptography.decrypt(m_string) + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Decrypt a string.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "-string=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
