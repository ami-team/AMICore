package net.hep.ami.command.admin;

import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class AddCommand extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddCommand(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String commandName = arguments.get("command");
		String commandClass = arguments.get("class");

		String commandVisible = arguments.get("visible");
		String commandSecured = arguments.get("secured");

		if(commandClass == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(commandClass);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00
		   ||
		   !ClassSingleton.extendsClass(clazz, AbstractCommand.class)
		 ) {
			throw new Exception("class '" + commandClass + "' doesn't extend 'AbstractCommand'");
		}

		if(commandName == null)
		{
			commandName = clazz.getSimpleName();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		CommandMetadata commandMetadata = clazz.getAnnotation(CommandMetadata.class);

		if(commandMetadata != null)
		{
			if(commandVisible == null) {
				commandVisible = commandMetadata.visible() ? "1" : "0";
			}
		}
		else
		{
			if(commandVisible == null) {
				commandVisible = "1";
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		Update update;

		try
		{
			update = querier.executeSQLUpdate("router_command", "INSERT INTO `router_command` (`command`, `class`, `visible`, `secured`) VALUES (?0, ?1, ?2, ?3)",
				commandName,
				commandClass,
				commandVisible,
				commandSecured
			);
		}
		catch(Exception e)
		{
			update = querier.executeSQLUpdate("router_command", "UPDATE `router_command` SET `class` = ?1, `visible` = ?2, `secured` = ?3 WHERE `command` = ?0",
				commandName,
				commandClass,
				commandVisible,
				commandSecured
			);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Add or update a command.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-class=\"\" (-command=\"\")? (-visible=\"1\")? (-secured=\"0\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
