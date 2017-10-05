package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.structure.*;

public class UpdateElements extends AbstractCommand
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

		if(catalog == null || entity == null || fields.length == 0 || fields.length != values.length || keyFields.length != keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Joins joins;

		/*-----------------------------------------------------------------*/

		joins = new Joins(catalog);

		for(int i = 0; i < fields.length; i++)
		{
			AutoJoinSingleton.resolveWithNestedSelect(
				joins,
				catalog,
				entity,
				fields[i],
				values[i]
			);
		}

		/*-----------------------------------------------------------------*/

		List<String> setList = new ArrayList<>();

		for(String comp: joins.getJoin(Joins.DUMMY, Joins.DUMMY).toList())
		{
			comp = comp.substring(comp.indexOf('.') + 1);
			comp = comp.substring(comp.indexOf('.') + 1);

			setList.add(comp);
		}

		/*-----------------------------------------------------------------*/

		joins = new Joins(catalog);

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

		for(String comp: joins.getJoin(Joins.DUMMY, Joins.DUMMY).toList())
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

		whereList.add("1=1");

		/*-----------------------------------------------------------------*/

		String sql = new StringBuilder().append("UPDATE `" + entity + "` SET ").append(String.join(",", setList)).append(" WHERE ").append(String.join(" AND ", whereList)).toString();

		/*-----------------------------------------------------------------*/

		getQuerier(catalog).executeSQLUpdate(sql);

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
		return "-catalog=\"\" -entity=\"\" (-separator=\"\")? -fields=\"\" -values=\"\" (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
