package net.hep.ami.utility;

import java.security.cert.*;

import javax.net.ssl.*;

import net.hep.ami.*;

public class PermissiveSocketFactory
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String SSL_PROTOCOL = "SSLv3";
	private static final String TLS_PROTOCOL = "TLSv1";

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final class PermissiveX509TrustManager implements X509TrustManager
	{
		@Override
		public void checkClientTrusted(@Nullable X509Certificate[] chain, @Nullable String authType)
		{
			/* DO NOTHING */
		}

		@Override
		public void checkServerTrusted(@Nullable X509Certificate[] chain, @Nullable String authType)
		{
			/* DO NOTHING */
		}

		@NotNull
		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[] {};
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private final SSLSocketFactory m_sslSocketFactory;
	private final SSLSocketFactory m_tlsSocketFactory;

	/*----------------------------------------------------------------------------------------------------------------*/

	public PermissiveSocketFactory()
	{
		this(null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public PermissiveSocketFactory(@Nullable KeyManager[] keyManagers)
	{
		SSLSocketFactory tmp;

		/*------------------------------------------------------------------------------------------------------------*/
		/* SSL                                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE CONTEXT                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);

			sslContext.init(keyManagers, new TrustManager[] {
					new PermissiveX509TrustManager()
				}, new java.security.SecureRandom()
			);

			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE FACTORY                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			tmp = sslContext.getSocketFactory();

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error("could not initialize SSL context", e);

			tmp = null;
		}

		m_sslSocketFactory = tmp;

		/*------------------------------------------------------------------------------------------------------------*/
		/* TLS                                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE CONTEXT                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			SSLContext tlsContext = SSLContext.getInstance(TLS_PROTOCOL);

			tlsContext.init(keyManagers, new TrustManager[] {
					new PermissiveX509TrustManager()
				}, new java.security.SecureRandom()
			);

			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE FACTORY                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			tmp = tlsContext.getSocketFactory();

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error("could not initialize TLS context", e);

			tmp = null;
		}

		m_tlsSocketFactory = tmp;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public SSLSocketFactory getSSLSocketFactory()
	{
		return m_sslSocketFactory;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public SSLSocketFactory getTLSSocketFactory()
	{
		return m_tlsSocketFactory;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
