package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class AddUser extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddUser(Set<String> roles, Map<String, String> arguments, long transactionId)
	{
		super(roles, arguments, transactionId);
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

		if(amiLogin == null
		   ||
		   amiPassword == null
		   ||
		   firstName == null
		   ||
		   lastName == null
		   ||
		   email == null
		 ) {
			throw new Exception("invalid usage");
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
			clientDN = "";
			issuerDN = "";
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

		Update update = getQuerier("self").executeSQLUpdate("INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`) VALUES (?, ?, ?, ?, ?, ?, ?)",
			amiLogin,
			SecuritySingleton.encrypt(amiPassword),
			clientDN.isEmpty() == false ? SecuritySingleton.encrypt(clientDN) : null,
			issuerDN.isEmpty() == false ? SecuritySingleton.encrypt(issuerDN) : null,
			firstName,
			lastName,
			email
		);

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
		return "-amiLogin=\"\" -amiPassword=\"\" (-clientDN=\"\")? (-issuerDN=\"\")? -firstName=\"\" -lastName=\"\" -email=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
