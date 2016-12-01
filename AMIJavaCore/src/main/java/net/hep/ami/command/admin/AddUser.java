package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddUser extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public AddUser(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");
		String amiPassword = arguments.get("amiPassword");

		String clientDN = arguments.containsKey("clientDN") ? arguments.get("clientDN")
		                                                    : ""
		;

		String issuerDN = arguments.containsKey("issuerDN") ? arguments.get("issuerDN")
		                                                    : ""
		;

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

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		amiPassword = CryptographySingleton.encrypt(amiPassword);
		clientDN = CryptographySingleton.encrypt(clientDN);
		issuerDN = CryptographySingleton.encrypt(issuerDN);

		String sql = String.format("INSERT INTO `router_user` (`AMIUser`,`AMIPass`,`clientDN`,`issuerDN`,`firstName`,`lastName`,`email`) VALUES ('%s','%s','%s','%s','%s','%s','%s')",
			amiLogin.replace("'", "''"),
			amiPassword.replace("'", "''"),
			clientDN.replace("'", "''"),
			issuerDN.replace("'", "''"),
			firstName.replace("'", "''"),
			lastName.replace("'", "''"),
			email.replace("'", "''")
		);

		int nb = transactionalQuerier.executeUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			nb > 0 ? "<info><![CDATA[done with success]]></info>"
			       : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add user.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"value\" -amiPassword=\"value\" (-clientDN=\"value\")? (-issuerDN=\"value\")? -firstName=\"value\" -lastName=\"value\" -email=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
