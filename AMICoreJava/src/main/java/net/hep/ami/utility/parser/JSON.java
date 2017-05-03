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

	public static Object parse(CharStream charStream) throws Exception
	{
		JSONParser parser = new JSONParser(new CommonTokenStream(new JSONLexer(charStream)));

		parser.setErrorHandler(new DefaultErrorStrategy());

		return parser.file().v;
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		System.out.println(JSON.parse("null"));
		System.out.println(JSON.parse("true"));
		System.out.println(JSON.parse("12.3"));
		System.out.println(JSON.parse("[]"));
		System.out.println(JSON.parse("[1, \"HELLO\"]"));
		System.out.println(JSON.parse("{}"));
		System.out.println(JSON.parse("{\"foo\": \"bar\", \"toto\": 123.4}"));

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
