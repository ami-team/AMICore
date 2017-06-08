package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class SearchQuery extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public SearchQuery(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String sql = arguments.get("sql");
		String mql = arguments.get("mql");

		if(catalog == null || (sql == null && (mql == null || entity == null)))
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		RowSet queryResult;

		if(sql != null)
		{
			queryResult = querier.executeSQLQuery(sql);
		}
		else
		{
			queryResult = querier.executeMQLQuery(entity, mql);
		}

		/*-----------------------------------------------------------------*/

		return queryResult.toStringBuilder().append("<sql><![CDATA[" + sql + "]]></sql>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple SQL or MQL query.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" (-sql=\"\" | (-entity=\"\" -mql=\"\"))";
	}

	/*---------------------------------------------------------------------*/
}
