package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

public class FindCommands extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public FindCommands(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET TRANSACTIONAL QUERIER                                       */
		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* FIND COMMANDS                                                   */
		/*-----------------------------------------------------------------*/

		ClassFinder classFinder = new ClassFinder("net.hep.ami");

		Collections.sort(classFinder.getClassList());

		/*-----------------------------------------------------------------*/
		/* ADD COMMANDS                                                    */
		/*-----------------------------------------------------------------*/

		for(String className: classFinder.getClassList()) {

			addCommand(transactionalQuerier, className);
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Find commands by introspection";
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private void addCommand(TransactionalQuerier transactionalQuerier, String className) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<CommandAbstractClass> clazz = (Class<CommandAbstractClass>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD COMMAND                                                     */
		/*-----------------------------------------------------------------*/

		if(ClassFinder.extendsClass(clazz, CommandAbstractClass.class)) {

			String simpleName = clazz.getSimpleName();
			String name = clazz.getName();

			String sql = String.format("INSERT INTO `router_command` (`command`,`class`) VALUES ('%s','%s') ON DUPLICATE KEY UPDATE `class`='%s'",
				simpleName,
				name,
				name
			);

			transactionalQuerier.executeUpdate(sql);
		}
	}

	/*---------------------------------------------------------------------*/
}
