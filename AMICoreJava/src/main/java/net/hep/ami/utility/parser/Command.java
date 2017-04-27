package net.hep.ami.utility.parser;

import java.util.*;
import java.util.regex.*;

public class Command
{
	/*---------------------------------------------------------------------*/

	public static class CommandTuple
	{
		/*-----------------------------------------------------------------*/

		public final String command;

		public final Map<String, String> arguments;

		/*-----------------------------------------------------------------*/

		public CommandTuple(String _command, Map<String, String> _arguments)
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

	private Command() {}

	/*---------------------------------------------------------------------*/

	public static CommandTuple parse(String s) throws Exception
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

		Map<String, String> arguments = new LinkedHashMap<>();

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
			/* EAT ARGUMENT                                                */
			/*-------------------------------------------------------------*/

			else if((m = s_pattern2.matcher(s.substring(i))).find()
			        ||
			        (m = s_pattern3.matcher(s.substring(i))).find()
			        ||
			        (m = s_pattern4.matcher(s.substring(i))).find()
			 ) {
				arguments.put(m.group(1), (m.groupCount() == 2) ? Unescape.unescape(m.group(2)) : "");

				i += m.group(0).length();
			}

			/*-------------------------------------------------------------*/
			/* SYNTAX ERROR                                                */
			/*-------------------------------------------------------------*/

			else throw new Exception("command syntax error, invalid argument syntax");

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/* RETURN RESULT                                                   */
		/*-----------------------------------------------------------------*/

		return new CommandTuple(
			command,
			arguments
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
