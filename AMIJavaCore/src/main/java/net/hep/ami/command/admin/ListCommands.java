package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.CommandAbstractClass;

public class ListCommands extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListCommands(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		return CommandSingleton.listCommands();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List commands.";
	}

	/*---------------------------------------------------------------------*/
}
