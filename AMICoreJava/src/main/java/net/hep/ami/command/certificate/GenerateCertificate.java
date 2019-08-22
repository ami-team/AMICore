package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_CERT", visible = false, secured = true)
public class GenerateCertificate extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GenerateCertificate(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		StringBuilder result = new StringBuilder();

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

		String fileName = ConfigSingleton.getConfigPathName() + File.separator + "ca.pem";

		/*------------------------------------------------------------------------------------------------------------*/

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

		KeyStore keyStore_JKS = SecuritySingleton.generateJKSKeyStore(pem.privateKeys[0], pem.x509Certificates, password.toCharArray());
		KeyStore keyStore_PKCS12 = SecuritySingleton.generatePKCS12KeyStore(pem.privateKeys[0], pem.x509Certificates, password.toCharArray());

		keyStore_JKS.setCertificateEntry("AMI-CA", caCrt);
		keyStore_PKCS12.setCertificateEntry("AMI-CA", caCrt);

		/*------------------------------------------------------------------------------------------------------------*/

		try(ByteArrayOutputStream output1 = new ByteArrayOutputStream())
		{
			keyStore_JKS.store(output1, password.toCharArray());

			try(ByteArrayOutputStream output2 = new ByteArrayOutputStream())
			{
				keyStore_PKCS12.store(output2, password.toCharArray());

				result.append("<rowset type=\"certificates\">")
				      .append("<row>")
				      .append("<field name=\"client_dn\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getSubjectX500Principal())).append("]]></field>")
				      .append("<field name=\"issuer_dn\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getIssuerX500Principal())).append("]]></field>")
				      .append("<field name=\"serial\"><![CDATA[").append(pem.x509Certificates[0].getSerialNumber()).append("]]></field>")
				      .append("<field name=\"pem\">").append(pem.toString()).append("</field>")
				      .append("<field name=\"keystore_jks\">").append(SecuritySingleton.byteArrayToBase64String(output1.toByteArray())).append("</field>")
				      .append("<field name=\"keystore_p12\">").append(SecuritySingleton.byteArrayToBase64String(output2.toByteArray())).append("</field>")
				      .append("</row>")
				      .append("</rowset>")
				;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Generate a client or server certificate. Default validity: 1 year.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" (-email=\"\")? (-virtOrg=\"\")? -password=\"\" (-validity=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
