package net.hep.ami.utility;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

public class XMLFactories {
	/*---------------------------------------------------------------------*/

	private static final DocumentBuilderFactory m_documentBuilderFactory = DocumentBuilderFactory.newInstance();

	private static final   TransformerFactory     m_transformerFactory   =   TransformerFactory.newInstance()  ;

	/*---------------------------------------------------------------------*/

	public static Document newDocument(InputStream inputStream) throws Exception {

		try {
			return m_documentBuilderFactory.newDocumentBuilder().parse(inputStream);

		} finally {
			inputStream.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public static Transformer newTransformer(InputStream inputStream) throws Exception {

		try {
			return m_transformerFactory.newTransformer(new StreamSource(inputStream));

		} finally {
			inputStream.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public static String getAttribute(Node node, String name) {

		return getAttribute(node, name, "");
	}

	/*---------------------------------------------------------------------*/

	public static String getAttribute(Node node, String name, String defaultValue) {

		NamedNodeMap attributes = node.getAttributes();

		final int numberOfAttributes = attributes.getLength();

		for(int i = 0; i < numberOfAttributes; i++) {

			Attr attr = (Attr) attributes.item(i);

			String attrName = attr.getNodeName().trim();
			String attrValue = attr.getNodeValue().trim();

			if(attrName.equals(name)) {
				return attrValue;
			}
		}

		return defaultValue;
	}

	/*---------------------------------------------------------------------*/

	public static String getContent(Node node) {
		return node.getTextContent().trim();
	}

	/*---------------------------------------------------------------------*/
}
