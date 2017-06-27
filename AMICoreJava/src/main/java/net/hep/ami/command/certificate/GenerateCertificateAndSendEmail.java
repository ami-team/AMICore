package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.util.*;
import javax.mail.internet.*;

import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class GenerateCertificateAndSendEmail extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GenerateCertificateAndSendEmail(Map<String, String> arguments, long transactionId)
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

		String email = arguments.containsKey("email") ? arguments.get("email")
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

		try(ByteArrayOutputStream output1 = new ByteArrayOutputStream())
		{
			try(ByteArrayOutputStream output2 = new ByteArrayOutputStream())
			{
				/*---------------------------------------------------------*/

				keyStore_JKS.store(output1, password.toCharArray());
				keyStore_PKCS12.store(output2, password.toCharArray());

				/*---------------------------------------------------------*/

				BodyPart mainBodyPart1 = new MimeBodyPart();

				mainBodyPart1.setDataHandler(
					new javax.activation.DataHandler(
						new ByteArrayDataSource(output2.toByteArray(), "application/octet-stream")
					)
				);

				mainBodyPart1.setFileName(commonName + ".jks");

				/*---------------------------------------------------------*/

				BodyPart mainBodyPart2 = new MimeBodyPart();

				mainBodyPart2.setDataHandler(
					new javax.activation.DataHandler(
						new ByteArrayDataSource(output2.toByteArray(), "application/octet-stream")
					)
				);

				mainBodyPart2.setFileName(commonName + ".p12");

				/*---------------------------------------------------------*/

				BodyPart mainBodyPart3 = new MimeBodyPart();

				mainBodyPart3.setDataHandler(
					new javax.activation.DataHandler(
						new ByteArrayDataSource(pem.toByteArray(), "application/x-pem-file")
					)
				);

				mainBodyPart3.setFileName(commonName + ".pem");

				/*---------------------------------------------------------*/

				MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, "", "AMI X509 certificate", "AMI X509 certificate", new BodyPart[] {mainBodyPart1, mainBodyPart2, mainBodyPart3});

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<rowset><row><field name=\"SERIAL\"><![CDATA[").append(pem.x509Certificates[0].getSerialNumber()).append("]]></field></row></rowset><info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Generate a client or server certificates. Default validity: 1 year.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" -password=\"\" (-validity=\"\")? -email=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
