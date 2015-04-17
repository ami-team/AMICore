package net.hep.ami.utility;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.sf.saxon.*;
import net.sf.saxon.dom.*;

import org.w3c.dom.*;

public class XMLFactories {
	/*---------------------------------------------------------------------*/

	public static Document newDocument(InputStream inputStream) throws Exception {

		DocumentBuilder documentBuilderFactory = new DocumentBuilderImpl();

		try {
			return documentBuilderFactory.parse(inputStream);

		} finally {
			inputStream.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public static Templates newTemplates(InputStream inputStream) throws Exception {

		TransformerFactory transformerFactory = new TransformerFactoryImpl();

		try {
			return transformerFactory.newTemplates(new StreamSource(inputStream));

		} finally {
			inputStream.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public static String getAttribute(Node node, String name) {

		Node attr = node.getAttributes().getNamedItem(name);

		return attr != null ? attr.getNodeValue()
		                    : ""
		;
	}

	/*---------------------------------------------------------------------*/

	public static String getAttribute(Node node, String name, String defaultValue) {

		Node attr = node.getAttributes().getNamedItem(name);

		return attr != null ? attr.getNodeValue()
		                    : defaultValue
		;
	}

	/*---------------------------------------------------------------------*/

	public static String getContent(Node node) {

		return node.getTextContent().trim();
	}

	/*---------------------------------------------------------------------*/
}
