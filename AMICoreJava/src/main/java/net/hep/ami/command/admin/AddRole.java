package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class AddRole extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddRole(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String parent = arguments.containsKey("parent") ? arguments.get("parent")
		                                                : "AMI_GUEST"
		;

		String role = arguments.get("role");

		String roleValidatorClass = arguments.containsKey("roleValidatorClass") ? arguments.get("roleValidatorClass")
		                                                                        : ""
		;

		boolean insertAfter = arguments.containsKey("insertAfter");

		if(role == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		RoleSingleton.addRole(getQuerier("self"), parent, role, roleValidatorClass, insertAfter);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-parent=\"value\")? -role=\"value\" (-roleValidatorClass=\"value\" -insertAfter)?";
	}

	/*---------------------------------------------------------------------*/
}
