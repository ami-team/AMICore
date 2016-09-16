package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = DBType.SQL,
	clazz = "org.sqlite.JDBC",
	proto = "jdbc:sqlite"
)

public class SQLiteDriver extends DriverAbstractClass
{
	/*---------------------------------------------------------------------*/

	public SQLiteDriver(String jdbc_url, String user, String pass) throws Exception
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
