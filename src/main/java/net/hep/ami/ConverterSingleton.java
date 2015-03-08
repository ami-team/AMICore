package net.hep.ami;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.hep.ami.utility.*;

import org.w3c.dom.*;

public class ConverterSingleton {
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple2<Transformer, String> {

		public Tuple(Transformer _x, String _y) {
			super(_x, _y);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> m_transformers = new HashMap<String, Tuple>();

	/*---------------------------------------------------------------------*/

	static {

		try {
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

			final int nr = nodeList.getLength();

			/*-------------------------------------------------------------*/
			/* ADD CONVERTERS                                              */
			/*-------------------------------------------------------------*/

			for(int i = 0; i < nr; i++) {

				Node node = nodeList.item(i);

				addConverter(
					XMLFactories.getAttribute(node, "xslt", (((((""))))))
					,
					XMLFactories.getAttribute(node, "mime", "text/plain")
				);
			}

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addConverter(String xslt, String mime) {

		try {
			/*-------------------------------------------------------------*/
			/* GET INPUT STREAM                                            */
			/*-------------------------------------------------------------*/

			InputStream inputStream = ConverterSingleton.class.getResourceAsStream(xslt);

			/*-------------------------------------------------------------*/
			/* PARSE FILE                                                  */
			/*-------------------------------------------------------------*/

			Transformer transformer = XMLFactories.newTransformer(inputStream);

			/*-------------------------------------------------------------*/
			/* ADD CONVERTER                                               */
			/*-------------------------------------------------------------*/

			m_transformers.put(
				new File(xslt).getName()
				,
				new Tuple(
					transformer,
					mime
				)
			);

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	public static String applyConverter(String fileName, Reader reader, Writer writer) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET TRANSFORM                                                   */
		/*-----------------------------------------------------------------*/

		Tuple tuple = m_transformers.get(fileName);

		if(tuple == null) {
			throw new Exception("converter `" + fileName + "` not found");
		}

		/*-----------------------------------------------------------------*/
		/* APPLY TRANSFORM                                                 */
		/*-----------------------------------------------------------------*/

		Source source = new StreamSource(reader);
		Result target = new StreamResult(writer);

		tuple.x.transform(source, target);

		/*-----------------------------------------------------------------*/
		/* RETURN MIME                                                     */
		/*-----------------------------------------------------------------*/

		return tuple.y;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listConverters() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Entry<String, Tuple> entry: m_transformers.entrySet()) {

			String xslt = entry.getKey();
			String mime = entry.getValue().y;

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

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
