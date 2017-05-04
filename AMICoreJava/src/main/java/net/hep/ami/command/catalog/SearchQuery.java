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

		String sql = arguments.get("sql");
		String glite = arguments.get("glite");

		if(catalog == null || (sql == null && glite == null))
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		RowSet queryResult;

		if(sql != null)
		{
			queryResult = querier.executeQuery(sql);
		}
		else
		{
			queryResult = querier.executeMQLQuery(glite);
		}

		/*-----------------------------------------------------------------*/

		return queryResult.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple SQL or gLite query.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"value\" (-sql=\"value\" | -glite=\"value\")";
	}

	/*---------------------------------------------------------------------*/
}
