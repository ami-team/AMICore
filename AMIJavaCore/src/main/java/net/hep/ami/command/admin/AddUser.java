package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class AddUser extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private static final UserValidatorInterface m_userValidator = _getUserValidator();

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	static UserValidatorInterface _getUserValidator()
	{
		/*-----------------------------------------------------------------*/
		/* GET USER VALIDATOR CLASS NAME                                   */
		/*-----------------------------------------------------------------*/

		String userValidatorClass = ConfigSingleton.getProperty("user_validator_class", null);

		/*-----------------------------------------------------------------*/
		/* GET USER VALIDATOR INSTANCE                                     */
		/*-----------------------------------------------------------------*/

		if(userValidatorClass != null)
		{
			try
			{
				Class<UserValidatorInterface> clazz = (Class<UserValidatorInterface>) Class.forName(userValidatorClass);

				if(ClassFinder.extendsClass(clazz, UserValidatorInterface.class))
				{
					return clazz.newInstance();
				}

			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*-----------------------------------------------------------------*/
		/* PERMISSIVE CASE                                                 */
		/*-----------------------------------------------------------------*/

		return new UserValidatorInterface() {

			@Override
			public boolean check(
				String AMIUser,
				String AMIPass,
				String clientDN,
				String issuerDN,
				String firstName,
				String lastName,
				String email
			 ) {
				return true;
			}
		};

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String m_AMIUser;
	private String m_AMIPass;
	private String m_clientDN;
	private String m_issuerDN;
	private String m_firstName;
	private String m_lastName;
	private String m_email;

	/*---------------------------------------------------------------------*/

	public AddUser(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);

		m_AMIUser = arguments.get("amiLogin");
		m_AMIPass = arguments.get("amiPassword");

		m_clientDN = arguments.containsKey("clientDN") ? arguments.get("clientDN")
		                                               : ""
		;

		m_issuerDN = arguments.containsKey("issuerDN") ? arguments.get("issuerDN")
		                                               : ""
		;

		m_firstName = arguments.get("firstName");
		m_lastName = arguments.get("lastName");
		m_email = arguments.get("email");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_AMIUser == null
		   ||
		   m_AMIPass == null
		   ||
		   m_firstName == null
		   ||
		   m_lastName == null
		   ||
		   m_email == null
		 ) {
			throw new Exception("invalid usage");
		}

		if(m_userValidator.check(
			m_AMIUser,
			m_AMIPass,
			m_clientDN,
			m_issuerDN,
			m_firstName,
			m_lastName,
			m_email
		  ) == false
		 ) {
			throw new Exception("not allowed");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		m_AMIPass = Cryptography.encrypt(m_AMIPass);
		m_clientDN = Cryptography.encrypt(m_clientDN);
		m_issuerDN = Cryptography.encrypt(m_issuerDN);

		String sql = String.format("INSERT INTO `router_user` (`AMIUser`,`AMIPass`,`clientDN`,`issuerDN`,`firstName`,`lastName`,`email`) VALUES ('%s','%s','%s','%s','%s','%s','%s')",
			m_AMIUser.replace("'", "''"),
			m_AMIPass.replace("'", "''"),
			m_clientDN.replace("'", "''"),
			m_issuerDN.replace("'", "''"),
			m_firstName.replace("'", "''"),
			m_lastName.replace("'", "''"),
			m_email.replace("'", "''")
		);

		transactionalQuerier.executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add new user.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"value\" -amiPassword=\"value\" (-clientDN=\"value\")? (-issuerDN=\"value\")? -firstName=\"value\" -lastName=\"value\" -email=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
