package net.hep.ami.utility.parser;

import java.io.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;

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
		JSONLexer lexer = new JSONLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		JSONParser parser = new JSONParser(tokenStream);

		ANTLRErrorListener listener = new AMIErrorListener();

		lexer.addErrorListener(listener);
		parser.addErrorListener(listener);

		parser.setErrorHandler(new BailErrorStrategy());

		try
		{
			return parser.file().v;
		}
		catch(ParseCancellationException e)
		{
			throw new ParseCancellationException(listener.toString(), e);
		}
	}

	/*---------------------------------------------------------------------*/
}
