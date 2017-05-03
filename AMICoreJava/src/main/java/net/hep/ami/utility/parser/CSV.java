package net.hep.ami.utility.parser;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;

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

		final int nb1 = list.size();

		/*-----------------------------------------------------------------*/

		if(nb1 > 0)
		{
			Map<String, String> map;

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
		CSVParser parser = new CSVParser(new CommonTokenStream(new CSVLexer(charStream)));

		parser.setErrorHandler(new DefaultErrorStrategy());

		return parser.file().v;
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		System.out.println(CSV.parseAsList(""));
		System.out.println("----");
		//System.out.println(CSV.parseAsList("a,b # hello \nc,d\n,k"));
		System.out.println(CSV.parseAsList(",k,l,"));
		System.out.println("----");
		System.out.println(CSV.parseAsList(" # hello "));

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
