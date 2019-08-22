package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListUsers extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ListUsers(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		return getQuerier("self").executeSQLQuery("router_user", "SELECT `AMIUser`, `firstName`, `lastName`, `email`, `country`, `valid` FROM `router_user`").toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "List the registered users.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
