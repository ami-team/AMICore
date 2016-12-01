package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class FindCommands extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public FindCommands(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		Set<String> commands = new HashSet<String>();

		transactionalQuerier.executeUpdate("DELETE FROM `router_command`");

		for(String className: ClassFinder.findClassNames("net.hep.ami.command"))
		{
			if(CommandSingleton.registerCommand(transactionalQuerier, className))
			{
				commands.add(className);
			}
		}

		CommandSingleton.reload();

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, " + commands + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Automatically find commands.";
	}

	/*---------------------------------------------------------------------*/
}
