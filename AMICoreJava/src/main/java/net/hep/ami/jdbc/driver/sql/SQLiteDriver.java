package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:sqlite",
	clazz = "org.sqlite.JDBC",
	flags = DriverMetadata.FLAG_HAS_DUAL
)

public class SQLiteDriver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SQLiteDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, boolean isAdmin, boolean links) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, isAdmin, links);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(@NotNull String db, @NotNull String tz)
	{
		/* DO NOTHING */
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql)
	{
		return sql;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
