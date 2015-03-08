package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

public class RemoveElements extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_catalog;
	private String m_entity;

	private String m_keyFields[];
	private String m_keyValues[];

	private String m_where;

	/*---------------------------------------------------------------------*/

	public RemoveElements(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_catalog = arguments.get("catalog");
		m_entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
		                                                      : ","
		;

		m_keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator)
		                                                 : new String[] {}
		;

		m_keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator)
		                                                 : new String[] {}
		;

		m_where = arguments.get("where");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_catalog == null || m_entity == null || m_keyFields.length != m_keyValues.length) {

			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		String sql = "DELETE FROM `" + m_entity + "`";

		/*-----------------------------------------------------------------*/

		if(m_keyFields.length > 0) {

			String part = "";

			for(int i = 0; i < m_keyFields.length; i++) {

				part = part.concat(",`" + m_keyFields[i] + "`='" + m_keyValues[i].replaceFirst("'", "''") + "'");
			}

			sql = sql.concat(" WHERE (" + part.substring(1) + ")");
		}

		/*-----------------------------------------------------------------*/

		if(m_where != null && m_where.isEmpty() == false) {

			if(m_keyFields.length > 0) {
				sql = sql.concat(" AND (" + m_where + ")");
			} else {
				sql = sql.concat(" WHERE (" + m_where + ")");
			}
		}

		/*-----------------------------------------------------------------*/

		transactionalQuerier.executeUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Remove elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "-catalog=\"value\" -entity=\"value\" (-separator=\"value\")? (-keyFields=\"comma_separated_values\" -keyValues=\"comma_separated_values\")? (-where=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
