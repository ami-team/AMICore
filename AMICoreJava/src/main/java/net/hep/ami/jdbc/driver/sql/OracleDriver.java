package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.sql.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.SQL,
	proto = "jdbc:oracle",
	clazz = "oracle.jdbc.driver.OracleDriver"
)

public class OracleDriver extends DriverAbstractClass
{
	/*---------------------------------------------------------------------*/

	public OracleDriver(String name, String jdbc_url, String user, String pass) throws Exception
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
		StringBuilder result = new StringBuilder();

		for(String token: Tokenizer.tokenize(sql))
		{
			if(token.startsWith("`")
			   &&
			   token.endsWith("`")
			 ) {
				token = token.replace("\"", "\\\"")
				             .replace("`", "\"")
				;
			}

			result.append(token);
		}

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
