package net.hep.ami.jdbc.query.sql;

import java.math.*;
import java.util.*;
import java.util.regex.*;

public class Tokenizer
{
	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern1 = Pattern.compile(
		"^\\s+"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern2 = Pattern.compile(
		"^[a-zA-Z0-9_]+"
		+ "|" +
		"^\"(\"\"|[^\"])*\""
		+ "|" +
		"^'(''|[^'])*'"
		+ "|" +
		"^`(``|[^`])*`"
	);

	/*---------------------------------------------------------------------*/

	private Tokenizer() {}

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String sql)
	{
		List<String> result = new ArrayList<>();

		/***/ int i = 0x0000000000;
		final int l = sql.length();

		Matcher m;

		while(i < l)
		{
			/*-------------------------------------------------------------*/
			/* EAT SPACES                                                  */
			/*-------------------------------------------------------------*/

			/**/ if((m = s_pattern1.matcher(sql.substring(i))).find())
			{
				result.add((((" "))));

				i += m.group(0).length();
			}

			/*-------------------------------------------------------------*/
			/* STRING                                                      */
			/*-------------------------------------------------------------*/

			else if((m = s_pattern2.matcher(sql.substring(i))).find())
			{
				result.add(m.group(0));

				i += m.group(0).length();
			}

			/*-------------------------------------------------------------*/
			/* OTHER                                                       */
			/*-------------------------------------------------------------*/

			else result.add(String.valueOf(sql.charAt(i++)));

			/*-------------------------------------------------------------*/
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String format(String sql, Object... args) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		/***/ int i = 0x000000000;
		final int l = args.length;

		if(l == 0)
		{
			return sql;
		}

		/*-----------------------------------------------------------------*/

		Object arg;

		for(String token: Tokenizer.tokenize(sql))
		{
			if("?".equals(token))
			{
				/*---------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*---------------------------------------------------------*/

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
				 ) {
					stringBuilder.append(arg);
				}
				else
				{
					stringBuilder.append("'")
					             .append(arg.toString().replace("'", "''"))
					             .append("'")
					;
				}

				/*---------------------------------------------------------*/
			}
			else
			{
				/*---------------------------------------------------------*/

				stringBuilder.append(token);

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		return stringBuilder.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String backQuotesToDoubleQuotes(String token)
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

		return token;
	}

	/*---------------------------------------------------------------------*/
}
