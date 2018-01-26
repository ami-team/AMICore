package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.mql.*;

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

		StringBuilder stringBuilder = new StringBuilder("DELETE");

		/*-----------------------------------------------------------------*/

		List<String> whereList = new ArrayList<>();

		for(int i = 0; i < keyFields.length; i++)
		{
			whereList.add(keyFields[i] + "=`" + keyValues[i].replace("'", "''") + "`");
		}

		/*-----------------------------------------------------------------*/

		if(where.isEmpty() == false)
		{
			whereList.add(where);
		}

		/*-----------------------------------------------------------------*/

		if(whereList.isEmpty() == false)
		{
			stringBuilder.append(" WHERE ").append(String.join(" AND ", whereList));
		}

		/*-----------------------------------------------------------------*/

		String mql = stringBuilder.toString();

		String sql = MQLToSQL.parseDelete(catalog, entity, mql);

		/*-----------------------------------------------------------------*/

		System.out.println(sql);
		int nb = 0;
		//int nb = getQuerier(catalog).executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<mql><![CDATA[" + mql + "]]></mql>")
		                          .append("<sql><![CDATA[" + sql + "]]></sql>")
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
