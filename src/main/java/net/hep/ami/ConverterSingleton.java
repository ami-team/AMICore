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

	private static Map<String, Transformer> m_transformers = new HashMap<String, Transformer>();

	/*---------------------------------------------------------------------*/

	static {

		try {
			/*-------------------------------------------------------------*/
			/* GET INPUT STREAM                                            */
			/*-------------------------------------------------------------*/

			InputStream inputStream = ConfigSingleton.class.getResourceAsStream("/XSLT.xml");

			/*-------------------------------------------------------------*/
			/* PARSE FILE                                                  */
			/*-------------------------------------------------------------*/

			Document document = XMLFactories.newDocument(inputStream);

			/*-------------------------------------------------------------*/
			/* READ FILE                                                   */
			/*-------------------------------------------------------------*/

			NodeList nodeList = document.getElementsByTagName("transform");

			/*-------------------------------------------------------------*/
			/* ADD CONVERTERS                                              */
			/*-------------------------------------------------------------*/

			for(int i = 0; i < nodeList.getLength(); i++) {

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

			InputStream inputStream = ConfigSingleton.class.getResourceAsStream(xslt);

			/*-------------------------------------------------------------*/
			/* PARSE FILE                                                  */
			/*-------------------------------------------------------------*/

			Transformer transformer = XMLFactories.newTransformer(inputStream);

			/*-------------------------------------------------------------*/
			/* ADD CONVERTER                                               */
			/*-------------------------------------------------------------*/

			transformer.setParameter("mime", mime);

			m_transformers.put(new File(xslt).getName(), transformer);

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	public static String applyConverter(String fileName, Reader reader, Writer writer) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CHECK TRANSFORM                                                 */
		/*-----------------------------------------------------------------*/

		if(m_transformers.containsKey(fileName) == false) {
			throw new Exception("converter `" + fileName + "` not found");
		}

		/*-----------------------------------------------------------------*/
		/* APPLY TRANSFORM                                                 */
		/*-----------------------------------------------------------------*/

		Transformer transformer = m_transformers.get(fileName);

		Source source = new StreamSource(reader);
		Result target = new StreamResult(writer);

		transformer.transform(source, target);

		/*-----------------------------------------------------------------*/
		/* RETURN MIME                                                     */
		/*-----------------------------------------------------------------*/

		return transformer.getParameter("mime").toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listConverters() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Entry<String, Transformer> entry: m_transformers.entrySet()) {

			String xslt = entry.getKey();
			String mime = entry.getValue().getParameter("mime").toString();

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
