package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class ChangePassword extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ChangePassword(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");
		String amiPasswordOld = arguments.get("amiPasswordOld");
		String amiPasswordNew = arguments.get("amiPasswordNew");

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(amiPasswordOld, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(amiPasswordNew, Empty.STRING_NULL_EMPTY_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		UserValidator.Bean bean = new UserValidator.Bean(
			amiLogin,
			null,
			amiPasswordOld,
			amiPasswordNew,
			m_clientDN,
			m_issuerDN,
			null,
			null,
			null,
			null
		);

		boolean valid = RoleSingleton.checkUser(
			ConfigSingleton.getProperty("user_password_validator_class"),
			UserValidator.Mode.PASSWORD,
			bean
		);

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_user", "UPDATE `router_user` SET `AMIPass` = ?#2, `valid` = ?3 WHERE `AMIUser` = ?0 AND `AMIPass` = ?#1",
			bean.getAmiUsername(),
			bean.getPasswordOld(),
			bean.getPasswordNew(),
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
		return "Change user password.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-amiLogin=\"\" -amiPasswordOld=\"\" -amiPasswordNew=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
