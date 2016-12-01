package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CSVParser
{
	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern1 = Pattern.compile(
		"^\\s*,\\s*"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern2 = Pattern.compile(
		"^\"(\\\\\"|[^\"])*\""
		+ "|" +
		"^'(\\\\'|[^'])*'"
		+ "|" +
		"^`(\\\\`|[^`])*`"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern3 = Pattern.compile(
		"^[^,\\s\\\"\\\'`]+"
	);

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String sql) throws Exception
	{
		List<String> result = new ArrayList<>();

		/***/ int i = 0x0000000000;
		final int l = sql.length();

		String word = "";

		Matcher m;

		while(i < l)
		{
			/*-------------------------------------------------------------*/
			/* EAT COMMENT                                                 */
			/*-------------------------------------------------------------*/

			if(sql.charAt(i) == '#')
			{
				break;
			}

			/*-------------------------------------------------------------*/
			/* EAT COMMA                                                   */
			/*-------------------------------------------------------------*/

			m = s_pattern1.matcher(sql.substring(i));

			if(m.find())
			{
				result.add(word); word = "";

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* STRING                                                      */
			/*-------------------------------------------------------------*/

			m = s_pattern2.matcher(sql.substring(i));

			if(m.find())
			{
				word = m.group(0);

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* ATOM                                                        */
			/*-------------------------------------------------------------*/

			m = s_pattern3.matcher(sql.substring(i));

			if(m.find())
			{
				word = m.group(0);

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* OTHER                                                       */
			/*-------------------------------------------------------------*/

			throw new Exception("syntax error");

			/*-------------------------------------------------------------*/
		}

		result.add(word);

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static List<Map<String, String>> parse(File file) throws Exception
	{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		try
		{
			String line;

			Map<String, String> map;

			List<String> keys = null;
			List<String> values = null;

			for(int i = 0; (line = bufferedReader.readLine()) != null; i++)
			{
				if((line = line.trim()).isEmpty())
				{
					continue;
				}

				if(i == 0)
				{
					keys = tokenize(line);
				}
				else
				{
					values = tokenize(line);

					if(keys.size() == values.size())
					{
						map = new HashMap<String, String>();

						for(int j = 0; j < keys.size(); j++)
						{
							map.put(keys.get(j), values.get(j));
						}

						result.add(map);
					}
					else
					{
						throw new Exception("syntax error");
					}
				}
			}
		}
		finally
		{
			bufferedReader.close();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
}
