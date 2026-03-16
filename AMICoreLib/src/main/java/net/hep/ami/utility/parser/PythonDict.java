package net.hep.ami.utility.parser;

import java.io.*;

import org.antlr.v4.runtime.*;

import org.jetbrains.annotations.*;

public class PythonDict
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public static <T> T parse(@NotNull String s, Class<T> clazz, boolean simpleQuotes) throws Exception
	{
		return parse(CharStreams.fromString(s), clazz, simpleQuotes);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public static <T> T parse(@NotNull InputStream inputStream, Class<T> clazz, boolean simpleQuotes) throws Exception
	{
		return parse(CharStreams.fromStream(inputStream), clazz, simpleQuotes);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@SuppressWarnings("unchecked")
	private static <T> T parse(@NotNull CharStream charStream, Class<T> clazz, boolean simpleQuotes) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		PythonDictLexer lexer = new PythonDictLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		PythonDictParser parser = new PythonDictParser(tokenStream);

		/*------------------------------------------------------------------------------------------------------------*/

		parser.simpleQuotes = simpleQuotes;

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

		@Nullable Object result = parser.file().result;

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(result == null)
		{
			return null;
		}
		if(clazz.isAssignableFrom(result.getClass()))
		{
			return (T) result;
		}
		else
		{
			throw new Exception("invalid cast from `" + result.getClass().getName() + "` to `" + clazz.getName() + "`");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
