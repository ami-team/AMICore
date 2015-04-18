package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.introspection.*;

public class ListFields extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_catalog;
	private String m_entity;

	/*---------------------------------------------------------------------*/

	public ListFields(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_catalog = arguments.get("catalog");
		m_entity = arguments.get("entity");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_catalog == null
		   ||
		   m_entity == null
		 ) {
			throw new Exception("invalid usage");
		}

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(String field: SchemaSingleton.getColumnNames(m_catalog, m_entity)) {

			result.append(
				"<row>"
				+
				"<field name=\"field\">" + field + "</field>"
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

		return "List fields.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "-catalog=\"value\" -entity=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
