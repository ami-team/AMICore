package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.command.*;

public class AddElement extends AbstractCommand
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

		if(catalog == null || entity == null || fields.length == 0 || fields.length != values.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Structure.Joins joins = new Structure.Joins();

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

		String[] parts;

		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();

		for(String assign: joins.getJoin(Structure.DUMMY).toList())
		{
			assign = assign.substring(assign.indexOf('.') + 1);
			assign = assign.substring(assign.indexOf('.') + 1);

			parts = assign.split("=", 2);

			list1.add(parts[0]);
			list2.add(parts[1]);
		}

		/*-----------------------------------------------------------------*/

		String sql = new StringBuilder().append("INSERT INTO `").append(entity).append("`").append(" (" + String.join(",", list1) + ") VALUES (" + String.join(",", list2) + ")").toString();

		/*-----------------------------------------------------------------*/

		getQuerier(catalog).executeSQLUpdate(sql);

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
		return "-catalog=\"\" -entity=\"\" (-separator=\"\")? -fields=\"\" -values=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
