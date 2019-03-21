package net.hep.ami.jdbc.driver.nosql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

@DriverMetadata(
	type = DriverMetadata.Type.NoSQL,
	proto = "jdbc:neo4j",
	clazz = "org.neo4j.jdbc.Driver"
)

public class Neo4jDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public Neo4jDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String AMIUser, boolean isAdmin, boolean links) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, isAdmin, links);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setDB(String db) throws Exception
	{
		/* TODO */
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		return sql;
	}

	/*---------------------------------------------------------------------*/
}
