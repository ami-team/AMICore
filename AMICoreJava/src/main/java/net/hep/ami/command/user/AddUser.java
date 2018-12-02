package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class AddUser extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	static final String SHORT_EMAIL = "%s";

	static final String LONG_EMAIL = "%s %s";

	/*---------------------------------------------------------------------*/

	public AddUser(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
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

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`) VALUES (?, ?, ?, ?, ?, ?, ?)",
			amiLogin,
			SecuritySingleton.encrypt(amiPassword),
			clientDN != null && clientDN.isEmpty() == false ? SecuritySingleton.encrypt(clientDN) : null,
			issuerDN != null && issuerDN.isEmpty() == false ? SecuritySingleton.encrypt(issuerDN) : null,
			firstName,
			lastName,
			email
		);

		/*-----------------------------------------------------------------*/

		querier.executeSQLUpdate("INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", amiLogin, "AMI_USER");

		/*-----------------------------------------------------------------*/

		try
		{
			MailSingleton.sendMessage(
				ConfigSingleton.getProperty("admin_email"),
				email,
				null,
				"New AMI account",
				generatedPassword == false ? String.format(SHORT_EMAIL, /*--*/ amiLogin /*--*/)
				                           : String.format(LONG_EMAIL, amiLogin, amiPassword)
			);
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add a user.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"\" (-amiPassword=\"\")? (-clientDN=\"\")? (-issuerDN=\"\")? -firstName=\"\" -lastName=\"\" -email=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
