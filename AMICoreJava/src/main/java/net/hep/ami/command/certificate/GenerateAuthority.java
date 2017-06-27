package net.hep.ami.command.certificate;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class GenerateAuthority extends AbstractCommand
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

		SecuritySingleton.PEM pem = SecuritySingleton.PEM.generateCA(
			4096,
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

		result.append("<rowset><row>");

		/*-----------------------------------------------------------------*/

		result.append("<field name=\"CLIENT_DN\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getSubjectX500Principal())).append("]]></field>")
		      .append("<field name=\"ISSUER_DN\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getIssuerX500Principal())).append("]]></field>")
		      .append("<field name=\"SERIAL\"><![CDATA[").append(pem.x509Certificates[0].getSerialNumber()).append("]]></field>")
		;

		result.append("<field name=\"PEM\">")
		      .append(pem.toString())
		      .append("</field>")
		;

		/*-----------------------------------------------------------------*/

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Generate a CA certificates. Default validity: 15 years.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" (-validity=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
