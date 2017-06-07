package net.hep.ami;

import java.io.*;
import java.math.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import java.security.spec.*;

/* CERTIFICATES */

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.*;
import org.bouncycastle.cert.jcajce.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.*;

/* CRYPTOGRAPHY */

import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.paddings.*;

public class SecuritySingleton
{
	/*---------------------------------------------------------------------*/

	public static final class PEM
	{
		/*-----------------------------------------------------------------*/

		public static final int PRIVATE_KEY = 1;
		public static final int PUBLIC_KEY = 2;
		public static final int X509_CERTIFICATE = 4;

		/*-----------------------------------------------------------------*/

		public final PrivateKey[] privateKeys;
		public final PublicKey[] publicKeys;
		public final X509Certificate[] x509Certificates;

		/*-----------------------------------------------------------------*/

		public PEM(InputStream inputStream) throws Exception
		{
			this(inputStream, PRIVATE_KEY | PUBLIC_KEY | X509_CERTIFICATE);
		}

		/*-----------------------------------------------------------------*/

		public PEM(InputStream inputStream, int flag) throws Exception
		{
			/*-------------------------------------------------------------*/
			/* LOAD FILE                                                   */
			/*-------------------------------------------------------------*/

			String line;

			boolean append = false;

			StringBuilder stringBuilder = null;

			List<StringBuilder> list1 = new ArrayList<>();
			List<StringBuilder> list2 = new ArrayList<>();
			List<StringBuilder> list3 = new ArrayList<>();

			try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)))
			{
				while((line = bufferedReader.readLine()) != null)
				{
					/*-----------------------------------------------------*/

					/**/ if(line.matches("-----BEGIN( | [^ ]+ )PRIVATE KEY-----")
						    ||
						    line.matches("-----BEGIN( | [^ ]+ )PUBLIC KEY-----")
						    ||
						    line.matches("-----BEGIN CERTIFICATE-----")
					 ) {
						stringBuilder = new StringBuilder();

						append = true;
					}

					/*-----------------------------------------------------*/

					else if(line.matches("-----END( | [^ ]+ )PRIVATE KEY-----"))
					{
						if((flag & PRIVATE_KEY) != 0)
						{
							list1.add(stringBuilder);
						}

						append = false;
					}

					/*-----------------------------------------------------*/

					else if(line.matches("-----END( | [^ ]+ )PUBLIC KEY-----"))
					{
						if((flag & PUBLIC_KEY) != 0)
						{
							list2.add(stringBuilder);
						}

						append = false;
					}

					/*-----------------------------------------------------*/

					else if(line.matches("-----END CERTIFICATE-----"))
					{
						if((flag & X509_CERTIFICATE) != 0)
						{
							list3.add(stringBuilder);
						}

						append = false;
					}

					/*-----------------------------------------------------*/

					else if(append)
					{
						stringBuilder.append(line);
					}

					/*-----------------------------------------------------*/
				}
			}

			/*-------------------------------------------------------------*/
			/* GET NUMBER OF OBJECTS                                       */
			/*-------------------------------------------------------------*/

			final int numberOfPrivateKeys = list1.size();
			final int numberOfPublicKeys = list2.size();
			final int numberOfCertificates = list3.size();

			/*-------------------------------------------------------------*/
			/* BUILD OBJECTS                                               */
			/*-------------------------------------------------------------*/

			privateKeys = new PrivateKey[numberOfPrivateKeys];

			for(int i = 0; i < numberOfPrivateKeys; i++)
			{
				privateKeys[i] = KeyFactory.getInstance("RSA", BC).generatePrivate(

					new PKCS8EncodedKeySpec(org.bouncycastle.util.encoders.Base64.decode(
						list1.get(i).toString()
					))
				);
			}

			/*-------------------------------------------------------------*/

			publicKeys = new PublicKey[numberOfPublicKeys];

			for(int i = 0; i < numberOfPublicKeys; i++)
			{
				publicKeys[i] = KeyFactory.getInstance("RSA", BC).generatePublic(

					new X509EncodedKeySpec(org.bouncycastle.util.encoders.Base64.decode(
						list2.get(i).toString()
					))
				);
			}

			/*-------------------------------------------------------------*/

			x509Certificates = new X509Certificate[numberOfCertificates];

			for(int i = 0; i < numberOfCertificates; i++)
			{
				x509Certificates[i] = (X509Certificate) CertificateFactory.getInstance("X509", BC).generateCertificate(

					new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(
						list3.get(i).toString()
					))
				);
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			StringBuilder stringBuilder = new StringBuilder();

			/*-------------------------------------------------------------*/

			for(PrivateKey privateKey: privateKeys)
			{
				stringBuilder.append("-----BEGIN PRIVATE KEY-----\n")
				             .append(byteArrayToBase64String(privateKey.getEncoded()))
				             .append("-----END PRIVATE KEY-----\n")
				;
			}

			/*-------------------------------------------------------------*/

			for(PublicKey _publicKey: publicKeys)
			{
				stringBuilder.append("-----BEGIN PUBLIC KEY-----\n")
				             .append(byteArrayToBase64String(_publicKey.getEncoded()))
				             .append("-----END PUBLIC KEY-----\n")
				;
			}

			/*-------------------------------------------------------------*/

			for(X509Certificate x509Certificate: x509Certificates)
			{
				try
				{
					stringBuilder.append("-----BEGIN CERTIFICATE-----\n")
					             .append(byteArrayToBase64String(x509Certificate.getEncoded()))
					             .append("-----END CERTIFICATE-----\n")
					;
				}
				catch(CertificateEncodingException e)
				{
					stringBuilder.append(e.getMessage());
				}
			}

			/*-------------------------------------------------------------*/

			return stringBuilder.toString();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static final String BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

	/*---------------------------------------------------------------------*/

	private static final PaddedBufferedBlockCipher s_encryptCipher = new PaddedBufferedBlockCipher(new AESEngine());
	private static final PaddedBufferedBlockCipher s_decryptCipher = new PaddedBufferedBlockCipher(new AESEngine());

	/*---------------------------------------------------------------------*/

	private SecuritySingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/*---------------------------------------------------------------------*/

	public static void init(String password) throws Exception
	{
		byte[] key;

		final int length = password.length();

		/****/ if(length <= 16) {
			key = String.format("%1$-16s", password).getBytes();
		} else if(length <= 24) {
			key = String.format("%1$-24s", password).getBytes();
		} else if(length <= 32) {
			key = String.format("%1$-32s", password).getBytes();
		} else {
			throw new Exception("too long password (max 32)");
		}

		s_encryptCipher.init(true, new KeyParameter(key));

		s_decryptCipher.init(false, new KeyParameter(key));
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
	/* KEYES AND CERTIFICATES                                              */
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	public static KeyPair generateKeyPair(int keysize) throws Exception
	{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC);

		keyPairGenerator.initialize(keysize, new SecureRandom());

		return keyPairGenerator.generateKeyPair();
	}

	/*---------------------------------------------------------------------*/

	public static X509Certificate generateCA(PrivateKey privateKey, PublicKey publicKey, String subject, int validity) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE X509 BUILDER                                             */
		/*-----------------------------------------------------------------*/

		javax.security.auth.x500.X500Principal clientDN = new javax.security.auth.x500.X500Principal(subject);

		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

		Calendar calendar = Calendar.getInstance();

		Date date1 = calendar.getTime();
		calendar.add(Calendar.YEAR, validity);
		Date date2 = calendar.getTime();

		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
			clientDN,
			serial,
			date1,
			date2,
			clientDN,
			publicKey
		);

		/*-----------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                             */
		/*-----------------------------------------------------------------*/

		// Basic Constraints
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(true));

		// Subject Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

		/*-----------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE                                         */
		/*-----------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512WithRSA").setProvider(BC).build(privateKey);

		return new JcaX509CertificateConverter().setProvider(BC).getCertificate(
			builder.build(contentSigner)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static X509Certificate generateCertificate(PrivateKey caPrivateKey, X509Certificate caCertificate, PublicKey publicKey, String subject, int validity) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE X509 BUILDER                                             */
		/*-----------------------------------------------------------------*/

		javax.security.auth.x500.X500Principal clientDN = new javax.security.auth.x500.X500Principal(subject);

		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

		Calendar calendar = Calendar.getInstance();

		Date date1 = calendar.getTime();
		calendar.add(Calendar.YEAR, validity);
		Date date2 = calendar.getTime();

		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
			caCertificate,
			serial,
			date1,
			date2,
			clientDN,
			publicKey
		);

		/*-----------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                             */
		/*-----------------------------------------------------------------*/

		// Basic Constraints
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), false, new BasicConstraints(false));

		// Subject Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

		// Authority Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(caCertificate));

		/*-----------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE                                         */
		/*-----------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512WithRSA").setProvider(BC).build(caPrivateKey);

		return new JcaX509CertificateConverter().setProvider(BC).getCertificate(
			builder.build(contentSigner)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static KeyStore generateJKSKeyStore(PrivateKey privateKey, X509Certificate[] certificates, char[] password) throws Exception
	{
		KeyStore result = KeyStore.getInstance("JKS");

		result.load(null, null);

		result.setKeyEntry("AMI", privateKey, password, certificates);

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static KeyStore generatePKCS12KeyStore(PrivateKey privateKey, X509Certificate[] certificates, char[] password) throws Exception
	{
		KeyStore result = KeyStore.getInstance("PKCS12");

		result.load(null, null);

		result.setKeyEntry("AMI", privateKey, password, certificates);

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String byteArrayToBase64String(byte[] data)
	{
		/*-----------------------------------------------------------------*/
		/* ENCODE TO BASE64                                                */
		/*-----------------------------------------------------------------*/

		String string = org.bouncycastle.util.encoders.Base64.toBase64String(data);

		/*-----------------------------------------------------------------*/
		/* SPLIT BASE64 STRING                                             */
		/*-----------------------------------------------------------------*/

		int i = 0;
		int j = 0;

		final int l = string.length();

		StringBuilder result = new StringBuilder();

		for(;;)
		{
			j = i + 64;

			if(j > l)
			{
				result.append(string.substring(i, l));
				result.append("\n");

				break;
			}
			else
			{
				result.append(string.substring(i, j));
				result.append("\n");

				i = j;
			}
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static boolean isProxy(X509Certificate certificate)
	{
		byte[] data;

		/*-----------------------------------------------------------------*/
		/* CHECK RFC3820 PROXY                                             */
		/*-----------------------------------------------------------------*/

		data = certificate.getExtensionValue("1.3.6.1.5.5.7.1.14");

		if(data != null && data.length > 0)
		{
			return true;
		}

		/*-----------------------------------------------------------------*/
		/* CHECK RFC_DRAFT PROXY                                           */
		/*-----------------------------------------------------------------*/

		data = certificate.getExtensionValue("1.3.6.1.4.1.3536.1.222");

		if(data != null && data.length > 0)
		{
			return true;
		}

		/*-----------------------------------------------------------------*/
		/* CHECK VOMS PROXY                                                */
		/*-----------------------------------------------------------------*/

		String[] parts = certificate.getSubjectX500Principal().getName("RFC2253").split(",");

		for(String part: parts)
		{
			if("CN=limited proxy".equals(part)
			   ||
			   /**/"CN=proxy"/**/.equals(part)
			 ) {
				return true;
			}
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static String getDN(javax.security.auth.x500.X500Principal principal)
	{
		StringBuilder result = new StringBuilder();

		for(String part: principal.getName("RFC2253").split(","))
		{
			result.insert(0, "/" + part);
		}

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
	/* CRYPTO                                                              */
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	private static void encrypt(OutputStream outputStreamut, InputStream inputStream) throws Exception
	{
		int noBytesRead;
		int noBytesProcessed;

		byte[] ibuff = new byte[16];
		byte[] obuff = new byte[512];

		while((noBytesRead = inputStream.read(ibuff)) >= 0)
		{
			noBytesProcessed = s_encryptCipher.processBytes(ibuff, 0, noBytesRead, obuff, 0);
			outputStreamut.write(obuff, 0, noBytesProcessed);
		}

		noBytesProcessed = s_encryptCipher.doFinal(obuff, 0);
		outputStreamut.write(obuff, 0, noBytesProcessed);
	}

	/*---------------------------------------------------------------------*/

	private static void decrypt(OutputStream outputStream, InputStream inputStream) throws Exception
	{
		int noBytesRead;
		int noBytesProcessed;

		byte[] ibuff = new byte[16];
		byte[] obuff = new byte[512];

		while((noBytesRead = inputStream.read(ibuff)) >= 0)
		{
			noBytesProcessed = s_decryptCipher.processBytes(ibuff, 0, noBytesRead, obuff, 0);
			outputStream.write(obuff, 0, noBytesProcessed);
		}

		noBytesProcessed = s_decryptCipher.doFinal(obuff, 0);
		outputStream.write(obuff, 0, noBytesProcessed);
	}

	/*---------------------------------------------------------------------*/

	private static byte[] encrypt(byte[] data) throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
 		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		encrypt(outputStream, inputStream);

		return outputStream.toByteArray();
	}

	/*---------------------------------------------------------------------*/

	private static byte[] decrypt(byte[] data) throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		decrypt(outputStream, inputStream);

		return outputStream.toByteArray();
	}

	/*---------------------------------------------------------------------*/

	public static String encrypt(String s) throws Exception
	{
		return s.isEmpty() == false ? new String(
			org.bouncycastle.util.encoders.Base64.encode(encrypt(s.getBytes()))
		) : "";
	}

	/*---------------------------------------------------------------------*/

	public static String decrypt(String s) throws Exception
	{
		return s.isEmpty() == false ? new String(
			decrypt(org.bouncycastle.util.encoders.Base64.decode(s.toString()))
		) : "";
	}

	/*---------------------------------------------------------------------*/
}
