package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

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
			LogSingleton.root.error(LogSingleton.FATAL, "could not load `CatalogSingleton`", e);
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
	public void setReadOnly(boolean readOnly) throws Exception
	{
		m_driver.setReadOnly(readOnly);
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
	public RowSet executeRawQuery(String raw, Object... args) throws Exception
	{
		return m_driver.executeRawQuery(raw, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Update executeMQLUpdate(String entity, String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLUpdate(entity, mql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Update executeSQLUpdate(String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLUpdate(sql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Update executeRawUpdate(String raw, Object... args) throws Exception
	{
		return m_driver.executeRawUpdate(raw, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement prepareStatement(String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames) throws Exception
	{
		return m_driver.prepareStatement(sql, returnGeneratedKeys, columnNames);
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
	@Deprecated
	public Connection getConnection()
	{
		return m_driver.getConnection();
	}

	/*---------------------------------------------------------------------*/

	@Override
	@Deprecated
	public Statement getStatement()
	{
		return m_driver.getStatement();
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
	public DriverMetadata.Type getJdbcType()
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
