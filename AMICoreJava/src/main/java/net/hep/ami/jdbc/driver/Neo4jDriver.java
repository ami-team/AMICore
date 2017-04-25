package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.NoSQL,
	proto = "jdbc:neo4j",
	clazz = "org.neo4j.jdbc.Driver"
)

public class Neo4jDriver extends DriverAbstractClass
{
	/*---------------------------------------------------------------------*/

	public Neo4jDriver(String name, String jdbc_url, String user, String pass) throws Exception
	{
		super(name, jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType jdbcTypeToAMIType(FieldType fieldType) throws Exception
	{
		return fieldType;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType amiTypeToJDBCType(FieldType fieldType) throws Exception
	{
		return fieldType;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patch(String sql) throws Exception
	{
		return sql;
	}

	/*---------------------------------------------------------------------*/
}
