package net.hep.ami.command.certificate;

import java.io.*;
import java.math.*;
import java.util.*;

import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class GenerateRevocationList extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GenerateRevocationList(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		java.sql.ResultSet resultSet = getQuerier("self").executeSQLQuery("router_authority", "SELECT `serial`, `reason`, `modified` FROM `router_authority` WHERE `reason` IS NOT NULL").getResultSet();

		/*------------------------------------------------------------------------------------------------------------*/

		List<SecuritySingleton.Revocation> revocations = new ArrayList<>();

		while(resultSet.next())
		{
			revocations.add(new SecuritySingleton.Revocation(
				new BigInteger(resultSet.getString(1)),
				resultSet.getInt(2),
				resultSet.getDate(3)
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		SecuritySingleton.PEM pem = SecuritySingleton.PEM.generateCRL(caKey, caCrt, revocations);

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"revocation_lists\">")
		      .append("<row>")
		      .append("<field name=\"pem\">").append(pem.toString()).append("</field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Generate a certificate revocation list.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
