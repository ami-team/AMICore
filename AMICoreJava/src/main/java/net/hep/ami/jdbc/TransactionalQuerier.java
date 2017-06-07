package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionalQuerier implements Querier
{
	/*---------------------------------------------------------------------*/

	private final long m_transactionId;

	private final AbstractDriver m_driver;

	/*---------------------------------------------------------------------*/

	static
	{
		try
		{
			Class.forName("net.hep.ami.jdbc.CatalogSingleton");
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not load `CatalogSingleton`", e);
		}
	}

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(String catalog, long transactionId) throws Exception
	{
		m_driver = TransactionPoolSingleton.getConnection(catalog, m_transactionId = transactionId);
	}

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, long transactionId) throws Exception
	{
		m_driver = TransactionPoolSingleton.getConnection(externalCatalog, internalCatalog, jdbcUrl, user, pass, m_transactionId = transactionId);
	}

	/*---------------------------------------------------------------------*/

	public long getTransactionId()
	{
		return m_transactionId;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToSQL(String entity, String mql) throws Exception
	{
		return m_driver.mqlToSQL(entity, mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToAST(String entity, String mql) throws Exception
	{
		return m_driver.mqlToAST(entity, mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String entity, String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLQuery(entity, mql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeSQLQuery(String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLQuery(sql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeSQLUpdate(String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLUpdate(sql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement prepareStatement(String sql) throws Exception
	{
		return m_driver.prepareStatement(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws Exception
	{
		return m_driver.prepareStatement(sql, columnNames);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getInternalCatalog()
	{
		return m_driver.getInternalCatalog();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getExternalCatalog()
	{
		return m_driver.getExternalCatalog();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Jdbc.Type getJdbcType()
	{
		return m_driver.getJdbcType();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcProto()
	{
		return m_driver.getJdbcProto();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcClass()
	{
		return m_driver.getJdbcClass();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcUrl()
	{
		return m_driver.getJdbcUrl();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getUser()
	{
		return m_driver.getUser();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getPass()
	{
		return m_driver.getPass();
	}

	/*---------------------------------------------------------------------*/
}
