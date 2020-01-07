package net.hep.ami.utility;

import java.net.*;

import javax.net.ssl.*;

import org.jetbrains.annotations.*;

public class HttpConnectionFactory
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final PermissiveSocketFactory s_socketFactory = new PermissiveSocketFactory();

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private HttpConnectionFactory() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static HttpURLConnection openConnection(@NotNull String url) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		URLConnection result = new URL(url).openConnection();

		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(result instanceof HttpsURLConnection)
		{
			SSLSocketFactory socketFactory;

			/*--------------------------------------------------------------------------------------------------------*/

			socketFactory = s_socketFactory.getTLSSocketFactory();

			if(socketFactory == null)
			{
				socketFactory = s_socketFactory.getSSLSocketFactory();

				if(socketFactory == null)
				{
					throw new Exception("could not initialize connection");
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			((HttpsURLConnection) result).setSSLSocketFactory(socketFactory);

			/*--------------------------------------------------------------------------------------------------------*/

			return (HttpURLConnection) result;
		}
		else if(result instanceof HttpURLConnection)
		{
			return (HttpURLConnection) result;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		throw new Exception("not and HTTP(S) connection");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
