package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
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

		if(catalog == null || entity == null || (keyFields.length == 0 && where.isEmpty()) || keyFields.length != keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		List<String> whereList = new ArrayList<>();

		for(int i = 0; i < keyFields.length; i++)
		{
			whereList.add("`" + keyFields[i].replace("`", "``") + "` = '" + keyValues[i].replace("'", "''") + "'");
		}

		/*-----------------------------------------------------------------*/

		if(where.isEmpty() == false)
		{
			whereList.add(where);
		}

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder("DELETE");

		if(whereList.isEmpty() == false)
		{
			stringBuilder.append(" WHERE ").append(String.join(" AND ", whereList));
		}

		/*-----------------------------------------------------------------*/

		String mql = stringBuilder.toString();

		/*-----------------------------------------------------------------*/

		Update result = getQuerier(catalog).executeMQLUpdate(entity, mql);

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
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
