package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

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

		for(String className: ClassSingleton.findClassNames("net.hep.ami.command"))
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
