package net.hep.ami.utility.parser;

import lombok.*;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;

import org.jetbrains.annotations.*;

public class Command
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static final class Tuple
	{
		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull private final String command;

		@NotNull private final Map<String, String> arguments;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Command.Tuple parse(@NotNull String s) throws Exception
	{
		return parse(CharStreams.fromString(s));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Command.Tuple parse(@NotNull InputStream inputStream) throws Exception
	{
		return parse(CharStreams.fromStream(inputStream));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static Command.Tuple parse(@NotNull CharStream charStream) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		CommandLexer lexer = new CommandLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		CommandParser parser = new CommandParser(tokenStream);

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple result = parser.command().tuple;

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
