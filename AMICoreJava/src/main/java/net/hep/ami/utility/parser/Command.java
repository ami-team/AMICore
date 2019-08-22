package net.hep.ami.utility.parser;

import java.io.*;
import java.util.*;

import net.hep.ami.utility.*;

import org.antlr.v4.runtime.*;

public class Command
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public static class CommandTuple
	{
		/*------------------------------------------------------------------------------------------------------------*/

		public final String command;

		public final Map<String, String> arguments;

		/*------------------------------------------------------------------------------------------------------------*/

		@org.jetbrains.annotations.Contract(pure = true)
		public CommandTuple(@NotNull String _command, @NotNull Map<String, String> _arguments)
		{
			command = _command;

			arguments = _arguments;
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static CommandTuple parse(@NotNull String s) throws Exception
	{
		return parse(CharStreams.fromString(s));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static CommandTuple parse(@NotNull InputStream inputStream) throws Exception
	{
		return parse(CharStreams.fromStream(inputStream));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static CommandTuple parse(@NotNull CharStream charStream) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		CommandLexer lexer = new CommandLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		CommandParser parser = new CommandParser(tokenStream);

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

		CommandTuple result = parser.command().commandTuple;

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
