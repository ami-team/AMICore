package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_CERT", visible = false, secured = true)
public class GenerateCertificateAndSendEmail extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GenerateCertificateAndSendEmail(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		PrivateKey      caKey;
		X509Certificate caCrt;

		String country = arguments.getOrDefault("country", "").trim();
		String locality = arguments.getOrDefault("locality", "").trim();
		String organization = arguments.getOrDefault("organization", "").trim();
		String organizationalUnit = arguments.getOrDefault("organizationalUnit", "").trim();
		String commonName = arguments.getOrDefault("commonName", "").trim();
		String email = arguments.getOrDefault("email", "").trim();
		String virtOrg = arguments.getOrDefault("virtOrg", "").trim();
		String password = arguments.getOrDefault("password", "").trim();

		if(country.isEmpty()
			   ||
			   locality.isEmpty()
			   ||
			   organization.isEmpty()
			   ||
			   organizationalUnit.isEmpty()
			   ||
			   commonName.isEmpty()
			   ||
			   email.isEmpty()
			   ||
			   virtOrg.isEmpty()
			   ||
			   password.isEmpty()
		) {
			throw new Exception("invalid usage");
		}

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

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE NEW CERTIFICATE                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		String fileName = ConfigSingleton.getConfigPathName() + File.separator + "ca.pem";

		/*-----------------------------------------------------------------*/

		SecuritySingleton.PEM ca = new SecuritySingleton.PEM(new FileInputStream(fileName));

		if(ca.getPrivateKeys().length == 0)
		{
			throw new Exception("no private key in  `" + fileName + "`");
		}

		if(ca.getX509Certificates().length == 0)
		{
			throw new Exception("no certificate in  `" + fileName + "`");
		}

		caKey = ca.getPrivateKeys()[0];
		caCrt = ca.getX509Certificates()[0];

		/*------------------------------------------------------------------------------------------------------------*/

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
			virtOrg,
			validity
		);

		/*------------------------------------------------------------------------------------------------------------*/

		KeyStore keyStore_JKS = SecuritySingleton.generateJKSKeyStore(pem.getPrivateKeys()[0], pem.getX509Certificates(), password.toCharArray());
		KeyStore keyStore_PKCS12 = SecuritySingleton.generatePKCS12KeyStore(pem.getPrivateKeys()[0], pem.getX509Certificates(), password.toCharArray());

		keyStore_JKS.setCertificateEntry("AMI-CA", caCrt);
		keyStore_PKCS12.setCertificateEntry("AMI-CA", caCrt);

		/*------------------------------------------------------------------------------------------------------------*/
		/* SEND NEW CERTIFICATE                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		try(ByteArrayOutputStream output1 = new ByteArrayOutputStream())
		{
			try(ByteArrayOutputStream output2 = new ByteArrayOutputStream())
			{
				/*----------------------------------------------------------------------------------------------------*/

				keyStore_JKS.store(output1, password.toCharArray());
				keyStore_PKCS12.store(output2, password.toCharArray());

				/*----------------------------------------------------------------------------------------------------*/

				MailSingleton.Attachment attachment1 = new MailSingleton.Attachment(
					commonName + ".jks",
					output1.toByteArray(),
					"application/octet-stream"
				);

				/*----------------------------------------------------------------------------------------------------*/

				MailSingleton.Attachment attachment2 = new MailSingleton.Attachment(
					commonName + ".p12",
					output2.toByteArray(),
					"application/x-pkcs12"
				);

				/*----------------------------------------------------------------------------------------------------*/

				MailSingleton.Attachment attachment3 = new MailSingleton.Attachment(
					commonName + ".crt",
					pem.toByteArray(),
					"application/x-pem-file"
				);

				/*----------------------------------------------------------------------------------------------------*/

				MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, "", "New AMI certificate", "Dear user,\n\nThis is your new AMI certificate. You can install \"" + commonName + ".p12\" in your web browser.\n\nBest regards.", new MailSingleton.Attachment[] {attachment1, attachment2, attachment3});

				/*----------------------------------------------------------------------------------------------------*/
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* REVOKE OLD CERTIFICATE                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		querier.executeSQLUpdate("router_authority", "UPDATE `router_authority` SET `reason` = ?0, `modified` = CURRENT_TIMESTAMP, `modifiedBy` = ?1 WHERE `vo` = ?2 AND `email` = ?3 AND `notAfter` > CURRENT_TIMESTAMP AND `reason` IS NULL",
			4, /* superseded */
			m_AMIUser,
			virtOrg,
			email
		);

		/*------------------------------------------------------------------------------------------------------------*/
		/* SAVE NEW CERTIFICATE                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		querier.executeSQLUpdate("router_authority", "INSERT INTO `router_authority` (`vo`, `clientDN`, `issuerDN`, `notBefore`, `notAfter`, `serial`, `email`, `created`, `createdBy`, `modified`, `modifiedBy`) VALUES (?0, ?1, ?2, ?3, ?4, ?5, ?6, CURRENT_TIMESTAMP, ?7, CURRENT_TIMESTAMP, ?7)",
			virtOrg,
			SecuritySingleton.getDN(pem.getX509Certificates()[0].getSubjectX500Principal()),
			SecuritySingleton.getDN(pem.getX509Certificates()[0].getIssuerX500Principal()),
			new java.sql.Date(pem.getX509Certificates()[0].getNotBefore().getTime()),
			new java.sql.Date(pem.getX509Certificates()[0].getNotAfter().getTime()),
			pem.getX509Certificates()[0].getSerialNumber().toString(10),
			email,
			m_AMIUser
		);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Generate a client or server certificates. Default validity: 1 year.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" -email=\"\" -virtOrg=\"\" -password=\"\" (-validity=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
