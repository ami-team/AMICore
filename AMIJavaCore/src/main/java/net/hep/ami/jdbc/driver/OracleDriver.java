package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.sql.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = DBType.SQL,
	clazz = "oracle.jdbc.driver.OracleDriver",
	proto = "jdbc:oracle"
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
		StringBuilder result = new StringBuilder();

		for(String token: Tokenizer.tokenize(sql))
		{
			if(token.startsWith("`")
			   &&
			   token.endsWith("`")
			 ) {
				token = token.replace("`", "\"");
			}
		}

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
