package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	proto = "jdbc:oracle",
	clazz = "oracle.jdbc.driver.OracleDriver"
)

public class OracleDriver extends DriverAbstractClass
{
	/*---------------------------------------------------------------------*/

	public OracleDriver(String jdbc_url, String user, String pass) throws Exception
	{
		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Type jdbcTypeToAMIType(DriverInterface.Type type) throws Exception
	{
		throw new Exception("unimplemented");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Type amiTypeToJDBCType(DriverInterface.Type type) throws Exception
	{
		throw new Exception("unimplemented");
	}

	/*---------------------------------------------------------------------*/
}
