package net.hep.ami.command.admin;

import java.sql.*;
import java.util.*;
import java.util.stream.*;
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

	private record CommandDescr(
		@NotNull String commandName,
		@NotNull String commandClass,
		@NotNull Integer commandVisible,
		@Nullable String commandRole
	) {}

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

		Set<String> dbCommandClasses = querier.executeSQLQuery("router_command", "SELECT DISTINCT `class` FROM `router_command`").getAll().stream().map(x -> {

            try
			{
                return x.getValue(0);
            }
			catch(Exception e)
			{
                throw new RuntimeException(e);
            }

        }).collect(Collectors.toSet());

		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> jarCommandClasses = new HashSet<>();

		Map<String, CommandDescr> jarCommandDescrs = new HashMap<>();

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
				 ) {
					/*------------------------------------------------------------------------------------------------*/

					String commandName = clazz.getSimpleName();

					String commandRole = commandMetadata.role();

					Integer commandVisible = commandMetadata.visible() ? 1 : 0;

					/*------------------------------------------------------------------------------------------------*/

					jarCommandClasses.add(commandClass);

					jarCommandDescrs.put(commandClass, new CommandDescr(commandName, commandClass, commandVisible, commandRole));

					/*------------------------------------------------------------------------------------------------*/
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> toBeRemoved = dbCommandClasses.stream().filter(x -> !jarCommandClasses.contains(x)).collect(Collectors.toSet());

		Set<String> toBeAdded = jarCommandClasses.stream().filter(x -> !dbCommandClasses.contains(x)).collect(Collectors.toSet());

		LOG.info("Commands to be removed {}", String.join(", ", toBeRemoved));
		LOG.info("Commands to be added {}", String.join(", ", toBeRemoved));

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMMAND CLEANUP                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		try(PreparedStatement statement = querier.sqlPreparedStatement("router_command", "DELETE FROM `router_command` WHERE `command` = ?", false, null, false))
		{
			for(String commandName : toBeRemoved)
			{
				statement.setString(1, commandName);

				statement.addBatch();
			}

			statement.executeBatch();
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMMAND INSERTION                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

//		try(PreparedStatement statement1 = querier.sqlPreparedStatement("router_command", "INSERT INTO `router_command` (`command`, `class`, `visible`) VALUES (?, ?, ?)", false, null, false))
//		{
//			try(PreparedStatement statement2 = querier.sqlPreparedStatement("router_command_role", "INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", false, null, false))
//			{
//
//				for(String commandName : toBeAdded)
//				{
//					CommandDescr descr = jarCommandDescrs.get(commandName);
//
//					statement1.setString(1, descr.commandName);
//					statement1.setString(2, descr.commandClass);
//					statement1.setInt(3, descr.commandVisible);
//					statement1.addBatch();
//
//					statement2.setString(1, descr.commandName);
//					statement2.setString(2, descr.commandRole);
//					statement2.addBatch();
//				}
//
//				statement1.executeBatch();
//				statement2.executeBatch();
//			}
//		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* RELOAD                                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		if(!toBeAdded.isEmpty()
		   ||
		   !toBeRemoved.isEmpty()
		 ) {
			CommandSingleton.reload();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, removed command(s): [" + String.join(", ", toBeRemoved) + "], added command(s): [" + String.join(", ", toBeAdded) + "]]]></info>");
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
