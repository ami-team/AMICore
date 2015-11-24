package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.driver.*;

public class BasicQuerier implements QuerierInterface
{
	/*---------------------------------------------------------------------*/

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public BasicQuerier(String catalog) throws Exception
	{
		m_driver = CatalogSingleton.getConnection(catalog);
	}

	/*---------------------------------------------------------------------*/

	public BasicQuerier(String jdbcUrl, String user, String pass) throws Exception
	{
		m_driver = DriverSingleton.getConnection(jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeSQLQuery(String sql) throws Exception
	{
		return m_driver.executeSQLQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql) throws Exception
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

	public void commit() throws Exception
	{
		m_driver.commit();
	}

	/*---------------------------------------------------------------------*/

	public void rollback() throws Exception
	{
		m_driver.rollback();
	}

	/*---------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception
	{
		m_driver.commitAndRelease();
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception
	{
		m_driver.rollbackAndRelease();
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
