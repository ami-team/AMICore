package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

public class AddElement extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_catalog;
	private String m_entity;

	private String[] m_fields;
	private String[] m_values;

	/*---------------------------------------------------------------------*/

	public AddElement(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_catalog = arguments.get("catalog");
		m_entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
		                                                      : ","
		;

		m_fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator)
		                                           : new String[] {}
		;

		m_values = arguments.containsKey("values") ? arguments.get("values").split(separator)
		                                           : new String[] {}
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_catalog == null || m_entity == null || m_fields.length != m_values.length) {

			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("INSERT INTO `" + m_entity + "`");

		/*-----------------------------------------------------------------*/

		if(m_fields.length > 0) {

			String part1 = "";
			String part2 = "";

			for(int i = 0; i < m_fields.length; i++) {

				part1 = part1.concat(",`" + m_fields[i] + "`");

				part2 = part2.concat(",'" + m_values[i].replaceFirst("'", "''") + "'");
			}

			stringBuilder.append(" (" + part1.substring(1) + ") VALUES (" + part2.substring(1) + ")");
		}

		/*-----------------------------------------------------------------*/

		String sql = stringBuilder.toString();

		/*-----------------------------------------------------------------*/

		transactionalQuerier.executeUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<sql><![CDATA[" + sql + "]]></sql><info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Add element.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "-catalog=\"value\" -entity=\"value\" (-separator=\"value\")? -fields=\"comma_separated_values\" -values=\"comma_separated_values\"";
	}

	/*---------------------------------------------------------------------*/
}
