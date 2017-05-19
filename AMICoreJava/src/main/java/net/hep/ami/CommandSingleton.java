package net.hep.ami;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class CommandSingleton
{
	/*---------------------------------------------------------------------*/

	private static final class Tuple extends Tuple4<String, String, String, Constructor<?>>
	{
		public Tuple(String _x, String _y, String _z, Constructor<?> _t)
		{
			super(_x, _y, _z, _t);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_commands = new AMIMap<>();

	/*---------------------------------------------------------------------*/

	private static final Pattern s_xml10Pattern = Pattern.compile(
		  "[^"
		+ "\u0009\r\n"
		+ "\u0020-\uD7FF"
		+ "\uE000-\uFFFD"
		+ "\uD800\uDC00-\uDBFF\uDFFF"
		+ "]+"
	);

	/*---------------------------------------------------------------------*/

	private CommandSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		s_commands.clear();

		try
		{
			addCommands();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not add commands: " + e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addCommands() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("router"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = driver.executeQuery("SELECT `command`, `class` FROM `router_command`");

			/*-------------------------------------------------------------*/
			/* ADD COMMANDS                                                */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet.iter())
			{
				try
				{
					addCommand(
						row.getValue(0),
						row.getValue(1)
					);
				}
				catch(Exception e)
				{
					LogSingleton.root.error("for command `" + row.getValue(0) + "`: " + e.getMessage(), e);
				}
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void addCommand(String commandName, String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<?> clazz = Class.forName(className);

		if(ClassSingleton.extendsClass(clazz, AbstractCommand.class) == false || (clazz.getModifiers() & Modifier.ABSTRACT) != 0x00)
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* ADD COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		s_commands.put(
			commandName
			,
			new Tuple(
				commandName,
				clazz.getMethod("help").invoke(null).toString(),
				clazz.getMethod("usage").invoke(null).toString(),
				clazz.getConstructor(
					Map.class,
					long.class
				)
			)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void registerCommand(Querier querier, @Nullable String commandName, String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<?> clazz = Class.forName(className);

		if(ClassSingleton.extendsClass(clazz, AbstractCommand.class) == false)
		{
			throw new Exception("class '" + className + "' doesn't extend 'AbstractCommand'");
		}

		/*-----------------------------------------------------------------*/
		/* REGISTER COMMAND                                                */
		/*-----------------------------------------------------------------*/

		if(commandName == null) commandName = clazz.getSimpleName();

		querier.executeUpdate(String.format("INSERT INTO `router_command` (`command`, `class`) VALUES ('%s', '%s') ON DUPLICATE KEY UPDATE `command` = '%s'",
			commandName.replace("'", "''"),
			className.replace("'", "''"),
			commandName.replace("'", "''")
		));

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void unregisterCommand(Querier querier, String commandName) throws Exception
	{
		querier.executeUpdate(String.format("DELETE FROM `router_command` WHERE `simpleName` = `%s`",
			commandName.replace("'", "''")
		));
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command) throws Exception
	{
		Command.CommandTuple tuple = Command.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, boolean checkRoles) throws Exception
	{
		Command.CommandTuple tuple = Command.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments, checkRoles);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, boolean checkRoles, long transactionId) throws Exception
	{
		Command.CommandTuple tuple = Command.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments, checkRoles, transactionId);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, Map<String, String> arguments) throws Exception
	{
		return executeCommand(command, arguments, true, -1);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, Map<String, String> arguments, boolean checkRoles) throws Exception
	{
		return executeCommand(command, arguments, checkRoles, -1);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, Map<String, String> arguments, boolean checkRoles, long transactionId) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		Tuple tuple;

		for(;;)
		{
			/*-------------------------------------------------------------*/

			tuple = s_commands.get(command);

			if(tuple != null)
			{
				break;
			}

			/*-------------------------------------------------------------*/

			if(command.startsWith("AMI"))
			{
				command = command.substring(3);
			}
			else
			{
				throw new Exception("command `" + command + "` not found");
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/* CHECK ROLES                                                     */
		/*-----------------------------------------------------------------*/

		/* TODO */

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND AND BUILD RESULT                                */
		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		             .append("\n")
		             .append("<AMIMessage>")
		             .append("<command><![CDATA[").append(command).append("]]></command>")
		;

		/*-----------------------------------------------------------------*/

		String key;
		String value;

		stringBuilder.append("<arguments>");

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			key = entry.getKey();
			value = entry.getValue();

			if("AMIUser".equals(key) == false
			   &&
			   "AMIPass".equals(key) == false
			   &&
			   "clientDN".equals(key) == false
			   &&
			   "issuerDN".equals(key) == false
			 ) {
				stringBuilder.append("<argument name=\"").append(key.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")).append("\" value=\"").append(value.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")).append("\" />");
			}
		}

		stringBuilder.append("</arguments>");

		/*-----------------------------------------------------------------*/

		if(arguments.containsKey("help") == false)
		{
			/*-------------------------------------------------------------*/
			/* CREATE COMMAND INSTANCE                                     */
			/*-------------------------------------------------------------*/

			AbstractCommand commandObject = (AbstractCommand) tuple.t.newInstance(
				arguments,
				transactionId
			);

			/*-------------------------------------------------------------*/
			/* EXECUTE COMMAND INSTANCE                                    */
			/*-------------------------------------------------------------*/

			long t1 = System.currentTimeMillis();
			StringBuilder content = commandObject.execute();
			long t2 = System.currentTimeMillis();

			/**/

			stringBuilder.append("<executionTime>" + String.format(Locale.US, "%.3f", 0.001f * (t2 - t1)) + "</executionTime>");

			if(content != null)
			{
				stringBuilder.append(s_xml10Pattern.matcher(content).replaceAll("?"));
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/

			stringBuilder.append("<executionTime>0.000</executionTime>");

			if(tuple.y != null)
			{
				stringBuilder.append("<help><![CDATA[")
				             .append(s_xml10Pattern.matcher(tuple.y).replaceAll("?"))
				             .append("]]></help>")
				;
			}

			if(tuple.z != null)
			{
				stringBuilder.append("<usage><![CDATA[")
				             .append(s_xml10Pattern.matcher(tuple.z).replaceAll("?"))
				             .append("]]></usage>")
				;
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		stringBuilder.append("</AMIMessage>");

		/*-----------------------------------------------------------------*/

		return stringBuilder.toString();
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listCommands()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(Tuple tuple: s_commands.values())
		{
			result.append("<row>")
			      .append("<field name=\"command\"><![CDATA[").append(tuple.x).append("]]></field>")
			      .append("<field name=\"help\"><![CDATA[").append(tuple.y).append("]]></field>")
			      .append("<field name=\"usage\"><![CDATA[").append(tuple.z).append("]]></field>")
			      .append("<field name=\"class\"><![CDATA[").append(tuple.t).append("]]></field>")
			      .append("</row>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
