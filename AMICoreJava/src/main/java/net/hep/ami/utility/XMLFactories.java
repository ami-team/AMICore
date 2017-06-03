package net.hep.ami.utility;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.sf.saxon.*;
import net.sf.saxon.dom.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class XMLFactories
{
	/*---------------------------------------------------------------------*/

	private XMLFactories() {}

	/*---------------------------------------------------------------------*/

	public static Document newDocument(InputStream inputStream) throws Exception
	{
		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.parse(new InputSource(
			inputStream
		));
	}

	/*---------------------------------------------------------------------*/

	public static Templates newTemplates(InputStream inputStream) throws Exception
	{
		TransformerFactory transformerFactory = new TransformerFactoryImpl();

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

	public static Iterable<Node> toIterable(NodeList nodeList)
	{
		if(nodeList == null)
		{
			throw new NullPointerException();
		}

		return new Iterable<Node>()
		{
			public Iterator<Node> iterator()
			{
				return new Iterator<Node>()
				{
					private /***/ int m_i = 0x000000000000000000;
					private final int m_l = nodeList.getLength();

					public boolean hasNext()
					{
						return m_i < m_l;
					}

					public Node next()
					{
						return nodeList.item(m_i++);
					}
				};
			}
		};
	}

	/*---------------------------------------------------------------------*/
}
