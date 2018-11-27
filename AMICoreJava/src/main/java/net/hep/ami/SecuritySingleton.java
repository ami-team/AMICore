package net.hep.ami;

import java.io.*;
import java.math.*;
import java.util.*;
import java.nio.charset.*;

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

import net.hep.ami.utility.*;

public class SecuritySingleton
{
	/*---------------------------------------------------------------------*/

	public static class Revocation
	{
		public static final int SUPERSEDED = 0;
		public static final int COMPROMISED = 1;
		public static final int AFFILIATION_CHANGED = 3;
		public static final int PRIVILEGE_WITHDRAWN = 4;
		public static final int CESSATION_OF_OPERATION = 5;

		public final BigInteger serial;
		public final Integer reason;
		public final Date date;

		public Revocation(BigInteger _serial, Integer _reason, Date _date)
		{
			serial = _serial;
			reason = _reason;
			date = _date;
		}
	}

	/*---------------------------------------------------------------------*/

	public static final class PEM
	{
		/*-----------------------------------------------------------------*/

		public static final int PRIVATE_KEY = 1;
		public static final int PUBLIC_KEY = 2;
		public static final int X509_CERTIFICATE = 4;
		public static final int X509_CRL = 8;

		/*-----------------------------------------------------------------*/

		public final PrivateKey[] privateKeys;
		public final PublicKey[] publicKeys;
		public final X509Certificate[] x509Certificates;
		public final X509CRL[] x509CRLs;

		/*-----------------------------------------------------------------*/

