package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	proto = "jdbc:mysql",
	clazz = "org.gjt.mm.mysql.Driver"
)

public class MySQLDriver extends DriverAbstractClass
{
	/*---------------------------------------------------------------------*/

	public MySQLDriver(String jdbc_url, String user, String pass) throws Exception
	{
		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType jdbcTypeToAMIType(DriverInterface.FieldType fieldType) throws Exception
	{
		throw new Exception("unimplemented");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType amiTypeToJDBCType(DriverInterface.FieldType fieldType) throws Exception
	{
		throw new Exception("unimplemented");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patch(String sql) throws Exception
	{
		return sql;
	}

	/*---------------------------------------------------------------------*/
}
