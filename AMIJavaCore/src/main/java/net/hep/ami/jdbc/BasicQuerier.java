package net.hep.ami.jdbc;

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
	public void executeSQLUpdate(String sql) throws Exception
	{
		m_driver.executeSQLUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void executeMQLUpdate(String mql) throws Exception
	{
		m_driver.executeMQLUpdate(mql);
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
