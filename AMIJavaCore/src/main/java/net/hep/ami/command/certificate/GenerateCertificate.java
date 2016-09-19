package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class GenerateCertificate extends CommandAbstractClass
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

		String m_country = arguments.containsKey("country") ? arguments.get("country")
		                                                    : ""
		;

		String m_locality = arguments.containsKey("locality") ? arguments.get("locality")
		                                                      : ""
		;

		String m_organization = arguments.containsKey("organization") ? arguments.get("organization")
		                                                              : ""
		;

		String m_organizationalUnit = arguments.containsKey("organizationalUnit") ? arguments.get("organizationalUnit")
		                                                                          : ""
		;

		String m_commonName = arguments.containsKey("commonName") ? arguments.get("commonName")
		                                                          : ""
		;

		String m_password = arguments.containsKey("password") ? arguments.get("password")
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
			InputStream inputStream = new FileInputStream(fileName);

			Cryptography.PEMTuple tuple = Cryptography.loadPEM(inputStream);

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
			throw new Exception("could not open `" + fileName + "`: " + e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		KeyPair keyPair = Cryptography.generateKeyPair(2048);

		X509Certificate certificate = Cryptography.generateCertificate(
				caKey,
				caCrt,
				keyPair.getPublic(),
				String.format(
					"CN=%s, OU=%s, O=%s, L=%s, C=%s",
					m_commonName,
					m_organizationalUnit,
					m_organization,
					m_locality,
					m_country
				),
				m_validity
		);

		/*-----------------------------------------------------------------*/

		KeyStore keyStore_JKS = Cryptography.generateKeyStore_JKS(keyPair.getPrivate(), new X509Certificate[] {certificate}, m_password.toCharArray());
		KeyStore keyStore_PKCS12 = Cryptography.generateKeyStore_PKCS12(keyPair.getPrivate(), new X509Certificate[] {certificate}, m_password.toCharArray());

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset><row>");

		/*-----------------------------------------------------------------*/

		ByteArrayOutputStream output;

		result.append("<field name=\"CLIENT_DN\"><![CDATA[" + Cryptography.getAMIName(certificate.getSubjectX500Principal()) + "]]></field>");
		result.append("<field name=\"ISSUER_DN\"><![CDATA[" + Cryptography.getAMIName(certificate.getIssuerX500Principal()) + "]]></field>");

		result.append("<field name=\"PRIVATE_KEY\">");
		result.append("-----BEGIN PRIVATE KEY-----\n");
		result.append(Cryptography.byteArrayToBase64String(keyPair.getPrivate().getEncoded()));
		result.append("-----END PRIVATE KEY-----\n");
		result.append("</field>");

		result.append("<field name=\"PUBLIC_KEY\">");
		result.append("-----BEGIN PUBLIC KEY-----\n");
		result.append(Cryptography.byteArrayToBase64String(keyPair.getPublic().getEncoded()));
		result.append("-----END PUBLIC KEY-----\n");
		result.append("</field>");

		result.append("<field name=\"CERTIFICATE\">");
		result.append("-----BEGIN CERTIFICATE-----\n");
		result.append(Cryptography.byteArrayToBase64String(certificate.getEncoded()));
		result.append("-----END CERTIFICATE-----\n");
		result.append("</field>");

		output = new ByteArrayOutputStream();

		try
		{
			keyStore_JKS.store(output, m_password.toCharArray());

			result.append("<field name=\"KEYSTORE_JKS\">");
			result.append(Cryptography.byteArrayToBase64String(output.toByteArray()));
			result.append("</field>");
		}
		finally
		{
			output.close();
		}

		output = new ByteArrayOutputStream();

		try
		{
			keyStore_PKCS12.store(output, m_password.toCharArray());

			result.append("<field name=\"KEYSTORE_P12\">");
			result.append(Cryptography.byteArrayToBase64String(output.toByteArray()));
			result.append("</field>");
		}
		finally
		{
			output.close();
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
		return "-country=\"value\" -locality=\"value\" -organization=\"value\" -organizationalUnit=\"value\" -commonName=\"value\" -password=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
