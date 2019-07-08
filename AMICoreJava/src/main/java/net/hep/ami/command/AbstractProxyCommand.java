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
		StringBuilder output = new StringBuilder();
		StringBuilder input = new StringBuilder();

		/*-----------------------------------------------------------------*/

		List<String> list = new ArrayList<>();

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			list.add(
				new StringBuilder().append("\"")
				                   .append(Utility.escapeJSONString(entry.getKey()))
				                   .append("\":\"")
				                   .append(Utility.escapeJSONString(entry.getValue()))
				                   .append("\"")
				                   .toString()
			);
		}

		/*-------------------------------------------------------------*/

		output.append("{").append(String.join(",", list)).append("}");

		/*-----------------------------------------------------------------*/

		HttpURLConnection connection = HttpConnectionFactory.connection(ConfigSingleton.getProperty("proxy_command_api_url") + "/command/" + command() + "/xml");

		/*-----------------------------------------------------------------*/

		try
		{
			connection.setRequestMethod("POST");

			connection.setRequestProperty(
				ConfigSingleton.getProperty("proxy_command_token_name", "AMI-Token"),
				ConfigSingleton.getProperty("proxy_command_token_value", m_userSession)
			);

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/xml");

			connection.setDoOutput(true);

			/*-------------------------------------------------------------*/

			try(OutputStream outputStream = connection.getOutputStream())
			{
				TextFile.write(outputStream, output);
			}

			/*-------------------------------------------------------------*/

			try(InputStream inputStream = connection.getInputStream())
			{
				TextFile.read(input, inputStream);
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			connection.disconnect();
		}

		/*-----------------------------------------------------------------*/

		return input;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	abstract String command();

	/*---------------------------------------------------------------------*/
}
