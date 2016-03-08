package net.hep.ami;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.util.log.*;

import org.w3c.dom.*;

public class AMIMini extends AbstractHandler
{
	/*---------------------------------------------------------------------*/

	public static interface Handler
	{
		public void init(Map<String, String> config);

		public StringBuilder exec(String command, Map<String, String> arguments, Map<String, String> config, String ip);

		public StringBuilder help(String command, Map<String, String> arguments, Map<String, String> config, String ip);
	}

	/*---------------------------------------------------------------------*/

	private Handler m_handler;

	private Map<String, String> m_config;

	/*---------------------------------------------------------------------*/

	private static File _toFile(String configPathName)
	{
		return new File(
			configPathName.endsWith(".xml") == false ? configPathName + File.separator + "AMI.xml"
			                                         : configPathName ////////////////////////////
		);
	}

	/*---------------------------------------------------------------------*/

	private AMIMini(Handler handler)
	{
		super();

		/*-----------------------------------------------------------------*/
		/* SET HANDLER                                                     */
		/*-----------------------------------------------------------------*/

		m_handler = handler;

		/*-----------------------------------------------------------------*/
		/* READ CONFIG                                                     */
		/*-----------------------------------------------------------------*/

		m_config = new HashMap<String, String>();

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* FIND CONFFILE                                               */
			/*-------------------------------------------------------------*/

			File file;
			String path;

			path = System.getProperty("ami.conffile");

			if(path == null
			   ||
			   (file = _toFile(path.trim())).exists() == false
			 ) {

				path = System.getProperty("catalina.base");

				if(path == null
				   ||
				   (file = _toFile(path.trim() + File.separator + "conf")).exists() == false
				 ) {

					path = System.getProperty((("user.home")));

					if(path == null
					   ||
					   (file = _toFile(path.trim() + File.separator + ".ami")).exists() == false
					 ) {
						/*----------------------------*/
						/* DEFAULT FOR DEBs/RPMs      */
						/*----------------------------*/

						file = _toFile("/etc/ami");

						/*----------------------------*/
					}
				}
			}

			/*-------------------------------------------------------------*/
			/* PARSE FILE                                                  */
			/*-------------------------------------------------------------*/

			InputStream inputStream = new FileInputStream(file);

			/*-------------------------------------------------------------*/

			Document document;

			try
			{
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
			}
			finally
			{
				inputStream.close();
			}

			/*-------------------------------------------------------------*/
			/* READ FILE                                                   */
			/*-------------------------------------------------------------*/

			NodeList nodeList = document.getElementsByTagName("property");

			/*-------------------------------------------------------------*/
			/* GET NUMBER OF PROPERTIES                                    */
			/*-------------------------------------------------------------*/

			final int numberOfNodes = nodeList.getLength();

			/*-------------------------------------------------------------*/
			/* ADD PROPERTIES                                              */
			/*-------------------------------------------------------------*/

			Node node;
			Node attr;

			for(int i = 0; i < numberOfNodes; i++)
			{
				node = nodeList.item(i);
				attr = node.getAttributes()
				           .getNamedItem("name");

				if(attr != null)
				{
					m_config.put(
							attr.getNodeValue().trim()
							,
							node.getTextContent().trim()
					);
				}
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			Log.getRootLogger().warn("could not read configuration file: " + e.getMessage());
		}

		/*-----------------------------------------------------------------*/
		/* INIT                                                            */
		/*-----------------------------------------------------------------*/

		m_handler.init(m_config);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static final SimpleDateFormat s_simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_xml10Pattern = Pattern.compile(
		  "[^"
		+ "\u0009\r\n"
		+ "\u0020-\uD7FF"
		+ "\uE000-\uFFFD"
		+ "\uD800\uDC00-\uDBFF\uDFFF"
		+ "]+"
	);

	/*---------------------------------------------------------------------*/

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
	{
		/*-----------------------------------------------------------------*/
		/* SET UTF-8 AS DEFAULT ENCODING                                   */
		/*-----------------------------------------------------------------*/

		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");

		/*-----------------------------------------------------------------*/
		/* CROSS-ORIGIN RESOURCE SHARING                                   */
		/*-----------------------------------------------------------------*/

		String origin = req.getHeader("Origin");

		if(origin != null)
		{
			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Origin", origin);
		}

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES                                              */
		/*-----------------------------------------------------------------*/

		String command = req.getParameter("Command");
		command = (command != null) ? command.trim() : "";

		String converter = req.getParameter("Converter");
		converter = (converter != null) ? converter.trim() : "";

		String textOutput = req.getParameter("TextOutput");
		textOutput = (textOutput != null) ? textOutput.trim() : "";

		/*-----------------------------------------------------------------*/
		/* GET WRITER                                                      */
		/*-----------------------------------------------------------------*/

		PrintWriter writer = res.getWriter();

		/*-----------------------------------------------------------------*/
		/* PING                                                            */
		/*-----------------------------------------------------------------*/

		if(command.trim().startsWith("Ping")
		   ||
		   command.trim().startsWith("AMIPing")
		 ) {
			res.setContentType("text/xml");

			writer.print(info(
				"AMI is alive.")
			);

			writer.close();

			return;
		}

		/*-----------------------------------------------------------------*/
		/* SET CONTENT DISPOSITION                                         */
		/*-----------------------------------------------------------------*/

		if(textOutput.isEmpty())
		{
			res.setHeader("Content-disposition", "inline; filename=" + ((("AMI"))) );
		}
		else
		{
			res.setHeader("Content-disposition", "attachment; filename=" + textOutput);
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		String data;

		try
		{
			/*-------------------------------------------------------------*/
			/* PARSE COMMAND AND ARGUMENTS                                 */
			/*-------------------------------------------------------------*/

			AbstractMap.SimpleEntry<String, Map<String, String>> result = parse(command);

			/*-------------------------------------------------------------*/
			/* BUILD RESULT AND EXECUTE COMMAND                            */
			/*-------------------------------------------------------------*/

			StringBuilder stringBuilder = new StringBuilder();

			/*-------------------------------------------------------------*/

			stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

			stringBuilder.append("<AMIMessage>");

			stringBuilder.append("<command><![CDATA[" + result.getKey() + "]]></command>");

			stringBuilder.append("<arguments>");
			for(Map.Entry<String, String> entry: result.getValue().entrySet()) stringBuilder.append("<argument name=\"" + entry.getKey() + "\" value=\"" + entry.getValue().replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") + "\"/>");
			stringBuilder.append("</arguments>");

			stringBuilder.append("<executionDate>" + s_simpleDateFormat.format(new Date()) + "</executionDate>");

			if(result.getValue().containsKey("help") == false)
			{
				/*---------------------------------------------------------*/
				/* EXEC                                                    */
				/*---------------------------------------------------------*/

				long t1 = System.currentTimeMillis();
				StringBuilder content = m_handler.exec(result.getKey(), result.getValue(), m_config, req.getRemoteAddr());
				long t2 = System.currentTimeMillis();

				/**/

				stringBuilder.append("<executionTime>" + String.format(Locale.US, "%.3f", 0.001f * (t2 - t1)) + "</executionTime>");

				stringBuilder.append("<Result>");
				stringBuilder.append(s_xml10Pattern.matcher(content).replaceAll("?"));
				stringBuilder.append("</Result>");

				/*---------------------------------------------------------*/
			}
			else
			{
				/*---------------------------------------------------------*/
				/* HELP                                                    */
				/*---------------------------------------------------------*/

				long t1 = System.currentTimeMillis();
				StringBuilder content = m_handler.help(result.getKey(), result.getValue(), m_config, req.getRemoteAddr());
				long t2 = System.currentTimeMillis();

				/**/

				stringBuilder.append("<executionTime>" + String.format(Locale.US, "%.3f", 0.001f * (t2 - t1)) + "</executionTime>");

				stringBuilder.append("<help><![CDATA[");
				stringBuilder.append(s_xml10Pattern.matcher(content).replaceAll("?"));
				stringBuilder.append("]]></help>");

				/*---------------------------------------------------------*/
			}

			stringBuilder.append("</AMIMessage>");

			/*-------------------------------------------------------------*/

			data = stringBuilder.toString();

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			data = error(e.getMessage());
		}

		/*-----------------------------------------------------------------*/
		/* CONVERT RESULT                                                  */
		/*-----------------------------------------------------------------*/

		String mime;

		if(converter.isEmpty() == false)
		{
			StringReader stringReader = new StringReader(data);
			StringWriter stringWriter = new StringWriter(/**/);

			try
			{
				convert(converter, stringReader, stringWriter);

				data = stringWriter.toString();

				mime = "text/plain";
			}
			catch(Exception e)
			{
				data = error(e.getMessage());

				mime = "text/xml";
			}
		}
		else
		{
			mime = "text/xml";
		}

		/*-----------------------------------------------------------------*/
		/* WRITE RESULT                                                    */
		/*-----------------------------------------------------------------*/

		res.setContentType(mime);

		writer.print(data);

		/*-----------------------------------------------------------------*/
		/* CLOSE WRITER                                                    */
		/*-----------------------------------------------------------------*/

		writer.close();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String info(String s)
	{
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><info><![CDATA[" + s + "]]></info></AMIMessage>";
	}

	/*---------------------------------------------------------------------*/

	private String error(String s)
	{
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><error><![CDATA[" + s + "]]></error></AMIMessage>";
	}

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern1 = Pattern.compile(
		"^\\s*([a-zA-Z][a-zA-Z0-9]*)"
	);

	/*---------------------------------------------------------------------*/

	private static final Pattern s_pattern2 = Pattern.compile(
		"^[-]+([a-zA-Z][a-zA-Z0-9]*)(?:=\"("
		+
		"(?:\\\"|[^\"])*"
		+
		")\")?"
	);

	/*---------------------------------------------------------------------*/

	private static AbstractMap.SimpleEntry<String, Map<String, String>> parse(String s) throws Exception
	{
		String command;

		/***/ int i = 0x00000000;
		final int l = s.length();

		Map<String, String> arguments = new HashMap<String, String>();

		/*-----------------------------------------------------------------*/
		/* PARSE COMMAND                                                   */
		/*-----------------------------------------------------------------*/

		Matcher m = s_pattern1.matcher(s);

		if(m.find())
		{
			i += (command = m.group(1)).length();
		}
		else
		{
			throw new Exception("command syntax error, missing command name");
		}

		/*-----------------------------------------------------------------*/
		/* PARSE ARGUMENTS                                                 */
		/*-----------------------------------------------------------------*/

		char c;
		String key;
		String value;

		while(i < l)
		{
			/*-------------------------------------------------------------*/
			/* EAT SPACE                                                   */
			/*-------------------------------------------------------------*/

			c = s.charAt(i);

			if(c == ' '
			   ||
			   c == '\t'
			   ||
			   c == '\n'
			   ||
			   c == '\r'
			 ) {
				i++;

				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT ARGUMENT                                                */
			/*-------------------------------------------------------------*/

			m = s_pattern2.matcher(s.substring(i));

			if(m.find())
			{
				key = m.group(1);
				value = m.group(2);

				arguments.put(key, value != null ? unescape(value) : null);

				i += m.group(0).length();

				continue;
			}

			/*-------------------------------------------------------------*/
			/* SYNTAX ERROR                                                */
			/*-------------------------------------------------------------*/

			throw new Exception("command syntax error, invalid argument syntax");

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/* RETURN RESULT                                                   */
		/*-----------------------------------------------------------------*/

		return new AbstractMap.SimpleEntry<String, Map<String, String>>(
			command,
			arguments
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static String unescape(String s)
	{
		StringBuilder result = new StringBuilder(s.length());

		/*-----------------------------------------------------------------*/

		/***/ int i = 0x00000000;
		final int l = s.length();

		String code;
		char c;

		while(i < l)
		{
			c = s.charAt(i++);

			if(c == '\\')
			{
				c = s.charAt(i++);

				switch(c)
				{
					case '\\':
						c = '\\';
						break;

					case 'b':
						c = '\b';
						break;

					case 'f':
						c = '\f';
						break;

					case 'n':
						c = '\n';
						break;

					case 'r':
						c = '\r';
						break;

					case 't':
						c = '\t';
						break;

					case '\"':
						c = '\"';
						break;

					case '\'':
						c = '\'';
						break;

					case 'u':
						/*-------------------------------------------------*/
						/* UNICODE                                         */
						/*-------------------------------------------------*/

						if(l - i < 4)
						{
							c = 'u';
							break;
						}

						code = s.substring(i + 0, i + 4);
						i += 4;

						try
						{ 
							result.append(Character.toChars(Integer.parseInt(code, 16)));
						}
						catch(java.lang.NumberFormatException e)
						{
							result.append(/******************/ '?' /******************/);
						}

						continue;

						/*-------------------------------------------------*/
				}
			}

			result.append(c);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	private static void convert(String converter, Reader reader, Writer writer) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Source source = new StreamSource(reader);
		Result target = new StreamResult(writer);

		/*-----------------------------------------------------------------*/

		InputStream inputStream = AMIMini.class.getResourceAsStream("../../../xslt/" + converter);

		if(inputStream == null)
		{
			throw new Exception("converter `" + converter + "` not found");
		}

		/*-----------------------------------------------------------------*/

		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(inputStream));

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(source, target);
		}
		finally
		{
			inputStream.close();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Server newInstance(int port, Handler handler)
	{
		Server result = new Server(port);

		result.setHandler(new AMIMini(handler));

		return result;
	}

	/*---------------------------------------------------------------------*/
}
