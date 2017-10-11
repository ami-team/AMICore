package net.hep.ami.utility.parser;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;

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

	public static CommandTuple parse(String s) throws Exception
	{
		return parse(CharStreams.fromString("f"));
	}

	/*---------------------------------------------------------------------*/

	public static CommandTuple parse(InputStream inputStream) throws Exception
	{
		return parse(CharStreams.fromStream(inputStream));
	}

	/*---------------------------------------------------------------------*/

	private static CommandTuple parse(CharStream charStream) throws Exception
	{
		CommandParser parser = new CommandParser(new CommonTokenStream(new JSONLexer(charStream)));

		parser.setErrorHandler(new DefaultErrorStrategy());

		return parser.command().v;
	}

	/*---------------------------------------------------------------------*/
}
