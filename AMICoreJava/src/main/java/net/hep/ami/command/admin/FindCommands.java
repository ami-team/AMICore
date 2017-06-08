package net.hep.ami.command.admin;

import java.lang.reflect.Modifier;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.Querier;

public class FindCommands extends AbstractCommand
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

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		Set<String> commands = new HashSet<>();

		for(String className: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			try
			{
				/*---------------------------------------------------------*/

				Class<?> clazz = Class.forName(className);

				if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00
				   ||
				   ClassSingleton.extendsClass(clazz, AbstractCommand.class) == false
				 ) {
					continue;
				}

				/*---------------------------------------------------------*/

				querier.executeSQLUpdate("INSERT INTO `router_command` (`command`, `class`) VALUES (?, ?)",
					clazz.getSimpleName(),
					clazz.getName()
				);

				/*---------------------------------------------------------*/

				commands.add(className);

				/*---------------------------------------------------------*/
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*-----------------------------------------------------------------*/

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
