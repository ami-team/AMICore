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

	public OracleDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String AMIUser, String timeZone, boolean isAdmin, boolean links) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, isAdmin, links);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setupSession(String db, String tz) throws Exception
	{
		this.m_statement.executeQuery("ALTER SESSION SET time_zone = '" + tz + "';");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		/*-----------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(sql.trim());

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		boolean selectFound = false;
		boolean fromFound = false;

		int limitValue = -1;
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
					try
					{
						limitValue = Integer.valueOf(token);
						flag = 0;
					}
					catch(NumberFormatException e) { /* IGNORE */ }
				}
				else if(flag == 2)
				{
					try
					{
						offsetValue = Integer.valueOf(token);
						flag = 0;
					}
					catch(NumberFormatException e) { /* IGNORE */ }
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
					else
					{
						if(cnt == 0)
						{
							/**/ if("SELECT".equalsIgnoreCase(token))
							{
								selectFound = true;
							}
							else if("FROM".equalsIgnoreCase(token))
							{
								fromFound = true;
							}
						}
					}

					result.append(Tokenizer.backQuotesToDoubleQuotes(token));
				}
			}
		}

		/*-----------------------------------------------------------------*/

		if(selectFound == true && fromFound == false)
		{
			result.append(" FROM dual");
		}

		/*-----------------------------------------------------------------*/

		if(limitValue >= 0)
		{
			result = new StringBuilder().append("SELECT * FROM (SELECT a.*, ROWNUM rnum FROM (").append(result).append(") a WHERE ROWNUM <= ").append(limitValue + offsetValue).append(") WHERE rnum >= ").append(offsetValue + 1);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
