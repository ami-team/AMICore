package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public class Encrypt extends CommandAbstractClass {

	private String m_string;

	/*---------------------------------------------------------------------*/

	public Encrypt(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_string = arguments.get("string");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_string == null) {

			throw new Exception("invalid usage");
		}

		return new StringBuilder("<info><![CDATA[" + Cryptography.encrypt(m_string) + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Encode a string.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "-string=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
