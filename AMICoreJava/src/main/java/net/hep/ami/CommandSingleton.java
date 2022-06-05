package net.hep.ami;

import lombok.*;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.shell.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class CommandSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	private static final class CommandDescr
	{
		@NotNull private final String name;
		@Nullable private final String help;
		@Nullable private final String usage;
		@NotNull private final Boolean visible;
		@NotNull private final Constructor<?> constructor;
		@Nullable private final String commandRoleValidatorClass;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, CommandDescr> s_commands = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

	private static final Set<String> s_reservedArguments = new HashSet<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Pattern s_xml10Pattern = Pattern.compile(
		  "[^"
		+ "\u0009\r\n"
		+ "\u0020-\uD7FF"
		+ "\uE000-\uFFFD"
		+ "\uD800\uDC00-\uDBFF\uDFFF"
		+ "]+"
	);

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final String HOSTNAME;

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String hostName;

		try
		{
			SimpleShell simpleShell = new SimpleShell();

			simpleShell.connect();
			SimpleShell.ShellTuple shellTuple = simpleShell.exec(new String[] {"hostname", "-f"});
			simpleShell.disconnect();

			if(shellTuple.errorCode == 0)
			{
				hostName = shellTuple.inputStringBuilder.toString().trim();
			}
			else
			{
				hostName = "N/A";
			}
		}
		catch(Exception e)
		{
			hostName = "N/A";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		HOSTNAME = hostName;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private CommandSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		s_reservedArguments.add("AMIUser");
		s_reservedArguments.add("AMIPass");

		s_reservedArguments.add("clientDN");
		s_reservedArguments.add("issuerDN");

		s_reservedArguments.add("notBefore");
		s_reservedArguments.add("notAfter");

		s_reservedArguments.add("isSecure");

		s_reservedArguments.add("userAgent");
		s_reservedArguments.add("userSession");

		reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload()
	{
		s_commands.clear();

		try
		{
			addCommands();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not add commands: {}", e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addCommands() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE QUERIER                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		RouterQuerier querier = new RouterQuerier();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet = querier.executeSQLQuery("router_command", "SELECT `command`, `class`, `visible`, `roleValidatorClass` FROM `router_command`");

			/*--------------------------------------------------------------------------------------------------------*/
			/* ADD COMMANDS                                                                                           */
			/*--------------------------------------------------------------------------------------------------------*/

			for(Row row: rowSet.iterate())
			{
				try
				{
					addCommand(
						row.getValue(0),
						row.getValue(1),
						row.getValue(2),
						row.getValue(3)
					);
				}
				catch(Exception e)
				{
					LogSingleton.root.error("for command `{}`" , row.getValue(0), e);
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		finally
		{
			querier.rollbackAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addCommand(@NotNull String commandName, @NotNull String commandClass, @NotNull String commandVisible, @NotNull String commandRoleValidatorClass) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(commandClass);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00)
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(!ClassSingleton.extendsClass(clazz, AbstractCommand.class))
		{
			throw new Exception("class '" + commandClass + "' doesn't extend 'AbstractCommand'");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD COMMAND                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		s_commands.put(
			commandName
			,
			new CommandDescr(
				commandName,
				clazz.getMethod("help").invoke(null).toString(),
				clazz.getMethod("usage").invoke(null).toString(),
				Bool.valueOf(commandVisible),
				clazz.getConstructor(
					Set.class,
					Map.class,
					long.class
				),
				commandRoleValidatorClass
			)
		);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command) throws Exception
	{
		Command.CommandAndArguments commandAndArguments = Command.parse(command);

		return CommandSingleton.executeCommand(commandAndArguments.getCommand(), commandAndArguments.getArguments());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command, boolean checkRoles) throws Exception
	{
		Command.CommandAndArguments commandAndArguments = Command.parse(command);

		return CommandSingleton.executeCommand(commandAndArguments.getCommand(), commandAndArguments.getArguments(), checkRoles);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command, boolean checkRoles, long transactionId) throws Exception
	{
		Command.CommandAndArguments commandAndArguments = Command.parse(command);

		return CommandSingleton.executeCommand(commandAndArguments.getCommand(), commandAndArguments.getArguments(), checkRoles, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command, @NotNull Map<String, String> arguments) throws Exception
	{
		return executeCommand(command, arguments, true, -1);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command, @NotNull Map<String, String> arguments, boolean checkRoles) throws Exception
	{
		return executeCommand(command, arguments, checkRoles, -1);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command, @NotNull Map<String, String> arguments, boolean checkRoles, long transactionId) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* RESOLVE COMMAND                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		CommandDescr commandDescr = resolveCommand(command);

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK ROLES                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> userRoles;

		RouterQuerier querier = new RouterQuerier();

		try
		{
			userRoles = RoleSingleton.checkRoles(querier, commandDescr.getName(), arguments, commandDescr.getCommandRoleValidatorClass(), checkRoles);
		}
		finally
		{
			querier.commitAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE COMMAND AND BUILD RESULT                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		             .append("\n")
		             .append("<AMIMessage>")
		             .append("<command><![CDATA[").append(commandDescr.getName()).append("]]></command>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		String key;
		String value;

		stringBuilder.append("<arguments>");

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			key = entry.getKey();
			value = entry.getValue();

			if(!s_reservedArguments.contains(key))
			{
				key = Utility.escapeHTML(Utility.escapeJavaString(key));
				value = Utility.escapeHTML(Utility.escapeJavaString(value));

				stringBuilder.append("<argument name=\"").append(key).append("\" value=\"").append(value).append("\" />");
			}
		}

		stringBuilder.append("</arguments>");

		/*------------------------------------------------------------------------------------------------------------*/

		if(!arguments.containsKey("help"))
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE COMMAND INSTANCE                                                                                */
			/*--------------------------------------------------------------------------------------------------------*/

			AbstractCommand commandObject = (AbstractCommand) commandDescr.getConstructor().newInstance(
				userRoles,
				arguments,
				transactionId
			);

			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE COMMAND INSTANCE                                                                               */
			/*--------------------------------------------------------------------------------------------------------*/

			long t1 = System.currentTimeMillis();
			StringBuilder content = commandObject.execute();
			long t2 = System.currentTimeMillis();

			/*--------------------------------------------------------------------------------------------------------*/

			stringBuilder.append("<node><![CDATA[").append(HOSTNAME).append("]]></node>")
			             .append(String.format(Locale.US, "<executionTime><![CDATA[%.3f]]></executionTime>", 0.001f * (t2 - t1)))
			             .append(s_xml10Pattern.matcher(content).replaceAll("?"))
			;

			/*--------------------------------------------------------------------------------------------------------*/
		}
		else
		{
			/*--------------------------------------------------------------------------------------------------------*/

			stringBuilder.append("<node><![CDATA[").append(HOSTNAME).append("]]></node>")
			             .append("<executionTime><![CDATA[0.000]]></executionTime>")

			             .append("<help><![CDATA[")
			             .append((commandDescr.getHelp() != null) ? s_xml10Pattern.matcher(commandDescr.getHelp()).replaceAll("?") : "")
			             .append("]]></help>")

			             .append("<usage><![CDATA[")
			             .append((commandDescr.getUsage() != null) ? s_xml10Pattern.matcher(commandDescr.getUsage()).replaceAll("?") : "")
			             .append("]]></usage>")
			;

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		stringBuilder.append("</AMIMessage>");

		/*------------------------------------------------------------------------------------------------------------*/

		return stringBuilder.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder chainCommand(@NotNull String command, @NotNull Set<String> userRoles, @NotNull Map<String, String> arguments) throws Exception
	{
		return chainCommand(command, userRoles, arguments, -1);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder chainCommand(@NotNull String command, @NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* RESOLVE COMMAND                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		CommandDescr commandDescr = resolveCommand(command);

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE COMMAND INSTANCE                                                                                    */
		/*------------------------------------------------------------------------------------------------------------*/

		AbstractCommand commandObject = (AbstractCommand) commandDescr.getConstructor().newInstance(
			userRoles,
			arguments,
			transactionId
		);

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE COMMAND INSTANCE                                                                                   */
		/*------------------------------------------------------------------------------------------------------------*/

		return commandObject.execute();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static CommandDescr resolveCommand(String command) throws Exception
	{
		CommandDescr result;

		for(;;)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			result = s_commands.get(command);

			if(result != null)
			{
				break;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(command.startsWith("AMI"))
			{
				command = command.substring(3);
			}
			else
			{
				throw new Exception("command `" + command + "` not found");
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static Set<String> getCommandNames()
	{
		return s_commands.keySet();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder listCommands()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"commands\">");

		for(CommandDescr commandDescr: s_commands.values())
		{
			if(commandDescr.getName().equals("GetConfig"))
			{
				continue;
			}

			result.append("<row>")
			      .append("<field name=\"command\"><![CDATA[").append(commandDescr.getName()).append("]]></field>")
			      .append("<field name=\"help\"><![CDATA[").append(commandDescr.getHelp()).append("]]></field>")
			      .append("<field name=\"usage\"><![CDATA[").append(commandDescr.getUsage()).append("]]></field>")
			      .append("<field name=\"class\"><![CDATA[").append(commandDescr.getConstructor()).append("]]></field>")
			      .append("<field name=\"visible\"><![CDATA[").append(commandDescr.getVisible()).append("]]></field>")
			      .append("<field name=\"roleValidatorClass\"><![CDATA[").append(commandDescr.getCommandRoleValidatorClass()).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
