package net.hep.ami.jdbc.driver;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.query.sql.Formatter;

import org.jetbrains.annotations.*;

/**
 * The parent class of all SQL and NoSQL drivers in AMI. Drivers must also implement the decorator <code>net.hep.ami.jdbc.driver.DriverMetadata</code>.
 */

public abstract class AbstractDriver implements Querier
{
	/*----------------------------------------------------------------------------------------------------------------*/

	protected final String m_externalCatalog;
	protected final String m_internalCatalog;
	protected int m_queryFlags = 0;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final DriverMetadata.Type m_jdbcType;
	protected final String m_jdbcProto;
	protected final String m_jdbcClass;
	protected final int    m_jdbcFlags;
	protected final String m_jdbcUrl;
	protected final String m_user;
	protected final String m_pass;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final String m_AMIUser;
	protected final String m_timeZone;
	protected final boolean m_isAdmin;
	protected final boolean m_links;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final Connection m_connection;
	protected final Statement m_statement;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final Map<String, Statement> m_statementMap = new HashMap<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param externalCatalog The external catalog name.
	 * @param internalCatalog The internal catalog name.
	 * @param jdbcUrl The JDBC URL.
	 * @param user The database user name.
	 * @param pass The database password.
	 */

	public AbstractDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, @NotNull String AMIUser, @NotNull String timeZone, boolean isAdmin, boolean links) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET JDBC ANNOTATION                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		DriverMetadata annotation = getClass().getAnnotation(DriverMetadata.class);

		if(annotation == null)
		{
			throw new Exception("annotation `Jdbc` not found for driver `" + getClass().getName() + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* SET CATALOG INFO                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		if(externalCatalog == null)
		{
			externalCatalog = "N/A";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		m_externalCatalog = externalCatalog;
		m_internalCatalog = internalCatalog;

		/*------------------------------------------------------------------------------------------------------------*/

		m_jdbcType = annotation.type();
		m_jdbcProto = annotation.proto();
		m_jdbcClass = annotation.clazz();
		m_jdbcFlags = annotation.flags();
		m_jdbcUrl = jdbcUrl;
		m_user = user;
		m_pass = pass;

		/*------------------------------------------------------------------------------------------------------------*/

		m_AMIUser = AMIUser;
		m_timeZone = timeZone;
		m_isAdmin = isAdmin;
		m_links = links;

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE CONNECTION                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		m_connection = ConnectionPoolSingleton.getConnection(
			m_externalCatalog,
			m_jdbcClass,
			m_jdbcUrl,
			m_user,
			m_pass
		);

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			m_connection.setReadOnly(false);
		}
		catch(SQLException e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE STATEMENT                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		m_statementMap.put("@", m_statement = m_connection.createStatement());

		/*------------------------------------------------------------------------------------------------------------*/
		/* SETUP SESSION                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		setupSession(internalCatalog, timeZone);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Puts this connection in read-only mode.
	 *
	 * @param readOnly `true` enables read-only mode; `false` disables it.
	 */

	public void setReadOnly(boolean readOnly) throws Exception
	{
		m_connection.setReadOnly(readOnly);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Setup the session.
	 *
	 * @param db The internal catalog.
	 * @param tz The time zone.
	 */

	public abstract void setupSession(String db, String tz) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Patches the given SQL.
	 * 
	 * @return The patched SQL;
	 */

	public abstract String patchSQL(String sql) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String mqlToSQL(String entity, String mql) throws Exception
	{
		if(m_jdbcType == DriverMetadata.Type.SQL)
		{
			return net.hep.ami.jdbc.query.mql.MQLToSQL.parse(m_externalCatalog, entity, m_AMIUser, m_isAdmin, mql);
		}
		else
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String mqlToAST(String entity, String mql) throws Exception
	{
		if(m_jdbcType == DriverMetadata.Type.SQL)
		{
			return net.hep.ami.jdbc.query.mql.MQLToAST.parse(m_externalCatalog, entity, m_AMIUser, m_isAdmin, mql);
		}
		else
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String entity, String mql, Object... args) throws Exception
	{
		String SQL = "";
		String AST = "";

		try
		{
			mql = Formatter.formatStatement(this, mql, args);

			SQL = mqlToSQL(entity, mql);
			AST = mqlToAST(entity, mql);

			return new RowSet(m_queryFlags, m_statement.executeQuery(patchSQL(SQL)), m_externalCatalog, entity, m_isAdmin, m_links, SQL, mql, AST);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql + " -> " + SQL, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public RowSet executeSQLQuery(@Nullable String entity, String sql, Object... args) throws Exception
	{
		try
		{
			sql = Formatter.formatStatement(this, sql, args);

			return new RowSet(m_queryFlags, m_statement.executeQuery(patchSQL(sql)), m_externalCatalog, entity, m_isAdmin, m_links, sql, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public RowSet executeRawQuery(@Nullable String entity, String raw, Object... args) throws Exception
	{
		try
		{
			raw = Formatter.formatStatement(this, raw, args);

			return new RowSet(m_queryFlags, m_statement.executeQuery(/*----*/(raw)), m_externalCatalog, entity, m_isAdmin, m_links, raw, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for RAW query: " + raw, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Update executeMQLUpdate(String entity, String mql, Object... args) throws Exception
	{
		String sql = "";
		String ast = "";

		try
		{
			mql = Formatter.formatStatement(this, mql, args);

			sql = mqlToSQL(entity, mql);
			ast = mqlToAST(entity, mql);

			return new Update(m_statement.executeUpdate(patchSQL(sql)), sql, mql, ast);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql + " -> " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Update executeSQLUpdate(String sql, Object... args) throws Exception
	{
		try
		{
			sql = Formatter.formatStatement(this, sql, args);

			return new Update(m_statement.executeUpdate(patchSQL(sql)), sql, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Update executeRawUpdate(String raw, Object... args) throws Exception
	{
		try
		{
			raw = Formatter.formatStatement(this, raw, args);

			return new Update(m_statement.executeUpdate(/*----*/(raw)), raw, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for RAW query: " + raw, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public PreparedStatement preparedStatement(String sql, boolean isRawQuery, boolean returnGeneratedKeys, @Nullable String[] columnNames) throws Exception
	{
		try
		{
			String SQL = !isRawQuery ? patchSQL(sql)
			                         : /*----*/(sql)
			;

			PreparedStatement result = (PreparedStatement) m_statementMap.get(SQL);

			if(result == null || result.isClosed())
			{
				m_statementMap.put(SQL, result = !returnGeneratedKeys ? (
						m_connection.prepareStatement(SQL)
					) : (
						(columnNames == null) ? (
							m_connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)
						) : (
							m_connection.prepareStatement(SQL, /*-----*/ columnNames /*-----*/)
						)
					)
				);
			}

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for " + (isRawQuery ? "RAW" : "SQL") + " query: " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void commit() throws Exception
	{
		if(!m_connection.getAutoCommit())
		{
			m_connection.commit();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void rollback() throws Exception
	{
		if(!m_connection.getAutoCommit())
		{
			m_connection.rollback();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception
	{
		try
		{
			if(!m_connection.getAutoCommit())
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
					if(!statement.isClosed())
					{
						statement.close();
					}
				}
				catch(Exception e)
				{
					LogSingleton.root.error(
						"could not close statement", e
					);
				}
			}

			m_connection.close();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception
	{
		try
		{
			if(!m_connection.getAutoCommit())
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
					if(!statement.isClosed())
					{
						statement.close();
					}
				}
				catch(Exception e)
				{
					LogSingleton.root.error(
						"could not close statement", e
					);
				}
			}

			m_connection.close();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * @deprecated (for internal use only)
	 */

	@Deprecated
	public Connection getConnection()
	{
		return m_connection;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * @deprecated (for internal use only)
	 */

	@Deprecated
	public Statement getStatement()
	{
		return m_statement;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String getExternalCatalog()
	{
		return m_externalCatalog;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String getInternalCatalog()
	{
		return m_internalCatalog;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public DriverMetadata.Type getJdbcType()
	{
		return m_jdbcType;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String getJdbcProto()
	{
		return m_jdbcProto;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String getJdbcClass()
	{
		return m_jdbcClass;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public int getJdbcFlags()
	{
		return m_jdbcFlags;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String getJdbcUrl()
	{
		return m_jdbcUrl;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String getUser()
	{
		return m_user;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String getPass()
	{
		return m_pass;
	}
	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setQueryFlags(int queryFlags)
	{
		m_queryFlags = queryFlags;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
