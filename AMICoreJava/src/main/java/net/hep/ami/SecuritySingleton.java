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

import org.jetbrains.annotations.*;

public class SecuritySingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public static class Revocation
	{
		public static final int SUPERSEDED = 0;
		public static final int COMPROMISED = 1;
		public static final int AFFILIATION_CHANGED = 2;

		public final BigInteger serial;
		public final Integer reason;
		public final Date date;

		@Contract(pure = true)
		public Revocation(BigInteger _serial, Integer _reason, Date _date)
		{
			serial = _serial;
			reason = _reason;
			date = _date;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final class PEM
	{
		/*------------------------------------------------------------------------------------------------------------*/

		public static final int PRIVATE_KEY = (1 << 0);
		public static final int PUBLIC_KEY = (1 << 1);
		public static final int X509_CERTIFICATE = (1 << 2);
		public static final int X509_CRL = (1 << 3);

		/*------------------------------------------------------------------------------------------------------------*/

		public final PrivateKey[] privateKeys;
		public final PublicKey[] publicKeys;
		public final X509Certificate[] x509Certificates;
		public final X509CRL[] x509CRLs;

		/*------------------------------------------------------------------------------------------------------------*/

		public PEM(@NotNull InputStream inputStream) throws Exception
		{
			this(inputStream, PRIVATE_KEY | PUBLIC_KEY | X509_CERTIFICATE | X509_CRL);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		public PEM(@NotNull InputStream inputStream, int flag) throws Exception
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* LOAD FILE                                                                                              */
			/*--------------------------------------------------------------------------------------------------------*/

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
					/*------------------------------------------------------------------------------------------------*/

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

					/*------------------------------------------------------------------------------------------------*/

					else if(line.matches("-----END( | [^ ]+ )PRIVATE KEY-----"))
					{
						if((flag & PRIVATE_KEY) != 0)
						{
							list1.add(stringBuilder);
						}

						append = false;
					}

					/*------------------------------------------------------------------------------------------------*/

					else if(line.matches("-----END( | [^ ]+ )PUBLIC KEY-----"))
					{
						if((flag & PUBLIC_KEY) != 0)
						{
							list2.add(stringBuilder);
						}

						append = false;
					}

					/*------------------------------------------------------------------------------------------------*/

					else if(line.matches("-----END CERTIFICATE-----"))
					{
						if((flag & X509_CERTIFICATE) != 0)
						{
							list3.add(stringBuilder);
						}

						append = false;
					}

					/*------------------------------------------------------------------------------------------------*/

					else if(line.matches("-----END X509 CRL-----"))
					{
						if((flag & X509_CRL) != 0)
						{
							list4.add(stringBuilder);
						}

						append = false;
					}

					/*------------------------------------------------------------------------------------------------*/

					else if(append)
					{
						stringBuilder.append(line);
					}

					/*------------------------------------------------------------------------------------------------*/
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
			/* GET NUMBER OF OBJECTS                                                                                  */
			/*--------------------------------------------------------------------------------------------------------*/

			final int numberOfPrivateKeys = list1.size();
			final int numberOfPublicKeys = list2.size();
			final int numberOfCertificates = list3.size();
			final int numberOfCRLs = list4.size();

			/*--------------------------------------------------------------------------------------------------------*/
			/* BUILD OBJECTS                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			privateKeys = new PrivateKey[numberOfPrivateKeys];

			for(int i = 0; i < numberOfPrivateKeys; i++)
			{
				privateKeys[i] = KeyFactory.getInstance("RSA", BC).generatePrivate(

					new PKCS8EncodedKeySpec(org.bouncycastle.util.encoders.Base64.decode(
						list1.get(i).toString()
					))
				);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			publicKeys = new PublicKey[numberOfPublicKeys];

			for(int i = 0; i < numberOfPublicKeys; i++)
			{
				publicKeys[i] = KeyFactory.getInstance("RSA", BC).generatePublic(

					new X509EncodedKeySpec(org.bouncycastle.util.encoders.Base64.decode(
						list2.get(i).toString()
					))
				);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			x509Certificates = new X509Certificate[numberOfCertificates];

			for(int i = 0; i < numberOfCertificates; i++)
			{
				x509Certificates[i] = (X509Certificate) CertificateFactory.getInstance("X509", BC).generateCertificate(

					new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(
						list3.get(i).toString()
					))
				);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			x509CRLs = new X509CRL[numberOfCRLs];

			for(int i = 0; i < numberOfCRLs; i++)
			{
				x509CRLs[i] = (X509CRL) CertificateFactory.getInstance("X509", BC).generateCRL(

					new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(
						list4.get(i).toString()
					))
				);
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@Contract(pure = true)
		public PEM(@Nullable PrivateKey[] _privateKeys, @Nullable PublicKey[] _publicKeys, @Nullable X509Certificate[] _x509Certificates, @Nullable X509CRL[] _x509CRLs)
		{
			privateKeys = _privateKeys;
			publicKeys = _publicKeys;
			x509Certificates = _x509Certificates;
			x509CRLs = _x509CRLs;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		public static PEM generateCA(int keySize, @NotNull String subject, @Nullable String email, int validity) throws Exception
		{
			KeyPair keyPair = SecuritySingleton.generateKeyPair(keySize);

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

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		public static PEM generateCertificate(@NotNull PrivateKey caPrivateKey, @NotNull X509Certificate caCertificate, int keySize, @NotNull String subject, @Nullable String email, @Nullable String virtOrg, int validity) throws Exception
		{
			KeyPair keyPair = SecuritySingleton.generateKeyPair(keySize);

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

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		public static PEM generateCRL(@NotNull PrivateKey caPrivateKey, @NotNull X509Certificate caCertificate, @Nullable List<Revocation> revocations) throws Exception
		{
			X509CRL crl = SecuritySingleton.generateCRL(caPrivateKey, caCertificate, revocations);

			return new PEM(
				null,
				null,
				null,
				new X509CRL[] {crl}
			);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		public String toString()
		{
			StringBuilder stringBuilder = new StringBuilder();

			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/

			return stringBuilder.toString();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		public byte[] toByteArray()
		{
			return toString().getBytes(StandardCharsets.UTF_8);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

	/*----------------------------------------------------------------------------------------------------------------*/

	private static KeyParameter s_keyParameter;

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private SecuritySingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void init(@NotNull String password) throws Exception
	{
		final int length = password.length();

		/*--*/ if(length <= 16) {
			s_keyParameter = new KeyParameter(String.format("%1$-16s", password).getBytes(StandardCharsets.UTF_8));
		} else if(length <= 24) {
			s_keyParameter = new KeyParameter(String.format("%1$-24s", password).getBytes(StandardCharsets.UTF_8));
		} else if(length <= 32) {
			s_keyParameter = new KeyParameter(String.format("%1$-32s", password).getBytes(StandardCharsets.UTF_8));
		} else {
			throw new Exception("too long password (max 32)");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
	/* KEYS AND CERTIFICATES                                                                                          */
	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static KeyPair generateKeyPair(int keySize) throws Exception
	{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC);

		keyPairGenerator.initialize(keySize, new SecureRandom());

		return keyPairGenerator.generateKeyPair();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static X509Certificate generateCA(@NotNull PrivateKey privateKey, @NotNull PublicKey publicKey, @NotNull String subject, @Nullable String email, int validity) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE BUILDER                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		// Basic Constraints
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(true));

		// Subject Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

		if(email != null)
		{
			// Subject Alternative Name
			builder.addExtension(new ASN1ObjectIdentifier("2.5.29.17"), false, new GeneralNames(new GeneralName(GeneralName.rfc822Name, email)));
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE                                                                                    */
		/*------------------------------------------------------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512WithRSA").setProvider(BC).build(privateKey);

		return new JcaX509CertificateConverter().setProvider(BC).getCertificate(
			builder.build(contentSigner)
		);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public static DERSequence amiVirtOrg(@NotNull String virtOrg)
	{
		return new DERSequence(new ASN1Encodable[] {
			new ASN1ObjectIdentifier("1.3.6.1.4.1.10813.666"),
			new DERUTF8String(virtOrg)
		});
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static X509Certificate generateCertificate(@NotNull PrivateKey caPrivateKey, @NotNull X509Certificate caCertificate, @NotNull PublicKey publicKey, @NotNull String subject, @Nullable String email, @Nullable String virtOrg, int validity) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE BUILDER                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		ArrayList<GeneralName> generalNames = new ArrayList<>();

		if(email != null) {
			generalNames.add(new GeneralName(GeneralName.rfc822Name, /*--*/email/*--*/));
		}

		if(virtOrg != null) {
			generalNames.add(new GeneralName(GeneralName.otherName, amiVirtOrg(virtOrg)));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		// Basic Constraints
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), false, new BasicConstraints(false));

		// Subject Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey));

		// Authority Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(caCertificate));

		// Subject Alternative Name
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.17"), false, new GeneralNames(generalNames.toArray(new GeneralName[generalNames.size()])));

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE X509 CERTIFICATE                                                                                    */
		/*------------------------------------------------------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512WithRSA").setProvider(BC).build(caPrivateKey);

		return new JcaX509CertificateConverter().setProvider(BC).getCertificate(
			builder.build(contentSigner)
		);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static X509CRL generateCRL(@NotNull PrivateKey caPrivateKey, @NotNull X509Certificate caCertificate, @Nullable List<Revocation> revocations) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE X509 CRL BUILDER                                                                                    */
		/*------------------------------------------------------------------------------------------------------------*/

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

					case Revocation.COMPROMISED:
						builder.addCRLEntry(revocation.serial, revocation.date, org.bouncycastle.asn1.x509.CRLReason.keyCompromise);
						break;

					case Revocation.AFFILIATION_CHANGED:
						builder.addCRLEntry(revocation.serial, revocation.date, org.bouncycastle.asn1.x509.CRLReason.affiliationChanged);
						break;

					default:
						builder.addCRLEntry(revocation.serial, revocation.date, org.bouncycastle.asn1.x509.CRLReason.unspecified);
						break;
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD X509 EXTENSIONS                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		// Authority Key Identifier
		builder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(caCertificate));

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE X509 CRL                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA512WithRSA").setProvider(BC).build(caPrivateKey);

		return new JcaX509CRLConverter().setProvider(BC).getCRL(
			builder.build(contentSigner)
		);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static KeyStore generateJKSKeyStore(@NotNull PrivateKey privateKey, @NotNull X509Certificate[] certificates, @NotNull char[] password) throws Exception
	{
		KeyStore result = KeyStore.getInstance("JKS");

		result.load(null, null);

		result.setKeyEntry("AMI", privateKey, password, certificates);

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static KeyStore generatePKCS12KeyStore(@NotNull PrivateKey privateKey, @NotNull X509Certificate[] certificates, @NotNull char[] password) throws Exception
	{
		KeyStore result = KeyStore.getInstance("PKCS12");

		result.load(null, null);

		result.setKeyEntry("AMI", privateKey, password, certificates);

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String byteArrayToBase64String(@NotNull byte[] data)
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* ENCODE TO BASE64                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		String string = org.bouncycastle.util.encoders.Base64.toBase64String(data);

		/*------------------------------------------------------------------------------------------------------------*/
		/* SPLIT BASE64 STRING                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		int i = 0, j;

		final int l = string.length();

		StringBuilder result = new StringBuilder();

		for(;;)
		{
			j = i + 64;

			if(j > l)
			{
				result.append(string, i, l);
				result.append("\n");

				break;
			}
			else
			{
				result.append(string, i, j);
				result.append("\n");

				i = j;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static boolean isProxy(@NotNull X509Certificate certificate)
	{
		byte[] data;

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK RFC3820 PROXY                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		data = certificate.getExtensionValue("1.3.6.1.5.5.7.1.14");

		if(data != null && data.length > 0)
		{
			return true;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK RFC_DRAFT PROXY                                                                                      */
		/*------------------------------------------------------------------------------------------------------------*/

		data = certificate.getExtensionValue("1.3.6.1.4.1.3536.1.222");

		if(data != null && data.length > 0)
		{
			return true;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK VOMS PROXY                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		String[] parts = certificate.getSubjectX500Principal().getName("RFC2253").split(",", -1);

		for(String part: parts)
		{
			if("CN=limited proxy".equals(part)
			   ||
			   /**/"CN=proxy"/**/.equals(part)
			 ) {
				return true;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return false;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getDN(@NotNull javax.security.auth.x500.X500Principal principal)
	{
		StringBuilder result = new StringBuilder();

		for(String part: principal.getName("RFC2253").split(",", -1))
		{
			result.insert(0, "/" + part);
		}

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
	/* CRYPTO                                                                                                         */
	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	private static void encrypt(@NotNull OutputStream outputStream, @NotNull InputStream inputStream) throws Exception
	{
		if(s_keyParameter == null)
		{
			throw new Exception("SecuritySingleton not initialized");
		}

		int noBytesRead;
		int noBytesProcessed;

		byte[] ibuff = new byte[16];
		byte[] obuff = new byte[512];

		/*------------------------------------------------------------------------------------------------------------*/

		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine());

		cipher.init(true, s_keyParameter);

		/*------------------------------------------------------------------------------------------------------------*/

		while((noBytesRead = inputStream.read(ibuff)) >= 0)
		{
			noBytesProcessed = cipher.processBytes(ibuff, 0, noBytesRead, obuff, 0);
			outputStream.write(obuff, 0, noBytesProcessed);
		}

		noBytesProcessed = cipher.doFinal(obuff, 0);
		outputStream.write(obuff, 0, noBytesProcessed);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void decrypt(@NotNull OutputStream outputStream, @NotNull InputStream inputStream) throws Exception
	{
		if(s_keyParameter == null)
		{
			throw new Exception("SecuritySingleton not initialized");
		}

		int noBytesRead;
		int noBytesProcessed;

		byte[] ibuff = new byte[16];
		byte[] obuff = new byte[512];

		/*------------------------------------------------------------------------------------------------------------*/

		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine());

		cipher.init(false, s_keyParameter);

		/*------------------------------------------------------------------------------------------------------------*/

		while((noBytesRead = inputStream.read(ibuff)) >= 0)
		{
			noBytesProcessed = cipher.processBytes(ibuff, 0, noBytesRead, obuff, 0);
			outputStream.write(obuff, 0, noBytesProcessed);
		}

		noBytesProcessed = cipher.doFinal(obuff, 0);
		outputStream.write(obuff, 0, noBytesProcessed);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static byte[] encrypt(@NotNull byte[] data) throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
 		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		encrypt(outputStream, inputStream);

		return outputStream.toByteArray();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static byte[] decrypt(@NotNull byte[] data) throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		decrypt(outputStream, inputStream);

		return outputStream.toByteArray();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String encrypt(@Nullable String s) throws Exception
	{
		return s != null && !s.isEmpty() ? new String(
			org.bouncycastle.util.encoders.Base64.encode(encrypt(s.getBytes(StandardCharsets.UTF_8)))
		, StandardCharsets.UTF_8) : "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String decrypt(@Nullable String s) throws Exception
	{
		return s != null && !s.isEmpty() ? new String(
			decrypt(org.bouncycastle.util.encoders.Base64.decode(s.getBytes(StandardCharsets.UTF_8)))
		, StandardCharsets.UTF_8) : "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
	/* HASH                                                                                                           */
	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String md5Sum(@NotNull byte[] s) throws Exception
	{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");

		messageDigest.update(s);

		return String.format("%032x", new BigInteger(1, messageDigest.digest()));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String md5Sum(@NotNull String s) throws Exception
	{
		return md5Sum(s.getBytes(StandardCharsets.UTF_8));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String sha256Sum(@NotNull byte[] s) throws Exception
	{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		messageDigest.update(s);

		return String.format("%064x", new BigInteger(1, messageDigest.digest()));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String sha256Sum(@NotNull String s) throws Exception
	{
		return sha256Sum(s.getBytes(StandardCharsets.UTF_8));
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
	/* TEMPORARY PASSWORD                                                                                             */
	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	static private final String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_=+-/";

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String generatePassword()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		final Random random = new Random();

		final int max = PASSWORD_CHARACTERS.length();

		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));
		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));
		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));
		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));
		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));
		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));
		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));
		result.append(PASSWORD_CHARACTERS.charAt(random.nextInt(max)));

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String buildTmpPassword(@NotNull String user, @NotNull String pass) throws Exception
	{
		return generateTmpPassword(user, pass, true);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String generateTmpPassword(@NotNull String user, @NotNull String pass, boolean full) throws Exception
	{
		String result;

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		if(full)
		{
			calendar.add(Calendar.MINUTE, 60 - calendar.get(Calendar.MINUTE));

			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_, _, _ -> new")
	public static Tuple2<String, String> checkPassword(@NotNull String user, @NotNull String pass_from_user, @NotNull String pass_from_db) throws Exception
	{
		if(!pass_from_user.equals(pass_from_db))
		{
			if(pass_from_user.length() == 32)
			{
				String a = /**/ pass_from_user /**/.substring(0, 16);
				String b = /**/ pass_from_user /**/.substring(16, 32);
				String c = generateTmpPassword(user, pass_from_db, false);

				if(!a.equals(c)
				   &&
				   !b.equals(c)
				 ) {
					throw new Exception("invalid password");
				}
			}
			else
			{
				throw new Exception("invalid password");
			}
		}

		return new Tuple2<>(
			/**/user/**/,
			pass_from_db
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
