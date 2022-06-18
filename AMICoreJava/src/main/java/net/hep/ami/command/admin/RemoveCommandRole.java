package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class RemoveCommandRole extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RemoveCommandRole(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String command = arguments.get("command");
		String role = arguments.get("role");

		if(command == null
		   ||
		   role == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_command_role", "DELETE FROM `router_command_role` WHERE `commandFK` = (SELECT `id` FROM `router_command` WHERE `command` = ?0) AND `roleFK` = (SELECT `id` FROM `router_role` WHERE `role` = ?1)",
			command,
			role
		);

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
		return "Remove a command role.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-command=\"\" -role=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
