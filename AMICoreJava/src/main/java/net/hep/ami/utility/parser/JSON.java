package net.hep.ami.utility.parser;

import java.io.*;

import org.antlr.v4.runtime.*;

@SuppressWarnings("unchecked")
public class JSON
{
	/*---------------------------------------------------------------------*/

	public static <T> T parse(String s, Class<T> clazz) throws Exception
	{
		return (T) parse(CharStreams.fromString(s), clazz);
	}

	/*---------------------------------------------------------------------*/

	public static <T> T parse(InputStream inputStream, Class<T> clazz) throws Exception
	{
		return (T) parse(CharStreams.fromStream(inputStream), clazz);
	}

	/*---------------------------------------------------------------------*/

	private static <T> T parse(CharStream charStream, Class<T> clazz) throws Exception
	{
		/*-----------------------------------------------------------------*/

		JSONLexer lexer = new JSONLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		JSONParser parser = new JSONParser(tokenStream);

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		T result = (T) parser.file().v;

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
