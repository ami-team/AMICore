package net.hep.ami.command.certificate;

import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class GenerateAuthority extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public GenerateAuthority(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
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

		int m_validity;

		if(arguments.containsKey("validity"))
		{
			try
			{
				m_validity = Integer.parseInt(arguments.get("validity"));
			}
			catch(NumberFormatException e)
			{
				m_validity = 15;
			}
		}
		else
		{
			m_validity = 15;
		}

		/*-----------------------------------------------------------------*/

		KeyPair keyPair = SecuritySingleton.generateKeyPair(4096);

		X509Certificate certificate = SecuritySingleton.generateCA(
			keyPair.getPrivate(),
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

		/*-----------------------------------------------------------------*/

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Generate CA certificates.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-country=\"value\" -locality=\"value\" -organization=\"value\" -organizationalUnit=\"value\" -commonName=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
