package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

public class SimpleQuerier implements Querier
{
	/*---------------------------------------------------------------------*/

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
			LogSingleton.root.error(LogSingleton.FATAL, e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	public SimpleQuerier(String catalog) throws Exception
	{
		m_driver = CatalogSingleton.getConnection(catalog);
	}

	/*---------------------------------------------------------------------*/

	public SimpleQuerier(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		m_driver = DriverSingleton.getConnection(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToSQL(String mql, String entity) throws Exception
	{
		return m_driver.mqlToSQL(mql, entity);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToAST(String mql, String entity) throws Exception
	{
		return m_driver.mqlToAST(mql, entity);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql, String entity) throws Exception
	{
		return m_driver.executeMQLQuery(mql, entity);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeQuery(String sql) throws Exception
	{
		return m_driver.executeQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeUpdate(String sql) throws Exception
	{
		return m_driver.executeUpdate(sql);
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
