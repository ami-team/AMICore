package net.hep.ami.command.admin;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class FindNewCommands extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public FindNewCommands(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		/*-----------------------------------------------------------------*/

		ClassSingleton.reload();

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		String commandName;
		String commandRole;

		int commandVisible;
		int commandSecured;

		Set<String> foundCommandNames = new HashSet<>();

		Set<String> existingCommandNames = CommandSingleton.getCommandNames();

		PreparedStatement statement1 = querier.prepareStatement("INSERT INTO `router_command` (`command`, `class`, `visible`, `secured`) VALUES (?, ?, ?, ?)", false, false, null);

		PreparedStatement statement2 = querier.prepareStatement("INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", false, false, null);

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
				   existingCommandNames.contains(commandName = clazz.getSimpleName()) == false
				 ) {

					/*-----------------------------------------------------*/

					commandRole = commandMetadata.role();

					commandVisible = commandMetadata.visible() ? 1 : 0;
					commandSecured = commandMetadata.secured() ? 1 : 0;

					/*-----------------------------------------------------*/

					statement1.setString(1, commandName);
					statement1.setString(2, commandClass);
					statement1.setInt(3, commandVisible);
					statement1.setInt(4, commandSecured);

					statement2.setString(1, commandName);
					statement2.setString(2, commandRole);

					statement1.addBatch();
					statement2.addBatch();

					/*-----------------------------------------------------*/

					foundCommandNames.add(commandName);

					/*-----------------------------------------------------*/
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
		}
		finally
		{
			statement2.close();
			statement1.close();
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, added command(s): " + foundCommandNames + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Automatically find new commands.";
	}

	/*---------------------------------------------------------------------*/
}
