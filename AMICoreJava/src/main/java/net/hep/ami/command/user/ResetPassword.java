package net.hep.ami.command.user;

import java.util.*;
import java.nio.charset.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.parser.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class ResetPassword extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	static final String EMAIL = "%s?subapp=resetPassword&userdata=%s";

	/*---------------------------------------------------------------------*/

	public ResetPassword(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");

		if(amiLogin == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		List<Row> rowList = getQuerier("self").executeSQLQuery(true, "SELECT `AMIUser`, `AMIPass`, `email` FROM `router_user` WHERE `AMIUser` = ? AND `valid` != 0", amiLogin).getAll(10, 0);

		if(rowList.size() > 0)
		{
			Row row = rowList.get(0);

			String tmpUser = /*---------------------*/(row.getValue(0));
			String tmpPass = SecuritySingleton.decrypt(row.getValue(1));
			String  email  = /*---------------------*/(row.getValue(2));

			String userdata = "{\"user\": \"" + Utility.escapeJavaString(tmpUser) + "\", \"old_pass\": \"" + SecuritySingleton.buildTmpPassword(tmpUser, tmpPass) + "\"}";

			userdata = new String(org.bouncycastle.util.encoders.Base64.encode(userdata.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

			MailSingleton.sendMessage(
				ConfigSingleton.getProperty("admin_email"),
				email,
				null,
				"Reset AMI password",
				String.format(EMAIL, ConfigSingleton.getProperty("host"), userdata)
			);
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[email sent with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Reset password.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
