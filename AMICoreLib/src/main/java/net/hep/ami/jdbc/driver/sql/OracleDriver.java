package net.hep.ami.jdbc.driver.sql;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.query.sql.*;

import org.jetbrains.annotations.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:oracle",
	clazz = "oracle.jdbc.driver.OracleDriver",
	flags = DriverMetadata.FLAG_HAS_CATALOG | DriverMetadata.FLAG_HAS_SCHEMA | DriverMetadata.FLAG_HAS_DUAL
)

public class OracleDriver extends AbstractDriver
{
	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(MariaDBDriver.class.getSimpleName());

	static final String JSON_PATHS_TEMPLATE;
	static final String JSON_VALUES_TEMPLATE;

	static
	{
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();

		try(InputStream inputStream = AbstractDriver.class.getResourceAsStream("/sql/oracle/json_paths.sql"))
		{
			TextFile.read(stringBuilder1, inputStream);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		try(InputStream inputStream = AbstractDriver.class.getResourceAsStream("/sql/oracle/json_values.sql"))
		{
			TextFile.read(stringBuilder2, inputStream);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		JSON_PATHS_TEMPLATE = stringBuilder1.toString();
		JSON_VALUES_TEMPLATE = stringBuilder2.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private final int MAJOR_VERSION;

	/*----------------------------------------------------------------------------------------------------------------*/

	public OracleDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);

		DatabaseMetaData metaData = m_connection.getMetaData();

		MAJOR_VERSION = metaData.getDatabaseMajorVersion();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(@NotNull String db, @NotNull String tz) throws Exception
	{
		try(Statement statement = m_connection.createStatement())
		{
			statement.executeUpdate("ALTER SESSION SET time_zone = '" + tz + "'");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Pattern JSON_PATHS_SUBSTITUTION = Pattern.compile("\\{%JSON_PATHS,(.*),(.*),(.*)%\\}");
	private static final Pattern JSON_VALUES_SUBSTITUTION = Pattern.compile("\\{%JSON_VALUES,(.*),(.*),(.*)%\\}");

	@Override
	public String patchSQL(@NotNull String sql) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Matcher m;

		/**/ if((m = JSON_PATHS_SUBSTITUTION.matcher(sql)).find())
		{
			String primaryKey = m.group(1);
			String field = m.group(2);
			String path = m.group(3);

			Tokenizer.XQLParts partInfo = Tokenizer.splitXQL(sql);

			List<String> select = partInfo.getSelect();
			List<String> from = partInfo.getFrom();
			List<String> where = partInfo.getWhere();

			sql = JSON_PATHS_TEMPLATE.replace("{%primaryKey%}", primaryKey)
					.replace("{%field%}", field)
					.replace("{%path%}", path)
					.replace("{%select1%}", String.join("", select).replace(m.group(), "a.\"JPATH\" AS \"PATH\""))
					.replace("{%select2%}", String.join("", select).replace(m.group(), "CONCAT(a.\"JPATH\",'[*]') AS \"PATH\""))
					.replace("{%select3%}", String.join("", select).replace(m.group(), "REPLACE(a.\"JPATH\",b.\"JPATH\",CONCAT(b.\"JPATH\",'[*]')) AS \"PATH\""))
					.replace("{%from%}", String.join("", from))
					.replace("{%where%}", where != null ? String.join("", where) : "1 = 1")
			;
		}
		else if((m = JSON_VALUES_SUBSTITUTION.matcher(sql)).find())
		{
			String primaryKey = m.group(1);
			String field = m.group(2);
			String path = m.group(3);
			String pathStart;
			String pathEnd;

			int idx = path.lastIndexOf("[*]");

			if(idx > 0)
			{
				pathStart = path.substring(0, idx);
				pathEnd = "$" + path.substring(idx);
			}
			else
			{
				pathStart = path;
				pathEnd = "$[*]";
			}

			Tokenizer.XQLParts partInfo = Tokenizer.splitXQL(sql);

			List<String> select = partInfo.getSelect();
			List<String> from = partInfo.getFrom();
			List<String> where = partInfo.getWhere();

			sql = JSON_VALUES_TEMPLATE.replace("{%primaryKey%}", primaryKey)
					.replace("{%field%}", field)
					.replace("{%path%}", path)
					.replace("{%pathStart%}", pathStart)
					.replace("{%pathEnd%}", pathEnd)
					.replace("{%select%}", String.join("", select).replace(m.group(), "a.\"JVALUE\" AS VALUE"))
					.replace("{%from%}", String.join("", from))
					.replace("{%where%}", where != null ? String.join("", where) : "1 = 1")
			;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(sql.trim());

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		boolean selectFound = false;
		boolean fromFound = false;
		boolean xxxFound = false;

		int limitValue = -1;
		int offsetValue = 0;

		int flag = 0;
		int cnt = 0;

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		if(selectFound && !fromFound && !xxxFound)
		{
			result.append(" FROM dual");

			fromFound = true;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(selectFound && !fromFound)
		{
			throw new Exception("Internal error");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(limitValue >= 0)
		{
			if(MAJOR_VERSION >= 12) {
				result = new StringBuilder().append(result).append(" OFFSET ").append(offsetValue).append(" ROWS FETCH NEXT ").append(limitValue).append(" ROWS ONLY");
			}
			else {
				result = new StringBuilder().append("SELECT * FROM (SELECT a.*, ROWNUM AS ORACLE_ROWNUM FROM (").append(result).append(") a WHERE ROWNUM <= ").append(limitValue + offsetValue).append(") WHERE ORACLE_ROWNUM >= ").append(offsetValue + 1).append(" ORDER BY ORACLE_ROWNUM");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString().replaceAll(":SSFF", ":SS.FF");
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
