package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionalQuerier implements QuerierInterface
{
	/*---------------------------------------------------------------------*/

	private long m_transactionId;

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(String catalog, long transactionId) throws Exception
	{
		m_driver = TransactionPoolSingleton.getConnection(catalog, m_transactionId = transactionId);
	}

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(String jdbcUrl, String user, String pass, long transactionId) throws Exception
	{
		m_driver = TransactionPoolSingleton.getConnection(jdbcUrl, user, pass, m_transactionId = transactionId);
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
	public int executeMQLUpdate(String mql) throws Exception
	{
		return m_driver.executeMQLUpdate(mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql) throws Exception
	{
		return m_driver.sqlPrepareStatement(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement mqlPrepareStatement(String mql) throws Exception
	{
		return m_driver.mqlPrepareStatement(mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql, String columnNames[]) throws Exception
	{
		return m_driver.sqlPrepareStatement(sql, columnNames);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement mqlPrepareStatement(String mql, String columnNames[]) throws Exception
	{
		return m_driver.mqlPrepareStatement(mql, columnNames);
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
	public DBType getJdbcType()
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
