package net.hep.ami.jdbc.query.sql;

import java.math.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class Formatter
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private Formatter() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@SuppressWarnings("ConstantConditions")
	public static String formatStatement(@NotNull Querier querier, @NotNull String sql, @NotNull Object[] args) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		final int l = args.length;

		if(l == 0)
		{
			return sql;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		boolean escape = (querier.getJdbcFlags() & DriverMetadata.FLAG_BACKSLASH_ESCAPE) == DriverMetadata.FLAG_BACKSLASH_ESCAPE;

		/*------------------------------------------------------------------------------------------------------------*/

		int i = 0;

		Object arg;

		for(String token: Tokenizer.tokenize(sql))
		{
			/**/ if("?#".equals(token))
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*----------------------------------------------------------------------------------------------------*/

				arg = args[i++];

				/**/ if(arg == null)
				{
					stringBuilder.append("NULL");
				}
				else
				{
					stringBuilder.append(Utility.textToSqlVal(SecuritySingleton.encrypt(arg.toString()), escape));
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("?".equals(token))
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*----------------------------------------------------------------------------------------------------*/

				arg = args[i++];

				/**/ if(arg == null)
				{
					stringBuilder.append("NULL");
				}
				else if(arg instanceof Float
				        ||
				        arg instanceof Double
				        ||
				        arg instanceof Integer
				        ||
				        arg instanceof BigInteger
				        ||
				        arg instanceof Long
				 ) {
					stringBuilder.append(arg.toString());
				}
				else if(arg instanceof Boolean)
				{
					stringBuilder.append(((Boolean) arg) ? "1" : "0");
				}
				else
				{
					stringBuilder.append(Utility.textToSqlVal(arg.toString(), escape));
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else
			{
				/*----------------------------------------------------------------------------------------------------*/

				stringBuilder.append(token);

				/*----------------------------------------------------------------------------------------------------*/
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return stringBuilder.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@SuppressWarnings("unused")
	@Contract("_, _, _ -> new")
	public static Tuple2<String, List<String>> formatPreparedStatement(@NotNull Querier querier, @NotNull String sql, @NotNull Object[] args) throws Exception
	{
		List<String> list = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		final int l = args.length;

		if(l == 0)
		{
			return new Tuple2<>(sql, list);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		int i;

		for(String token: Tokenizer.tokenize(sql))
		{
			/**/ if(token.startsWith("?#"))
			{
				/*----------------------------------------------------------------------------------------------------*/

				i = Integer.parseInt(token.substring(2));

				/*----------------------------------------------------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*----------------------------------------------------------------------------------------------------*/

				list.add(SecuritySingleton.encrypt(args[i].toString()));

				stringBuilder.append("?");

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if(token.startsWith("?"))
			{
				/*----------------------------------------------------------------------------------------------------*/

				i = Integer.parseInt(token.substring(1));

				/*----------------------------------------------------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*----------------------------------------------------------------------------------------------------*/

				list.add(/*---------------------*/(args[i].toString()));

				stringBuilder.append("?");

				/*----------------------------------------------------------------------------------------------------*/
			}
			else
			{
				/*----------------------------------------------------------------------------------------------------*/

				stringBuilder.append(token);

				/*----------------------------------------------------------------------------------------------------*/
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new Tuple2<>(stringBuilder.toString(), list);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void formatPreparedStatement(@NotNull PreparedStatement statement, @NotNull Tuple2<String, List<String>> tuple) throws Exception
	{
		for(int i = 0; i < tuple.y.size(); i++)
		{
			String value = tuple.y.get(i);

			try
			{
				switch(statement.getParameterMetaData().getParameterClassName(i + 1))
				{
					case "java.sql.Clob":
					case "java.lang.String":
					case "oracle.jdbc.OracleClob":
						statement.setString(i + 1, value);
						break;

					case "java.sql.Timestamp":
						statement.setTimestamp(i + 1, java.sql.Timestamp.valueOf(value));
						break;

					case "java.sql.Date":
						SimpleDateFormat dateFormater = new SimpleDateFormat(ConfigSingleton.getProperty("date_format", "yyyy-MM-dd"), Locale.US);
						statement.setDate(i + 1, new java.sql.Date(dateFormater.parse(value).getTime()));
						break;

					case "java.sql.Time":
						SimpleDateFormat timeFormater = new SimpleDateFormat(ConfigSingleton.getProperty("time_format", "HH:mm:ss"), Locale.US);
						statement.setTime(i + 1, new java.sql.Time(timeFormater.parse(value).getTime()));
						break;
				}
			}
			catch(SQLException e)
			{
				statement.setString(i + 1, value);
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
