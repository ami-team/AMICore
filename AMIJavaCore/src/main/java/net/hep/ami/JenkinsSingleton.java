package net.hep.ami;

import java.net.*;

import net.hep.ami.utility.*;

public class JenkinsSingleton
{
	/*-----------------------------------------------------------------*/

	public static String getToken() throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String authorization = ConfigSingleton.getProperty("jenkins_login")
		                       + ":" +
		                       ConfigSingleton.getProperty("jenkins_password")
		;

		String encodedAuthorization = new String(org.bouncycastle.util.encoders.Base64.encode(authorization.getBytes()));

		/*-----------------------------------------------------------------*/

		HttpURLConnection connection = HttpConnectionFactory.tlsConnection(ConfigSingleton.getProperty("jenkins_endpoint") + "/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)");

		connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();

		try
		{
			TextFile.readLine(result, connection.getInputStream());
		}
		finally
		{
			connection.disconnect();
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder execute(String token, String command) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String parts[] = token.split(":");

		if(parts.length != 2)
		{
			throw new Exception("invalid token format");
		}

		/*-----------------------------------------------------------------*/

		String authorization = ConfigSingleton.getProperty("jenkins_login")
		                       + ":" +
		                       ConfigSingleton.getProperty("jenkins_password")
		;

		String encodedAuthorization = new String(org.bouncycastle.util.encoders.Base64.encode(authorization.getBytes()));

		/*-----------------------------------------------------------------*/

		HttpURLConnection connection = HttpConnectionFactory.tlsConnection(ConfigSingleton.getProperty("jenkins_endpoint") + command);

		connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
		connection.setRequestProperty(parts[0], parts[1]);
		connection.setRequestMethod("POST");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();

		try
		{
			TextFile.read(result, connection.getInputStream());
		}
		finally
		{
			connection.disconnect();
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
