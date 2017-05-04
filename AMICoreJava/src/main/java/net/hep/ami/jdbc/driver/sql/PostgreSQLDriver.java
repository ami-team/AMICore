package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.SQL,
	proto = "jdbc:postgresql",
	clazz = "org.postgresql.Driver"
)

public class PostgreSQLDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public PostgreSQLDriver(String name, String jdbc_url, String user, String pass) throws Exception
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
	public String patch(String sql) throws Exception
	{
		return sql;
	}

	/*---------------------------------------------------------------------*/
}
