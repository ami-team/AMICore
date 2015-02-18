package net.hep.ami.command.admin;

import java.util.*;
import java.util.Map.*;

import net.hep.ami.*;

public class Echo extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public Echo(HashMap<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		StringBuilder result = new StringBuilder();

		result.append("<Result><rowset><row>");

		for(Entry<String, String> entry: m_arguments.entrySet()) result.append("<field name=\"" + entry.getKey() + "\"><![CDATA[" + entry.getValue() + "]]></field>");

		result.append("</row></rowset></Result>");

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "A stupid command.";
	}

	/*---------------------------------------------------------------------*/
}
