package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class SetUserInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SetUserInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.getOrDefault("amiLogin", m_AMIUser);
		String firstName = arguments.get("firstName");
		String lastName = arguments.get("lastName");
		String email = arguments.get("email");

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(firstName, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(lastName, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(email, Empty.STRING_NULL_EMPTY_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		if(amiLogin != m_AMIUser || !m_userRoles.contains("AMI_ADMIN"))
		{
			throw new Exception("");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		RoleSingleton.checkUser(
			ConfigSingleton.getProperty("user_info_validator_class"),
			UserValidator.Mode.INFO,
			amiLogin,
			"N/A",
			m_clientDN,
			m_issuerDN,
			firstName,
			lastName,
			email
		);

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_user", "UPDATE `router_user` SET `firstName` = ?1, `lastName` = ?2, `email` = ?3 WHERE `AMIUser` = ?0",
			amiLogin,
			firstName,
			lastName,
			email
		);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[bad user or password]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Change user information.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-amiLogin=\"\" -firstName=\"\" -lastName=\"\" -email=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
