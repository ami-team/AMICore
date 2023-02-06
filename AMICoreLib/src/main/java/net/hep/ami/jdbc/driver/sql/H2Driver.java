package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

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
	public void setupSession(@NotNull String db, @NotNull String tz)
	{
		/* DO NOTHING */
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(@NotNull String sql)
	{
		return sql.replace("`" + this.m_internalCatalog + "`.","")/*BERK*/.replace("`","\"");
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
