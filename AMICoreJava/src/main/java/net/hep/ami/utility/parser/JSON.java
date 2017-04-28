package net.hep.ami.utility.parser;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

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
		/*-----------------------------------------------------------------*/

		JSONParser parser = new JSONParser(new CommonTokenStream(new JSONLexer(charStream)));

		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy());

		/*-----------------------------------------------------------------*/

		return visitValue(parser.value());

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static Object visitValue(JSONParser.ValueContext ctx)
	{
		Object result;

		/*-----------------------------------------------------------------*/

		ParseTree child = ctx.getChild(0);

		/**/ if(child instanceof JSONParser.ObjectContext)
		{
			result = visitObject((JSONParser.ObjectContext) child);
		}
		else if(child instanceof JSONParser.ArrayContext)
		{
			result = visitArray((JSONParser.ArrayContext) child);
		}
		else if(child instanceof JSONParser.TermContext)
		{
			result = visitTerm((JSONParser.TermContext) child);
		}
		else
		{
			result = null;
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static Map<String, Object> visitObject(JSONParser.ObjectContext ctx)
	{
		Map<String, Object> result = new LinkedHashMap<>();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			if(child instanceof JSONParser.PairContext)
			{
				JSONParser.PairContext pairContext = ((JSONParser.PairContext) child);

				result.put(Unescape.unescape(pairContext.key.getText()), JSON.visitValue(pairContext.val));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static List<Object> visitArray(JSONParser.ArrayContext ctx)
	{
		List<Object> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			if(child instanceof JSONParser.ValueContext)
			{
				result.add(visitValue((JSONParser.ValueContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static Object visitTerm(JSONParser.TermContext ctx)
	{
		Object result;

		/*-----------------------------------------------------------------*/

		/**/ if(ctx instanceof JSONParser.StringContext)
		{
			result = Unescape.unescape(ctx.getText());
		}
		else if(ctx instanceof JSONParser.NumberContext)
		{
			result = Float.valueOf(ctx.getText());
		}
		else if(ctx instanceof JSONParser.TrueContext)
		{
			result = true;
		}
		else if(ctx instanceof JSONParser.FalseContext)
		{
			result = false;
		}
		else
		{
			result = null;
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
