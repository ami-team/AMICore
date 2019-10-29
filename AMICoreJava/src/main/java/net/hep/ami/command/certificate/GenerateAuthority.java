package net.hep.ami.command.certificate;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_CERT", visible = false, secured = true)
public class GenerateAuthority extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GenerateAuthority(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		String country = arguments.getOrDefault("country", "").trim();
		String locality = arguments.getOrDefault("locality", "").trim();
		String organization = arguments.getOrDefault("organization", "").trim();
		String organizationalUnit = arguments.getOrDefault("organizationalUnit", "").trim();
		String commonName = arguments.getOrDefault("commonName", "").trim();

		if(country.isEmpty()
		   ||
		   locality.isEmpty()
		   ||
		   organization.isEmpty()
		   ||
		   organizationalUnit.isEmpty()
		   ||
		   commonName.isEmpty()
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
				validity = 15;
			}
		}
		else
		{
			validity = 15;
		}

		/*------------------------------------------------------------------------------------------------------------*/

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
			arguments.get("email"),
			validity
		);

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"certificates\">")
		      .append("<row>")
		      .append("<field name=\"client_dn\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getSubjectX500Principal())).append("]]></field>")
		      .append("<field name=\"issuer_dn\"><![CDATA[").append(SecuritySingleton.getDN(pem.x509Certificates[0].getIssuerX500Principal())).append("]]></field>")
		      .append("<field name=\"serial\"><![CDATA[").append(pem.x509Certificates[0].getSerialNumber()).append("]]></field>")
		      .append("<field name=\"pem\">").append(pem.toString()).append("</field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Generate a CA certificate. Default validity: 15 years.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-country=\"\" -locality=\"\" -organization=\"\" -organizationalUnit=\"\" -commonName=\"\" (-email=\"\")? (-validity=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
