package net.hep.ami.jdbc.driver.nosql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.NoSQL,
	proto = "jdbc:neo4j",
	clazz = "org.neo4j.jdbc.Driver"
)

public class Neo4jDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public Neo4jDriver(String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		return sql;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setDB(String db) throws Exception
	{
		/* TODO */
	}

	/*---------------------------------------------------------------------*/
}
