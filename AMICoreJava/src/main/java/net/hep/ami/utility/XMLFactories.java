package net.hep.ami.utility;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.sf.saxon.*;
import net.sf.saxon.dom.*;

import org.w3c.dom.*;

public class XMLFactories
{
	/*---------------------------------------------------------------------*/

	private XMLFactories() {}

	/*---------------------------------------------------------------------*/

	public static Document newDocument(InputStream inputStream) throws Exception
	{
		DocumentBuilderImpl documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.parse(
			inputStream
		);
	}

	/*---------------------------------------------------------------------*/

	public static Templates newTemplates(InputStream inputStream) throws Exception
	{
		TransformerFactoryImpl transformerFactory = new TransformerFactoryImpl();

		return transformerFactory.newTemplates(new StreamSource(
			inputStream
		));
	}

	/*---------------------------------------------------------------------*/

	public static String getAttribute(Node node, String name)
	{
		Node attr = node.getAttributes().getNamedItem(name);

		return attr != null ? attr.getNodeValue().trim()
		                    : ""
		;
	}

	/*---------------------------------------------------------------------*/

	public static String getAttribute(Node node, String name, String defaultValue)
	{
		Node attr = node.getAttributes().getNamedItem(name);

		return attr != null ? attr.getNodeValue().trim()
		                    : defaultValue
		;
	}

	/*---------------------------------------------------------------------*/

	public static String getContent(Node node)
	{
		return node.getTextContent().trim();
	}

	/*---------------------------------------------------------------------*/
}
