package net.hep.ami.jdbc.driver;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.mql.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.driver.annotation.*;

public abstract class DriverAbstractClass implements QuerierInterface, DriverInterface
{
	/*---------------------------------------------------------------------*/

	protected Connection m_connection;

	protected final List<Statement> m_statementList = new ArrayList<Statement>();

	/*---------------------------------------------------------------------*/

	protected String m_internalCatalog;
	protected String m_externalCatalog;

	protected DBType m_jdbcType;
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

		DBType jdbcType = annotation.type();
		String jdbcProto = annotation.proto();
		String jdbcClass = annotation.clazz();

		/*-----------------------------------------------------------------*/
		/* GET CONNECTION                                                  */
		/*-----------------------------------------------------------------*/

		m_jdbcType = jdbcType;
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

		m_statementList.add(m_connection.createStatement());

		/*-----------------------------------------------------------------*/
		/* GET CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		try
		{
			m_externalCatalog = SchemaSingleton.internalCatalogToExternalCatalog(m_internalCatalog = m_connection.getCatalog());
		}
		catch(Exception e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public abstract String patch(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeQuery(String sql) throws Exception
	{
		try
		{
			return new RowSet(
				m_statementList.get(0).executeQuery(patch(sql))
			);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql) throws Exception
	{
		if(m_jdbcType != DBType.SQL)
		{
			throw new Exception("MQL not supported");
		}

		try
		{
			String ast = /* TODO */ null /* TODO */;
			String sql = SelectParser.parse(mql, this);

			return new RowSet(
				m_statementList.get(0).executeQuery(patch(sql)), ast, sql
			);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeUpdate(String sql) throws Exception
	{
		try
		{
			return m_statementList.get(0).executeUpdate(patch(sql));
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeMQLUpdate(String mql) throws Exception
	{
		if(m_jdbcType != DBType.SQL)
		{
			throw new Exception("MQL not supported");
		}

		try
		{
			return m_statementList.get(0).executeUpdate(patch(UpdateParser.parse(mql, this)));
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql) throws Exception
	{
		try
		{
			PreparedStatement result = m_connection.prepareStatement(patch(sql));

			m_statementList.add(result);

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement mqlPrepareStatement(String mql) throws Exception
	{
		try
		{
			PreparedStatement result = m_connection.prepareStatement(patch(UpdateParser.parse(mql, this)));

			m_statementList.add(result);

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql, String columnNames[]) throws Exception
	{
		try
		{
			PreparedStatement result = m_connection.prepareStatement(patch(sql), columnNames);

			m_statementList.add(result);

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement mqlPrepareStatement(String mql, String columnNames[]) throws Exception
	{
		try
		{
			PreparedStatement result = m_connection.prepareStatement(patch(UpdateParser.parse(mql, this)), columnNames);

			m_statementList.add(result);

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query " + mql);
		}
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
				for(Statement statement: m_statementList)
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
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
				for(Statement statement: m_statementList)
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
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
	public DBType getJdbcType()
	{
		return m_jdbcType;
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
