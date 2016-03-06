package net.hep.ami;

import java.io.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.hep.ami.utility.*;

import org.w3c.dom.*;

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

	private static final Map<String, Tuple> m_converters = new HashMap<String, Tuple>();

	/*---------------------------------------------------------------------*/

	static
	{
		try
		{
			/*-------------------------------------------------------------*/
			/* GET INPUT STREAM                                            */
			/*-------------------------------------------------------------*/

			InputStream inputStream = ConverterSingleton.class.getResourceAsStream("/XSLT.xml");

			/*-------------------------------------------------------------*/
			/* PARSE FILE                                                  */
			/*-------------------------------------------------------------*/

			Document document = (Document) XMLFactories.newDocument(inputStream);

			/*-------------------------------------------------------------*/
			/* READ FILE                                                   */
			/*-------------------------------------------------------------*/

			NodeList nodeList = document.getElementsByTagName("transform");

			/*-------------------------------------------------------------*/
			/* GET NUMBER OF CONVERTERS                                    */
			/*-------------------------------------------------------------*/

			final int numberOfConverters = nodeList.getLength();

			/*-------------------------------------------------------------*/
			/* ADD CONVERTERS                                              */
			/*-------------------------------------------------------------*/

			for(int i = 0; i < numberOfConverters; i++)
			{
				Node node = nodeList.item(i);

				addConverter(
					XMLFactories.getAttribute(node, "xslt", (((((""))))))
					,
					XMLFactories.getAttribute(node, "mime", "text/plain")
				);
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.fatal(e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addConverter(String xslt, String mime)
	{
		try
		{
			/*-------------------------------------------------------------*/
			/* GET INPUT STREAM                                            */
			/*-------------------------------------------------------------*/

			InputStream inputStream = ConverterSingleton.class.getResourceAsStream(xslt);

			/*-------------------------------------------------------------*/
			/* PARSE FILE                                                  */
			/*-------------------------------------------------------------*/

			Templates templates = XMLFactories.newTemplates(inputStream);

			/*-------------------------------------------------------------*/
			/* ADD CONVERTER                                               */
			/*-------------------------------------------------------------*/

			m_converters.put(
				new File(xslt).getName()
				,
				new Tuple(
					templates,
					mime
				)
			);

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.error(e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	public static String convert(String converter, Reader reader, Writer writer) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET TRANSFORM                                                   */
		/*-----------------------------------------------------------------*/

		Tuple tuple = m_converters.get(converter);

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

		for(Map.Entry<String, Tuple> entry: m_converters.entrySet())
		{
			xslt = entry.getKey();
			mime = entry.getValue().y;

			result.append(
				"<row>"
				+
				"<field name=\"xslt\"><![CDATA[" + xslt + "]]></field>"
				+
				"<field name=\"mime\"><![CDATA[" + mime + "]]></field>"
				+
				"</row>"
			);
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
