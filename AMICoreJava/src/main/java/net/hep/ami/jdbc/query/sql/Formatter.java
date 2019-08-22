package net.hep.ami.jdbc.query.sql;

import java.math.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class Formatter
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@org.jetbrains.annotations.Contract(pure = true)
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
					stringBuilder.append(SecuritySingleton.encrypt(arg.toString()));
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
	@org.jetbrains.annotations.Contract("_, _, _ -> new")
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
}
