package net.hep.ami.utility;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.sf.saxon.*;
import net.sf.saxon.om.*;
import net.sf.saxon.dom.*;
import net.sf.saxon.lib.*;
import net.sf.saxon.expr.*;
import net.sf.saxon.trans.*;
import net.sf.saxon.value.*;

import net.hep.ami.utility.parser.*;

public class XMLFactory
{
	/*---------------------------------------------------------------------*/

	private XMLFactory() {}

	/*---------------------------------------------------------------------*/

	private static class AMITransformerFactoryImpl extends TransformerFactoryImpl
	{
		/*-----------------------------------------------------------------*/

		private class Replace extends ExtensionFunctionDefinition
		{
			/*-------------------------------------------------------------*/

			@Override
			public StructuredQName getFunctionQName()
			{
				return new StructuredQName("ami", "http://ami.in2p3.fr/xsl", "replace");
			}

			/*-------------------------------------------------------------*/

			@Override
			public SequenceType[] getArgumentTypes()
			{
				return new SequenceType[] {
					SequenceType.OPTIONAL_STRING,
					SequenceType.SINGLE_BOOLEAN,
				};
			}

			/*-------------------------------------------------------------*/

			@Override
			public SequenceType getResultType(SequenceType[] suppliedArgumentTypes)
			{
				return SequenceType.SINGLE_STRING;
			}

			/*-------------------------------------------------------------*/

			@Override
			@SuppressWarnings("rawtypes")
			public ExtensionFunctionCall makeCallExpression()
			{
				/*---------------------------------------------------------*/

				return new ExtensionFunctionCall() {

					public Sequence<?> call(XPathContext context, Sequence[] arguments) throws XPathException
					{
						Item arg0 = arguments[0].head();

						String s = (arg0 != null) ? arg0.getStringValue() : "";

						Boolean q = ((BooleanValue) arguments[1]).getBooleanValue();

						if(q) {
							return StringValue.makeStringValue(Utility.escapeJSONString(s));
						}
						else {
							return StringValue.makeStringValue(s.replace("\n", "\\n").replace("\t", "\\t"));
						}
					}
				};

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		public AMITransformerFactoryImpl()
		{
			this.getConfiguration().registerExtensionFunction(new Replace());
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static org.w3c.dom.Document newDocument(InputStream inputStream) throws Exception
	{
		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.parse(new org.xml.sax.InputSource(
			inputStream
		));
	}

	/*---------------------------------------------------------------------*/

	public static javax.xml.transform.Templates newTemplates(InputStream inputStream) throws Exception
	{
		TransformerFactory transformerFactory = new AMITransformerFactoryImpl();

		return transformerFactory.newTemplates(new StreamSource(
			inputStream
		));
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	public static String getAttribute(org.w3c.dom.Node node, String name)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		return attr != null ? attr.getNodeValue().trim()
		                    : ""
		;
	}

	/*---------------------------------------------------------------------*/

	public static String getAttribute(org.w3c.dom.Node node, String name, String defaultValue)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		return attr != null ? attr.getNodeValue().trim()
		                    : defaultValue
		;
	}

	/*---------------------------------------------------------------------*/

	public static String getContent(org.w3c.dom.Node node)
	{
		return node.getTextContent().trim();
	}

	/*---------------------------------------------------------------------*/

	public static Iterable<org.w3c.dom.Node> toIterable(org.w3c.dom.NodeList nodeList)
	{
		if(nodeList == null)
		{
			throw new NullPointerException();
		}

		return () -> new Iterator<org.w3c.dom.Node>()
		{
			private /***/ int m_i = 0x000000000000000000;
			private final int m_l = nodeList.getLength();

			@Override
			public boolean hasNext()
			{
				return m_i < m_l;
			}

			@Override
			public org.w3c.dom.Node next()
			{
				if(m_i < m_l)
				{
					return nodeList.item(m_i++);
				}

				throw new NoSuchElementException();
			}
		};
	}

	/*---------------------------------------------------------------------*/
}
