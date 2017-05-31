package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.command.*;

public class RemoveElements extends AbstractCommand
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
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
		                                                      : ","
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

		if(catalog == null || entity == null || keyFields.length != keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("DELETE FROM `" + entity + "`");

		/*-----------------------------------------------------------------*/

		boolean wherePresent = false;

		if(keyFields.length > 0)
		{
			/*-------------------------------------------------------------*/

			AutoJoinSingleton.AMIJoins joins = new AutoJoinSingleton.AMIJoins();

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

			String _where = joins.toSQL().where;

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

		querier.executeUpdate(sql);

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
		return "-catalog=\"\" -entity=\"\" (-separator=\"\")? (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
