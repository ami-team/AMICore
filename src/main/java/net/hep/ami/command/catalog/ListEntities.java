package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.SchemaSingleton;

public class ListEntities extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_catalog;

	/*---------------------------------------------------------------------*/

	public ListEntities(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_catalog = arguments.get("catalog");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_catalog == null) {

			throw new Exception("invalid usage");
		}

		if(m_catalog.equals("self")) {

		}

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(String entity: SchemaSingleton.getTableList(m_catalog)) {

			result.append(
				"<row>"
				+
				"<field name=\"entity\">" + entity + "</field>"
				+
				"</row>"
			);
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "List Entities.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "-catalog=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
