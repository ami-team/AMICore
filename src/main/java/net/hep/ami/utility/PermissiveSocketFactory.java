package net.hep.ami.utility;

import java.security.cert.*;

import javax.net.ssl.*;

public class PermissiveSocketFactory {
	/*---------------------------------------------------------------------*/

	private static class PermissiveX509TrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			/* IGNORE */
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			/* IGNORE */
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	/*---------------------------------------------------------------------*/

	private static SSLSocketFactory m_sslSocketFactory;
	private static SSLSocketFactory m_tlsSocketFactory;

	/*---------------------------------------------------------------------*/

	static {

		try {
			/*-------------------------------------------------------------*/
			/* CREATE SSL CONTEXT                                          */
			/*-------------------------------------------------------------*/

			SSLContext sslContext = SSLContext.getInstance("SSL");

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

			SSLContext tlsContext = SSLContext.getInstance("TLS");

			tlsContext.init(null, new TrustManager[] {
				new PermissiveX509TrustManager()
			}, null);

			/*-------------------------------------------------------------*/
			/* CREATE TSL FACTORY                                          */
			/*-------------------------------------------------------------*/

			m_tlsSocketFactory = tlsContext.getSocketFactory();

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			/* IGNORE */
		}
	}

	/*---------------------------------------------------------------------*/

	public static SSLSocketFactory getSSLSocketFactory() throws Exception {

		return m_sslSocketFactory;
	}

	/*---------------------------------------------------------------------*/

	public static SSLSocketFactory getTLSSocketFactory() throws Exception {

		return m_tlsSocketFactory;
	}

	/*---------------------------------------------------------------------*/
}
