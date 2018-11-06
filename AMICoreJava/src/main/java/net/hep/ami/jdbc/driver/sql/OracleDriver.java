package net.hep.ami.jdbc.driver.sql;

import java.util.*;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.utility.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:oracle",
	clazz = "oracle.jdbc.driver.OracleDriver"
)

public class OracleDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public OracleDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setDB(String db) throws Exception
	{
		/* DO NOTHING */
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		/*-----------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(sql.trim());

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		int limit = 0;
		int offset = 0;
		int flag = 0;

		for(String token: tokens)
		{
			/**/ if("LIMIT".equalsIgnoreCase(token))
			{
				flag = 1;
			}
			else if("OFFSET".equalsIgnoreCase(token))
			{
				flag = 2;
			}
			else if(flag == 1)
			{
				limit = Integer.valueOf(token);
				flag = 0;
			}
			else if(flag == 2)
			{
				offset = Integer.valueOf(token);
				flag = 0;
			}
			else
			{
				if(";".equals(token) == false)
				{
					result.append(Tokenizer.backQuotesToDoubleQuotes(token));
				}
			}
		}

		/*-----------------------------------------------------------------*/

		String SQL = sql.trim().toUpperCase();

		if(SQL.startsWith("SELECT") && SQL.contains("FROM") == false)
		{
			result.append(" FROM dual");
		}

		/*-----------------------------------------------------------------*/

		if(limit > 0)
		{
			result = new StringBuilder().append("SELECT * FROM (SELECT a.*, ROWNUM rnum FROM (").append(result).append(") a WHERE ROWNUM <= ").append(limit + offset).append(") WHERE rnum >= ").append(offset + 1);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
