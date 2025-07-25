package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true)
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
		String firstName = arguments.get("firstName");
		String lastName = arguments.get("lastName");
		String email = arguments.get("email");

		if(Empty.is(firstName, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(lastName, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(email, Empty.STRING_NULL_EMPTY_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(!m_userRoles.contains("AMI_ADMIN"))
		{
			throw new Exception("invalid operation for guest user");
		}
		/*------------------------------------------------------------------------------------------------------------*/

		UserValidator.Bean bean = new UserValidator.Bean(
			m_AMIUser,
			null,
			null,
			null,
			m_clientDN,
			m_issuerDN,
			firstName,
			lastName,
			email,
			null
		);

		/*------------------------------------------------------------------------------------------------------------*/

		boolean valid = RoleSingleton.checkUser(
			ConfigSingleton.getProperty("user_info_validator_class"),
			UserValidator.Mode.INFO,
			bean
		);

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_user", "UPDATE `router_user` SET `firstName` = ?1, `lastName` = ?2, `email` = ?3, `valid` = ?4 WHERE `AMIUser` = ?0",
			bean.getAmiUsername(),
			bean.getFirstName(),
			bean.getLastName(),
			bean.getEmail(),
			valid ? 1 : 0
		);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[bad user and/or password]]></error>"
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
		return "-firstName=\"\" -lastName=\"\" -email=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
