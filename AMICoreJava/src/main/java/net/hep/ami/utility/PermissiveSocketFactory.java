package net.hep.ami.utility;

import java.security.cert.*;

import javax.net.ssl.*;

import net.hep.ami.*;

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
		/* SSL                                                             */
		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* CREATE CONTEXT                                              */
			/*-------------------------------------------------------------*/

			SSLContext sslContext = SSLContext.getInstance("SSLv3");

			sslContext.init(keyManagers, new TrustManager[] {
				new PermissiveX509TrustManager()
			}, new java.security.SecureRandom());

			/*-------------------------------------------------------------*/
			/* CREATE FACTORY                                              */
			/*-------------------------------------------------------------*/

			tmp = sslContext.getSocketFactory();

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error("could not initialize SSL context", e);

			tmp = null;
		}

		m_sslSocketFactory = tmp;

		/*-----------------------------------------------------------------*/
		/* TLS                                                             */
		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* CREATE CONTEXT                                              */
			/*-------------------------------------------------------------*/

			SSLContext tlsContext = SSLContext.getInstance("TLSv1");

			tlsContext.init(keyManagers, new TrustManager[] {
				new PermissiveX509TrustManager()
			}, new java.security.SecureRandom());

			/*-------------------------------------------------------------*/
			/* CREATE FACTORY                                              */
			/*-------------------------------------------------------------*/

			tmp = tlsContext.getSocketFactory();

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error("could not initialize TLS context", e);

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
