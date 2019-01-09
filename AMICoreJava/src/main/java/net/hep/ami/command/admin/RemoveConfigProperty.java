package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = true)
public class RemoveConfigProperty extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveConfigProperty(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String name = arguments.get("name");

		if(name == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		ConfigSingleton.removeProperty(name);

		int nb = ConfigSingleton.removePropertyInDataBase(getQuerier("self"), name);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			nb > 0 ? "<info><![CDATA[done with success]]></info>"
			       : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove a global configuration property.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-name=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
