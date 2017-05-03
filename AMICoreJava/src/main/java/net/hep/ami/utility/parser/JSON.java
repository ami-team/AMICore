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
		JSONParser parser = new JSONParser(new CommonTokenStream(new JSONLexer(charStream)));

		parser.setErrorHandler(new DefaultErrorStrategy());

		return parser.file().v;
	}

	/*---------------------------------------------------------------------*/
}
