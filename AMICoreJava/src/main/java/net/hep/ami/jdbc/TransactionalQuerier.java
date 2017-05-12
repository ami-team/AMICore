package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

public class TransactionalQuerier implements Querier
{
	/*---------------------------------------------------------------------*/

	private long m_transactionId;

	private AbstractDriver m_driver;

	/*---------------------------------------------------------------------*/

	static
	{
		try
		{
			Class.forName("net.hep.ami.jdbc.CatalogSingleton");
		}
		catch(Exception e)
		{
			/* IGNORE */
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
	public RowSet executeQuery(String sql) throws Exception
	{
		return m_driver.executeQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql) throws Exception
	{
		return m_driver.executeMQLQuery(mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeUpdate(String sql) throws Exception
	{
		return m_driver.executeUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql) throws Exception
	{
		return m_driver.sqlPrepareStatement(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql, String[] columnNames) throws Exception
	{
		return m_driver.sqlPrepareStatement(sql, columnNames);
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
