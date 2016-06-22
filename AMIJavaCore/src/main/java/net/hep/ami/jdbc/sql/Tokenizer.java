package net.hep.ami.jdbc.sql;

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
		"^\"(\\\\\"|[^\"])*\""
		+ "|" +
		"^'(\\\\'|[^'])*'"
		+ "|" +
		"^`(\\\\`|[^`])*`"
	);

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

			m = s_pattern1.matcher(sql.substring(i));

			if(m.find())
			{
				result.add(" ");

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* STRINGS                                                     */
			/*-------------------------------------------------------------*/

			m = s_pattern2.matcher(sql.substring(i));

			if(m.find())
			{
				result.add(m.group(0));

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* OTHERS                                                      */
			/*-------------------------------------------------------------*/

			result.add(String.valueOf(sql.charAt(i++)));

			/*-------------------------------------------------------------*/
		}

		return result ;
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		System.out.println(Tokenizer.tokenize("SELECT * FROM `to\\`to`.\"ti\\\"ti\" WHERE `tutu`='foo\\'bar' AND 1=1"));

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
