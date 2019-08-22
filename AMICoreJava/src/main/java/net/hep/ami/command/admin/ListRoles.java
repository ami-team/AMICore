package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListRoles extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ListRoles(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		return getQuerier("self").executeSQLQuery("router_role", "SELECT `role`, `description` FROM `router_role`").toStringBuilder("roles");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "List the available roles.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
