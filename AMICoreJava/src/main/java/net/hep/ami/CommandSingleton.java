package net.hep.ami;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.shell.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class CommandSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final class Tuple extends Tuple7<String, String, String, Constructor<?>, Boolean, Boolean, String>
	{
		private static final long serialVersionUID = -1908438407272143175L;

		private Tuple(@NotNull String _x, @NotNull String _y, @NotNull String _z, @NotNull Constructor<?> _t, boolean _u, boolean _v, @NotNull String w)
		{
			super(_x, _y, _z, _t, _u, _v, w);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_commands = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

	private static final Set<String> s_reserved = new HashSet<>();

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
		s_reserved.add("AMIUser");
		s_reserved.add("AMIPass");

		s_reserved.add("clientDN");
		s_reserved.add("issuerDN");

		s_reserved.add("notBefore");
		s_reserved.add("notAfter");

		s_reserved.add("isSecure");

		s_reserved.add("userAgent");
		s_reserved.add("userSession");

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

		Router router = new Router();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet = router.executeSQLQuery("router_command", "SELECT `command`, `class`, `visible`, `secured`, `roleValidatorClass` FROM `router_command`");

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
						row.getValue(3),
						row.getValue(4)
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
			router.rollbackAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addCommand(@NotNull String commandName, @NotNull String commandClass, @NotNull String commandVisible, @NotNull String commandSecured, @NotNull String commandRoleValidatorClass) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(commandClass);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00)
		{
			return;
		}

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
			new Tuple(
				commandName,
				clazz.getMethod("help").invoke(null).toString(),
				clazz.getMethod("usage").invoke(null).toString(),
				clazz.getConstructor(
					Set.class,
					Map.class,
					long.class
				),
				!commandVisible.equals("0"),
				!commandSecured.equals("0"),
				commandRoleValidatorClass
			)
		);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command) throws Exception
	{
		Command.CommandTuple tuple = Command.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command, boolean checkRoles) throws Exception
	{
		Command.CommandTuple tuple = Command.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments, checkRoles);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String executeCommand(@NotNull String command, boolean checkRoles, long transactionId) throws Exception
	{
		Command.CommandTuple tuple = Command.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments, checkRoles, transactionId);
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
		/* GET COMMAND                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		Tuple tuple;

		for(;;)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			tuple = s_commands.get(command);

			if(tuple != null)
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

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK ROLES                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> userRoles;

		Router router = new Router();

		try
		{
			userRoles = RoleSingleton.checkRoles(router, command, arguments, tuple.w, checkRoles);
		}
		finally
		{
			router.commitAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE COMMAND AND BUILD RESULT                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		             .append("\n")
		             .append("<AMIMessage>")
		             .append("<command><![CDATA[").append(command).append("]]></command>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		String key;
		String value;

		stringBuilder.append("<arguments>");

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			key = entry.getKey();
			value = entry.getValue();

			if(!s_reserved.contains(key))
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

			AbstractCommand commandObject = (AbstractCommand) tuple.t.newInstance(
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
			             .append("<executionTime><![CDATA[").append(String.format(Locale.US, "%.3f", 0.001f * (t2 - t1))).append("]]></executionTime>")
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
			             .append((tuple.y != null) ? s_xml10Pattern.matcher(tuple.y).replaceAll("?") : "")
			             .append("]]></help>")

			             .append("<usage><![CDATA[")
			             .append((tuple.z != null) ? s_xml10Pattern.matcher(tuple.z).replaceAll("?") : "")
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

		for(Tuple tuple: s_commands.values())
		{
			if(tuple.x.equals("GetConfig"))
			{
				continue;
			}

			result.append("<row>")
			      .append("<field name=\"command\"><![CDATA[").append(tuple.x).append("]]></field>")
			      .append("<field name=\"help\"><![CDATA[").append(tuple.y).append("]]></field>")
			      .append("<field name=\"usage\"><![CDATA[").append(tuple.z).append("]]></field>")
			      .append("<field name=\"class\"><![CDATA[").append(tuple.t).append("]]></field>")
			      .append("<field name=\"visible\"><![CDATA[").append(tuple.u).append("]]></field>")
			      .append("<field name=\"secured\"><![CDATA[").append(tuple.v).append("]]></field>")
			      .append("<field name=\"roleValidatorClass\"><![CDATA[").append(tuple.w).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
