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

	private static class PEMTupleXZY
	{
		public final List<StringBuilder> x;
		public final List<StringBuilder> y;
		public final List<StringBuilder> z;

		PEMTupleXZY(List<StringBuilder> _x, List<StringBuilder> _y, List<StringBuilder> _z)
		{
			x = _x;
			y = _y;
			z = _z;
		}
	}

	/*---------------------------------------------------------------------*/

	public static class PEMTuple
	{
		public final PrivateKey[] privateKeys;
		public final PublicKey[] publicKeys;
		public final X509Certificate[] x509Certificates;

		public PEMTuple(PrivateKey[] _privateKeys, PublicKey[] _publicKeys, X509Certificate[] _x509Certificates)
		{
			privateKeys = _privateKeys;
			publicKeys = _publicKeys;
			x509Certificates = _x509Certificates;
		}
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

	private static PEMTupleXZY parsePEM(InputStream inputStream) throws Exception
	{
		String line;

		StringBuilder stringBuilder = null;

		boolean appendPrivateKey = false;
		boolean appendPublicKey = false;
		boolean appendCertificate = false;

		List<StringBuilder> privateKey = new ArrayList<>();
		List<StringBuilder> publicKey = new ArrayList<>();
		List<StringBuilder> certificates = new ArrayList<>();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try
		{
			while((line = bufferedReader.readLine()) != null)
			{
				/*---------------------------------------------------------*/
				/* PRIVATE KEY                                             */
				/*---------------------------------------------------------*/

				/**/ if(line.matches("-----BEGIN( | [^ ]+ )PRIVATE KEY-----"))
				{
					stringBuilder = new StringBuilder();
					appendPrivateKey = true;
				}
				else if(line.matches("-----END( | [^ ]+ )PRIVATE KEY-----"))
				{
					privateKey.add(stringBuilder);
					appendPrivateKey = false;
				}
				else if(appendPrivateKey)
				{
					stringBuilder.append(line);
				}

				/*---------------------------------------------------------*/
				/* PUBLIC KEY                                              */
				/*---------------------------------------------------------*/

				else if(line.matches("-----BEGIN( | [^ ]+ )PUBLIC KEY-----"))
				{
					stringBuilder = new StringBuilder();
					appendPublicKey = true;
				}
				else if(line.matches("-----END( | [^ ]+ )PUBLIC KEY-----"))
				{
					publicKey.add(stringBuilder);
					appendPublicKey = false;
				}
				else if(appendPublicKey)
				{
					stringBuilder.append(line);
				}

				/*---------------------------------------------------------*/
				/* CERTIFICATE                                             */
				/*---------------------------------------------------------*/

				else if(line.equals("-----BEGIN CERTIFICATE-----"))
				{
					stringBuilder = new StringBuilder();
					appendCertificate = true;
				}
				else if(line.equals("-----END CERTIFICATE-----"))
				{
					certificates.add(stringBuilder);
					appendCertificate = false;
				}
				else if(appendCertificate)
				{
					stringBuilder.append(line);
				}

				/*---------------------------------------------------------*/
			}

		}
		finally
		{
			bufferedReader.close();
		}

		return new PEMTupleXZY(
			privateKey,
			publicKey,
			certificates
		);
	}

	/*---------------------------------------------------------------------*/

	private static PrivateKey buildPrivateKey(byte[] encoded) throws Exception
	{
		return KeyFactory.getInstance("RSA", BC).generatePrivate(

			new PKCS8EncodedKeySpec(
				encoded
			)
		);
	}

	/*---------------------------------------------------------------------*/

	private static PublicKey buildPublicKey(byte[] encoded) throws Exception
	{
		return KeyFactory.getInstance("RSA", BC).generatePublic(

			new X509EncodedKeySpec(
				encoded
			)
		);
	}

	/*---------------------------------------------------------------------*/

	private static X509Certificate buildCertificate(byte[] encoded) throws Exception
	{
		return (X509Certificate) CertificateFactory.getInstance("X509", BC).generateCertificate(

			new ByteArrayInputStream(
				encoded
			)
		);
	}

	/*---------------------------------------------------------------------*/

	public static PrivateKey[] loadPrivateKeys(InputStream inputStream) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* LOAD FILE                                                       */
		/*-----------------------------------------------------------------*/

		PEMTupleXZY tuple = parsePEM(inputStream);

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF PRIVATE KEYS                                      */
		/*-----------------------------------------------------------------*/

		final int numberOfPrivateKeys = tuple.x.size();

		/*-----------------------------------------------------------------*/
		/* BUILD PRIVATE KEYS                                              */
		/*-----------------------------------------------------------------*/

		PrivateKey[] result = new PrivateKey[numberOfPrivateKeys];

		for(int i = 0; i < numberOfPrivateKeys; i++)
		{
			result[i] = buildPrivateKey(org.bouncycastle.util.encoders.Base64.decode(
				tuple.x.get(i).toString()
			));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static PublicKey[] loadPublicKeys(InputStream inputStream) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* LOAD FILE                                                       */
		/*-----------------------------------------------------------------*/

		PEMTupleXZY tuple = parsePEM(inputStream);

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF PUBLIC KEYS                                       */
		/*-----------------------------------------------------------------*/

		final int numberOfPublicKeys = tuple.y.size();

		/*-----------------------------------------------------------------*/
		/* BUILD PUBLIC KEYS                                               */
		/*-----------------------------------------------------------------*/

		PublicKey[] result = new PublicKey[numberOfPublicKeys];

		for(int i = 0; i < numberOfPublicKeys; i++)
		{
			result[i] = buildPublicKey(org.bouncycastle.util.encoders.Base64.decode(
				tuple.y.get(i).toString()
			));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static X509Certificate[] loadCertificates(InputStream inputStream) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* LOAD FILE                                                       */
		/*-----------------------------------------------------------------*/

		PEMTupleXZY tuple = parsePEM(inputStream);

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF CERTIFICATES                                      */
		/*-----------------------------------------------------------------*/

		final int numberOfCertificates = tuple.z.size();

		/*-----------------------------------------------------------------*/
		/* BUILD X509 CERTIFICATES                                         */
		/*-----------------------------------------------------------------*/

		X509Certificate[] result = new X509Certificate[numberOfCertificates];

		for(int i = 0; i < numberOfCertificates; i++)
		{
			result[i] = buildCertificate(org.bouncycastle.util.encoders.Base64.decode(
				tuple.z.get(i).toString()
			));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static PEMTuple loadPEM(InputStream inputStream) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* LOAD FILE                                                       */
		/*-----------------------------------------------------------------*/

		PEMTupleXZY tuple = parsePEM(inputStream);

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF OBJECTS                                           */
		/*-----------------------------------------------------------------*/

		final int numberOfPrivateKeys = tuple.x.size();
		final int numberOfPublicKeys = tuple.y.size();
		final int numberOfCertificates = tuple.z.size();

		/*-----------------------------------------------------------------*/
		/* BUILD OBJECTS                                                   */
		/*-----------------------------------------------------------------*/

		PrivateKey[] privateKeys = new PrivateKey[numberOfPrivateKeys];

		for(int i = 0; i < numberOfPrivateKeys; i++)
		{
			privateKeys[i] = buildPrivateKey(org.bouncycastle.util.encoders.Base64.decode(
				tuple.x.get(i).toString()
			));
		}

		/*-----------------------------------------------------------------*/

		PublicKey[] publicKeys = new PublicKey[numberOfPublicKeys];

		for(int i = 0; i < numberOfPublicKeys; i++)
		{
			publicKeys[i] = buildPublicKey(org.bouncycastle.util.encoders.Base64.decode(
				tuple.y.get(i).toString()
			));
		}

		/*-----------------------------------------------------------------*/

		X509Certificate[] certificates = new X509Certificate[numberOfCertificates];

		for(int i = 0; i < numberOfCertificates; i++)
		{
			certificates[i] = buildCertificate(org.bouncycastle.util.encoders.Base64.decode(
				tuple.z.get(i).toString()
			));
		}

		/*-----------------------------------------------------------------*/

		return new PEMTuple(
			privateKeys,
			publicKeys,
			certificates
		);
	}

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

	public static X509Certificate generateCertificate(PrivateKey CAPrivateKey, X509Certificate CACertificate, PublicKey publicKey, String subject, int validity) throws Exception
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
			CACertificate,
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
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(CACertificate));

		/*-----------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE                                         */
		/*-----------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512WithRSA").setProvider(BC).build(CAPrivateKey);

		return new JcaX509CertificateConverter().setProvider(BC).getCertificate(
			builder.build(contentSigner)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static KeyStore generateKeyStore_JKS(PrivateKey privateKey, X509Certificate[] certificates, char[] password) throws Exception
	{
		KeyStore result = KeyStore.getInstance("JKS");

		result.load(null, null);

		result.setKeyEntry("AMI", privateKey, password, certificates);

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static KeyStore generateKeyStore_PKCS12(PrivateKey privateKey, X509Certificate[] certificates, char[] password) throws Exception
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

		final int length = string.length();

		StringBuilder result = new StringBuilder();

		for(;;)
		{
			j = i + 64;

			if(j > length)
			{
				j = length;

				result.append(string.substring(i, j));
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
			if(part.equals("CN=limited proxy")
			   ||
			   part.equals(/**/"CN=proxy"/**/)
			 ) {
				return true;
			}
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static String getAMIName(javax.security.auth.x500.X500Principal principal)
	{
		StringBuilder result = new StringBuilder();

		String[] parts = principal.getName("RFC2253").split(",");

		for(String part: parts)
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

	public static void encrypt(OutputStream outputStreamut, InputStream inputStream) throws Exception
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

	public static void decrypt(OutputStream outputStream, InputStream inputStream) throws Exception
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

	public static byte[] encrypt(byte[] data) throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		encrypt(outputStream, inputStream);

		return outputStream.toByteArray();
	}

	/*---------------------------------------------------------------------*/

	public static byte[] decrypt(byte[] data) throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		decrypt(outputStream, inputStream);

		return outputStream.toByteArray();
	}

	/*---------------------------------------------------------------------*/

	public static String encrypt(String s) throws Exception
	{
		return new String(
			org.bouncycastle.util.encoders.Base64.encode(encrypt(s.getBytes()))
		);
	}

	/*---------------------------------------------------------------------*/

	public static String decrypt(String s) throws Exception
	{
		return new String(
			decrypt(org.bouncycastle.util.encoders.Base64.decode(s.toString()))
		);
	}

	/*---------------------------------------------------------------------*/
}
