package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.command.*;

public class UpdateElements extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public UpdateElements(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
		                                                      : ","
		;

		String[] fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                                  : new String[] {}
		;

		String[] values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                                  : new String[] {}
		;

		String[] keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator, -1)
		                                                        : new String[] {}
		;

		String[] keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator, -1)
		                                                        : new String[] {}
		;

		String where = arguments.containsKey("where") ? arguments.get("where").trim()
		                                              : ""
		;

		if(catalog == null || entity == null || fields.length != values.length || keyFields.length != keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("UPDATE `" + entity + "` SET ");

		/*-----------------------------------------------------------------*/

		if(fields.length > 0)
		{
			List<String> list = new ArrayList<String>();

			AutoJoinSingleton.SQLFieldValue fieldValue;

			for(int i = 0; i < fields.length; i++)
			{
				fieldValue = AutoJoinSingleton.resolveFieldValue(
					catalog,
					entity,
					fields[i],
					values[i]
				);

				list.add(fieldValue.field + "=" + fieldValue.value);
			}

			stringBuilder.append(String.join(",", list));
		}

		/*-----------------------------------------------------------------*/

		boolean wherePresent = false;

		if(keyFields.length > 0)
		{
			/*-------------------------------------------------------------*/

			Map<String, List<String>> joins = new HashMap<String, List<String>>();

			for(int i = 0; i < keyFields.length; i++)
			{
				AutoJoinSingleton.resolveWithNestedSelect(
					joins,
					catalog,
					entity,
					keyFields[i],
					keyValues[i]
				);
			}

			/*-------------------------------------------------------------*/

			String _where = AutoJoinSingleton.joinsToSQL(joins).where;

			if(_where.isEmpty() == false)
			{
				stringBuilder.append(" WHERE " + _where);

				wherePresent = true;
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		if(where.isEmpty() == false)
		{
			if(wherePresent)
			{
				stringBuilder.append(" AND (" + where + ")");
			}
			else
			{
				stringBuilder.append(" WHERE (" + where + ")");
			}
		}

		/*-----------------------------------------------------------------*/

		String sql = stringBuilder.toString();

		/*-----------------------------------------------------------------*/

		//transactionalQuerier.executeUpdate(sql);

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
