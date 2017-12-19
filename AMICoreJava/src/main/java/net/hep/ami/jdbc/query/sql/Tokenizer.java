package net.hep.ami.jdbc.query.sql;

import java.math.*;
import java.util.*;

import org.antlr.v4.runtime.*;

public class Tokenizer
{
	/*---------------------------------------------------------------------*/

	private Tokenizer() {}

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String s)
	{
		return tokenize(CharStreams.fromString(s));
	}

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(CharStream charStream)
	{
		/*-----------------------------------------------------------------*/

		SQLLexer lexer = new SQLLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		SQLParser parser = new SQLParser(tokenStream);

		/*-----------------------------------------------------------------*/

		return parser.query().tokens;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String format(String sql, Object... args) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		/***/ int i = 0x000000000;
		final int l = args.length;

		if(l == 0)
		{
			return sql;
		}

		/*-----------------------------------------------------------------*/

		Object arg;

		for(String token: Tokenizer.tokenize(sql))
		{
			if("?".equals(token))
			{
				/*---------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*---------------------------------------------------------*/

				arg = args[i++];

				/**/ if(arg == null)
				{
					stringBuilder.append("NULL");
				}
				else if(arg instanceof Float
				        ||
				        arg instanceof Double
				        ||
				        arg instanceof Integer
				        ||
				        arg instanceof BigInteger
				 ) {
					stringBuilder.append(arg);
				}
				else
				{
					stringBuilder.append("'")
					             .append(arg.toString().replace("'", "''"))
					             .append("'")
					;
				}

				/*---------------------------------------------------------*/
			}
			else
			{
				/*---------------------------------------------------------*/

				stringBuilder.append(token);

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		return stringBuilder.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String backQuotesToDoubleQuotes(String token)
	{
		if(token.startsWith("`")
		   &&
		   token.endsWith("`")
		 ) {
			token = token.replace("\"", "\"\"")
			             .replace("``", "\"\"")
			             .replace("`", "\"")
			;
		}

		return token;
	}

	/*---------------------------------------------------------------------*/
}
