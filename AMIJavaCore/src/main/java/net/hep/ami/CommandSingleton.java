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

	private static class Tuple extends Tuple4<String, String, Constructor<CommandAbstractClass>, String>
	{
		public Tuple(String _x, String _y, Constructor<CommandAbstractClass> _z, String _t)
		{
			super(_x, _y, _z, _t);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_commands = new java.util.concurrent.ConcurrentHashMap<String, Tuple>();

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
			LogSingleton.defaultLogger.fatal(e.getMessage());
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

			RowSet rowSet = driver.executeQuery("SELECT `command`, `class`, `archived` FROM `router_command`");

			/*-------------------------------------------------------------*/
			/* ADD COMMANDS                                                */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet.iter())
			{
				try
				{
					addCommand(
						row.getValue("command"),
						row.getValue("class"),
						row.getValue("archived")
					);
				}
				catch(Exception e)
				{
					/* CETTE LIGNE DOIT ETRE DECOMMENTEE EN PROD */
					//LogSingleton.defaultLogger.error(e.getMessage());
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

	private static void addCommand(String commandName, String className, String archived) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<CommandAbstractClass> clazz = (Class<CommandAbstractClass>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		if(ClassFinder.extendsClass(clazz, CommandAbstractClass.class))
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
					),
					archived
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

		if(ClassFinder.extendsClass(clazz, CommandAbstractClass.class))
		{
			String simpleName = clazz.getSimpleName();
			String name = clazz.getName();

			querier.executeUpdate(String.format("INSERT INTO `router_command` (`command`, `class`) VALUES ('%s', '%s')",
				simpleName,
				name,
				name
			));

			return true;
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command) throws Exception
	{
		CommandParser.CommandParserTuple tuple = CommandParser.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, boolean checkRoles) throws Exception
	{
		CommandParser.CommandParserTuple tuple = CommandParser.parse(command);

		return CommandSingleton.executeCommand(tuple.command, tuple.arguments, checkRoles);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, boolean checkRoles, long transactionId) throws Exception
	{
		CommandParser.CommandParserTuple tuple = CommandParser.parse(command);

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
		/* EXECUTE COMMAND AND BUILD RESULT                                */
		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		stringBuilder.append("<AMIMessage>");

		stringBuilder.append("<command><![CDATA[" + command + "]]></command>");

		stringBuilder.append("<arguments>");

		String key;

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			key = entry.getKey();

			if(key.equals("AMIUser") == false
			   &&
			   key.equals("AMIPass") == false
			   &&
			   key.equals("clientDN") == false
			   &&
			   key.equals("issuerDN") == false
			 ) {
				stringBuilder.append("<argument name=\"" + entry.getKey().replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") + "\" value=\"" + entry.getValue().replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") + "\"/>");
			}
		}

		stringBuilder.append("</arguments>");

		stringBuilder.append("<executionDate>" + DateFormater.format(new Date()) + "</executionDate>");

		if(arguments.containsKey("help") == false)
		{
			/*-------------------------------------------------------------*/
			/* CHECK ROLES                                                 */
			/*-------------------------------------------------------------*/

/*			RoleSingleton.checkRoles(command, arguments, ???);
 */
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

			stringBuilder.append("<help><![CDATA[");
			stringBuilder.append(s_xml10Pattern.matcher(tuple.x).replaceAll("?"));
			stringBuilder.append("]]></help>");

			stringBuilder.append("<usage><![CDATA[");
			stringBuilder.append(s_xml10Pattern.matcher(tuple.y).replaceAll("?"));
			stringBuilder.append("]]></usage>");

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

		Set<Map.Entry<String, Tuple>> entrySet = new TreeSet<Map.Entry<String, Tuple>>(new MapEntryKeyComparator());

		entrySet.addAll(s_commands.entrySet());

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		String command;

		String help;
		String usage;
		String clazz;
		String archived;

		for(Map.Entry<String, Tuple> entry: entrySet)
		{
			command = entry.getKey();

			help = entry.getValue().x.toString();
			usage = entry.getValue().y.toString();
			clazz = entry.getValue().z.getName();
			archived = entry.getValue().t.toString();

			result.append(
				"<row>"
				+
				"<field name=\"command\"><![CDATA[" + command + "]]></field>"
				+
				"<field name=\"help\"><![CDATA[" + help + "]]></field>"
				+
				"<field name=\"usage\"><![CDATA[" + usage + "]]></field>"
				+
				"<field name=\"class\"><![CDATA[" + clazz + "]]></field>"
				+
				"<field name=\"archived\"><![CDATA[" + archived + "]]></field>"
				+
				"</row>"
			);
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

				if(l >= 2)
				{
					if((right.charAt(0) != '\'' || right.charAt(l - 1) != '\'')
					   &&
					   (right.charAt(0) != '\"' || right.charAt(l - 1) != '\"')
					 ) {
						arg = left + "=\"" + right + "\"";
					}
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
