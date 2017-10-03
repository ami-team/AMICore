package net.hep.ami.command.catalog;

import java.util.*;

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

		if(catalog == null || entity == null || keyFields.length == 0 || keyFields.length != keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Structure.Joins joins = new Structure.Joins();

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

		/*-----------------------------------------------------------------*/

		List<String> whereList = new ArrayList<>();

		for(String comp: joins.getJoin(Structure.DUMMY).toList())
		{
			comp = comp.substring(comp.indexOf('.') + 1);
			comp = comp.substring(comp.indexOf('.') + 1);

			whereList.add(comp);
		}

		/*-----------------------------------------------------------------*/

		if(where.isEmpty() == false)
		{
			whereList.add(where);
		}

		/*-----------------------------------------------------------------*/

		String sql = new StringBuilder().append("DELETE FROM `").append(entity).append("`").append(" WHERE ").append(String.join(" AND ", whereList)).toString();

		/*-----------------------------------------------------------------*/

		getQuerier(catalog).executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<sql><![CDATA[" + sql + "]]></sql><info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove one or more elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\"\")? -keyFields=\"\" -keyValues=\"\" (-where=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
