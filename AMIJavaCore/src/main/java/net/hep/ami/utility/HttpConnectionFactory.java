package net.hep.ami.utility;

import java.net.*;

import javax.net.ssl.*;

public class HttpConnectionFactory
{
	/*---------------------------------------------------------------------*/

	public static HttpURLConnection sslConnection(String spec) throws Exception
	{
		URL url = new URL(spec);

		HttpsURLConnection result = (HttpsURLConnection) url.openConnection();

		if(url.getProtocol().equals("https"))
		{
			result.setSSLSocketFactory(PermissiveSocketFactory.getSSLSocketFactory());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static HttpURLConnection tlsConnection(String spec) throws Exception
	{
		URL url = new URL(spec);

		HttpsURLConnection result = (HttpsURLConnection) url.openConnection();

		if(url.getProtocol().equals("https"))
		{
			result.setSSLSocketFactory(PermissiveSocketFactory.getTLSSocketFactory());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
}
