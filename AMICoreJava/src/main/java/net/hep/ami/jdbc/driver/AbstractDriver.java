package net.hep.ami.jdbc.driver;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.mql.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.driver.annotation.*;

public abstract class AbstractDriver implements Querier
{
	/*---------------------------------------------------------------------*/

	protected final String m_externalCatalog;
	protected final String m_internalCatalog;

	/*---------------------------------------------------------------------*/

	protected final Jdbc.Type m_jdbcType;
	protected final String m_jdbcProto;
	protected final String m_jdbcClass;
	protected final String m_jdbcUrl;
	protected final String m_user;
	protected final String m_pass;

	/*---------------------------------------------------------------------*/

	private final Connection m_connection;

	private final Statement m_statement;

	/*---------------------------------------------------------------------*/

	private final Map<String, Statement> m_statementMap = new HashMap<>();

	/*---------------------------------------------------------------------*/

	public AbstractDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET JDBC ANNOTATION                                             */
		/*-----------------------------------------------------------------*/

		Jdbc annotation = getClass().getAnnotation(Jdbc.class);

		if(annotation == null)
		{
			throw new Exception("annotation `Jdbc` not found for driver `" + getClass().getName() + "`");
		}

		/*-----------------------------------------------------------------*/
		/* SET CATALOG INFO                                                */
		/*-----------------------------------------------------------------*/

		if(externalCatalog == null)
		{
			externalCatalog = SchemaSingleton.internalCatalogToExternalCatalog_noException(internalCatalog);

			if(externalCatalog == null)
			{
				externalCatalog = /*--------------------------------------------------------*/(internalCatalog);
			}
		}

		/*-----------------------------------------------------------------*/

		m_externalCatalog = externalCatalog;
		m_internalCatalog = internalCatalog;

		/*-----------------------------------------------------------------*/

		m_jdbcType = annotation.type();
		m_jdbcProto = annotation.proto();
		m_jdbcClass = annotation.clazz();
		m_jdbcUrl = jdbcUrl;
		m_user = user;
		m_pass = pass;

		/*-----------------------------------------------------------------*/
		/* CREATE CONNECTION                                               */
		/*-----------------------------------------------------------------*/

		m_connection = ConnectionPoolSingleton.getConnection(
			m_externalCatalog,
			m_jdbcClass,
			m_jdbcUrl,
			m_user,
			m_pass
		);

		/*-----------------------------------------------------------------*/
		/* CREATE STATEMENT                                                */
		/*-----------------------------------------------------------------*/

		m_statementMap.put("@", m_statement = m_connection.createStatement());

		/*-----------------------------------------------------------------*/
		/* SET DB                                                          */
		/*-----------------------------------------------------------------*/

		setDB(internalCatalog);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public abstract String patchSQL(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract void setDB(String db) throws Exception;

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToSQL(String mql, String entity) throws Exception
	{
		if(m_jdbcType == Jdbc.Type.SQL)
		{
			return patchSQL(MQLToSQL.parse(mql, this.m_externalCatalog, entity));
		}
		else
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToAST(String mql, String entity) throws Exception
	{
		if(m_jdbcType == Jdbc.Type.SQL)
		{
			return /*----*/(MQLToAST.parse(mql, this.m_externalCatalog, entity));
		}
		else
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql, String entity) throws Exception
	{
		try
		{
			String SQL = mqlToSQL(mql, entity);
			String AST = mqlToAST(mql, entity);

			return new RowSet(m_statement.executeQuery(SQL), SQL, mql, AST);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeQuery(String sql) throws Exception
	{
		try
		{
			String SQL = patchSQL(sql);
			String AST =     null     ;

			return new RowSet(m_statement.executeQuery(SQL), SQL, null, AST);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeUpdate(String sql) throws Exception
	{
		try
		{
			return m_statement.executeUpdate(patchSQL(sql));
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement prepareStatement(String sql) throws Exception
	{
		try
		{
			String SQL = patchSQL(sql);

			PreparedStatement result = (PreparedStatement) m_statementMap.get(SQL);

			if(result == null)
			{
				m_statementMap.put(SQL, result = m_connection.prepareStatement(SQL));
			}

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws Exception
	{
		try
		{
			String SQL = patchSQL(sql);

			PreparedStatement result = (PreparedStatement) m_statementMap.get(SQL);

			if(result == null)
			{
				m_statementMap.put(SQL, result = m_connection.prepareStatement(SQL, columnNames));
			}

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
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
			for(Statement statement: m_statementMap.values())
			{
				try
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
				catch(Exception e)
				{
					LogSingleton.root.error(
						e.getMessage(), e
					);
				}
			}

			m_connection.close();
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
			for(Statement statement: m_statementMap.values())
			{
				try
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
				catch(Exception e)
				{
					LogSingleton.root.error(
						e.getMessage(), e
					);
				}
			}

			m_connection.close();
		}
	}

	/*---------------------------------------------------------------------*/

	/**
	 * @deprecated (for internal use only)
	 */

	@Deprecated
	public Connection getConnection()
	{
		return m_connection;
	}

	/*---------------------------------------------------------------------*/

	/**
	 * @deprecated (for internal use only)
	 */

	@Deprecated
	public Statement getStatement()
	{
		return m_statement;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getExternalCatalog()
	{
		return m_externalCatalog;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getInternalCatalog()
	{
		return m_internalCatalog;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Jdbc.Type getJdbcType()
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
