package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = DBType.SQL,
	proto = "jdbc:h2",
	clazz = "org.h2.Driver"
)

public class H2Driver extends DriverAbstractClass
{
	/*---------------------------------------------------------------------*/

	public H2Driver(String jdbc_url, String user, String pass) throws Exception
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
