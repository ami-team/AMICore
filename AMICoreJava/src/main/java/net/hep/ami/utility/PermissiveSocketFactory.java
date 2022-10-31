package net.hep.ami.utility;

import java.security.cert.*;

import javax.net.ssl.*;

import net.hep.ami.*;

import org.jetbrains.annotations.*;

public class PermissiveSocketFactory
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(PermissiveSocketFactory.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String NEW_TLS_PROTOCOL = "TLSv1.3";
	private static final String OLD_TLS_PROTOCOL = "TLSv1.2";

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

	private final SSLSocketFactory m_newTLSSocketFactory;
	private final SSLSocketFactory m_oldTLSSocketFactory;

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
		/* NEW TLS                                                                                                    */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE CONTEXT                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			SSLContext sslContext = SSLContext.getInstance(NEW_TLS_PROTOCOL);

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
			LOG.error("could not initialize {} context", NEW_TLS_PROTOCOL, e);

			tmp = null;
		}

		m_newTLSSocketFactory = tmp;

		/*------------------------------------------------------------------------------------------------------------*/
		/* OLD TLS                                                                                                    */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE CONTEXT                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			SSLContext tlsContext = SSLContext.getInstance(OLD_TLS_PROTOCOL);

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
			LOG.error("could not initialize {} context", OLD_TLS_PROTOCOL, e);

			tmp = null;
		}

		m_oldTLSSocketFactory = tmp;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public SSLSocketFactory getNewTLSSocketFactory()
	{
		return m_newTLSSocketFactory;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public SSLSocketFactory getOldTLSSocketFactory()
	{
		return m_oldTLSSocketFactory;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
