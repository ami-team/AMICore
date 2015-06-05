package net.hep.ami;

import java.util.*;
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

	static
	{
		try
		{
			addCommands();
		}
		catch(Exception e)
		{
			LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addCommands() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		QueryResult queryResult;

		try
		{
			queryResult = driver.executeSQLQuery("SELECT `command`,`class`,`archived` FROM `router_command`");
		}
		finally
		{
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF CATALOGS                                          */
		/*-----------------------------------------------------------------*/

		final int numberOfRows = queryResult.getNumberOfRows();

		/*-----------------------------------------------------------------*/
		/* ADD CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < numberOfRows; i++)
		{
			try
			{
				addCommand(
					queryResult.getValue(i, "command"),
					queryResult.getValue(i, "class"),
					queryResult.getValue(i, "archived")
				);
			}
			catch(Exception e)
			{
				LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
			}
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
						int.class
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

	public static String executeCommand(String command, Map<String, String> arguments, boolean checkRoles, int transactionID) throws Exception
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
			/* CREATE COMMAND INSTANCE                                     */
			/*-------------------------------------------------------------*/

			CommandAbstractClass commandObject = tuple.z.newInstance(
				arguments,
				transactionID
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

			/*-------------------------------------------------------------*/
			/* GET EXECUTION TIME                                          */
			/*-------------------------------------------------------------*/

			String executionTime = String.format(Locale.US, "%.3f", 0.001f * (t2 - t1));

			/*-------------------------------------------------------------*/
			/* GET RESULT                                                  */
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

			stringBuilder.append(content);

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

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Map.Entry<String, Tuple> entry: m_commands.entrySet())
		{
			String command = entry.getKey();

			String help = entry.getValue().x.toString();
			String usage = entry.getValue().y.toString();
			String clazz = entry.getValue().z.getName();
			String archived = entry.getValue().t.toString();

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

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
