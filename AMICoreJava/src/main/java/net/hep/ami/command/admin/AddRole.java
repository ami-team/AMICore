package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class AddRole extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddRole(Set<String> roles, Map<String, String> arguments, long transactionId)
	{
		super(roles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String role = arguments.get("role");

		String description = arguments.get("description");

		String roleValidatorClass = arguments.get("roleValidatorClass");

		if(role == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("INSERT INTO `router_role` (`role`, `description`, `validatorClass`) VALUES (?, ?, ?)",
			role,
			description,
			roleValidatorClass
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
		return "Add a role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-role=\"value\" (-description=\"\")? (-roleValidatorClass=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
