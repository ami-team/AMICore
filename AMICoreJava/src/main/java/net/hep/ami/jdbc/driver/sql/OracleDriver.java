package net.hep.ami.jdbc.driver.sql;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.query.sql.*;

import org.jetbrains.annotations.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:oracle",
	clazz = "oracle.jdbc.driver.OracleDriver",
	flags =  DriverMetadata.FLAG_HAS_CATALOG | DriverMetadata.FLAG_HAS_DUAL
)

public class OracleDriver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final int MAJOR_VERSION;

	/*----------------------------------------------------------------------------------------------------------------*/

	public OracleDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, boolean isAdmin, boolean links) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, isAdmin, links);

		DatabaseMetaData metaData = m_connection.getMetaData();

		MAJOR_VERSION = metaData.getDatabaseMajorVersion();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(@NotNull String db, @NotNull String tz) throws Exception
	{
		this.m_statement.executeUpdate("ALTER SESSION SET time_zone = '" + tz + "'");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(sql.trim());

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		boolean selectFound = false;
		boolean fromFound = false;
		boolean xxxFound = false;

		int cnt = 0;

		if(MAJOR_VERSION >= 12)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			for(String token: tokens)
			{
				if("TIMESTAMP".equalsIgnoreCase(token))
				{
					token = "TO_TIMESTAMP";
				}

				if(!";".equals(token))
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
								fromFound = false;
								xxxFound = false;
							}
							else if("FROM".equalsIgnoreCase(token))
							{
								fromFound = true;
							}
							else if("WHERE".equalsIgnoreCase(token) || "LIMIT".equalsIgnoreCase(token) || "ORDER".equalsIgnoreCase(token))
							{
								if(selectFound && !fromFound && !xxxFound)
								{
									result.append(" FROM dual ");

									fromFound = true;
								}

								xxxFound = true;
							}
						}
					}

					result.append(Tokenizer.backQuotesToDoubleQuotes(token));
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(selectFound && !fromFound && !xxxFound)
			{
				result.append(" FROM dual");

				fromFound = true;
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		else
		{
			/*--------------------------------------------------------------------------------------------------------*/

			int limitValue = -1;
			int offsetValue = 0;

			int flag = 0;

			for(String token: tokens)
			{
				if("TIMESTAMP".equalsIgnoreCase(token))
				{
					token = "TO_TIMESTAMP";
				}

				if(!";".equals(token))
				{
					/**/ if("LIMIT".equalsIgnoreCase(token))
					{
						if(selectFound && !fromFound && !xxxFound)
						{
							result.append(" FROM dual ");

							fromFound = true;
						}

						xxxFound = true;

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
							limitValue = Integer.parseInt(token);
							flag = 0;
						}
						catch(NumberFormatException e) { /* IGNORE */ }
					}
					else if(flag == 2)
					{
						try
						{
							offsetValue = Integer.parseInt(token);
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
									fromFound = false;
									xxxFound = false;
								}
								else if("FROM".equalsIgnoreCase(token))
								{
									fromFound = true;
								}
								else if("WHERE".equalsIgnoreCase(token) || "ORDER".equalsIgnoreCase(token))
								{
									if(selectFound && !fromFound && !xxxFound)
									{
										result.append(" FROM dual ");

										fromFound = true;
									}

									xxxFound = true;
								}
							}
						}

						result.append(Tokenizer.backQuotesToDoubleQuotes(token));
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(selectFound && !fromFound && !xxxFound)
			{
				result.append(" FROM dual");

				fromFound = true;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(limitValue >= 0)
			{
				result = new StringBuilder().append("SELECT * FROM (SELECT a.*, ROWNUM AS ORACLE_ROWNUM FROM (").append(result).append(") a WHERE ROWNUM <= ").append(limitValue + offsetValue).append(") WHERE ORACLE_ROWNUM >= ").append(offsetValue + 1).append(" ORDER BY ORACLE_ROWNUM");
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
