package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class AddRole extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddRole(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String role = arguments.get("role");

		String description = arguments.get("description");

		String roleValidatorClass = arguments.get("roleValidatorClass");

		if(role == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_role", "INSERT INTO `router_role` (`role`, `description`, `validatorClass`) VALUES (?, ?, ?)",
			role,
			description,
			roleValidatorClass
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
		return "Add a role.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-role=\"value\" (-description=\"\")? (-roleValidatorClass=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