		public PEM(InputStream inputStream) throws Exception
		{
			this(inputStream, PRIVATE_KEY | PUBLIC_KEY | X509_CERTIFICATE | X509_CRL);
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
			List<StringBuilder> list4 = new ArrayList<>();

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
						    ||
						    line.matches("-----BEGIN X509 CRL-----")
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

					else if(line.matches("-----END X509 CRL-----"))
					{
						if((flag & X509_CRL) != 0)
						{
							list4.add(stringBuilder);
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
			final int numberOfCRLs = list4.size();

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

			x509CRLs = new X509CRL[numberOfCRLs];

			for(int i = 0; i < numberOfCRLs; i++)
			{
				x509CRLs[i] = (X509CRL) CertificateFactory.getInstance("X509", BC).generateCRL(

					new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(
						list4.get(i).toString()
					))
				);
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		public PEM(@Nullable PrivateKey[] _privateKeys, @Nullable PublicKey[] _publicKeys, @Nullable X509Certificate[] _x509Certificates, @Nullable X509CRL[] _x509CRLs)
		{
			privateKeys = _privateKeys;
			publicKeys = _publicKeys;
			x509Certificates = _x509Certificates;
			x509CRLs = _x509CRLs;
		}

		/*-----------------------------------------------------------------*/

		public static PEM generateCA(int keysize, String subject, @Nullable String email, int validity) throws Exception
		{
			KeyPair keyPair = SecuritySingleton.generateKeyPair(keysize);

			X509Certificate certificate = SecuritySingleton.generateCA(
				keyPair.getPrivate(),
				keyPair.getPublic(),
				subject,
				email,
				validity
			);

			return new PEM(
				new PrivateKey[] {keyPair.getPrivate()},
				new PublicKey[] {keyPair.getPublic()},
				new X509Certificate[] {certificate},
				null
			);
		}

		/*-----------------------------------------------------------------*/

		public static PEM generateCertificate(PrivateKey caPrivateKey, X509Certificate caCertificate, int keysize, String subject, @Nullable String email, @Nullable String virtOrg, int validity) throws Exception
		{
			KeyPair keyPair = SecuritySingleton.generateKeyPair(keysize);

			X509Certificate certificate = SecuritySingleton.generateCertificate(
				caPrivateKey,
				caCertificate,
				keyPair.getPublic(),
				subject,
				email,
				virtOrg,
				validity
			);

			return new PEM(
				new PrivateKey[] {keyPair.getPrivate()},
				new PublicKey[] {keyPair.getPublic()},
				new X509Certificate[] {certificate},
				null
			);
		}

		/*-----------------------------------------------------------------*/

		public static PEM generateCRL(PrivateKey caPrivateKey, X509Certificate caCertificate, @Nullable List<Revocation> revocations) throws Exception
		{
			X509CRL crl = SecuritySingleton.generateCRL(caPrivateKey, caCertificate, revocations);

			return new PEM(
				null,
				null,
				null,
				new X509CRL[] {crl}
			);
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			StringBuilder stringBuilder = new StringBuilder();

			/*-------------------------------------------------------------*/

			if(privateKeys != null)
			{
				for(PrivateKey privateKey: privateKeys)
				{
					stringBuilder.append("-----BEGIN PRIVATE KEY-----\n")
					             .append(byteArrayToBase64String(privateKey.getEncoded()))
					             .append("-----END PRIVATE KEY-----\n")
					;
				}
			}

			/*-------------------------------------------------------------*/

			if(publicKeys != null)
			{
				for(PublicKey _publicKey: publicKeys)
				{
					stringBuilder.append("-----BEGIN PUBLIC KEY-----\n")
					             .append(byteArrayToBase64String(_publicKey.getEncoded()))
					             .append("-----END PUBLIC KEY-----\n")
					;
				}
			}

			/*-------------------------------------------------------------*/

			if(x509Certificates != null)
			{
				for(X509Certificate x509Certificate: x509Certificates)
				{
					try
					{
						stringBuilder.append("-----BEGIN CERTIFICATE-----\n")
						             .append(byteArrayToBase64String(x509Certificate.getEncoded()))
						             .append("-----END CERTIFICATE-----\n")
						;
					}
					catch(Exception e)
					{
						stringBuilder.append(e.getMessage());
					}
				}
			}

			/*-------------------------------------------------------------*/

			if(x509CRLs != null)
			{
				for(X509CRL x509CRL: x509CRLs)
				{
					try
					{
						stringBuilder.append("-----BEGIN X509 CRL-----\n")
						             .append(byteArrayToBase64String(x509CRL.getEncoded()))
						             .append("-----END X509 CRL-----\n")
						;
					}
					catch(Exception e)
					{
						stringBuilder.append(e.getMessage());
					}
				}
			}

			/*-------------------------------------------------------------*/

			return stringBuilder.toString();
		}

		/*-----------------------------------------------------------------*/

		public byte[] toByteArray()
		{
			return toString().getBytes(StandardCharsets.UTF_8);
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
			key = String.format("%1$-16s", password).getBytes(StandardCharsets.UTF_8);
		} else if(length <= 24) {
			key = String.format("%1$-24s", password).getBytes(StandardCharsets.UTF_8);
		} else if(length <= 32) {
			key = String.format("%1$-32s", password).getBytes(StandardCharsets.UTF_8);
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

	public static X509Certificate generateCA(PrivateKey privateKey, PublicKey publicKey, String subject, @Nullable String email, int validity) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE BUILDER                                 */
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

		if(email != null)
		{
			// Subject Alternative Name
			builder.addExtension(new ASN1ObjectIdentifier("2.5.29.17"), false, new GeneralNames(new GeneralName(GeneralName.rfc822Name, email)));
		}

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

	public static DERSequence amiVirtOrg(final String virtOrg)
	{
		return new DERSequence(new ASN1Encodable[] {
			new ASN1ObjectIdentifier("1.3.6.1.4.1.10813.666"),
			new DERUTF8String(virtOrg)
		});
	}

	/*---------------------------------------------------------------------*/

	public static X509Certificate generateCertificate(PrivateKey caPrivateKey, X509Certificate caCertificate, PublicKey publicKey, String subject, @Nullable String email, @Nullable String virtOrg, int validity) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE BUILDER                                 */
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

		List<GeneralName> generalNames = new ArrayList<>();

		if(email != null) {
			generalNames.add(new GeneralName(GeneralName.rfc822Name, /*--*/email/*--*/));
		}

		if(virtOrg != null) {
			generalNames.add(new GeneralName(GeneralName.otherName, amiVirtOrg(virtOrg)));
		}

		/*-----------------------------------------------------------------*/

		// Basic Constraints
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), false, new BasicConstraints(false));

		// Subject Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

		// Authority Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(caCertificate));

		// Subject Alternative Name
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.17"), false, new GeneralNames(generalNames.stream().toArray(size -> new GeneralName[size])));

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

	public static X509CRL generateCRL(PrivateKey caPrivateKey, X509Certificate caCertificate, @Nullable List<Revocation> revocations) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE X509 CRL BUILDER                                         */
		/*-----------------------------------------------------------------*/

		org.bouncycastle.asn1.x500.X500Name issuerDN = org.bouncycastle.asn1.x500.X500Name.getInstance(caCertificate.getSubjectX500Principal().getEncoded());

		X509v2CRLBuilder builder = new X509v2CRLBuilder(issuerDN, new Date());

		if(revocations != null)
		{
			for(Revocation revocation: revocations)
			{
				switch(revocation.reason)
				{
					case Revocation.SUPERSEDED:
						builder.addCRLEntry(revocation.serial, revocation.date, org.bouncycastle.asn1.x509.CRLReason.superseded);
						break;

					case Revocation.AFFILIATION_CHANGED:
						builder.addCRLEntry(revocation.serial, revocation.date, org.bouncycastle.asn1.x509.CRLReason.affiliationChanged);
						break;

					case Revocation.COMPROMISED:
						builder.addCRLEntry(revocation.serial, revocation.date, org.bouncycastle.asn1.x509.CRLReason.keyCompromise);
						break;

					default:
						builder.addCRLEntry(revocation.serial, revocation.date, org.bouncycastle.asn1.x509.CRLReason.unspecified);
						break;
				}
			}
		}

		/*-----------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                             */
		/*-----------------------------------------------------------------*/

		// Authority Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(caCertificate));

		/*-----------------------------------------------------------------*/
		/* CREATE X509 CRL                                                 */
		/*-----------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512WithRSA").setProvider(BC).build(caPrivateKey);

		return new JcaX509CRLConverter().setProvider(BC).getCRL(
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

	public static String encrypt(@Nullable String s) throws Exception
	{
		return s != null && s.isEmpty() == false ? new String(
			org.bouncycastle.util.encoders.Base64.encode(encrypt(s.getBytes(StandardCharsets.UTF_8)))
		, StandardCharsets.UTF_8) : "";
	}

	/*---------------------------------------------------------------------*/

	public static String decrypt(@Nullable String s) throws Exception
	{
		return s != null && s.isEmpty() == false ? new String(
			decrypt(org.bouncycastle.util.encoders.Base64.decode(s.toString(/*------------------*/)))
		, StandardCharsets.UTF_8) : "";
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
	/* HASH                                                                */
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	public static String md5Sum(byte[] s) throws Exception
	{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");

		messageDigest.update(s);

		return String.format("%032x", new BigInteger(1, messageDigest.digest()));
	}

	/*---------------------------------------------------------------------*/

	public static String md5Sum(String s) throws Exception
	{
		return md5Sum(s.getBytes(StandardCharsets.UTF_8));
	}

	/*---------------------------------------------------------------------*/

	public static String sha256Sum(byte[] s) throws Exception
	{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		messageDigest.update(s);

		return String.format("%064x", new BigInteger(1, messageDigest.digest()));
	}

	/*---------------------------------------------------------------------*/

	public static String sha256Sum(String s) throws Exception
	{
		return sha256Sum(s.getBytes(StandardCharsets.UTF_8));
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
	/* TEMPORARY PASSWORD                                                  */
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	public static String buildTmpPassword(String user, String pass) throws Exception
	{
		return buildTmpPassword(user, pass, true);
	}

	/*---------------------------------------------------------------------*/

	public static String buildTmpPassword(String user, String pass, boolean full) throws Exception
	{
		String result;

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		/*-----------------------------------------------------------------*/

		result = sha256Sum(encrypt(
			calendar.get(Calendar.   YEAR    )
			+ "|" +
			calendar.get(Calendar.DAY_OF_YEAR)
			+ "|" +
			calendar.get(Calendar.HOUR_OF_DAY)
			+ "|" +
			user
			+ "|" +
			pass
		)).substring(0, 16);

		/*-----------------------------------------------------------------*/

		if(full)
		{
			calendar.add(Calendar.MINUTE, 60 - calendar.get(Calendar.MINUTE));

			/*-------------------------------------------------------------*/

			result += sha256Sum(encrypt(
				calendar.get(Calendar.   YEAR    )
				+ "|" +
				calendar.get(Calendar.DAY_OF_YEAR)
				+ "|" +
				calendar.get(Calendar.HOUR_OF_DAY)
				+ "|" +
				user
				+ "|" +
				pass
			)).substring(0, 16);

			/*-------------------------------------------------------------*/
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<String, String> checkPassword(String user, String pass_from_user, String pass_from_db) throws Exception
	{
		if(pass_from_user.equals(pass_from_db) == false)
		{
			if(pass_from_user.length() == 32)
			{
				String a = /**/ pass_from_user /**/.substring(0, 16);
				String b = /**/ pass_from_user /**/.substring(16, 32);
				String c = buildTmpPassword(user, pass_from_db, false);

				if(a.equals(c) == false
				   &&
				   b.equals(c) == false
				 ) {
					throw new Exception("invalid password");
				}
			}
			else
			{
				throw new Exception("invalid password");
			}
		}

		return new Tuple2<String, String>(
			/**/user/**/,
			pass_from_db
		);
	}

	/*---------------------------------------------------------------------*/
}
