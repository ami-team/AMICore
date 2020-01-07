package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class AddUserRole extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddUserRole(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String user = arguments.get("user");
		String role = arguments.get("role");

		if(user == null
		   ||
		   role == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*----------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET USER ID                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList1 = querier.executeSQLQuery("router_user", "SELECT `id` FROM `router_user` WHERE `AMIUser` = ?", user).getAll();

		if(rowList1.size() != 1)
		{
			throw new Exception("unknown user `" + user + "`");
		}

		String userID = rowList1.get(0).getValue(0);

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

		Update update = querier.executeSQLUpdate("router_user_role", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES (?, ?)",
			userID,
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
		return "Add a user role.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-user=\"\" -role=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
