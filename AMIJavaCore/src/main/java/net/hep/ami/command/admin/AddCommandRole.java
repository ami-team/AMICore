package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.role.commandValidator.*;

public class AddCommandRole extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_command;
	private String m_role;
	private String m_roleValidatorClass;

	/*---------------------------------------------------------------------*/

	public AddCommandRole(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_command = arguments.get("command");
		m_role = arguments.get("role");
		m_roleValidatorClass = arguments.get("roleValidatorClass");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_command == null
		   ||
		   m_role == null
		 ) {
			throw new Exception("invalid usage");
		}

		if(m_roleValidatorClass != null) {

			Class<?> clazz = Class.forName(m_roleValidatorClass);

			if(ClassFinder.extendsClass(clazz, CommandRoleValidatorInterface.class) == false) {

				throw new Exception("class `" + m_roleValidatorClass + "` must implement `" + CommandRoleValidatorInterface.class.getName() + "`");
			}
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET COMMAND ID                                                  */
		/*-----------------------------------------------------------------*/

		String sql1 = String.format("SELECT `id` FROM `router_command` WHERE `command`='%s'",
			m_command.replace("'", "''")
		);

		QueryResult queryResult1 = transactionalQuerier.executeSQLQuery(sql1);

		if(queryResult1.getNumberOfRows() != 1) {
			throw new Exception("unknown command `" + m_command + "`");
		}

		String commandID = queryResult1.getValue(0, 0);

		/*-----------------------------------------------------------------*/
		/* GET ROLE ID                                                     */
		/*-----------------------------------------------------------------*/

		String sql2 = String.format("SELECT `id` FROM `router_role` WHERE `role`='%s'",
			m_role.replace("'", "''")
		);

		QueryResult queryResult2 = transactionalQuerier.executeSQLQuery(sql2);

		if(queryResult2.getNumberOfRows() != 1) {
			throw new Exception("unknown role `" + m_role + "`");
		}

		String roleID = queryResult2.getValue(0, 0);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql3;

		if(m_roleValidatorClass == null) {
			sql3 = String.format("INSERT INTO `router_command_role` (`commandFK`,`roleFK`) VALUES ('%s','%s')",
				commandID,
				roleID
			);
		} else {
			sql3 = String.format("INSERT INTO `router_command_role` (`commandFK`,`roleFK`,`roleValidatorClass`) VALUES ('%s','%s','%s')",
				commandID,
				roleID,
				m_roleValidatorClass.replace("'", "''")
			);
		}

		transactionalQuerier.executeUpdate(sql3);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Add command role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "-command=\"value\" -role=\"value\" (-roleValidatorClass=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
