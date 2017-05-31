package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class GenerateCertificate extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GenerateCertificate(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		PrivateKey      caKey;
		X509Certificate caCrt;

		String country = arguments.containsKey("country") ? arguments.get("country")
		                                                  : ""
		;

		String locality = arguments.containsKey("locality") ? arguments.get("locality")
		                                                    : ""
		;

		String organization = arguments.containsKey("organization") ? arguments.get("organization")
		                                                            : ""
		;

		String organizationalUnit = arguments.containsKey("organizationalUnit") ? arguments.get("organizationalUnit")
		                                                                        : ""
		;

		String commonName = arguments.containsKey("commonName") ? arguments.get("commonName")
		                                                        : ""
		;

		String password = arguments.containsKey("password") ? arguments.get("password")
		                                                    : ""
		;

		int m_validity;

		if(arguments.containsKey("validity"))
		{
			try
			{
				m_validity = Integer.parseInt(arguments.get("validity"));
			}
			catch(NumberFormatException e)
			{
				m_validity = 1;
			}
		}
		else
		{
			m_validity = 1;
		}

		/*-----------------------------------------------------------------*/

		String fileName = ConfigSingleton.getConfigPathName() + File.separator + "ca.pem";

		/*-----------------------------------------------------------------*/

		try
		{
			SecuritySingleton.PEMTuple tuple = SecuritySingleton.loadPEM(new FileInputStream(fileName));

			if(tuple.privateKeys.length == 0)
			{
				throw new Exception("no private key in  `" + fileName + "`");
			}

			if(tuple.x509Certificates.length == 0)
			{
				throw new Exception("no certificate in  `" + fileName + "`");
			}

			caKey = tuple.privateKeys[0];
			caCrt = tuple.x509Certificates[0];
		}
		catch(Exception e)
		{
			throw new Exception("could not open `" + fileName + "`: " + e.getMessage(), e);
		}

		/*-----------------------------------------------------------------*/

		KeyPair keyPair = SecuritySingleton.generateKeyPair(2048);

		X509Certificate certificate = SecuritySingleton.generateCertificate(
			caKey,
			caCrt,
			keyPair.getPublic(),
			String.format(
				"CN=%s, OU=%s, O=%s, L=%s, C=%s",
				commonName,
				organizationalUnit,
				organization,
				locality,
				country
			),
			m_validity
		);

		/*-----------------------------------------------------------------*/

		KeyStore keyStore_JKS = SecuritySingleton.generateJKSKeyStore(keyPair.getPrivate(), new X509Certificate[] {certificate}, password.toCharArray());
		KeyStore keyStore_PKCS12 = SecuritySingleton.generatePKCS12KeyStore(keyPair.getPrivate(), new X509Certificate[] {certificate}, password.toCharArray());

		keyStore_JKS.setCertificateEntry("AMI-CA", caCrt);
		keyStore_PKCS12.setCertificateEntry("AMI-CA", caCrt);

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset><row>");

		/*-----------------------------------------------------------------*/

		result.append("<field name=\"CLIENT_DN\"><![CDATA[" + SecuritySingleton.getDNName(certificate.getSubjectX500Principal()) + "]]></field>");
		result.append("<field name=\"ISSUER_DN\"><![CDATA[" + SecuritySingleton.getDNName(certificate.getIssuerX500Principal()) + "]]></field>");

		result.append("<field name=\"PRIVATE_KEY\">");
		result.append("-----BEGIN PRIVATE KEY-----\n");
		result.append(SecuritySingleton.byteArrayToBase64String(keyPair.getPrivate().getEncoded()));
		result.append("-----END PRIVATE KEY-----\n");
		result.append("</field>");

		result.append("<field name=\"PUBLIC_KEY\">");
		result.append("-----BEGIN PUBLIC KEY-----\n");
		result.append(SecuritySingleton.byteArrayToBase64String(keyPair.getPublic().getEncoded()));
		result.append("-----END PUBLIC KEY-----\n");
		result.append("</field>");

		result.append("<field name=\"CERTIFICATE\">");
		result.append("-----BEGIN CERTIFICATE-----\n");
		result.append(SecuritySingleton.byteArrayToBase64String(certificate.getEncoded()));
		result.append("-----END CERTIFICATE-----\n");
		result.append("</field>");

		try(ByteArrayOutputStream output = new ByteArrayOutputStream())
		{
			keyStore_JKS.store(output, password.toCharArray());

			result.append("<field name=\"KEYSTORE_JKS\">");
			result.append(SecuritySingleton.byteArrayToBase64String(output.toByteArray()));
			result.append("</field>");
		}

		try(ByteArrayOutputStream output = new ByteArrayOutputStream())
		{
			keyStore_PKCS12.store(output, password.toCharArray());

			result.append("<field name=\"KEYSTORE_P12\">");
			result.append(SecuritySingleton.byteArrayToBase64String(output.toByteArray()));
			result.append("</field>");
		}

		/*-----------------------------------------------------------------*/

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Generate client or server certificates.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" -password=\"\" (-validity=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
