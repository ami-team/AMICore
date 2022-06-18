package net.hep.ami.jdbc.driver;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

/**
 * The parent class of all SQL and NoSQL drivers in AMI. Drivers must also implement the decorator <code>net.hep.ami.jdbc.driver.DriverMetadata</code>.
 */

public abstract class AbstractDriver implements Querier
{
	/*----------------------------------------------------------------------------------------------------------------*/

	protected final String m_externalCatalog;
	protected final String m_internalCatalog;

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
	protected final int m_flags;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final Connection m_connection;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final Map<String, PreparedStatement> m_statementMap = new HashMap<>();

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

	public AbstractDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
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
		m_flags = flags;

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE CONNECTION                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			m_connection = ConnectionPoolSingleton.getConnection(
				m_externalCatalog,
				m_jdbcClass,
				m_jdbcUrl,
				m_user,
				m_pass
			);
		}
		catch(SQLException e)
		{
			throw new SQLException(e.getMessage() + " (DD79E02F_E3F5_C0BF_350D_7AB71C194639)", e);
		}

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
		/* SETUP SESSION                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		setupSession(m_internalCatalog, m_timeZone);

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

	public abstract void setupSession(@NotNull String db, @NotNull String tz) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Patches the given SQL.
	 *
	 * @return The patched SQL;
	 */

	public abstract String patchSQL(@NotNull String sql) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String mqlToSQL(@NotNull String entity, @NotNull String mql) throws Exception
	{
		if(m_jdbcType == DriverMetadata.Type.SQL)
		{
			return net.hep.ami.jdbc.query.mql.MQLToSQL.parse(m_externalCatalog, entity, m_AMIUser, (m_flags & Querier.FLAG_ADMIN) != 0, mql);
		}
		else
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String mqlToAST(@NotNull String entity, @NotNull String mql) throws Exception
	{
		if(m_jdbcType == DriverMetadata.Type.SQL)
		{
			return net.hep.ami.jdbc.query.mql.MQLToAST.parse(m_externalCatalog, entity, m_AMIUser, (m_flags & Querier.FLAG_ADMIN) != 0, mql);
		}
		else
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(@NotNull String entity, @NotNull String mql, @NotNull Object[] args) throws Exception
	{
		String sql = "";
		String ast = "";

		try
		{
			sql = mqlToSQL(entity, mql);
			ast = mqlToAST(entity, mql);

			PreparedStatement statement = PreparedStatementFactory.createStatement(m_statementMap, m_connection, patchSQL(sql), false, null, true, args);

			return new RowSet(statement.executeQuery(), m_externalCatalog, entity, m_flags, sql, mql, ast);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql + " -> " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public RowSet executeSQLQuery(@Nullable String entity, @NotNull String sql, @NotNull Object[] args) throws Exception
	{
		try
		{
			PreparedStatement statement = PreparedStatementFactory.createStatement(m_statementMap, m_connection, patchSQL(sql), false, null, true, args);

			return new RowSet(statement.executeQuery(), m_externalCatalog, entity, m_flags, sql, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public RowSet executeRawQuery(@Nullable String entity, @NotNull String raw, @NotNull Object[] args) throws Exception
	{
		try
		{
			PreparedStatement statement = PreparedStatementFactory.createStatement(m_statementMap, m_connection, /*----*/(raw), false, null, true, args);

			return new RowSet(statement.executeQuery(), m_externalCatalog, entity, m_flags, raw, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for RAW query: " + raw, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Update executeMQLUpdate(@NotNull String entity, @NotNull String mql, @Nullable Object[] args) throws Exception
	{
		String sql = "";
		String ast = "";

		try
		{
			sql = mqlToSQL(entity, mql);
			ast = mqlToAST(entity, mql);

			PreparedStatement statement = PreparedStatementFactory.createStatement(m_statementMap, m_connection, patchSQL(sql), false, null, true, args);

			return new Update(statement.executeUpdate(), sql, mql, ast);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql + " -> " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Update executeSQLUpdate(@NotNull String entity, @NotNull String sql, @Nullable Object[] args) throws Exception
	{
		try
		{
			PreparedStatement statement = PreparedStatementFactory.createStatement(m_statementMap, m_connection, patchSQL(sql), false, null, true, args);

			return new Update(statement.executeUpdate(), sql, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Update executeRawUpdate(@NotNull String entity, @NotNull String raw, @Nullable Object[] args) throws Exception
	{
		try
		{
			PreparedStatement statement = PreparedStatementFactory.createStatement(m_statementMap, m_connection, /*----*/(raw), false, null, true, args);

			return new Update(statement.executeUpdate(), raw, null, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for RAW query: " + raw, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public PreparedStatement mqlPreparedStatement(@NotNull String entity, @NotNull String mql, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, @Nullable Object[] args) throws Exception
	{
		String sql = "";

		try
		{
			sql = mqlToSQL(entity, mql);

			return PreparedStatementFactory.createStatement(m_statementMap, m_connection, patchSQL(mql), returnGeneratedKeys, columnNames, injectArgs, args);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql + " -> " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPreparedStatement(@NotNull String entity, @NotNull String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, @Nullable Object[] args) throws Exception
	{
		try
		{
			return PreparedStatementFactory.createStatement(m_statementMap, m_connection, patchSQL(sql), returnGeneratedKeys, columnNames, injectArgs, args);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public PreparedStatement rawPreparedStatement(@NotNull String entity, @NotNull String raw, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, @Nullable Object[] args) throws Exception
	{
		try
		{
			return PreparedStatementFactory.createStatement(m_statementMap, m_connection, raw, returnGeneratedKeys, columnNames, injectArgs, args);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for RAW query: " + raw, e);
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

	@Override
	public Connection getConnection()
	{
		return m_connection;
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
}
