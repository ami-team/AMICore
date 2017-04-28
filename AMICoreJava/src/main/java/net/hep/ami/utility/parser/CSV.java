package net.hep.ami.utility.parser;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class CSV
{
	/*---------------------------------------------------------------------*/

	public static List<Map<String, String>> parseAsMap(String s) throws Exception
	{
		return parseAsMap(CharStreams.fromString(s));
	}

	/*---------------------------------------------------------------------*/

	public static List<List<String>> parseAsList(String s) throws Exception
	{
		return parseAsList(CharStreams.fromString(s));
	}

	/*---------------------------------------------------------------------*/

	public static List<Map<String, String>> parseAsMap(InputStream inputStream) throws Exception
	{
		return parseAsMap(CharStreams.fromStream(inputStream));
	}

	/*---------------------------------------------------------------------*/

	public static List<List<String>> parseAsList(InputStream inputStream) throws Exception
	{
		return parseAsList(CharStreams.fromStream(inputStream));
	}

	/*---------------------------------------------------------------------*/

	public static List<Map<String, String>> parseAsMap(CharStream charStream) throws Exception
	{
		List<Map<String, String>> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		List<List<String>> list = parseAsList(charStream);

		/*-----------------------------------------------------------------*/

		Map<String, String> map;

		if(list.isEmpty() == false)
		{
			final int nb1 = list.size();
			final int nb2 = list.get(0).size();

			for(int i = 1; i < nb1; i++)
			{
				/*---------------------------------------------------------*/

				if(list.get(i).size() != nb2)
				{
					throw new Exception("corrupted CSV file");
				}

				/*---------------------------------------------------------*/

				map = new LinkedHashMap<>();

				for(int j = 0; j < nb2; j++)
				{
					map.put(
						list.get(0).get(j)
						,
						list.get(i).get(j)
					);
				}

				result.add(map);

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static List<List<String>> parseAsList(CharStream charStream) throws Exception
	{
		/*-----------------------------------------------------------------*/

		CSVParser parser = new CSVParser(new CommonTokenStream(new CSVLexer(charStream)));

		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy());

		/*-----------------------------------------------------------------*/

		return visitFile(parser.file());

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static List<List<String>> visitFile(CSVParser.FileContext ctx)
	{
		List<List<String>> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			if(child instanceof CSVParser.RowContext)
			{
				result.add(visitRow((CSVParser.RowContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static List<String> visitRow(CSVParser.RowContext ctx)
	{
		List<String> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			if(child instanceof CSVParser.FieldContext)
			{
				result.add(visitField((CSVParser.FieldContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static String visitField(CSVParser.FieldContext ctx)
	{
		String result = ctx.getText();

		/**/ if(result.charAt(0) == '\"')
		{
			result = Unescape.unescape(
				result.substring(1, result.length() - 1).replace("\"\"", "\"")
			);
		}
		else if(result.charAt(0) == '\'')
		{
			result = Unescape.unescape(
				result.substring(1, result.length() - 1).replace("\'\'", "\'")
			);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
}
