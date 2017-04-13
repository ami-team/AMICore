package net.hep.ami;

import java.io.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

public class ConverterSingleton
{
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple2<Templates, String>
	{
		public Tuple(Templates _x, String _y)
		{
			super(_x, _y);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_converters = new ConcurrentHashMap<>();

	/*---------------------------------------------------------------------*/

	private ConverterSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		s_converters.clear();

		try
		{
			addConverters();
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.error(LogSingleton.FATAL, "could not add converters", e);
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addConverters() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = driver.executeQuery("SELECT `xslt`, `mime` FROM `router_converter`");

			/*-------------------------------------------------------------*/
			/* ADD CONVERTERS                                              */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet.iter())
			{
				try
				{
					addConverter(
						row.getValue(0)
						,
						row.getValue(1)
					);
				}
				catch(Exception e)
				{
					LogSingleton.defaultLogger.error(LogSingleton.FATAL, "for converter `" + row.getValue(0) + "`", e);
				}
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void addConverter(String xslt, String mime) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = ConverterSingleton.class.getResourceAsStream(xslt);

		/*-----------------------------------------------------------------*/
		/* PARSE XSLT FILE                                                 */
		/*-----------------------------------------------------------------*/

		Templates templates = XMLFactories.newTemplates(inputStream);

		/*-----------------------------------------------------------------*/
		/* ADD CONVERTER                                                   */
		/*-----------------------------------------------------------------*/

		s_converters.put(
			new File(xslt).getName()
			,
			new Tuple(
				templates,
				mime
			)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String convert(String converter, Reader reader, Writer writer) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET TRANSFORM                                                   */
		/*-----------------------------------------------------------------*/

		Tuple tuple = s_converters.get(converter);

		if(tuple == null)
		{
			throw new Exception("converter `" + converter + "` not found");
		}

		/*-----------------------------------------------------------------*/
		/* APPLY TRANSFORM                                                 */
		/*-----------------------------------------------------------------*/

		Source source = new StreamSource(reader);
		Result target = new StreamResult(writer);

		Transformer transformer = tuple.x.newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.transform(source, target);

		/*-----------------------------------------------------------------*/
		/* RETURN MIME                                                     */
		/*-----------------------------------------------------------------*/

		return tuple.y;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listConverters()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		String xslt;
		String mime;

		for(Map.Entry<String, Tuple> entry: s_converters.entrySet())
		{
			xslt = entry.getKey();
			mime = entry.getValue().y;

			result.append("<row>")
			      .append("<field name=\"xslt\"><![CDATA[").append(xslt).append("]]></field>")
			      .append("<field name=\"mime\"><![CDATA[").append(mime).append("]]></field>")
			      .append("</row>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
