package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

public class AddUser extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_login;
	private String m_password;
	private String m_firstName;
	private String m_lastName;
	private String m_email;

	/*---------------------------------------------------------------------*/

	private static final String m_emptyDN = Cryptography.encrypt("");

	/*---------------------------------------------------------------------*/

	public AddUser(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_login = arguments.get("amiLogin");
		m_password = arguments.get("amiPassword");
		m_firstName = arguments.get("firstName");
		m_lastName = arguments.get("lastName");
		m_email = arguments.get("email");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_login.isEmpty()
		   ||
		   m_password.isEmpty()
		   ||
		   m_firstName.isEmpty()
		   ||
		   m_lastName.isEmpty()
		   ||
		   m_email.isEmpty()
		 ) {
			throw new Exception("invalid usage");
		}

		m_password = Cryptography.encrypt(m_password);

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* ADD USER                                                        */
		/*-----------------------------------------------------------------*/

		String sql = String.format("INSERT INTO `router_user` (`AMIUser`,`AMIPass`,`clientDN`,`issuerDN`,`firstName`,`lastName`,`email`) VALUES ('%s','%s','%s','%s','%s','%s','%s')",
			m_login.replace("'", "''"),
			m_password,
			m_emptyDN,
			m_emptyDN,
			m_firstName.replace("'", "''"),
			m_lastName.replace("'", "''"),
			m_email.replace("'", "''")
		);

		transactionalQuerier.executeUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Add new user.";
	}

	/*---------------------------------------------------------------------*/
}
