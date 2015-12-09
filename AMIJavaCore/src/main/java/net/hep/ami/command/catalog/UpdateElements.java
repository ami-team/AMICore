package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.introspection.*;
import net.hep.ami.command.*;

public class UpdateElements extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_catalog;
	private String m_entity;

	private String[] m_fields;
	private String[] m_values;

	private String[] m_keyFields;
	private String[] m_keyValues;

	private String m_where;

	/*---------------------------------------------------------------------*/

	public UpdateElements(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);

		m_catalog = arguments.get("catalog");
		m_entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
		                                                      : ","
		;

		m_fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                           : new String[] {}
		;

		m_values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                           : new String[] {}
		;

		m_keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator, -1)
                                                         : new String[] {}
		;

		m_keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator, -1)
                                                         : new String[] {}
		;

		m_where = arguments.containsKey("where") ? arguments.get("where").trim()
		                                         : ""
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_catalog == null || m_entity == null || m_fields.length != m_values.length || m_keyFields.length != m_keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("UPDATE `" + m_entity + "` SET ");

		/*-----------------------------------------------------------------*/

		if(m_fields.length > 0)
		{
			String part = "";

			for(int i = 0; i < m_fields.length; i++)
			{
				part = part.concat(",`" + m_fields[i] + "`='" + m_values[i].replaceFirst("'", "''") + "'");
			}

			stringBuilder.append(part.substring(1));
		}

		/*-----------------------------------------------------------------*/

		boolean wherePresent = false;

		if(m_keyFields.length > 0)
		{
			Map<String, List<String>> joins = new HashMap<String, List<String>>();

			for(int i = 0; i < m_keyFields.length; i++)
			{
				AutoJoinSingleton.resolveWithNestedSelect(
					joins,
					m_catalog,
					m_entity,
					m_keyFields[i],
					m_keyValues[i]
				);
			}

			/*-------------------------------------------------------------*/

			String where = AutoJoinSingleton.joinsToSQL(joins).where;

			if(where.isEmpty() == false)
			{
				stringBuilder.append(" WHERE " + where);

				wherePresent = true;
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		if(m_where.isEmpty() == false)
		{
			if(wherePresent)
			{
				stringBuilder.append(" AND (" + m_where + ")");
			}
			else
			{
				stringBuilder.append(" WHERE (" + m_where + ")");
			}
		}

		/*-----------------------------------------------------------------*/

		String sql = stringBuilder.toString();

		/*-----------------------------------------------------------------*/

		transactionalQuerier.executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<sql><![CDATA[" + sql + "]]></sql><info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Update elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"value\" -entity=\"value\" (-separator=\"value\")? -fields=\"comma_separated_values\" -values=\"comma_separated_values\" (-keyFields=\"comma_separated_values\" -keyValues=\"comma_separated_values\")? (-where=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
