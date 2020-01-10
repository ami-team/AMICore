package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class AddUser extends AbstractCommand
{
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

		if(amiLogin == null || (amiLogin = amiLogin.trim()).isEmpty()
		   ||
		   firstName == null || (firstName = firstName.trim()).isEmpty()
		   ||
		   lastName == null || (lastName = lastName.trim()).isEmpty()
		   ||
		   email == null || (email = email.trim()).isEmpty()
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(!arguments.containsKey("agree"))
		{
			throw new Exception("you must accept the terms and conditions");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		boolean generatedPassword;

		if(amiPassword == null || (amiPassword = amiPassword.trim()).isEmpty())
		{
			amiPassword = SecuritySingleton.generatePassword();

			generatedPassword = true;
		}
		else
		{
			generatedPassword = false;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String clientDN;
		String issuerDN;

		if(arguments.containsKey("attach"))
		{
			clientDN = m_clientDN;
			issuerDN = m_issuerDN;
		}
		else
		{
			clientDN = null;
			issuerDN = null;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		RoleSingleton.checkNewUser(
			ConfigSingleton.getProperty("user_validator_class"),
			amiLogin,
			amiPassword,
			clientDN,
			issuerDN,
			firstName,
			lastName,
			email
		);

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("router_user","INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`) VALUES (?0, ?#1, ?2, ?3, ?4, ?5, ?6)",
			amiLogin,
			amiPassword,
			!clientDN.isEmpty() ? SecuritySingleton.encrypt(clientDN) : null,
			!issuerDN.isEmpty() ? SecuritySingleton.encrypt(issuerDN) : null,
			firstName,
			lastName,
			email
		);

		/*------------------------------------------------------------------------------------------------------------*/

		querier.executeSQLUpdate("router_user_role", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?0), (SELECT `id` FROM `router_role` WHERE `role` = ?1))",
			amiLogin,
			"AMI_USER"
		);

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			MailSingleton.sendMessage(
				ConfigSingleton.getProperty("admin_email"),
				email,
				null,
				"New AMI account",
				!generatedPassword ? String.format(SHORT_EMAIL, /*-*/ amiLogin /*-*/)
				                   : String.format(LONG_EMAIL, amiLogin, amiPassword)
			);
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
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
		return "-amiLogin=\"\" (-amiPassword=\"\")? (-clientDN=\"\")? (-issuerDN=\"\")? -firstName=\"\" -lastName=\"\" -email=\"\" -agree";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
