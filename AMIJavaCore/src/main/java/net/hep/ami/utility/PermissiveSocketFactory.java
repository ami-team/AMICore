package net.hep.ami.utility;

import java.security.cert.*;

import javax.net.ssl.*;

public class PermissiveSocketFactory
{
	/*---------------------------------------------------------------------*/

	private static class PermissiveX509TrustManager implements X509TrustManager
	{
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
		{
			/* IGNORE */
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
		{
			/* IGNORE */
		}

		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[] {};
		}
	}

	/*---------------------------------------------------------------------*/

	private static SSLSocketFactory m_sslSocketFactory;
	private static SSLSocketFactory m_tlsSocketFactory;

	/*---------------------------------------------------------------------*/

	private PermissiveSocketFactory() {}

	/*---------------------------------------------------------------------*/

	static
	{
		try
		{
			/*-------------------------------------------------------------*/
			/* CREATE SSL CONTEXT                                          */
			/*-------------------------------------------------------------*/

			SSLContext sslContext = SSLContext.getInstance("SSLv3");

			sslContext.init(null, new TrustManager[] {
				new PermissiveX509TrustManager()
			}, null);

			/*-------------------------------------------------------------*/
			/* CREATE SSL FACTORY                                          */
			/*-------------------------------------------------------------*/

			m_sslSocketFactory = sslContext.getSocketFactory();

			/*-------------------------------------------------------------*/
			/* CREATE TLS CONTEXT                                          */
			/*-------------------------------------------------------------*/

			SSLContext tlsContext = SSLContext.getInstance("TLSv1.2");

			tlsContext.init(null, new TrustManager[] {
				new PermissiveX509TrustManager()
			}, null);

			/*-------------------------------------------------------------*/
			/* CREATE TSL FACTORY                                          */
			/*-------------------------------------------------------------*/

			m_tlsSocketFactory = tlsContext.getSocketFactory();

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			/* IGNORE */
		}
	}

	/*---------------------------------------------------------------------*/

	public static SSLSocketFactory getSSLSocketFactory() throws Exception
	{
		return m_sslSocketFactory;
	}

	/*---------------------------------------------------------------------*/

	public static SSLSocketFactory getTLSSocketFactory() throws Exception
	{
		return m_tlsSocketFactory;
	}

	/*---------------------------------------------------------------------*/
}
