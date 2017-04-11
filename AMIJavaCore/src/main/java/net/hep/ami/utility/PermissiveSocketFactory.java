package net.hep.ami.utility;

import java.security.cert.*;

import javax.net.ssl.*;

public class PermissiveSocketFactory
{
	/*---------------------------------------------------------------------*/

	private static final class PermissiveX509TrustManager implements X509TrustManager
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

	private final SSLSocketFactory m_sslSocketFactory;
	private final SSLSocketFactory m_tlsSocketFactory;

	/*---------------------------------------------------------------------*/

	public PermissiveSocketFactory()
	{
		this(null);
	}

	/*---------------------------------------------------------------------*/

	public PermissiveSocketFactory(@Nullable KeyManager[] keyManagers)
	{
		SSLSocketFactory tmp;

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* CREATE SSL CONTEXT                                          */
			/*-------------------------------------------------------------*/

			SSLContext sslContext = SSLContext.getInstance("SSLv3");

			sslContext.init(keyManagers, new TrustManager[] {
				new PermissiveX509TrustManager()
			}, new java.security.SecureRandom());

			/*-------------------------------------------------------------*/
			/* CREATE SSL FACTORY                                          */
			/*-------------------------------------------------------------*/

			tmp = sslContext.getSocketFactory();

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			tmp = null;
		}

		m_sslSocketFactory = tmp;

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* CREATE TLS CONTEXT                                          */
			/*-------------------------------------------------------------*/

			SSLContext tlsContext = SSLContext.getInstance("TLSv1");

			tlsContext.init(keyManagers, new TrustManager[] {
				new PermissiveX509TrustManager()
			}, new java.security.SecureRandom());

			/*-------------------------------------------------------------*/
			/* CREATE TSL FACTORY                                          */
			/*-------------------------------------------------------------*/

			tmp = tlsContext.getSocketFactory();

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			tmp = null;
		}

		m_tlsSocketFactory = tmp;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public SSLSocketFactory getSSLSocketFactory() throws Exception
	{
		return m_sslSocketFactory;
	}

	/*---------------------------------------------------------------------*/

	public SSLSocketFactory getTLSSocketFactory() throws Exception
	{
		return m_tlsSocketFactory;
	}

	/*---------------------------------------------------------------------*/
}
