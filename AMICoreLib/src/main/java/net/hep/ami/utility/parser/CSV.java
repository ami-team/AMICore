package net.hep.ami.utility.parser;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;

import org.jetbrains.annotations.*;

public class CSV
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<Map<String, String>> parseAsMap(@NotNull String s) throws Exception
	{
		return parseAsMap(CharStreams.fromString(s));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<List<String>> parseAsList(@NotNull String s) throws Exception
	{
		return parseAsList(CharStreams.fromString(s));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<Map<String, String>> parseAsMap(@NotNull InputStream inputStream) throws Exception
	{
		return parseAsMap(CharStreams.fromStream(inputStream));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<List<String>> parseAsList(@NotNull InputStream inputStream) throws Exception
	{
		return parseAsList(CharStreams.fromStream(inputStream));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static List<Map<String, String>> parseAsMap(@NotNull CharStream charStream) throws Exception
	{
		List<Map<String, String>> result = new ArrayList<>();

		/*------------------------------------------------------------------------------------------------------------*/

		List<List<String>> list = parseAsList(charStream);

		/*------------------------------------------------------------------------------------------------------------*/

		if(!list.isEmpty())
		{
			Map<String, String> map;

			final int nb1 = list.size();
			final int nb2 = list.get(0).size();

			for(int i = 1; i < nb1; i++)
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(list.get(i).size() != nb2)
				{
					throw new Exception("corrupted CSV file");
				}

				/*----------------------------------------------------------------------------------------------------*/

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

				/*----------------------------------------------------------------------------------------------------*/
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static List<List<String>> parseAsList(@NotNull CharStream charStream) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		CSVLexer lexer = new CSVLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		CSVParser parser = new CSVParser(tokenStream);

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

		List<List<String>> result = parser.file().v;

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
