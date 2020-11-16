package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
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
		String commandName = ConfigSingleton.getProperty("user_override_change_password");

		if(!Empty.is(commandName, Empty.STRING_EMPTY | Empty.STRING_AMI_NULL | Empty.STRING_EMPTY | Empty.STRING_BLANK))
		{
			return executeCommand(commandName, arguments);
		}

		/**/

		String amiLogin = arguments.get("amiLogin");
		String amiPasswordOld = arguments.get("amiPasswordOld");
		String amiPasswordNew = arguments.get("amiPasswordNew");

		if(Empty.is(amiLogin, Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(amiPasswordOld, Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(amiPasswordNew, Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_user", "UPDATE `router_user` SET `AMIPass` = ?#2 WHERE `AMIUser` = ?0 AND `AMIPass` = ?#1",
			amiLogin,
			amiPasswordOld,
			amiPasswordNew
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
		return "Change password.";
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
