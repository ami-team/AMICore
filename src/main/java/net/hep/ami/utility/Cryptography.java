package net.hep.ami.utility;

import java.io.*;
import java.math.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import java.security.spec.*;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.*;
import org.bouncycastle.cert.jcajce.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.*;

import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.paddings.*;

public class Cryptography {
	/*---------------------------------------------------------------------*/

	private static final String BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

	/*---------------------------------------------------------------------*/

	private static final PaddedBufferedBlockCipher m_encryptCipher = new PaddedBufferedBlockCipher(new AESEngine());
	private static final PaddedBufferedBlockCipher m_decryptCipher = new PaddedBufferedBlockCipher(new AESEngine());

	/*---------------------------------------------------------------------*/

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/*---------------------------------------------------------------------*/

	public static void init(String password) throws Exception {

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

		m_encryptCipher.init(true, new KeyParameter(key));

		m_decryptCipher.init(false, new KeyParameter(key));
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
	/* KEYES AND CERTIFICATES                                              */
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	public static boolean isProxy(X509Certificate certificate) {
		/*-----------------------------------------------------------------*/
		/* CHECK RFC3820 PROXY                                             */
		/*-----------------------------------------------------------------*/

		if(certificate.getExtensionValue("1.3.6.1.5.5.7.1.14") != null && certificate.getExtensionValue("1.3.6.1.5.5.7.1.14").length > 0) {
			return true;
		}

		/*-----------------------------------------------------------------*/
		/* CHECK DRAFT_RFC PROXY                                           */
		/*-----------------------------------------------------------------*/

		if(certificate.getExtensionValue("1.3.6.1.4.1.3536.1.222") != null && certificate.getExtensionValue( "1.3.6.1.4.1.3536.1.222").length > 0) {
			return true;
		}

		/*-----------------------------------------------------------------*/
		/* CHECK VOMS PROXY                                                */
		/*-----------------------------------------------------------------*/

		String[] parts = certificate.getSubjectX500Principal().getName().split(",");

		for(String part: parts) {

			if(part.equals("CN=limited proxy") == false
			   &&
			   part.equals(/**/"CN=proxy"/**/) == false
			 ) {
				return true;
			}
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static X509Certificate loadCertificate(InputStream inputStream) throws Exception {

		try {
			return (X509Certificate) CertificateFactory.getInstance("X509", BC).generateCertificate(inputStream);

		} finally {
			inputStream.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public static PrivateKey loadPrivateKey(InputStream inputStream) throws Exception {
		/*-----------------------------------------------------------------*/
		/* READ PRIVATE KEY                                                */
		/*-----------------------------------------------------------------*/

		String line;
		Boolean append = false;
		StringBuilder result = new StringBuilder();

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		try {

			while((line = bufferedReader.readLine()) != null) {

				if(line.equals("-----END PRIVATE KEY-----")) append = false;
				if(append) result.append(line);
				if(line.equals("-----BEGIN PRIVATE KEY-----")) append = true;
			}

		} finally {
			bufferedReader.close();
		}

		/*-----------------------------------------------------------------*/
		/* BUILD PKCS8 PRIVATE KEY                                         */
		/*-----------------------------------------------------------------*/

		byte[] encoded = org.bouncycastle.util.encoders.Base64.decode(result.toString());

		return KeyFactory.getInstance("RSA", BC).generatePrivate(
			new PKCS8EncodedKeySpec(encoded)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static PublicKey loadPublicKey(InputStream inputStream) throws Exception {
		/*-----------------------------------------------------------------*/
		/* READ PRIVATE KEY                                                */
		/*-----------------------------------------------------------------*/

		String line;
		Boolean append = false;
		StringBuilder result = new StringBuilder();

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		try {

			while((line = bufferedReader.readLine()) != null) {

				if(line.equals("-----END PUBLIC KEY-----")) append = false;
				if(append) result.append(line);
				if(line.equals("-----BEGIN PUBLIC KEY-----")) append = true;
			}

		} finally {
			bufferedReader.close();
		}

		/*-----------------------------------------------------------------*/
		/* BUILD PKCS8 PRIVATE KEY                                         */
		/*-----------------------------------------------------------------*/

		byte[] encoded = org.bouncycastle.util.encoders.Base64.decode(result.toString());

		return KeyFactory.getInstance("RSA", BC).generatePublic(
			new PKCS8EncodedKeySpec(encoded)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static KeyPair generateKeyPair(int keysize) throws Exception {

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC);

		keyPairGenerator.initialize(keysize, new SecureRandom());

		return keyPairGenerator.generateKeyPair();
	}

	/*---------------------------------------------------------------------*/

	public static X509Certificate generateCA(PrivateKey privateKey, PublicKey publicKey, String subject, int validity) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CREATE X509 BUILDER                                             */
		/*-----------------------------------------------------------------*/

		javax.security.auth.x500.X500Principal x500clientDN = new javax.security.auth.x500.X500Principal(subject);

		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

		Calendar calendar = Calendar.getInstance();

		Date date1 = calendar.getTime();
		calendar.add(Calendar.YEAR, validity);
		Date date2 = calendar.getTime();

		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
			x500clientDN,
			serial,
			date1,
			date2,
			x500clientDN,
			publicKey
		);

		/*-----------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                             */
		/*-----------------------------------------------------------------*/

		// Basic Constraints
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), false, new BasicConstraints(true));

		// Subject Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

		/*-----------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE                                         */
		/*-----------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BC).build(privateKey);

		return new JcaX509CertificateConverter().setProvider(BC).getCertificate(
			builder.build(contentSigner)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static X509Certificate generateCertificate(PrivateKey CAKey, X509Certificate CACrt, PublicKey publicKey, String subject, int validity) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CREATE X509 BUILDER                                             */
		/*-----------------------------------------------------------------*/

		javax.security.auth.x500.X500Principal x500clientDN = new javax.security.auth.x500.X500Principal(subject);

		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

		Calendar calendar = Calendar.getInstance();

		Date date1 = calendar.getTime();
		calendar.add(Calendar.YEAR, validity);
		Date date2 = calendar.getTime();

		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
			CACrt,
			serial,
			date1,
			date2,
			x500clientDN,
			publicKey
		);

		/*-----------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                             */
		/*-----------------------------------------------------------------*/

		// Basic Constraints
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), false, new BasicConstraints(false));

		// Authority Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(CACrt));

		// Subject Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

		/*-----------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE                                         */
		/*-----------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BC).build(CAKey);

		return new JcaX509CertificateConverter().setProvider(BC).getCertificate(
			builder.build(contentSigner)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static KeyStore generateKeyStore_JKS(PrivateKey privateKey, X509Certificate certificate, char[] password) throws Exception {

		KeyStore result = KeyStore.getInstance("JKS");
		result.load(null, null);

		result.setKeyEntry("AMI", privateKey, password, new X509Certificate[] {
			certificate
		});

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static KeyStore generateKeyStore_PKCS12(PrivateKey privateKey, X509Certificate certificate, char[] password) throws Exception {

		KeyStore result = KeyStore.getInstance("PKCS12");
		result.load(null, null);

		result.setKeyEntry("AMI", privateKey, password, new X509Certificate[] {
			certificate
		});

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String byteArrayToBase64String(byte[] data) {
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

		for(;;) {
			j = i + 64;

			if(j > length) {
				j = length;

				result.append(string.substring(i, j));
				result.append("\n");

				break;
			}

			result.append(string.substring(i, j));
			result.append("\n");

			i = j;
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String getAMIShortDN(javax.security.auth.x500.X500Principal principal) {

		StringBuilder result = new StringBuilder();

		for(String part: principal.getName("RFC2253").split(",")) {

			if(part.equals("CN=limited proxy") == false
			   &&
			   part.equals(/**/"CN=proxy"/**/) == false
			 ) {
				result.insert(0, "/" + part);
			}
		}

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
	/* CRYPTO                                                              */
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	public static void encrypt(OutputStream outputStreamut, InputStream inputStream) throws Exception {

		int noBytesRead = 0;
		int noBytesProcessed = 0;

		byte[] ibuff = new byte[16];
		byte[] obuff = new byte[512];

		while((noBytesRead = inputStream.read(ibuff)) >= 0) {

			noBytesProcessed = m_encryptCipher.processBytes(ibuff, 0, noBytesRead, obuff, 0);
			outputStreamut.write(obuff, 0, noBytesProcessed);
		}

		noBytesProcessed = m_encryptCipher.doFinal(obuff, 0);
		outputStreamut.write(obuff, 0, noBytesProcessed);
	}

	/*---------------------------------------------------------------------*/

	public static void decrypt(OutputStream outputStream, InputStream inputStream) throws Exception {

		int noBytesRead = 0;
		int noBytesProcessed = 0;

		byte[] ibuff = new byte[16];
		byte[] obuff = new byte[512];

		while((noBytesRead = inputStream.read(ibuff)) >= 0) {

			noBytesProcessed = m_decryptCipher.processBytes(ibuff, 0, noBytesRead, obuff, 0);
			outputStream.write(obuff, 0, noBytesProcessed);
		}

		noBytesProcessed = m_decryptCipher.doFinal(obuff, 0);
		outputStream.write(obuff, 0, noBytesProcessed);
	}

	/*---------------------------------------------------------------------*/

	public static byte[] encrypt(byte[] data) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		try {
			encrypt(outputStream, inputStream);

		} catch(Exception e) {
			/* IGNORE */
		}

		return outputStream.toByteArray();
	}

	/*---------------------------------------------------------------------*/

	public static byte[] decrypt(byte[] data) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		try {
			decrypt(outputStream, inputStream);

		} catch(Exception e) {
			/* IGNORE */
		}

		return outputStream.toByteArray();
	}

	/*---------------------------------------------------------------------*/

	public static String encrypt(String s) {

		return new String(
			org.bouncycastle.util.encoders.Base64.encode(encrypt(s.getBytes()))
		);
	}

	/*---------------------------------------------------------------------*/

	public static String decrypt(String s) {

		return new String(
			decrypt(org.bouncycastle.util.encoders.Base64.decode(s.toString()))
		);
	}

	/*---------------------------------------------------------------------*/
}
