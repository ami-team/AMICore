package net.hep.ami.command.admin;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class FindNewCommands extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public FindNewCommands(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
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

		PreparedStatement statement3 = querier.sqlPreparedStatement("router_command", "INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", false, null, false);

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

					statement1.setString(1, commandName);

					statement2.setString(1, commandName);
					statement2.setString(2, commandClass);
					statement2.setInt(3, commandVisible);

					statement3.setString(1, commandName);
					statement3.setString(2, commandRole);

					statement1.addBatch();
					statement2.addBatch();
					statement3.addBatch();

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
		}
		finally
		{
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
