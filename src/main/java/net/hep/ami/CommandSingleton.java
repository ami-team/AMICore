package net.hep.ami;

import java.util.*;
import java.util.Map.*;
import java.lang.reflect.*;

import net.hep.ami.jdbc.pool.TransactionPoolSingleton;
import net.hep.ami.utility.*;

public class CommandSingleton {
	/*---------------------------------------------------------------------*/

	private static class CommandTuple extends Tuple3<String, String, Constructor<CommandAbstractClass>> {

		public CommandTuple(String _x, String _y, Constructor<CommandAbstractClass> _z) {
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static Map<String, CommandTuple> m_commands = new HashMap<String, CommandTuple>();

	/*---------------------------------------------------------------------*/

	private static final Class<?>[] m_ctor = new Class<?>[] {
		Map.class,
		int.class,
	};

	/*---------------------------------------------------------------------*/

	static {

		ClassFinder classFinder = new ClassFinder("net.hep.ami");

		for(String className: classFinder.getClassList()) {

			try {
				addCommand(className);

			} catch(Exception e) {
				LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
			}
		}
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private static void addCommand(String className) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<CommandAbstractClass> clazz = (Class<CommandAbstractClass>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		if(ClassFinder.extendsClass(clazz, CommandAbstractClass.class)) {

			m_commands.put(
				clazz.getSimpleName()
				,
				new CommandTuple(
					clazz.getMethod("help").invoke(null).toString(),
					clazz.getMethod("usage").invoke(null).toString(),
					clazz.getConstructor(m_ctor)
				)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, Map<String, String> arguments) throws Exception {

		return executeCommand(command, arguments, true, TransactionPoolSingleton.bookTransactionID());
	}

	/*---------------------------------------------------------------------*/

	public static String executeCommand(String command, Map<String, String> arguments, boolean checkRoles, long transactionID) throws Exception {
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

			CommandAbstractClass commandObject = m_commands.get(command).z.newInstance(new Object[] {
				arguments, transactionID
			});

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
				m_commands.get(command).x
				,
				m_commands.get(command).y
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

			String help = entry.getValue().x.toString();
			String usage = entry.getValue().y.toString();
			String clazz = entry.getValue().z.getName();

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
