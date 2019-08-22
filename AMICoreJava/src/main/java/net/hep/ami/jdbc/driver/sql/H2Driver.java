package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:h2",
	clazz = "org.h2.Driver",
	flags = DriverMetadata.FLAG_HAS_DUAL
)

public class H2Driver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public H2Driver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, boolean isAdmin, boolean links) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, isAdmin, links);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(String db, String tz)
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
