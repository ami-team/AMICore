package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.sql.*;

import java.util.List;

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
	public String patchSQL(String sql) throws Exception
	{
		/*-----------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(sql);

		/*-----------------------------------------------------------------*/

		if(tokens.size() == 2
		   &&
		   "SELECT".equalsIgnoreCase(tokens.get(0))
		   &&
		   "1".equalsIgnoreCase(tokens.get(1))
		 ) {
			return "SELECT 1 FROM \"dual\"";
		}

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		for(String token: tokens)
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

		/*-----------------------------------------------------------------*/

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
