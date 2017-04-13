package net.hep.ami;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class CommandSingleton
{
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple3<String, String, Constructor<CommandAbstractClass>>
	{
		public Tuple(String _x, String _y, Constructor<CommandAbstractClass> _z)
		{
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_commands = new ConcurrentHashMap<>();

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
		CommandSingleton.reload();
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
			LogSingleton.defaultLogger.error(LogSingleton.FATAL, "could not add commands", e);
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addCommands() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("jdbc_url"),
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
					LogSingleton.defaultLogger.error("for command `" + row.getValue(0) + "`", e);
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
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private static void addCommand(String commandName, String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<CommandAbstractClass> clazz = (Class<CommandAbstractClass>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		if(ClassSingleton.extendsClass(clazz, CommandAbstractClass.class))
		{
			s_commands.put(
				commandName
				,
				new Tuple(
					clazz.getMethod("help").invoke(null).toString(),
					clazz.getMethod("usage").invoke(null).toString(),
					clazz.getConstructor(
						Map.class,
						long.class
					)
				)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	public static boolean registerCommand(QuerierInterface querier, String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<CommandAbstractClass> clazz = (Class<CommandAbstractClass>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		if(ClassSingleton.extendsClass(clazz, CommandAbstractClass.class))
		{
			String simpleName = clazz.getSimpleName();
			String name = clazz.getName();

			querier.executeUpdate(String.format("INSERT INTO `router_command` (`command`, `class`) VALUES ('%s', '%s') ON DUPLICATE KEY UPDATE `command`='%s'",
				simpleName,
				name,
				simpleName
			));

			return true;
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command) throws Exception
	{
		AMIParser.CommandTuple tuple = AMIParser.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, boolean checkRoles) throws Exception
	{
		AMIParser.CommandTuple tuple = AMIParser.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments, checkRoles);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, boolean checkRoles, long transactionId) throws Exception
	{
		AMIParser.CommandTuple tuple = AMIParser.parse(command);

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

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		stringBuilder.append("<AMIMessage>");

		stringBuilder.append("<command><![CDATA[").append(command).append("]]></command>");

		stringBuilder.append("<arguments>");

		String key;
		String value;

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			key = entry.getKey();
			value = entry.getValue();

			if(key.equals("AMIUser") == false
			   &&
			   key.equals("AMIPass") == false
			   &&
			   key.equals("clientDN") == false
			   &&
			   key.equals("issuerDN") == false
			 ) {
				stringBuilder.append("<argument name=\"").append(key.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")).append("\" value=\"").append(value.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")).append("\"/>");
			}
		}

		stringBuilder.append("</arguments>");

		if(arguments.containsKey("help") == false)
		{
			/*-------------------------------------------------------------*/
			/* CREATE COMMAND INSTANCE                                     */
			/*-------------------------------------------------------------*/

			CommandAbstractClass commandObject = tuple.z.newInstance(
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

			if(tuple.x != null)
			{
				stringBuilder.append("<help><![CDATA[")
				             .append(s_xml10Pattern.matcher(tuple.x).replaceAll("?"))
				             .append("]]></help>")
				;
			}

			if(tuple.y != null)
			{
				stringBuilder.append("<usage><![CDATA[")
				             .append(s_xml10Pattern.matcher(tuple.y).replaceAll("?"))
				             .append("]]></usage>")
				;
			}

			/*-------------------------------------------------------------*/
		}

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

		String command;

		String help;
		String usage;
		String clazz;

		for(Map.Entry<String, Tuple> entry: s_commands.entrySet())
		{
			command = entry.getKey();

			help = entry.getValue().x.toString();
			usage = entry.getValue().y.toString();
			clazz = entry.getValue().z.getName();

			result.append("<row>")
			      .append("<field name=\"command\"><![CDATA[").append(command).append("]]></field>")
			      .append("<field name=\"help\"><![CDATA[").append(help).append("]]></field>")
			      .append("<field name=\"usage\"><![CDATA[").append(usage).append("]]></field>")
			      .append("<field name=\"class\"><![CDATA[").append(clazz).append("]]></field>")
			      .append("</row>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	static public void main(String[] args)
	{
		/*-----------------------------------------------------------------*/
		/* SHELL BARRIER                                                   */
		/*-----------------------------------------------------------------*/

		int idx, l;

		String s = "";

		String left, right;

		for(String arg: args)
		{
			idx = arg.indexOf('=');

			if(idx != -1)
			{
				l = arg.length();

				left = arg.substring(0, idx + 0);
				right = arg.substring(idx + 1, l);

				l = right.length();

				if(l >= 2
				   &&
				   (right.charAt(0) != '\'' || right.charAt(l - 1) != '\'')
				   &&
				   (right.charAt(0) != '\"' || right.charAt(l - 1) != '\"')
				 ) {
					arg = left + "=\"" + right + "\"";
				}
			}

			s += " " + arg;
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		try
		{
			System.out.println(CommandSingleton.executeCommand(s, false, -1));

			System.exit(0);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());

			System.exit(1);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
