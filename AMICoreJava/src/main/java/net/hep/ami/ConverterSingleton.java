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

	private static final class Tuple extends Tuple3<String, String, Templates>
	{
		private static final long serialVersionUID = -4041558092571279841L;

		public Tuple(String _x, String _y, Templates _z)
		{
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_converters = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

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
			LogSingleton.root.error(LogSingleton.FATAL, "could not add converters", e);
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addConverters() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("router_catalog"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = driver.executeSQLQuery(true, "SELECT `xslt`, `mime` FROM `router_converter`");

			/*-------------------------------------------------------------*/
			/* ADD CONVERTERS                                              */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet.iterate())
			{
				try
				{
					addConverter(
						row.getValue(0),
						row.getValue(1)
					);
				}
				catch(Exception e)
				{
					LogSingleton.root.error(LogSingleton.FATAL, "for converter `{}`: ", row.getValue(0), e);
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
		try(InputStream inputStream = ConverterSingleton.class.getResourceAsStream(xslt))
		{
			/*-------------------------------------------------------------*/
			/* PARSE XSLT FILE                                             */
			/*-------------------------------------------------------------*/

			Templates templates = XMLFactory.newTemplates(inputStream);

			/*-------------------------------------------------------------*/
			/* ADD CONVERTER                                               */
			/*-------------------------------------------------------------*/

			String name = new File(xslt).getName();

			s_converters.put(
				name
				,
				new Tuple(
					name,
					mime,
					templates
				)
			);

			/*-------------------------------------------------------------*/
		}
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

		Transformer transformer = tuple.z.newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		transformer.transform(
			new StreamSource(reader),
			new StreamResult(writer)
		);

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

		for(Tuple tuple: s_converters.values())
		{
			result.append("<row>")
			      .append("<field name=\"xslt\"><![CDATA[").append(tuple.x).append("]]></field>")
			      .append("<field name=\"mime\"><![CDATA[").append(tuple.y).append("]]></field>")
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
