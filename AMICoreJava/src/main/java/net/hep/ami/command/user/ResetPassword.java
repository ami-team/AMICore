package net.hep.ami.command.user;

import java.util.*;
import java.nio.charset.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class ResetPassword extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	static final String EMAIL = "%s?subapp=resetPassword&userdata=%s";

	/*----------------------------------------------------------------------------------------------------------------*/

	public ResetPassword(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");
		String captchaHash = arguments.get("captchaHash");
		String captchaText = arguments.get("captchaText");

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(captchaHash, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(captchaText, Empty.STRING_NULL_EMPTY_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(!CaptchaSingleton.checkCaptcha(captchaHash, captchaText))
		{
			throw new Exception("invalid captcha verification");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList = getQuerier("self").executeSQLQuery("router_user", "SELECT `AMIUser``email` FROM `router_user` WHERE `AMIUser` = ?0 AND `valid` != 0", amiLogin).getAll(10, 0);

		if(rowList.size() > 0)
		{
			Row row = rowList.get(0);

			String tmpUser = row.getValue(0);
			String  email  = row.getValue(1);

			String userdata = "{\"user\": \"" + Utility.escapeJavaString(tmpUser) + "\", \"old_pass\": \"" + SecuritySingleton.generateTmpPassword(tmpUser) + "\"}";

			userdata = new String(org.bouncycastle.util.encoders.UrlBase64.encode(userdata.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

			MailSingleton.sendMessage(
				ConfigSingleton.getProperty("admin_email"),
				email,
				null,
				"Reset AMI password",
				String.format(EMAIL, ConfigSingleton.getProperty("base_url"), userdata)
			);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[email sent with success]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Reset password.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-amiLogin=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
