package net.hep.ami.jdbc.driver;

import java.sql.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.mql.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.introspection.*;
import net.hep.ami.jdbc.driver.annotation.*;

public abstract class DriverAbstractClass implements QuerierInterface, DriverInterface
{
	/*---------------------------------------------------------------------*/

	protected Connection m_connection;
	protected Statement m_statement;

	/*---------------------------------------------------------------------*/

	protected String m_internalCatalog;
	protected String m_externalCatalog;
	protected String m_jdbcProto;
	protected String m_jdbcClass;
	protected String m_jdbcUrl;
	protected String m_user;
	protected String m_pass;

	/*---------------------------------------------------------------------*/

	public DriverAbstractClass(String jdbcUrl, String user, String pass) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Jdbc annotation = getClass().getAnnotation(Jdbc.class);

		if(annotation == null)
		{
			throw new Exception("no `Jdbc` annotation for driver `" + getClass().getName() + "`");
		}

		String jdbcProto = annotation.proto();
		String jdbcClass = annotation.clazz();

		/*-----------------------------------------------------------------*/
		/* GET CONNECTION                                                  */
		/*-----------------------------------------------------------------*/

		m_jdbcProto = jdbcProto;
		m_jdbcClass = jdbcClass;
		m_jdbcUrl = jdbcUrl;
		m_user = user;
		m_pass = pass;

		m_connection = ConnectionPoolSingleton.getConnection(
			m_jdbcClass,
			m_jdbcUrl,
			m_user,
			m_pass
		);

		m_connection.setAutoCommit(false);

		/*-----------------------------------------------------------------*/
		/* GET STATEMENT                                                   */
		/*-----------------------------------------------------------------*/

		m_statement = m_connection.createStatement();

		/*-----------------------------------------------------------------*/
		/* GET CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		m_internalCatalog = m_connection.getCatalog();

		try
		{
			m_externalCatalog = SchemaSingleton.internalCatalogToExternalCatalog(m_internalCatalog);
		}
		catch(Exception e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@Override
	public QueryResult executeSQLQuery(String sql) throws Exception
	{
		return new QueryResult(
			m_statement.executeQuery(sql)
		);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public QueryResult executeMQLQuery(String mql) throws Exception
	{
		String ast = /* TODO */ null /* TODO */;
		String sql = SelectParser.parse(mql, this);

		return new QueryResult(
			m_statement.executeQuery(sql), ast, sql
		);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeSQLUpdate(String sql) throws Exception
	{
		return m_statement.executeUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeMQLUpdate(String mql) throws Exception
	{
		String sql = UpdateParser.parse(mql, this);

		return m_statement.executeUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	public void commit() throws Exception
	{
		if(m_connection.getAutoCommit() == false)
		{
			m_connection.commit();
		}
	}

	/*---------------------------------------------------------------------*/

	public void rollback() throws Exception
	{
		if(m_connection.getAutoCommit() == false)
		{
			m_connection.rollback();
		}
	}

	/*---------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception
	{
		try
		{
			if(m_connection.getAutoCommit() == false)
			{
				m_connection.commit();
			}
		}
		finally
		{
			try
			{
				m_statement.close();
			}
			finally
			{
				m_connection.close();
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception
	{
		try
		{
			if(m_connection.getAutoCommit() == false)
			{
				m_connection.rollback();
			}
		}
		finally
		{
			try
			{
				m_statement.close();
			}
			finally
			{
				m_connection.close();
			}
		}
	}

	/*---------------------------------------------------------------------*/

	@Deprecated
	public Connection getConnection()
	{
		return m_connection;
	}

	/*---------------------------------------------------------------------*/

	@Deprecated
	public Statement getStatement()
	{
		return m_statement;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getInternalCatalog()
	{
		return m_internalCatalog;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getExternalCatalog()
	{
		return m_externalCatalog;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcProto()
	{
		return m_jdbcProto;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcClass()
	{
		return m_jdbcClass;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcUrl()
	{
		return m_jdbcUrl;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getUser()
	{
		return m_user;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getPass()
	{
		return m_pass;
	}

	/*---------------------------------------------------------------------*/
}
