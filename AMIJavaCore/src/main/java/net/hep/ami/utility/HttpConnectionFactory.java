package net.hep.ami.utility;

import java.net.*;

import javax.net.ssl.*;

public class HttpConnectionFactory
{
	/*---------------------------------------------------------------------*/

	private static final PermissiveSocketFactory s_socketFactory = new PermissiveSocketFactory();

	/*---------------------------------------------------------------------*/

	private HttpConnectionFactory() {}

	/*---------------------------------------------------------------------*/

	public static HttpURLConnection connection(String spec) throws Exception
	{
		HttpURLConnection result = (HttpURLConnection) new URL(spec).openConnection();

		if(result instanceof HttpsURLConnection)
		{
			SSLSocketFactory socketFactory;

			/**/

			socketFactory = s_socketFactory.getTLSSocketFactory();

			if(socketFactory == null)
			{
				socketFactory = s_socketFactory.getSSLSocketFactory();

				if(socketFactory == null)
				{
					throw new Exception("Could not initialize connection");
				}
			}

			/**/

			((HttpsURLConnection) result).setSSLSocketFactory(socketFactory);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
}
