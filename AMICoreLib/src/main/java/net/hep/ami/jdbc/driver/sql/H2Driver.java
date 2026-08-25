package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.ConfigSingleton;
import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

import java.sql.Statement;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:h2",
	clazz = "org.h2.Driver",
	flags = DriverMetadata.FLAG_HAS_DUAL
)

public class H2Driver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public H2Driver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(@NotNull String db, @NotNull String tz) throws Exception
	{
		try(Statement statement = m_connection.createStatement())
		{
			statement.setMaxRows(ConfigSingleton.getProperty("max_number_of_rows", 10000) + 1);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(@NotNull String sql)
	{
		return sql.replace("`" + this.m_internalCatalog + "`.","")/*BERK*/.replace("`","\"");
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
