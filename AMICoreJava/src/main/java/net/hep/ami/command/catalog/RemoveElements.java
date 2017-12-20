package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.structure.*;

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

		Islets islets = new Islets();

		for(int i = 0; i < keyFields.length; i++)
		{
			AutoJoinSingleton.resolveWithNestedSelect(
				islets,
				catalog,
				entity,
				keyFields[i],
				keyValues[i]
			);
		}

		/*-----------------------------------------------------------------*/

		List<String> whereList = new ArrayList<>();

		for(String comp: islets.toQuery().getWhereCollection())
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

		int nb = getQuerier(catalog).executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<sql><![CDATA[" + sql + "]]></sql>")
		                          .append("<info><![CDATA[" + nb + " element(s) removed with success]]></info>")
		;
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
