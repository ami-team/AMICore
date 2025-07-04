package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = false)
public class AddUser extends AbstractCommand
{
	/*------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(AddUser.class.getSimpleName());

	/*------------------------------------------------------------------------------------------------------------*/

	static final String SHORT_EMAIL = "%s";

	static final String LONG_EMAIL = "%s %s";

	/*------------------------------------------------------------------------------------------------------------*/

	public AddUser(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");
		String amiPassword = arguments.get("amiPassword");
		String firstName = arguments.get("firstName");
		String lastName = arguments.get("lastName");
		String email = arguments.get("email");
		String captchaHash = arguments.get("captchaHash");
		String captchaText = arguments.get("captchaText");

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(firstName, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(lastName, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(email, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(captchaHash, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(captchaText, Empty.STRING_NULL_EMPTY_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(!arguments.containsKey("agree"))
		{
			throw new Exception("you must accept the terms and conditions");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(!CaptchaSingleton.checkCaptcha(captchaHash, captchaText))
		{
			throw new Exception("invalid captcha verification");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		boolean generatedPassword = Empty.is(amiPassword, Empty.STRING_NULL_EMPTY_BLANK);

		if(generatedPassword)
		{
			amiPassword = SecuritySingleton.generatePassword();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		UserValidator.Bean bean = new UserValidator.Bean(
			amiLogin,
			null,
			amiPassword,
			amiPassword,
			arguments.containsKey("attachCert") && !Empty.is(m_clientDN, Empty.STRING_NULL_EMPTY_BLANK) ? m_clientDN : null,
			arguments.containsKey("attachCert") && !Empty.is(m_issuerDN, Empty.STRING_NULL_EMPTY_BLANK) ? m_issuerDN : null,
			firstName,
			lastName,
			email,
			"{}"
		);

		/*------------------------------------------------------------------------------------------------------------*/

		boolean valid = RoleSingleton.checkUser(
			ConfigSingleton.getProperty("new_user_validator_class"),
			UserValidator.Mode.ADD,
			bean
		);

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("router_user", "INSERT INTO `router_user` (`AMIUser`, `ssoUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `json`, `valid`) VALUES (?0, ?1, ?^2, ?#3, ?#4, ?5, ?6, ?7, ?8, ?9)",
			bean.getAmiUsername(),
			bean.getSsoUsername(),
			bean.getPasswordNew(),
			bean.getClientDN(),
			bean.getIssuerDN(),
			bean.getFirstName(),
			bean.getLastName(),
			bean.getEmail(),
			bean.getJson(),
			valid ? 1 : 0
		);

		/*------------------------------------------------------------------------------------------------------------*/

		querier.executeSQLUpdate("router_user_role", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?0), (SELECT `id` FROM `router_role` WHERE `role` = ?1))",
			bean.getAmiUsername(),
			"AMI_USER"
		);

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			MailSingleton.sendMessage(
				ConfigSingleton.getProperty("admin_email"),
				bean.getEmail(),
				null,
				"New AMI account",
				!generatedPassword ? String.format(SHORT_EMAIL, /*--------*/ bean.getAmiUsername() /*--------*/)
				                   : String.format(LONG_EMAIL, bean.getAmiUsername(), bean.getPasswordNew())
			);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Add a user.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-amiLogin=\"\" (-amiPassword=\"\")? -firstName=\"\" -lastName=\"\" -email=\"\" (-attachCert)? -agree";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
