package net.hep.ami.command.admin;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.command.user.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class FindNewCommands extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(AddUser.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	public FindNewCommands(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		ClassSingleton.reload();

		/*------------------------------------------------------------------------------------------------------------*/

		RouterQuerier querier = new RouterQuerier();

		/*------------------------------------------------------------------------------------------------------------*/

		String commandName;
		String commandRole;

		int commandVisible;

		Set<String> foundCommandNames = new HashSet<>();

		Set<String> existingCommandNames = CommandSingleton.getCommandNames();

		for(String commandClass: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			try
			{
				Class<?> clazz = ClassSingleton.forName(commandClass);

				CommandMetadata commandMetadata = clazz.getAnnotation(CommandMetadata.class);

				if(commandMetadata != null
						&&
						(clazz.getModifiers() & Modifier.ABSTRACT) == 0x00
						&&
						ClassSingleton.extendsClass(clazz, AbstractCommand.class)
						&&
						!existingCommandNames.contains(commandName = clazz.getSimpleName())
				) {
					/*------------------------------------------------------------------------------------------------*/

					commandRole = commandMetadata.role();

					commandVisible = commandMetadata.visible() ? 1 : 0;

					/*------------------------------------------------------------------------------------------------*/

					LOG.info("Installing command {} (class: {}, visible: {}, role: {})", commandName, commandClass, commandVisible, commandRole);

					/*------------------------------------------------------------------------------------------------*/

					querier.executeSQLUpdate("router_command", "DELETE FROM `router_command` WHERE `command` = ?", commandName);
					querier.executeSQLUpdate("router_command", "INSERT INTO `router_command` (`command`, `class`, `visible`) VALUES (?, ?, ?)", commandName, commandClass, commandVisible);
					querier.executeSQLUpdate("router_command_role", "DELETE FROM `router_command_role` WHERE `commandFK` = (SELECT `id` FROM `router_command` WHERE `command` = ?)", commandName);
					querier.executeSQLUpdate("router_command_role", "INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", commandName, commandRole);

					/*------------------------------------------------------------------------------------------------*/

					foundCommandNames.add(commandName);

					/*------------------------------------------------------------------------------------------------*/
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(foundCommandNames.size() > 0)
		{
			CommandSingleton.reload();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, added command(s): " + foundCommandNames + "]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/


	@NotNull
	//@Override
	public StringBuilder mainold(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		ClassSingleton.reload();

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		String commandName;
		String commandRole;

		int commandVisible;

		Set<String> foundCommandNames = new HashSet<>();

		Set<String> existingCommandNames = CommandSingleton.getCommandNames();

		PreparedStatement statement1 = querier.sqlPreparedStatement("router_command", "DELETE FROM `router_command` WHERE `command` = ?", false, null, false);

		PreparedStatement statement2 = querier.sqlPreparedStatement("router_command", "INSERT INTO `router_command` (`command`, `class`, `visible`) VALUES (?, ?, ?)", false, null, false);

		PreparedStatement statement3 = querier.sqlPreparedStatement("router_command_role", "DELETE FROM `router_command_role` WHERE `commandFK` = (SELECT `id` FROM `router_command` WHERE `command` = ?)", false, null, false);

		PreparedStatement statement4 = querier.sqlPreparedStatement("router_command_role", "INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", false, null, false);

		for(String commandClass: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			try
			{
				Class<?> clazz = ClassSingleton.forName(commandClass);

				CommandMetadata commandMetadata = clazz.getAnnotation(CommandMetadata.class);

				if(commandMetadata != null
				   &&
				   (clazz.getModifiers() & Modifier.ABSTRACT) == 0x00
				   &&
				   ClassSingleton.extendsClass(clazz, AbstractCommand.class)
				   &&
				   !existingCommandNames.contains(commandName = clazz.getSimpleName())
				 ) {
					/*------------------------------------------------------------------------------------------------*/

					commandRole = commandMetadata.role();

					commandVisible = commandMetadata.visible() ? 1 : 0;

					/*------------------------------------------------------------------------------------------------*/

					LOG.info("Installing command {} (class: {}, visible: {}, role: {})", commandName, commandClass, commandVisible, commandRole);

					/*------------------------------------------------------------------------------------------------*/

					statement1.setString(1, commandName);

					statement2.setString(1, commandName);
					statement2.setString(2, commandClass);
					statement2.setInt(3, commandVisible);

					statement3.setString(1, commandName);

					statement4.setString(1, commandName);
					statement4.setString(2, commandRole);

					statement1.addBatch();
					statement2.addBatch();
					statement3.addBatch();
					statement4.addBatch();

					/*------------------------------------------------------------------------------------------------*/

					foundCommandNames.add(commandName);

					/*------------------------------------------------------------------------------------------------*/
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/**/

		try
		{
			statement1.executeBatch();
			statement2.executeBatch();
			statement3.executeBatch();
			statement4.executeBatch();
		}
		catch(Exception e)
		{
			throw new Exception(String.format("Trying to add %s: %s", String.join(", ", foundCommandNames), e.getMessage()), e);
		}
		finally
		{
			statement4.close();
			statement3.close();
			statement2.close();
			statement1.close();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(foundCommandNames.size() > 0)
		{
			CommandSingleton.reload();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, added command(s): " + foundCommandNames + "]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Automatically find new commands.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
