package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class RemoveUser extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RemoveUser(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_user", "DELETE FROM `router_user` WHERE `AMIUser` = ?0", amiLogin);

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
		return "Remove a user.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-amiLogin=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
