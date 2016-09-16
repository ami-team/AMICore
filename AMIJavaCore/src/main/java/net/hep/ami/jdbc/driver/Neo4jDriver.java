package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = DBType.NoSQL,
	clazz = "org.neo4j.jdbc.Driver",
	proto = "jdbc:neo4j"
)

public class Neo4jDriver extends DriverAbstractClass
{
	/*---------------------------------------------------------------------*/

	public Neo4jDriver(String jdbc_url, String user, String pass) throws Exception
	{
		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType jdbcTypeToAMIType(DriverInterface.FieldType fieldType) throws Exception
	{
		return fieldType;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType amiTypeToJDBCType(DriverInterface.FieldType fieldType) throws Exception
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
