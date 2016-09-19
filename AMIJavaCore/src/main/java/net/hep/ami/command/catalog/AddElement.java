package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.introspection.*;
import net.hep.ami.command.*;

public class AddElement extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public AddElement(Map<String, String> arguments, long transactionId)
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

		String m_fields[] = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                                    : new String[] {}
		;

		String m_values[] = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                                    : new String[] {}
		;

		if(m_catalog == null || m_entity == null || m_fields.length != m_values.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("INSERT INTO `" + m_entity + "`");

		/*-----------------------------------------------------------------*/

		if(m_fields.length > 0)
		{
			String part1 = "";
			String part2 = "";

			AutoJoinSingleton.SQLFieldValue fieldValue;

			for(int i = 0; i < m_fields.length; i++)
			{
				fieldValue = AutoJoinSingleton.resolveFieldValue(
					m_catalog,
					m_entity,
					m_fields[i],
					m_values[i]
				);

				part1 = part1.concat("," + fieldValue.field);

				part2 = part2.concat("," + fieldValue.value);
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

	public static String help()
	{
		return "Add element.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"value\" -entity=\"value\" (-separator=\"value\")? -fields=\"comma_separated_values\" -values=\"comma_separated_values\"";
	}

	/*---------------------------------------------------------------------*/
}
