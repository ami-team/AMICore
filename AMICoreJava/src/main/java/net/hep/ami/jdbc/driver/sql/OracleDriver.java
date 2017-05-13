package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.sql.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.SQL,
	proto = "jdbc:oracle",
	clazz = "oracle.jdbc.driver.OracleDriver"
)

public class OracleDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public OracleDriver(String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType jdbcTypeToAMIType(FieldType fieldType) throws Exception
	{
		return fieldType;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
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

	public void setDB(String db) throws Exception
	{
		/* EMPTY */
	}

	/*---------------------------------------------------------------------*/
}
