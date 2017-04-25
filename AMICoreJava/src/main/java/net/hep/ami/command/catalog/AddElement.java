package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.*;
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
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
		                                                      : ","
		;

		String fields[] = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                                  : new String[] {}
		;

		String values[] = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                                  : new String[] {}
		;

		if(catalog == null || entity == null || fields.length != values.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("INSERT INTO `" + entity + "`");

		/*-----------------------------------------------------------------*/

		if(fields.length > 0)
		{
			List<String> list1 = new ArrayList<>();
			List<String> list2 = new ArrayList<>();

			AutoJoinSingleton.SQLFieldValue fieldValue;

			for(int i = 0; i < fields.length; i++)
			{
				fieldValue = AutoJoinSingleton.resolveFieldValue(
					catalog,
					entity,
					fields[i],
					values[i]
				);

				list1.add(fieldValue.field);
				list2.add(fieldValue.value);
			}

			stringBuilder.append(" (" + String.join(",", list1) + ") VALUES (" + String.join(",", list2) + ")");
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
