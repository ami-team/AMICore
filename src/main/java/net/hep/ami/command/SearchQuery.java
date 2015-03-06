package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.QueryResult;
import net.hep.ami.jdbc.TransactionalQuerier;

public class SearchQuery extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_sql;
	private String m_glite;
	private String m_catalog;

	/*---------------------------------------------------------------------*/

	public SearchQuery(Map<String, String> arguments, int transactionID) throws Exception {
		super(arguments, transactionID);

		m_sql = arguments.get("sql");
		m_glite = arguments.get("glite");
		m_catalog = arguments.get("catalog");

		if(m_sql == null || (m_glite == null && m_catalog == null)) {

			throw new Exception("invalid usage");
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		TransactionalQuerier transactionalQuerier = getQuerier(m_catalog);

		QueryResult queryResult;

		/****/ if(m_sql != null) {
			queryResult = transactionalQuerier.executeQuery(m_sql);

		} else if(m_glite != null) {
			queryResult = transactionalQuerier.executeGLiteQuery(m_sql);

		} else {
			throw new Exception();
		}

		return queryResult.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "???";
	}

	/*---------------------------------------------------------------------*/
}
