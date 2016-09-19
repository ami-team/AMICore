package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.introspection.*;
import net.hep.ami.command.*;

public class RemoveElements extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public RemoveElements(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String m_catalog = arguments.get("catalog");
		String m_entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
		                                                      : ","
		;

		String[] m_keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator, -1)
		                                                          : new String[] {}
		;

		String[] m_keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator, -1)
		                                                          : new String[] {}
		;

		String m_where = arguments.containsKey("where") ? arguments.get("where").trim()
		                                                : ""
		;

		if(m_catalog == null || m_entity == null || m_keyFields.length != m_keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("DELETE FROM `" + m_entity + "`");

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

		transactionalQuerier.executeUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<sql><![CDATA[" + sql + "]]></sql><info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"value\" -entity=\"value\" (-separator=\"value\")? (-keyFields=\"comma_separated_values\" -keyValues=\"comma_separated_values\")? (-where=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
