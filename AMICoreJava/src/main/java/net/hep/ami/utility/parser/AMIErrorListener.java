package net.hep.ami.utility.parser;

import java.util.*;

import net.hep.ami.*;

import org.antlr.v4.runtime.*;

import org.jetbrains.annotations.*;

public class AMIErrorListener extends BaseErrorListener
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final List<String> m_messages = new ArrayList<>();

	/*----------------------------------------------------------------------------------------------------------------*/

public static AMIErrorListener setListener(Lexer lexer, Parser parser)
	{
		AMIErrorListener result = new AMIErrorListener();

		lexer.removeErrorListeners();
		lexer.addErrorListener(result);

		parser.removeErrorListeners();
		parser.addErrorListener(result);

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int column, String message, RecognitionException e)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(ConfigSingleton.getProperty("dev_mode", false))
		{
			List<String> stack = ((Parser) recognizer).getRuleInvocationStack();

			Collections.reverse(stack);

			m_messages.add("stack: " + stack);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		m_messages.add("line " + line + ", column: " + column + ", " + message);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isError()
	{
		return !m_messages.isEmpty();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String toString()
	{
		return String.join(". ", m_messages);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
