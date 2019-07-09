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

		arguments.put("AMIUser", m_AMIUser);
		arguments.put("AMIPass", m_AMIPass);
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

		int idx1 = output.indexOf("<Result>");
		int idx2 = output.indexOf("</Result>");

		if(idx1 > 0 && idx1 < idx2)
		{
			output = new StringBuilder(output.substring(idx1 + 8, idx2 + 0));
		}
		else
		{
			int idx3 = output.indexOf("<AMIMessage>");
			int idx4 = output.indexOf("</AMIMessage>");

			if(idx3 > 0 && idx3 < idx4)
			{
				output = new StringBuilder(output.substring(idx1 + 12, idx2 + 0));
			}
		}

		/*-----------------------------------------------------------------*/

		return output;
	}

	/*---------------------------------------------------------------------*/

	abstract protected String command();

	/*---------------------------------------------------------------------*/
}
