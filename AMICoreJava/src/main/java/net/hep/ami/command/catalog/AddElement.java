package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.structure.*;

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

		String[] _fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                                   : new String[] {}
		;

		String[] _values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                                   : new String[] {}
		;

		if(catalog == null || entity == null || _fields.length == 0 || _fields.length != _values.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();

		for(int i = 0; i < _fields.length; i++)
		{
			fields.add(_fields[i]);
			values.add(_values[i]);
		}

		ExtraSingleton.patchFields(catalog, entity, fields, values, m_AMIUser, ExtraSingleton.Mode.ADD);

		/*-----------------------------------------------------------------*/

		Islets islets = new Islets();

		for(int i = 0; i < fields.size(); i++)
		{
			AutoJoinSingleton.resolveWithNestedSelect(
				islets,
				catalog,
				entity,
				fields.get(i),
				values.get(i)
			);
		}

		/*-----------------------------------------------------------------*/

		String[] parts;

		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();

		for(String assign: islets.toQuery().getWherePartSet())
		{
			assign = assign.substring(assign.indexOf('.') + 1);
			assign = assign.substring(assign.indexOf('.') + 1);

			parts = assign.split("=", 2);

			list1.add(parts[0]);
			list2.add(parts[1]);
		}


		/*-----------------------------------------------------------------*/

		String sql = new StringBuilder().append("INSERT INTO `").append(entity).append("`").append(" (" + String.join(",", list1) + ") VALUES (" + String.join(",", list2) + ")").toString();
		//System.out.println(sql);
		/*-----------------------------------------------------------------*/

		int id = getQuerier(catalog).executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<sql><![CDATA[" + sql + "]]></sql><id>" + id + "</id><info><![CDATA[done with success]]></info>");
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
