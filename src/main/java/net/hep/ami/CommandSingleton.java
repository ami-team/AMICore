package net.hep.ami;

import java.util.*;
import java.util.Map.*;
import java.lang.reflect.*;

import net.hep.ami.utility.*;

public class CommandSingleton {
	/*---------------------------------------------------------------------*/

	private static class CommandTuple extends Tuple3<Constructor<CommandAbstractClass>, String, String> {

		public CommandTuple(Constructor<CommandAbstractClass> _x, String _y, String _z) {
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static Map<String, CommandTuple> m_commands = new HashMap<String, CommandTuple>();

	/*---------------------------------------------------------------------*/

	private static final Class<?>[] m_ctor = new Class<?>[] { Map.class, int.class };

	/*---------------------------------------------------------------------*/

	static {

		ClassFinder classFinder = new ClassFinder("net.hep.ami");

		for(String className: classFinder.getClassList()) {

			try {
				addCommandClass(className);
			} catch(Exception e) {
				LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
			}
		}
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private static void addCommandClass(String className) throws Exception {

		Class<CommandAbstractClass> clazz = (Class<CommandAbstractClass>) Class.forName(className);

		if(isCommandClass(clazz)) {

			m_commands.put(
				clazz.getSimpleName()
				,
				new CommandTuple(
					clazz.getConstructor(m_ctor)
					,
					clazz.getMethod("help").invoke(null).toString()
					,
					clazz.getMethod("usage").invoke(null).toString()
				)
			);
		}
	}

	/*---------------------------------------------------------------------*/

	private static boolean isCommandClass(Class<?> clazz) {

		boolean result = false;

		while((clazz = clazz.getSuperclass()) != null) {

			if(clazz == CommandAbstractClass.class) {
				result = true;
				break;
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, Map<String, String> arguments) throws Exception {

		return executeCommand(command, arguments, -1);
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, Map<String, String> arguments, int transactionID) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CHECK COMMAND                                                   */
		/*-----------------------------------------------------------------*/

		if(command.startsWith("AMI")) {
			command = command.substring(3);
		}

		if(m_commands.containsKey(command) == false) {
			throw new Exception("command `" + command + "` not found");
		}

		/*-----------------------------------------------------------------*/

		String result;

		if(arguments.containsKey("help") == false) {
			/*-------------------------------------------------------------*/
			/* CREATE COMMAND INSTANCE                                     */
			/*-------------------------------------------------------------*/

			CommandAbstractClass commandObject = m_commands.get(command).x.newInstance(new Object[] {
				arguments, transactionID
			});

			/*-------------------------------------------------------------*/
			/* GET EXECUTION DATE                                          */
			/*-------------------------------------------------------------*/

			String executionDate = DateTimeFormater.format(new Date());

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
			for(Entry<String, String> entry: arguments.entrySet()) stringBuilder.append("<argument name=\"" + entry.getKey() + "\" value=\"" + entry.getValue().replace("\"", "&quot;") + "\"/>");
			stringBuilder.append("</arguments>");

			stringBuilder.append("<executionDate>" + executionDate + "</executionDate>");
			stringBuilder.append("<executionTime>" + executionTime + "</executionTime>");

			stringBuilder.append(content);

			stringBuilder.append("</AMIMessage>");

			/*-------------------------------------------------------------*/

			result = stringBuilder.toString();

			/*-------------------------------------------------------------*/
		} else {
			result = Templates.help(
				m_commands.get(command).y
				,
				m_commands.get(command).z
			);
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listCommands() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Entry<String, CommandTuple> entry: m_commands.entrySet()) {

			String command = entry.getKey();

			String clazz = entry.getValue().x.getName();
			String help = entry.getValue().y.toString();
			String usage = entry.getValue().z.toString();

			result.append(
				"<row>"
				+
				"<field name=\"command\"><![CDATA[" + command + "]]></field>"
				+
				"<field name=\"class\"><![CDATA[" + clazz + "]]></field>"
				+
				"<field name=\"help\"><![CDATA[" + help + "]]></field>"
				+
				"<field name=\"usage\"><![CDATA[" + usage + "]]></field>"
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
