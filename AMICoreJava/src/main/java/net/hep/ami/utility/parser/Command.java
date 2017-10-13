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
		return parse(CharStreams.fromString(s));
	}

	/*---------------------------------------------------------------------*/

	public static CommandTuple parse(InputStream inputStream) throws Exception
	{
		return parse(CharStreams.fromStream(inputStream));
	}

	/*---------------------------------------------------------------------*/

	private static CommandTuple parse(CharStream charStream) throws Exception
	{
		/*-----------------------------------------------------------------*/

		CommandLexer lexer = new CommandLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		CommandParser parser = new CommandParser(tokenStream);

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		CommandTuple result = parser.command().v;

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
