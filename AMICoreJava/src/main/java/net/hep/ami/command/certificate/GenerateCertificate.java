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

		StringBuilder result = new StringBuilder();

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

		SecuritySingleton.PEM ca = new SecuritySingleton.PEM(new FileInputStream(fileName));

		if(ca.privateKeys.length == 0)
		{
			throw new Exception("no private key in  `" + fileName + "`");
		}

		if(ca.x509Certificates.length == 0)
		{
			throw new Exception("no certificate in  `" + fileName + "`");
		}

		caKey = ca.privateKeys[0];
		caCrt = ca.x509Certificates[0];

		/*-----------------------------------------------------------------*/

		SecuritySingleton.PEM pem = SecuritySingleton.PEM.generateCertificate(
			caKey,
			caCrt,
			2048,
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

		KeyStore keyStore_JKS = SecuritySingleton.generateJKSKeyStore(pem.privateKeys[0], pem.x509Certificates, password.toCharArray());
		KeyStore keyStore_PKCS12 = SecuritySingleton.generatePKCS12KeyStore(pem.privateKeys[0], pem.x509Certificates, password.toCharArray());

		keyStore_JKS.setCertificateEntry("AMI-CA", caCrt);
		keyStore_PKCS12.setCertificateEntry("AMI-CA", caCrt);

		/*-----------------------------------------------------------------*/

		result.append("<rowset><row>");

		/*-----------------------------------------------------------------*/

		result.append("<field name=\"CLIENT_DN\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getSubjectX500Principal())).append("]]></field>")
		      .append("<field name=\"ISSUER_DN\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getIssuerX500Principal())).append("]]></field>")
		      .append("<field name=\"SERIAL\"><![CDATA[").append(pem.x509Certificates[0].getSerialNumber()).append("]]></field>")
		      .append("<field name=\"PASSWORD\"><![CDATA[").append(password).append("]]></field>")
		;

		result.append("<field name=\"PEM\">")
		      .append(pem.toString())
		      .append("</field>")
		;

		try(ByteArrayOutputStream output = new ByteArrayOutputStream())
		{
			keyStore_JKS.store(output, password.toCharArray());

			result.append("<field name=\"KEYSTORE_JKS\">")
			      .append(SecuritySingleton.byteArrayToBase64String(output.toByteArray()))
			      .append("</field>")
			;
		}

		try(ByteArrayOutputStream output = new ByteArrayOutputStream())
		{
			keyStore_PKCS12.store(output, password.toCharArray());

			result.append("<field name=\"KEYSTORE_P12\">")
			      .append(SecuritySingleton.byteArrayToBase64String(output.toByteArray()))
			      .append("</field>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Generate a client or server certificates. Default validity: 1 year.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" -password=\"\" (-validity=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
