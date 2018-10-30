package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListCommands extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListCommands(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		return CommandSingleton.listCommands();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the available commands.";
	}

	/*---------------------------------------------------------------------*/
}
