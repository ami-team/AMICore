package net.hep.ami.utility.parser;

import java.util.*;

import org.antlr.v4.runtime.*;

public class AMIErrorListener extends BaseErrorListener
{
	/*---------------------------------------------------------------------*/

	private List<String> m_messages = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public static AMIErrorListener setListener(Lexer lexer, Parser parser)
	{
		AMIErrorListener result = new AMIErrorListener();

		lexer.removeErrorListeners();
		lexer.addErrorListener(result);

		parser.removeErrorListeners();
		parser.addErrorListener(result);

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int column, String message, RecognitionException e)
	{
		m_messages.add("line " + line + ":" + column + ", " + message);
	}

	/*---------------------------------------------------------------------*/

	public boolean isSuccess()
	{
		return m_messages.isEmpty();
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return String.join(". ", m_messages);
	}

	/*---------------------------------------------------------------------*/
}
