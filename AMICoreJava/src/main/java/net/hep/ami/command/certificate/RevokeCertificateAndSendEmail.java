package net.hep.ami.command.certificate;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_USER", visible = false, secured = false)
public class RevokeCertificateAndSendEmail extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RevokeCertificateAndSendEmail(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String email = arguments.getOrDefault("email", "");
		String virtOrg = arguments.getOrDefault("virtOrg", "");
		String reason = arguments.getOrDefault("reason", "");
		String code = arguments.getOrDefault("code", "");

		if(email.isEmpty()
		   ||
		   virtOrg.isEmpty()
		   ||
		   (code.isEmpty() != reason.isEmpty())
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rows = getQuerier("self").executeSQLQuery("router_authority", "SELECT `clientDN`, `serial` FROM `router_authority` WHERE `vo` = ? AND `email` = ? AND `notAfter` > CURRENT_TIMESTAMP AND `reason` IS NULL", virtOrg, email).getAll();

		if(rows.size() == 0)
		{
			throw new Exception("no certificate found for email `" + email + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String dns = rows.stream().map(x -> {

			try
			{
				return x.getValue(0) + " (serial: " + x.getValue(1) + ")";
			}
			catch(Exception e)
			{
				return "N∕A";
			}

		}).collect(Collectors.joining("\n"));

		/*------------------------------------------------------------------------------------------------------------*/

		final String CODE = SecuritySingleton.md5Sum(
			Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
			+ "::" +
			SecuritySingleton.encrypt(email)
		);

		/*------------------------------------------------------------------------------------------------------------*/

		if(code.isEmpty())
		{
			MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, null, "AMI certificate revocation", "Dear user,\n\nYou are about to revoke the following AMI certificate(s):\n\n" + dns + "\n\nConfirmation code: " + CODE + "\n\nBest regards.");
		}
		else
		{
			if(CODE.equals(code))
			{
				MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, null, "AMI certificate revocation", "Dear user,\n\nThe following certificate(s) is(are) revoked:\n\n" + dns + "\n\nBest regards.");

				getQuerier("self").executeSQLUpdate("UPDATE `router_authority` SET `reason` = ?, `modified` = CURRENT_TIMESTAMP, `modifiedBy` = ? WHERE `vo` = ? AND `email` = ? AND `notAfter` > CURRENT_TIMESTAMP AND `reason` IS NULL",
					reason,
					m_AMIUser,
					virtOrg,
					email
				);
			}
			else
			{
				throw new Exception("invalid confirmation code");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Revoke client or server certificate(s).";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-email=\"\" (-vo=\"\" -reason=\"\" -code=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
