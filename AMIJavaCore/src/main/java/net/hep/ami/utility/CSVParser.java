package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CSVParser
{
	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern1 = Pattern.compile(
		"^\"((\\\\\"|[^\"])*)\""
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern2 = Pattern.compile(
		"^'((\\\\'|[^'])*)'"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern3 = Pattern.compile(
		"^`((\\\\`|[^`])*)`"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern4 = Pattern.compile(
		"^[^\\s#,\"'`]*"
	);

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String s) throws Exception
	{
		List<String> result = new ArrayList<>();

		/***/ int i = 0x00000000;
		final int l = s.length();

		String value = "";

		Matcher m;

		while(i < l && s.charAt(i) != '#')
		{
			/*-------------------------------------------------------------*/
			/* EAT SPACE                                                   */
			/*-------------------------------------------------------------*/

			/**/ if(Character.isWhitespace(s.charAt(i)))
			{
				i++;
			}

			/*-------------------------------------------------------------*/
			/* EAT COMMA                                                   */
			/*-------------------------------------------------------------*/

			else if(s.charAt(i) == ',')
			{
				result.add(value);

				value = "";

				i++;
			}

			/*-------------------------------------------------------------*/
			/* EAT VALUE                                                   */
			/*-------------------------------------------------------------*/

			else if((m = s_pattern1.matcher(s.substring(i))).find()
			        ||
			        (m = s_pattern2.matcher(s.substring(i))).find()
			        ||
			        (m = s_pattern3.matcher(s.substring(i))).find()
			 ) {
				value = AMIParser.unescape(m.group(1));

				i += m.group(0).length();
			}

			/*-------------------------------------------------------------*/

			else if((m = s_pattern4.matcher(s.substring(i))).find())
			{
				i += (value = m.group(0)).length();
			}

			/*-------------------------------------------------------------*/
			/* SYNTAX ERROR                                                */
			/*-------------------------------------------------------------*/

			else throw new Exception("syntax error");

			/*-------------------------------------------------------------*/
		}

		if(value.isEmpty() == false
		   ||
		   result.isEmpty() == false
		 ) {
			result.add(value);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static List<Map<String, String>> parse(InputStream inputStream) throws Exception
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try
		{
			String line;

			boolean first = true;

			Map<String, String> map;

			List<String> keys = null;
			List<String> values = null;

			while((line = bufferedReader.readLine()) != null)
			{
				if(first)
				{
					keys = tokenize(line);

					if(keys.isEmpty() == false)
					{
						first = false;
					}
				}
				else
				{
					values = tokenize(line);

					int keysSize = keys.size();
					int valuesSize = values.size();

					if(valuesSize > 0)
					{
						if(valuesSize != keysSize)
						{
							throw new Exception("syntax error");
						}

						map = new HashMap<String, String>();

						for(int j = 0; j < keysSize; j++)
						{
							map.put(
								keys.get(j)
								,
								values.get(j)
							);
						}

						result.add(map);
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
