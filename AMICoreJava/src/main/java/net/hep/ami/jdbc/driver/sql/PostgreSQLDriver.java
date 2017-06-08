package net.hep.ami.jdbc.driver.sql;

import java.util.*;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.query.sql.*;

@Jdbc(
	type = Jdbc.Type.SQL,
	proto = "jdbc:postgresql",
	clazz = "org.postgresql.Driver"
)

public class PostgreSQLDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public PostgreSQLDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
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

		StringBuilder result = new StringBuilder();

		for(String token: tokens)
		{
			result.append(Tokenizer.backQuotesToDoubleQuotes(token));
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setDB(String db) throws Exception
	{
		/* DO NOTHING */
	}

	/*---------------------------------------------------------------------*/
}
