package net.hep.ami.command;

import java.io.*;
import java.net.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public abstract class AbstractProxyCommand extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AbstractProxyCommand(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder input = new StringBuilder();
		StringBuilder output = new StringBuilder();

		/*-----------------------------------------------------------------*/
		/* BUILD POST DATA                                                 */
		/*-----------------------------------------------------------------*/

		StringBuffer argumentString = new StringBuffer();

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			argumentString.append(" -").append(entry.getKey()).append("=\"").append(Utility.escapeJavaString(entry.getValue())).append("\"");
		}

		/*-----------------------------------------------------------------*/

		argumentString.append(" -AMIUser=\"").append(Utility.escapeJavaString(m_AMIUser)).append("\"");
		argumentString.append(" -AMIPass=\"").append(Utility.escapeJavaString(m_AMIPass)).append("\"");

		/*-----------------------------------------------------------------*/

		input.append("Command=").append(command()).append(URLEncoder.encode(argumentString.toString(), "UTF-8"));

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		HttpURLConnection connection = HttpConnectionFactory.connection(ConfigSingleton.getProperty("proxy_command_endpoint"));

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/

			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF");
			connection.setRequestProperty("Content-Length", String.valueOf(input.length()));

			connection.setRequestProperty("Connection", "Close");

			connection.setRequestProperty("User-Agent", m_userAgent);

			connection.setConnectTimeout(1500);
			connection.setReadTimeout(1500);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			/*-------------------------------------------------------------*/

			try(OutputStream outputStream = connection.getOutputStream())
			{
				TextFile.write(outputStream, input);
			}

			/*-------------------------------------------------------------*/

			try(InputStream inputStream = connection.getInputStream())
			{
				TextFile.read(output, inputStream);
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			connection.disconnect();
		}

		/*-----------------------------------------------------------------*/

		int idx1 = output.  indexOf  ("<rowset>");
		int idx2 = output.lastIndexOf("</rowset>");

		if(idx1 > 0 && idx1 < idx2) {
			return new StringBuilder(output.substring(idx1 + 0, idx2 + 9));
		}
		else {
			return new StringBuilder(/*--------------------------------*/);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	abstract protected String command();

	/*---------------------------------------------------------------------*/
}
