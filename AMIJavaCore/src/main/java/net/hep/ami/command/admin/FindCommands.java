package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class FindCommands extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public FindCommands(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET TRANSACTIONAL QUERIER                                       */
		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* FIND COMMANDS                                                   */
		/*-----------------------------------------------------------------*/

		List<String> classes = new ArrayList<String>(ClassFinder.findClassNames("net.hep.ami.command"));

		Collections.sort(classes);

		/*-----------------------------------------------------------------*/
		/* ADD COMMANDS                                                    */
		/*-----------------------------------------------------------------*/

		Set<String> commands = new HashSet<String>();

		for(String className: classes)
		{
			addCommand(transactionalQuerier, commands, className);
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, " + commands + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Automatically find commands";
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private void addCommand(TransactionalQuerier transactionalQuerier, Set<String> commands, String className) throws Exception
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

			String sql = String.format("INSERT INTO `router_command` (`command`,`class`) VALUES ('%s','%s') ON DUPLICATE KEY UPDATE `class`='%s'",
				simpleName,
				name,
				name
			);

			transactionalQuerier.executeSQLUpdate(sql);

			commands.add(name);
		}
	}

	/*---------------------------------------------------------------------*/
}
