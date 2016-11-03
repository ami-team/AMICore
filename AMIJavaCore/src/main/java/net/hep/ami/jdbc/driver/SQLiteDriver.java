package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.SQL,
	proto = "jdbc:sqlite",
	clazz = "org.sqlite.JDBC"
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
