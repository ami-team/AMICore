package net.hep.ami.utility;

import java.util.*;
import java.util.regex.*;

public class CommandParser
{
	/*---------------------------------------------------------------------*/

	public static class CommandParserTuple
	{
		/*-----------------------------------------------------------------*/

		public String command;

		public Map<String, String> arguments;

		/*-----------------------------------------------------------------*/

		public CommandParserTuple(String _command, Map<String, String> _arguments)
		{
			command = _command;

			arguments = _arguments;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern1 = Pattern.compile(
		"^\\s*([a-zA-Z][a-zA-Z0-9]*)"
	);

	private static final Pattern s_pattern2 = Pattern.compile(
		"^[-]*([a-zA-Z][a-zA-Z0-9]*)\\s*=\\s*\"((?:\\\\\"|[^\"])*)\""
	);

	private static final Pattern s_pattern3 = Pattern.compile(
		"^[-]*([a-zA-Z][a-zA-Z0-9]*)\\s*=\\s*([^\\s]+)"
	);

	private static final Pattern s_pattern4 = Pattern.compile(
		"^[-]*([a-zA-Z][a-zA-Z0-9]*)"
	);

	/*---------------------------------------------------------------------*/

	public static CommandParserTuple parse(String s) throws Exception
	{
		/***/ int i = 0x00000000;
		final int l = s.length();

		/*-----------------------------------------------------------------*/
		/* PARSE COMMAND                                                   */
		/*-----------------------------------------------------------------*/

		String command;

		Matcher m = s_pattern1.matcher(s);

		if(m.find())
		{
			i += (command = m.group(1)).length();
		}
		else
		{
			throw new Exception("command syntax error, missing command name");
		}

		/*-----------------------------------------------------------------*/
		/* PARSE ARGUMENTS                                                 */
		/*-----------------------------------------------------------------*/

		Map<String, String> arguments = new LinkedHashMap<String, String>();

		while(i < l)
		{
			/*-------------------------------------------------------------*/
			/* EAT SPACE                                                   */
			/*-------------------------------------------------------------*/

			if(Character.isWhitespace(s.charAt(i)))
			{
				i++;

				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT ARGUMENT                                                */
			/*-------------------------------------------------------------*/

			m = s_pattern2.matcher(s.substring(i));

			if(m.find() == false)
			{
				m = s_pattern3.matcher(s.substring(i));

				if(m.find() == false)
				{
					m = s_pattern4.matcher(s.substring(i));

					if(m.find() == false)
					{
						throw new Exception("command syntax error, invalid argument syntax");
					}
				}
			}

			/*-------------------------------------------------------------*/

			arguments.put(m.group(1), (m.groupCount() == 2) ? unescape(m.group(2)) : "");

			i += m.group(0).length();

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/* RETURN RESULT                                                   */
		/*-----------------------------------------------------------------*/

		return new CommandParserTuple(
			command,
			arguments
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static String unescape(String s)
	{
		StringBuilder result = new StringBuilder(s.length());

		/*-----------------------------------------------------------------*/

		/***/ int i = 0x00000000;
		final int l = s.length();

		String code;
		char c;

		while(i < l)
		{
			c = s.charAt(i++);

			if(c == '\\')
			{
				c = s.charAt(i++);

				switch(c)
				{
					case '\\':
						c = '\\';
						break;

					case 'b':
						c = '\b';
						break;

					case 'f':
						c = '\f';
						break;

					case 'n':
						c = '\n';
						break;

					case 'r':
						c = '\r';
						break;

					case 't':
						c = '\t';
						break;

					case '\"':
						c = '\"';
						break;

					case '\'':
						c = '\'';
						break;

					case 'u':
						/*-------------------------------------------------*/
						/* UNICODE                                         */
						/*-------------------------------------------------*/

						if(l - i < 4)
						{
							c = 'u';
							break;
						}

						code = s.substring(i + 0, i + 4);
						i += 4;

						try
						{ 
							result.append(Character.toChars(Integer.parseInt(code, 16)));
						}
						catch(java.lang.NumberFormatException e)
						{
							result.append(/******************/ '?' /******************/);
						}

						continue;

						/*-------------------------------------------------*/

						default:
							break;

						/*-------------------------------------------------*/
				}
			}

			result.append(c);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
