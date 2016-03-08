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

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern2 = Pattern.compile(
		"^[-]+([a-zA-Z][a-zA-Z0-9]*)(?:=\"("
		+
		"(?:\\\"|[^\"])*"
		+
		")\")?"
	);

	/*---------------------------------------------------------------------*/

	public static CommandParserTuple parse(String s) throws Exception
	{
		String command;

		/***/ int i = 0x00000000;
		final int l = s.length();

		Map<String, String> arguments = new HashMap<String, String>();

		/*-----------------------------------------------------------------*/
		/* PARSE COMMAND                                                   */
		/*-----------------------------------------------------------------*/

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

		char c;
		String key;
		String value;

		while(i < l)
		{
			/*-------------------------------------------------------------*/
			/* EAT SPACE                                                   */
			/*-------------------------------------------------------------*/

			c = s.charAt(i);

			if(c == ' '
			   ||
			   c == '\t'
			   ||
			   c == '\n'
			   ||
			   c == '\r'
			 ) {
				i++;

				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT ARGUMENT                                                */
			/*-------------------------------------------------------------*/

			m = s_pattern2.matcher(s.substring(i));

			if(m.find())
			{
				key = m.group(1);
				value = m.group(2);

				arguments.put(key, value != null ? unescape(value) : null);

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* SYNTAX ERROR                                                */
			/*-------------------------------------------------------------*/

			throw new Exception("command syntax error, invalid argument syntax");

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
				}
			}

			result.append(c);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
