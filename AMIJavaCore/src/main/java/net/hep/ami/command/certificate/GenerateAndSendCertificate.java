package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.*;

import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class GenerateAndSendCertificate extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public GenerateAndSendCertificate(Map<String, String> arguments, long transactionId)
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

		String m_email = arguments.containsKey("email") ? arguments.get("email")
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

		KeyStore keyStore_PKCS12 = Cryptography.generateKeyStore_PKCS12(keyPair.getPrivate(), new X509Certificate[] {certificate}, m_email.toCharArray());

		/*-----------------------------------------------------------------*/

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		try
		{
			keyStore_PKCS12.store(output, "".toCharArray());

			BodyPart mainBodyPart = new MimeBodyPart();

			mainBodyPart.setDataHandler(
				new DataHandler(
					new ByteArrayDataSource(output.toByteArray(), "application/octet-stream")
				)
			);

			mainBodyPart.setFileName(m_commonName + ".crt");

			MailSingleton.sendMessage("ami@lpsc.in2p3.fr", m_email, "", "AMI X509 certificat", "AMI X509 certificat", new BodyPart[] {mainBodyPart});
		}
		finally
		{
			output.close();
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
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
