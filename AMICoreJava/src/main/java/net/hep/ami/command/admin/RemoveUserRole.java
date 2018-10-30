package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class RemoveUserRole extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveUserRole(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String user = arguments.get("user");
		String role = arguments.get("role");

		if(user == null
		   ||
		   role == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("DELETE FROM `router_user_role` WHERE `userFK` = (SELECT `id` FROM `router_user` WHERE `AMIUser` = ?) AND `roleFK` = (SELECT `id` FROM `router_role` WHERE `role` = ?)",
			user,
			role
		);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove a user role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-user=\"\" -role=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
