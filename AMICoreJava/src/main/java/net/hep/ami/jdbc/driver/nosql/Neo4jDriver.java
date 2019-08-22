package net.hep.ami.jdbc.driver.nosql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

@DriverMetadata(
	type = DriverMetadata.Type.NoSQL,
	proto = "jdbc:neo4j",
	clazz = "org.neo4j.jdbc.Driver",
	flags = 0
)

public class Neo4jDriver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public Neo4jDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, boolean isAdmin, boolean links) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, isAdmin, links);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(String db, String tz) throws Exception
	{
		/* TODO */
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		return sql;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
