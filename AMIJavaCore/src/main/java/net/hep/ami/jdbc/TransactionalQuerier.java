package net.hep.ami.jdbc;

import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionalQuerier implements QuerierInterface
{
	/*---------------------------------------------------------------------*/

	private int m_transactionID;

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(String catalog, int transactionID) throws Exception
	{
		m_driver = TransactionPoolSingleton.getConnection(catalog, m_transactionID = transactionID);
	}

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(String jdbcUrl, String user, String pass, int transactionID) throws Exception
	{
		m_driver = TransactionPoolSingleton.getConnection(jdbcUrl, user, pass, m_transactionID = transactionID);
	}

	/*---------------------------------------------------------------------*/

	public int getTransactionID()
	{
		return m_transactionID;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public QueryResult executeSQLQuery(String sql) throws Exception
	{
		return m_driver.executeSQLQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public QueryResult executeMQLQuery(String mql) throws Exception
	{
		return m_driver.executeMQLQuery(mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeSQLUpdate(String sql) throws Exception
	{
		return m_driver.executeSQLUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeMQLUpdate(String mql) throws Exception
	{
		return m_driver.executeMQLUpdate(mql);
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
