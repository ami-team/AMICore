package net.hep.ami.utility;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import net.hep.ami.*;
import net.hep.ami.utility.parser.*;

import net.sf.saxon.*;
import net.sf.saxon.om.*;
import net.sf.saxon.dom.*;
import net.sf.saxon.lib.*;
import net.sf.saxon.expr.*;
import net.sf.saxon.trans.*;
import net.sf.saxon.value.*;

import org.jetbrains.annotations.*;

public class XMLFactory
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private XMLFactory() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static class AMITransformerFactoryImpl extends TransformerFactoryImpl
	{
		/*------------------------------------------------------------------------------------------------------------*/

		private static class Replace extends ExtensionFunctionDefinition
		{
			/*--------------------------------------------------------------------------------------------------------*/

			@Override
			public StructuredQName getFunctionQName()
			{
				return new StructuredQName("ami", "http://ami.in2p3.fr/xsl", "replace");
			}

			/*--------------------------------------------------------------------------------------------------------*/

			@Override
			public SequenceType[] getArgumentTypes()
			{
				return new SequenceType[] {
					SequenceType.OPTIONAL_STRING,
					SequenceType.SINGLE_BOOLEAN,
				};
			}

			/*--------------------------------------------------------------------------------------------------------*/

			@Override
			public SequenceType getResultType(SequenceType[] suppliedArgumentTypes)
			{
				return SequenceType.SINGLE_STRING;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			@Override
			@SuppressWarnings("rawtypes")
			public ExtensionFunctionCall makeCallExpression()
			{
				/*----------------------------------------------------------------------------------------------------*/

				return new ExtensionFunctionCall() {

					public Sequence<?> call(XPathContext context, Sequence[] arguments) throws XPathException
					{

					Item arg0 = arguments[0].head();

					String s = (arg0 != null) ? arg0.getStringValue() : "";

					boolean q = ((BooleanValue) arguments[1]).getBooleanValue();

					if(q) {
						return StringValue.makeStringValue(Utility.escapeJSONString(s, false));
					}
					else {
						return StringValue.makeStringValue(s.replace("\n", "\\n").replace("\t", "\\t"));
					}
					}
				};

				/*----------------------------------------------------------------------------------------------------*/
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		public AMITransformerFactoryImpl()
		{
			this.getConfiguration().registerExtensionFunction(new Replace());
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static org.w3c.dom.Document newDocument(InputStream inputStream) throws Exception
	{
		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.parse(new org.xml.sax.InputSource(
			inputStream
		));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static javax.xml.transform.Templates newTemplates(InputStream inputStream) throws Exception
	{
		TransformerFactory transformerFactory = new AMITransformerFactoryImpl();

		return transformerFactory.newTemplates(new StreamSource(
			inputStream
		));
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* NODE ATTRIBUTES                                                                                                */
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getNodeAttribute(@NotNull org.w3c.dom.Node node, @NotNull String name)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		if(attr != null)
		{
			return ConfigSingleton.checkString(attr.getNodeValue(), "");
		}

		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("_, _, !null -> !null")
	public static String getNodeAttribute(@NotNull org.w3c.dom.Node node, @NotNull String name, @Nullable String defaultValue)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		if(attr != null)
		{
			return ConfigSingleton.checkString(attr.getNodeValue(), defaultValue);
		}

		return defaultValue;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, _, !null -> !null", pure = true)
	public static Boolean getNodeAttribute(@NotNull org.w3c.dom.Node node, @NotNull String name, @Nullable Boolean defaultValue)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		if(attr != null)
		{
			try
			{
				return Bool.valueOf(ConfigSingleton.checkString(attr.getNodeValue(), ""));
			}
			catch(NumberFormatException e)
			{
				return defaultValue;
			}
		}

		return defaultValue;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, _, !null -> !null", pure = true)
	public static Integer getNodeAttribute(@NotNull org.w3c.dom.Node node, @NotNull String name, @Nullable Integer defaultValue)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		if(attr != null)
		{
			try
			{
				return Integer.valueOf(ConfigSingleton.checkString(attr.getNodeValue(), ""));
			}
			catch(NumberFormatException e)
			{
				return defaultValue;
			}
		}

		return defaultValue;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, _, !null -> !null", pure = true)
	public static Float getNodeAttribute(@NotNull org.w3c.dom.Node node, @NotNull String name, @Nullable Float defaultValue)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		if(attr != null)
		{
			try
			{
				return Float.valueOf(ConfigSingleton.checkString(attr.getNodeValue(), ""));
			}
			catch(NumberFormatException e)
			{
				return defaultValue;
			}
		}

		return defaultValue;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, _, !null -> !null", pure = true)
	public static Double getNodeAttribute(@NotNull org.w3c.dom.Node node, @NotNull String name, @Nullable Double defaultValue)
	{
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);

		if(attr != null)
		{
			try
			{
				return Double.valueOf(ConfigSingleton.checkString(attr.getNodeValue(), ""));
			}
			catch(NumberFormatException e)
			{
				return defaultValue;
			}
		}

		return defaultValue;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* NODE CONTENT                                                                                                   */
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getNodeContent(@NotNull org.w3c.dom.Node node)
	{
		return ConfigSingleton.checkString(node.getTextContent(), "");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static String getNodeContent(@NotNull org.w3c.dom.Node node, @Nullable String defaultValue)
	{
		return ConfigSingleton.checkString(node.getTextContent(), defaultValue);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Boolean getNodeContent(@NotNull org.w3c.dom.Node node, Boolean defaultValue)
	{
		try
		{
			return Bool.valueOf(ConfigSingleton.checkString(node.getTextContent(), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Integer getNodeContent(@NotNull org.w3c.dom.Node node, Integer defaultValue)
	{
		try
		{
			return Integer.valueOf(ConfigSingleton.checkString(node.getTextContent(), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Float getNodeContent(@NotNull org.w3c.dom.Node node, Float defaultValue)
	{
		try
		{
			return Float.valueOf(ConfigSingleton.checkString(node.getTextContent(), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Double getNodeContent(@NotNull org.w3c.dom.Node node, Double defaultValue)
	{
		try
		{
			return Double.valueOf(ConfigSingleton.checkString(node.getTextContent(), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* HELPERS                                                                                                        */
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String nodeToString(@NotNull org.w3c.dom.Node node) throws Exception
	{
		StringWriter result = new StringWriter();

		/*------------------------------------------------------------------------------------------------------------*/

		Transformer transformer = TransformerFactoryImpl.newInstance().newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		transformer.transform(
			new DOMSource(node),
			new StreamResult(result)
		);

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static Iterable<org.w3c.dom.Node> nodeListToIterable(@NotNull org.w3c.dom.NodeList nodeList)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		return () -> new Iterator<>()
		{
			/*--------------------------------------------------------------------------------------------------------*/

			private /*-*/ int m_i = 0x000000000000000000;
			private final int m_l = nodeList.getLength();

			/*--------------------------------------------------------------------------------------------------------*/

			@Override
			public boolean hasNext()
			{
				return m_i < m_l;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			@NotNull
			@Override
			public org.w3c.dom.Node next()
			{
				if(m_i < m_l)
				{
					return nodeList.item(m_i++);
				}

				throw new NoSuchElementException();
			}

			/*--------------------------------------------------------------------------------------------------------*/
		};

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
