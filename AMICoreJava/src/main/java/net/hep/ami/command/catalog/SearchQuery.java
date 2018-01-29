package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.structure.*;

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

		String orderBy = arguments.get("orderBy");
		String orderWay = arguments.get("orderWay");

		String limit = arguments.get("limit");
		String offset = arguments.get("offset");

		if(catalog == null || (sql == null && (mql == null || entity == null)))
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		String extra = "";

		if(orderBy != null)
		{
			extra += " ORDER BY " + new QId(orderBy, QId.Deepness.COLUMN).toString();

			if(orderWay != null)
			{
				extra += " " + orderWay;
			}
		}

		if(limit != null)
		{
			extra += " LIMIT " + limit;

			if(offset != null)
			{
				extra += " OFFSET " + offset;
			}
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		RowSet result;

		if(sql != null)
		{
			result = querier.executeSQLQuery(sql + extra);
		}
		else
		{
			result = querier.executeMQLQuery(entity, mql + extra);
		}

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple SQL or MQL query (select mode).";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" (-sql=\"\" | (-entity=\"\" -mql=\"\")) (-orderBy=\"\" (-orderWay=\"\")?)? (-limit=\"\" (-offset=\"\")?)?";
	}

	/*---------------------------------------------------------------------*/
}
