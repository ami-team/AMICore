package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class BrowseQuery extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public BrowseQuery(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String m_catalog = arguments.get("catalog");

		String m_sql = arguments.get("sql");
		String m_glite = arguments.get("glite");

		if(m_catalog == null || (m_sql == null && m_glite == null))
		{
			throw new Exception("invalid usage");
		}

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		RowSet queryResult;

		if(m_sql != null)
		{
			queryResult = transactionalQuerier.executeQuery(m_sql);
		}
		else
		{
			queryResult = transactionalQuerier.executeMQLQuery(m_glite);
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
