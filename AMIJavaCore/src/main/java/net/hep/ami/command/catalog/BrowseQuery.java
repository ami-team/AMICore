package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class BrowseQuery extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_catalog;

	private String m_sql;
	private String m_glite;

	/*---------------------------------------------------------------------*/

	public BrowseQuery(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);

		m_catalog = arguments.get("catalog");

		m_sql = arguments.get("sql");
		m_glite = arguments.get("glite");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_catalog == null || (m_sql == null && m_glite == null))
		{
			throw new Exception("invalid usage");
		}

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		/*-----------------------------------------------------------------*/

		QueryResult queryResult;

		if(m_sql != null)
		{
			queryResult = transactionalQuerier.executeSQLQuery(m_sql);
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
