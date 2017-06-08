package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class ListCommands extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListCommands(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
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
