package net.hep.ami.jdbc.driver.sql;

import java.sql.*;

import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:mariadb",
	clazz = "org.mariadb.jdbc.Driver",
	flags = DriverMetadata.FLAG_BACKSLASH_ESCAPE | DriverMetadata.FLAG_HAS_CATALOG | DriverMetadata.FLAG_HAS_DUAL
)

public class MariaDBDriver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public MariaDBDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(@NotNull String db, @NotNull String tz) throws Exception
	{
		if("UTC".equalsIgnoreCase(tz))
		{
			tz = "+00:00";
		}

		try(Statement statement = m_connection.createStatement())
		{
			statement.executeQuery("USE `" + db + "`");

			statement.executeQuery("SET time_zone = '" + tz + "'");

			statement.executeQuery("SET sql_mode = 'ANSI_QUOTES'");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql)
	{
		return sql; /* MySQL/MariaDB is the default */
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
