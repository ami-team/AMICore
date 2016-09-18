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
		/* HTTP AUTHENTICATION                                             */
		/*-----------------------------------------------------------------*/

		String authorization = ConfigSingleton.getProperty("jenkins_login")
		                       + ":" +
		                       ConfigSingleton.getProperty("jenkins_password")
		;

		String encodedAuthorization = new String(org.bouncycastle.util.encoders.Base64.encode(authorization.getBytes()));

		/*-----------------------------------------------------------------*/
		/* HTTP CONNECTION                                                 */
		/*-----------------------------------------------------------------*/

		HttpURLConnection connection = HttpConnectionFactory.tlsConnection(ConfigSingleton.getProperty("jenkins_endpoint") + "/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)");

		connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();

		/*-----------------------------------------------------------------*/
		/* DATA                                                            */
		/*-----------------------------------------------------------------*/

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

	public static StringBuilder execute(String token, String command, String method, String data, boolean output, boolean input, int expectedResponseCode) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String parts[] = token.split(":");

		if(parts.length != 2)
		{
			throw new Exception("invalid token format");
		}

		/*-----------------------------------------------------------------*/
		/* HTTP AUTHENTICATION                                             */
		/*-----------------------------------------------------------------*/

		String authorization = ConfigSingleton.getProperty("jenkins_login")
		                       + ":" +
		                       ConfigSingleton.getProperty("jenkins_password")
		;

		String encodedAuthorization = new String(org.bouncycastle.util.encoders.Base64.encode(authorization.getBytes()));

		/*-----------------------------------------------------------------*/
		/* HTTP CONNECTION                                                 */
		/*-----------------------------------------------------------------*/

		HttpURLConnection connection = HttpConnectionFactory.tlsConnection(ConfigSingleton.getProperty("jenkins_endpoint") + command);

		connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
		connection.setRequestProperty(parts[0], parts[1]);
		connection.setRequestMethod(method);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.connect();

		/*-----------------------------------------------------------------*/
		/* DATA                                                            */
		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* OUTPUT STREAM                                               */
			/*-------------------------------------------------------------*/

			if(output)
			{
				connection.getOutputStream().write(data.getBytes());
			}

			/*-------------------------------------------------------------*/
			/* INPUT STREAM                                                */
			/*-------------------------------------------------------------*/

			if(input)
			{
				TextFile.read(result, connection.getInputStream());
			}

			/*-------------------------------------------------------------*/
			/* SUCCESS                                                     */
			/*-------------------------------------------------------------*/

			int responseCode = connection.getResponseCode();

			if(responseCode != expectedResponseCode)
			{
				throw new Exception(
					"error " + responseCode
				);
			}

			/*-------------------------------------------------------------*/
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
