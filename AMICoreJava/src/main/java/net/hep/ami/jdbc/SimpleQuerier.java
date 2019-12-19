package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

public class SimpleQuerier implements Querier
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final AbstractDriver m_driver;

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	public SimpleQuerier(@NotNull String catalog) throws Exception
	{
		String AMIUser = ConfigSingleton.getProperty("admin_user", "admin");
		String timeZone = ConfigSingleton.getProperty("time_zone", "UTC");

		m_driver = CatalogSingleton.getConnection(catalog, AMIUser, timeZone, 0x00);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public SimpleQuerier(@NotNull String catalog, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		m_driver = CatalogSingleton.getConnection(catalog, AMIUser, timeZone, flags);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public SimpleQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		m_driver = DriverSingleton.getConnection(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setReadOnly(boolean readOnly) throws Exception
	{
		m_driver.setReadOnly(readOnly);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String mqlToSQL(@NotNull String entity, @NotNull String mql) throws Exception
	{
		return m_driver.mqlToSQL(entity, mql);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String mqlToAST(@NotNull String entity, @NotNull String mql) throws Exception
	{
		return m_driver.mqlToAST(entity, mql);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public RowSet executeMQLQuery(@NotNull String entity, @NotNull String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLQuery(entity, mql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public RowSet executeSQLQuery(@NotNull String entity, @NotNull String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLQuery(entity, sql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public RowSet executeRawQuery(@NotNull String entity, @NotNull String raw, Object... args) throws Exception
	{
		return m_driver.executeRawQuery(entity, raw, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Update executeMQLUpdate(@NotNull String entity, @NotNull String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLUpdate(entity, mql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Update executeSQLUpdate(@NotNull String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLUpdate(sql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Update executeRawUpdate(@NotNull String raw, Object... args) throws Exception
	{
		return m_driver.executeRawUpdate(raw, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public PreparedStatement preparedStatement(@NotNull String sql, boolean isRawQuery, boolean returnGeneratedKeys, @Nullable String[] columnNames) throws Exception
	{
		return m_driver.preparedStatement(sql, isRawQuery, returnGeneratedKeys, columnNames);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void commit() throws Exception
	{
		m_driver.commit();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void rollback() throws Exception
	{
		m_driver.rollback();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception
	{
		m_driver.commitAndRelease();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception
	{
		m_driver.rollbackAndRelease();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	@Deprecated
	public Connection getConnection()
	{
		return m_driver.getConnection();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	@Deprecated
	public Statement getStatement()
	{
		return m_driver.getStatement();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getInternalCatalog()
	{
		return m_driver.getInternalCatalog();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getExternalCatalog()
	{
		return m_driver.getExternalCatalog();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public DriverMetadata.Type getJdbcType()
	{
		return m_driver.getJdbcType();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getJdbcProto()
	{
		return m_driver.getJdbcProto();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getJdbcClass()
	{
		return m_driver.getJdbcClass();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	////////
	@Override
	public int getJdbcFlags()
	{
		return m_driver.getJdbcFlags();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getJdbcUrl()
	{
		return m_driver.getJdbcUrl();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getUser()
	{
		return m_driver.getUser();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getPass()
	{
		return m_driver.getPass();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
