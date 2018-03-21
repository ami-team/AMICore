package net.hep.ami.command.admin;

import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
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
		String commandClass = arguments.get("class");

		String commandVisible = arguments.containsKey("visible") ? arguments.get("visible")
		                                                         : "1"
		;

		String commandSecured = arguments.containsKey("secured") ? arguments.get("secured")
		                                                         : "0"
		;


		if(commandClass == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Class<?> clazz = Class.forName(commandClass);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00
		   ||
		   ClassSingleton.extendsClass(clazz, AbstractCommand.class) == false
		 ) {
			throw new Exception("class '" + commandClass + "' doesn't extend 'AbstractCommand'");
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
			update = querier.executeSQLUpdate("INSERT INTO `router_command` (`command`, `class`, `visible`, `secured`) VALUES (?, ?, ?, ?)",
				commandName,
				commandClass,
				commandVisible,
				commandSecured
			);
		}
		catch(Exception e)
		{
			update = querier.executeSQLUpdate("UPDATE `router_command` SET `class` = ?, `visible` = ?, `secured` = ? WHERE `command` = ?",
				commandClass,
				commandName,
				commandVisible,
				commandSecured
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
		return "-class=\"\" (-command=\"\")? (-visible=\"1\")? (-secured=\"0\")?";
	}

	/*---------------------------------------------------------------------*/
}
