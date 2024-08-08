package net.hep.ami.command.admin;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;
import java.util.stream.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.data.Row;
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
		LOG.info("Commands to be added {}", String.join(", ", toBeAdded));

		long nbCommandRemoved = 0;
		long nbCommandAdded = 0;
		long nbCommandRoleAdded = 0;

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMMAND CLEANUP                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			try(PreparedStatement statement = querier.sqlPreparedStatement("router_command", "DELETE FROM `router_command` WHERE `class` = ?", false, null, false))
			{
				for(String commandClass : toBeRemoved)
				{
					statement.setString(1, commandClass);

					statement.addBatch();
				}

				nbCommandRemoved = Arrays.stream(statement.executeBatch()).filter(x -> x == Statement.SUCCESS_NO_INFO).count();

			}
		}
		catch(SQLException e)
		{
			throw new SQLException(String.format("%s - nbCommandRemoved: %d, nbCommandAdded: %d, nbCommandRoleAdded: %d", e.getMessage(), nbCommandRemoved, nbCommandAdded, nbCommandRoleAdded));
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMMAND INSERTION                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			try(PreparedStatement statement = querier.sqlPreparedStatement("router_command", "INSERT INTO `router_command` (`command`, `class`, `visible`) VALUES (?, ?, ?)", false, null, false))
			{
				for(String commandName : toBeAdded)
				{
					CommandDescr descr = jarCommandDescrs.get(commandName);

					statement.setString(1, descr.commandName);
					statement.setString(2, descr.commandClass);
					statement.setInt(3, descr.commandVisible);

					statement.addBatch();
				}

				nbCommandAdded = Arrays.stream(statement.executeBatch()).filter(x -> x == Statement.SUCCESS_NO_INFO).count();
			}
		}
		catch(SQLException e)
		{
			throw new SQLException(String.format("%s - nbCommandRemoved: %d, nbCommandAdded: %d, nbCommandRoleAdded: %d", e.getMessage(), nbCommandRemoved, nbCommandAdded, nbCommandRoleAdded));
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMMAND ROLE INSERTION                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			try(PreparedStatement statement = querier.sqlPreparedStatement("router_command_role", "INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES (?, ?)", false, null, false))
			{
				for(String commandName : toBeAdded)
				{
					CommandDescr descr = jarCommandDescrs.get(commandName);

					List<Row> rows = querier.executeSQLQuery("router_command", "SELECT rc.`id`, rr.`id` FROM `router_command` rc, `router_role` rr WHERE rc.`class` = ?0 AND rr.`role` = ?1", descr.commandClass, descr.commandRole).getAll();

					if(!rows.isEmpty())
					{
						statement.setInt(1, rows.get(0).getValue(0, 0));
						statement.setInt(2, rows.get(0).getValue(1 , 0));

						statement.addBatch();
					}
				}

				nbCommandRoleAdded = Arrays.stream(statement.executeBatch()).filter(x -> x == Statement.SUCCESS_NO_INFO).count();
			}
		}
		catch(SQLException e)
		{
			throw new SQLException(String.format("%s - nbCommandRemoved: %d, nbCommandAdded: %d, nbCommandRoleAdded: %d", e.getMessage(), nbCommandRemoved, nbCommandAdded, nbCommandRoleAdded));
		}

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

		Set<String> cmdRemoved = toBeRemoved.stream().map(x -> x.substring(x.lastIndexOf(".") + 1)).collect(Collectors.toSet());
		Set<String> cmdAdded = toBeAdded.stream().map(x -> x.substring(x.lastIndexOf(".") + 1)).collect(Collectors.toSet());

		return new StringBuilder("<info><![CDATA[done with success : ").append(nbCommandAdded).append(" added command(s): [").append(String.join(", ", cmdAdded)).append("], ").append(nbCommandRemoved).append(" removed command(s): [").append(String.join(", ", cmdRemoved)).append("]]]></info>");
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
