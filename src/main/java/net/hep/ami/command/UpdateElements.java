package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

public class UpdateElements extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_catalog;
	private String m_entity;

	private String m_keyFields[];
	private String m_keyValues[];

	private String m_fields[];
	private String m_values[];

	/*---------------------------------------------------------------------*/

	public UpdateElements(Map<String, String> arguments, int transactionID) {
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

		m_keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator)
                                                         : new String[] {}
		;

		m_keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator)
                                                         : new String[] {}
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_catalog == null || m_entity == null || m_fields.length != m_values.length || m_keyFields.length != m_keyValues.length) {

			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		String sql = "UPDATE " + m_entity;

		/*-----------------------------------------------------------------*/

		if(m_fields.length > 0) {

			sql = sql.concat(" SET ");

			for(int i = 0; i < m_fields.length; i++) {

				sql = sql.concat(", `" + m_fields[i] + "`='" + m_values[i].replaceFirst("'", "''") + "'");
			}
		}

		if(m_keyFields.length > 0) {

			sql = sql.concat(" WHERE ");

			for(int i = 0; i < m_keyFields.length; i++) {

				sql = sql.concat(", `" + m_keyFields[i] + "`='" + m_keyValues[i].replaceFirst("'", "''") + "'");
			}
		}

		/*-----------------------------------------------------------------*/

		transactionalQuerier.executeUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Update elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "-catalog=\"value\" -entity=\"value\" (-separator=\"value\")? -fields=\"comma_separated_values\" -values=\"comma_separated_values\" (-keyFields=\"comma_separated_values\" -keyValues=\"comma_separated_values\")?";
	}

	/*---------------------------------------------------------------------*/
}
