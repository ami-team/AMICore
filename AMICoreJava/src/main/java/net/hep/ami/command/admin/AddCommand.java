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

	public AddCommand(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String commandName = arguments.get("command");
		String commandClass = arguments.get("class");

		String commandVisible = arguments.get("visible");
		String commandSecured = arguments.get("secured");

		if(commandClass == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(commandClass);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00
		   ||
		   ClassSingleton.extendsClass(clazz, AbstractCommand.class) == false
		 ) {
			throw new Exception("class '" + commandClass + "' doesn't extend 'AbstractCommand'");
		}

		if(commandName == null)
		{
			commandName = clazz.getSimpleName();
		}

		/*-----------------------------------------------------------------*/

		CommandMetadata commandMetadata = clazz.getAnnotation(CommandMetadata.class);

		if(commandMetadata != null)
		{
			if(commandVisible == null) {
				commandVisible = commandMetadata.visible() ? "1" : "0";
			}

			if(commandSecured == null) {
				commandSecured = commandMetadata.secured() ? "1" : "0";
			}
		}
		else
		{
			if(commandVisible == null) {
				commandVisible = "1";
			}

			if(commandSecured == null) {
				commandSecured = "0";
			}
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
