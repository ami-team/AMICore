package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.sql.*;
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

	public PostgreSQLDriver(String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
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
				token = token.replace("\"", "\"\"")
				             .replace("``", "\"\"")
				             .replace("`", "\"")
				;
			}

			result.append(token);
		}

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setDB(String db) throws Exception
	{
		/* EMPTY */
	}

	/*---------------------------------------------------------------------*/
}
