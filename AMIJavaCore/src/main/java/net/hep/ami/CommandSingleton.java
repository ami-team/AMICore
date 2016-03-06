package net.hep.ami;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;

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

	private static final Map<String, Tuple> m_commands = new HashMap<String, Tuple>();

	/*---------------------------------------------------------------------*/

	private static Pattern m_xml10Pattern = Pattern.compile("[^"
		+ "\u0009\r\n"
		+ "\u0020-\uD7FF"
		+ "\uE000-\uFFFD"
		+ "\uD800\uDC00-\uDBFF\uDFFF"
		+ "]+"
	);

	/*---------------------------------------------------------------------*/

	static
	{
		try
		{
			addCommands();
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.error(e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addCommands() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
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

			RowSet rowSet = driver.executeSQLQuery("SELECT `command`,`class`,`archived` FROM `router_command`");

			/*-------------------------------------------------------------*/
			/* ADD COMMANDS                                                */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet)
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
			m_commands.put(
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
		if(command.startsWith("AMI"))
		{
			command = command.substring(3);
		}

		/*-----------------------------------------------------------------*/
		/* GET COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		Tuple tuple = m_commands.get(command);

		if(tuple == null)
		{
			throw new Exception("command `" + command + "` not found");
		}

		/*-----------------------------------------------------------------*/

		String result;

		if(arguments.containsKey("help") == false)
		{
			/*-------------------------------------------------------------*/
			/* CHECK ROLES                                                 */
			/*-------------------------------------------------------------*/

/*			RoleSingleton.checkRoles(command, arguments);
 */
			/*-------------------------------------------------------------*/
			/* CREATE COMMAND INSTANCE                                     */
			/*-------------------------------------------------------------*/

			CommandAbstractClass commandObject = tuple.z.newInstance(
				arguments,
				transactionId
			);

			/*-------------------------------------------------------------*/
			/* GET EXECUTION DATE                                          */
			/*-------------------------------------------------------------*/

			String executionDate = DateFormater.format(new Date());

			/*-------------------------------------------------------------*/
			/* EXECUTE COMMAND                                             */
			/*-------------------------------------------------------------*/

			long t1 = System.currentTimeMillis();
			StringBuilder content = commandObject.execute();
			long t2 = System.currentTimeMillis();

			String executionTime = String.format(Locale.US, "%.3f", 0.001f * (t2 - t1));

			/*-------------------------------------------------------------*/
			/* BUILD RESULT                                                */
			/*-------------------------------------------------------------*/

			StringBuilder stringBuilder = new StringBuilder();

			/*-------------------------------------------------------------*/

			stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

			stringBuilder.append("<AMIMessage>");

			stringBuilder.append("<command>" + command + "</command>");

			stringBuilder.append("<arguments>");
			for(Map.Entry<String, String> entry: arguments.entrySet()) stringBuilder.append("<argument name=\"" + entry.getKey() + "\" value=\"" + entry.getValue().replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") + "\"/>");
			stringBuilder.append("</arguments>");

			stringBuilder.append("<executionDate>" + executionDate + "</executionDate>");
			stringBuilder.append("<executionTime>" + executionTime + "</executionTime>");

			stringBuilder.append("<Result>");
			stringBuilder.append(m_xml10Pattern.matcher(content).replaceAll("?"));
			stringBuilder.append("</Result>");

			stringBuilder.append("</AMIMessage>");

			/*-------------------------------------------------------------*/

			result = stringBuilder.toString();

			/*-------------------------------------------------------------*/
		}
		else
		{
			result = XMLTemplates.help(
				tuple.x,
				tuple.y
			);
		}

		/*-----------------------------------------------------------------*/

		return result;
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
		String archived;

		for(Map.Entry<String, Tuple> entry: m_commands.entrySet())
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
}
