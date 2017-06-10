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

		SecuritySingleton.PEM tuple = new SecuritySingleton.PEM(new FileInputStream(fileName));

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

		KeyStore keyStore_PKCS12 = SecuritySingleton.generatePKCS12KeyStore(keyPair.getPrivate(), new X509Certificate[] {certificate}, email.toCharArray());

		keyStore_PKCS12.setCertificateEntry("AMI-CA", caCrt);

		/*-----------------------------------------------------------------*/

		try(ByteArrayOutputStream output = new ByteArrayOutputStream())
		{
			/*-------------------------------------------------------------*/

			keyStore_PKCS12.store(output, "".toCharArray());

			/*-------------------------------------------------------------*/

			BodyPart mainBodyPart1 = new MimeBodyPart();

			mainBodyPart1.setDataHandler(
				new javax.activation.DataHandler(
					new ByteArrayDataSource(output.toByteArray(), "application/octet-stream")
				)
			);

			mainBodyPart1.setFileName(commonName + ".crt");

			/*-------------------------------------------------------------*/

			/* TODO */

			/*-------------------------------------------------------------*/

			MailSingleton.sendMessage(ConfigSingleton.getProperty("email"), email, "", "AMI X509 certificat", "AMI X509 certificat", new BodyPart[] {mainBodyPart1});

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Generate a client or server certificates.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" -password=\"\" (-validity=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
