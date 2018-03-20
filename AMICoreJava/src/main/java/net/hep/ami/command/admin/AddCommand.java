package net.hep.ami.command.admin;

import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddCommand extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddCommand(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String commandName = arguments.get("command");
		String className = arguments.get("class");

		if(className == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Class<?> clazz = Class.forName(className);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00
		   ||
		   ClassSingleton.extendsClass(clazz, AbstractCommand.class) == false
		 ) {
			throw new Exception("class '" + className + "' doesn't extend 'AbstractCommand'");
		}

		/*-----------------------------------------------------------------*/

		if(commandName == null)
		{
			commandName = clazz.getSimpleName();
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		Update update;

		try
		{
			update = querier.executeSQLUpdate("INSERT INTO `router_command` (`command`, `class`) VALUES (?, ?)",
				commandName,
				className
			);
		}
		catch(Exception e)
		{
			update = querier.executeSQLUpdate("UPDATE `router_command` SET `class` = ? WHERE `command` = ?",
				className,
				commandName
			);
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add or update a command.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-class=\"\" (-command=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
