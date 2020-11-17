package net.hep.ami.command;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.*;

import org.w3c.dom.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public abstract class AbstractProxyCommand extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AbstractProxyCommand(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public final StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* BUILD POST DATA                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder argumentString = new StringBuilder();

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			argumentString.append(" -").append(entry.getKey()).append("=\"").append(Utility.escapeJavaString(entry.getValue())).append("\"");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		argumentString.append(" -AMIUser=\"").append(Utility.escapeJavaString(m_AMIUser)).append("\"");
		argumentString.append(" -AMIPass=\"").append(Utility.escapeJavaString(m_AMIPass)).append("\"");

		argumentString.append(" -clientDN=\"").append(SecuritySingleton.encrypt(m_clientDN)).append("\"");
		argumentString.append(" -issuerDN=\"").append(SecuritySingleton.encrypt(m_issuerDN)).append("\"");
		argumentString.append(" -notBefore=\"").append(SecuritySingleton.encrypt(m_notBefore)).append("\"");
		argumentString.append(" -notAfter=\"").append(SecuritySingleton.encrypt(m_notAfter)).append("\"");

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder input = new StringBuilder().append("Command=").append(command()).append(URLEncoder.encode(argumentString.toString(), StandardCharsets.UTF_8));

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		HttpURLConnection connection = HttpConnectionFactory.openConnection(ConfigSingleton.getProperty("proxy_command_endpoint"));

		/*------------------------------------------------------------------------------------------------------------*/

		Document document;

		try
		{
			try
			{
				/*----------------------------------------------------------------------------------------------------*/

				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF");
				connection.setRequestProperty("Content-Length", String.valueOf(input.length()));

				connection.setRequestProperty("Connection", "Close");

				connection.setRequestProperty("User-Agent", m_userAgent);

				connection.setConnectTimeout(ConfigSingleton.getProperty("proxy_command_connect_timeout", 60000));
				connection.setReadTimeout(ConfigSingleton.getProperty("proxy_command_read_timeout", 60000));
				connection.setDoOutput(true);
				connection.setDoInput(true);

				/*----------------------------------------------------------------------------------------------------*/

				try(OutputStream outputStream = connection.getOutputStream())
				{
					TextFile.write(outputStream, input);
				}

				/*----------------------------------------------------------------------------------------------------*/

				document = XMLFactory.newDocument(connection.getInputStream());

				/*----------------------------------------------------------------------------------------------------*/
			}
			finally
			{
				connection.disconnect();
			}
		}
		catch(IOException e)
		{
			throw new IOException(e.getMessage() + " (DD79E02F_E3F5_C0BF_350D_7AB71C194639)", e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		for(Node node: XMLFactory.nodeListToIterable(document.getElementsByTagName("info"))) {
			result.append(XMLFactory.nodeToString(node));
		}

		for(Node node: XMLFactory.nodeListToIterable(document.getElementsByTagName("error"))) {
			result.append(XMLFactory.nodeToString(node));
		}

		for(Node node: XMLFactory.nodeListToIterable(document.getElementsByTagName("fieldDescriptions"))) {
			result.append(XMLFactory.nodeToString(node));
		}

		for(Node node: XMLFactory.nodeListToIterable(document.getElementsByTagName("rowset"))) {
			result.append(XMLFactory.nodeToString(node));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	abstract protected String command();

	/*----------------------------------------------------------------------------------------------------------------*/
}
