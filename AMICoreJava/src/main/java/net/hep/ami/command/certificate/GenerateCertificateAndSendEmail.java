package net.hep.ami.command.certificate;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;

import javax.mail.*;
import javax.mail.util.*;
import javax.mail.internet.*;

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

		int validity;

		if(arguments.containsKey("validity"))
		{
			try
			{
				validity = Integer.parseInt(arguments.get("validity"));
			}
			catch(NumberFormatException e)
			{
				validity = 1;
			}
		}
		else
		{
			validity = 1;
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
			email,
			validity
		);

		/*-----------------------------------------------------------------*/

		KeyStore keyStore_JKS = SecuritySingleton.generateJKSKeyStore(pem.privateKeys[0], pem.x509Certificates, password.toCharArray());
		KeyStore keyStore_PKCS12 = SecuritySingleton.generatePKCS12KeyStore(pem.privateKeys[0], pem.x509Certificates, password.toCharArray());

		keyStore_JKS.setCertificateEntry("AMI-CA", caCrt);
		keyStore_PKCS12.setCertificateEntry("AMI-CA", caCrt);

		/*-----------------------------------------------------------------*/
		/* SEND CERTIFICATE                                                */
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

				MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, "", "New AMI certificate", "Hi,\n\nThis is you new AMI certificate. You can install \"" + commonName + ".p12\" in your web browser.\n\nBest regards.", new BodyPart[] {mainBodyPart1, mainBodyPart2, mainBodyPart3});

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/
		/* SAVE CERTIFICATE                                                */
		/*-----------------------------------------------------------------*/

		PreparedStatement preparedStatement = getQuerier("self").prepareStatement("INSERT INTO `router_authority` (`clientDN`, `issuerDN`, `notBefore`, `notAfter`, `serial`, `email`) VALUES (?, ?, ?, ?, ?, ?)");

		preparedStatement.setString(1, SecuritySingleton.getDN(pem.x509Certificates[0].getSubjectX500Principal()));
		preparedStatement.setString(2, SecuritySingleton.getDN(pem.x509Certificates[0].getIssuerX500Principal()));
		preparedStatement.setDate(3, new java.sql.Date(pem.x509Certificates[0].getNotBefore().getTime()));
		preparedStatement.setDate(4, new java.sql.Date(pem.x509Certificates[0].getNotAfter().getTime()));
		preparedStatement.setString(5, pem.x509Certificates[0].getSerialNumber().toString(10));
		preparedStatement.setString(6, email);

		preparedStatement.executeUpdate();

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Generate a client or server certificates. Default validity: 1 year.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" -email=\"\" -password=\"\" (-validity=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
