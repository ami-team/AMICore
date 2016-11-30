package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class RemoveRole extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public RemoveRole(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
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

		RoleSingleton.removeRole(getQuerier("self"), role);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-role=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
