package net.hep.ami.command.admin;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", secured = false)
public class FindNewCommands extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public FindNewCommands(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String commandRole = arguments.containsKey("role") ? arguments.get("role")
		                                                   : "AMI_USER"
		;

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		String commandName;

		Set<String> commands = new HashSet<>();

		PreparedStatement statement1 = querier.prepareStatement("INSERT INTO `router_command` (`command`, `class`) VALUES (?, ?)");

		PreparedStatement statement2 = querier.prepareStatement("INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))");

		for(String className: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			try
			{
				Class<?> clazz = Class.forName(className);

				if((clazz.getModifiers() & Modifier.ABSTRACT) == 0x00
				   &&
				   ClassSingleton.extendsClass(clazz, AbstractCommand.class)
				 ) {
					/*-----------------------------------------------------*/

					commandName = clazz.getSimpleName();

					/*-----------------------------------------------------*/

					statement1.setString(1, commandName);
					statement1.setString(2, className);
					statement2.setString(1, commandName);
					statement2.setString(2, commandRole);

					statement1.executeUpdate();
					statement2.executeUpdate();

					/*-----------------------------------------------------*/

					commands.add(commandName);

					/*-----------------------------------------------------*/
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		statement2.close();
		statement1.close();

		/*-----------------------------------------------------------------*/

		CommandSingleton.reload();

		/*-----------------------------------------------------------------*/

		System.out.println(commands);

		return new StringBuilder("<info><![CDATA[added with success: " + commands + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Automatically find new commands.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-role=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
