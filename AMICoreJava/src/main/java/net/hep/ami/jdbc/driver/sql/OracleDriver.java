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

		boolean fromFound = false;

		int limitValue = 0;
		int offsetValue = 0;

		int flag = 0;
		int cnt = 0;

		for(String token: tokens)
		{
			if(";".equals(token) == false)
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
					limitValue = Integer.valueOf(token);
					flag = 0;
				}
				else if(flag == 2)
				{
					offsetValue = Integer.valueOf(token);
					flag = 0;
				}
				else
				{
					/**/ if("(".equals(token))
					{
						cnt++;
					}
					else if(")".equals(token))
					{
						cnt--;
					}
					else if(cnt == 0 && "FROM".equalsIgnoreCase(token))
					{
						fromFound = true;
					}

					result.append(Tokenizer.backQuotesToDoubleQuotes(token));
				}
			}
		}

		/*-----------------------------------------------------------------*/

		if(fromFound == false)
		{
			result.append(" FROM dual");
		}

		/*-----------------------------------------------------------------*/

		if(limitValue > 0)
		{
			result = new StringBuilder().append("SELECT * FROM (SELECT a.*, ROWNUM rnum FROM (").append(result).append(") a WHERE ROWNUM <= ").append(limitValue + offsetValue).append(") WHERE rnum >= ").append(offsetValue + 1);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
