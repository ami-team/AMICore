package net.hep.ami.jdbc.query.sql;

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
