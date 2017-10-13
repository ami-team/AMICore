package net.hep.ami.utility.parser;

import java.io.*;

import org.antlr.v4.runtime.*;

public class JSON
{
	/*---------------------------------------------------------------------*/

	public static Object parse(String s) throws Exception
	{
		return parse(CharStreams.fromString(s));
	}

	/*---------------------------------------------------------------------*/

	public static Object parse(InputStream inputStream) throws Exception
	{
		return parse(CharStreams.fromStream(inputStream));
	}

	/*---------------------------------------------------------------------*/

	private static Object parse(CharStream charStream) throws Exception
	{
		/*-----------------------------------------------------------------*/

		JSONLexer lexer = new JSONLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		JSONParser parser = new JSONParser(tokenStream);

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		Object result = parser.file().v;

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
