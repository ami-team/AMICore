package net.hep.ami.command.certificate;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = false, secured = false)
public class RevokeCertificateAndSendEmail extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RevokeCertificateAndSendEmail(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String email = arguments.get("email");
		String reason = arguments.get("reason");
		String code = arguments.get("code");

		if(((email == null || email.isEmpty())) || (
			(reason == null || reason.isEmpty())
			!=
			(code == null || code.isEmpty())
		 )) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		List<Row> rows = getQuerier("self").executeSQLQuery("SELECT `clientDN`, `serial` FROM `router_authority` WHERE `email` = ? AND `notAfter` > CURRENT_TIMESTAMP AND `reason` IS NULL", email).getAll();

		if(rows.size() == 0)
		{
			throw new Exception("unknown email");
		}

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

		final String CODE = SecuritySingleton.md5Sum(
			Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
			+ "::" +
			SecuritySingleton.encrypt(email)
		);

		/*-----------------------------------------------------------------*/

		if(code == null)
		{
			MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, "", "AMI certificate revocation", "Hi,\n\nYou are about to revoke the following AMI certificate(s):\n\n" + dns + "\n\nConfirmation code: " + CODE);
		}
		else
		{
			if(CODE.equals(code))
			{
				getQuerier("self").executeSQLQuery("UPDATE `router_authority` SET `reason` = ?, `modified` = CURRENT_TIMESTAMP, `modifiedBy` = ?) WHERE `email` = ? AND `notAfter` > CURRENT_TIMESTAMP AND  `reason` IS NULL", reason, m_AMIUser);

				MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, "", "AMI certificate revocation", "Hi,\n\nThe following certivicate(s) has been revoked:\n\n" + dns);
			}
			else
			{
				getQuerier("self").executeSQLQuery("UPDATE `router_authority` SET `modified` = CURRENT_TIMESTAMP, `modifiedBy` = ?) WHERE `email` = ? AND `notAfter` > CURRENT_TIMESTAMP AND  `reason` IS NULL", reason, m_AMIUser);

				MailSingleton.sendMessage(ConfigSingleton.getProperty("admin_email"), email, "", "AMI certificate revocation", "Hi,\n\nInvalid confirmation code: \n\n" + code);
			}
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Revoke client or server certificate(s).";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-email=\"\" (-reason=\"\" -code=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
