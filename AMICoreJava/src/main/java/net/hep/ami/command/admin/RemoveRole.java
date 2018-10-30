package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class RemoveRole extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveRole(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String role = arguments.get("role");

		if(role == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("DELETE FROM `router_role` WHERE `role` = ?",
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
		return "Remove a role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-role=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
