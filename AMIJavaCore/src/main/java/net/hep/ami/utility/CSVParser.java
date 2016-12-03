package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CSVParser
{
	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern1 = Pattern.compile(
		"^\\s*#"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern2 = Pattern.compile(
		"^\\s*,\\s*"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern3 = Pattern.compile(
		"^\"((\\\\\"|[^\"])*)\""
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern4 = Pattern.compile(
		"^'((\\\\'|[^'])*)'"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern5 = Pattern.compile(
		"^`((\\\\`|[^`])*)`"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern6 = Pattern.compile(
		"^[^\\s#,\"'`]+"
	);

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String csv) throws Exception
	{
		List<String> result = new ArrayList<>();

		/***/ int i = 0x0000000000;
		final int l = csv.length();

		String value = "";

		Matcher m;

		while(i < l)
		{
			/*-------------------------------------------------------------*/
			/* EAT SPACE                                                   */
			/*-------------------------------------------------------------*/

			if(Character.isWhitespace(csv.charAt(i)))
			{
				i++;

				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT COMMENT                                                 */
			/*-------------------------------------------------------------*/

			if((m = s_pattern1.matcher(csv.substring(i))).find())
			{
				break;
			}

			/*-------------------------------------------------------------*/
			/* EAT COMMA                                                   */
			/*-------------------------------------------------------------*/

			if((m = s_pattern2.matcher(csv.substring(i))).find())
			{
				result.add(value); value = "";

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT VALUE                                                   */
			/*-------------------------------------------------------------*/

			if((m = s_pattern3.matcher(csv.substring(i))).find()
			   ||
			   (m = s_pattern4.matcher(csv.substring(i))).find()
			    ||
			   (m = s_pattern5.matcher(csv.substring(i))).find()
			 ) {
				value = AMIParser.unescape(m.group(1));

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/

			if((m = s_pattern6.matcher(csv.substring(i))).find())
			{
				value = /****************/(m.group(0));

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/

			throw new Exception("syntax error");

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

					if(keys.size() > 0)
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
						if(keysSize == valuesSize)
						{
							map = new HashMap<String, String>();

							for(int j = 0; j < keysSize; j++)
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
		}
		finally
		{
			bufferedReader.close();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
}
