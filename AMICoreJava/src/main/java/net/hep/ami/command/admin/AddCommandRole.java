package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class AddCommandRole extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddCommandRole(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET COMMAND ID                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList1 = querier.executeSQLQuery("router_command", "SELECT `id` FROM `router_command` WHERE `command` = ?", command).getAll();

		if(rowList1.size() != 1)
		{
			throw new Exception("unknown command `" + command + "`");
		}

		String commandID = rowList1.get(0).getValue(0);

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET ROLE ID                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList2 = querier.executeSQLQuery("router_role", "SELECT `id` FROM `router_role` WHERE `role` = ?", role).getAll();

		if(rowList2.size() != 1)
		{
			throw new Exception("unknown role `" + role + "`");
		}

		String roleID = rowList2.get(0).getValue(0);

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD ROLE                                                                                                   */
		/*------------------------------------------------------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES (?, ?)",
			commandID,
			roleID
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
		return "Add a command role.";
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
