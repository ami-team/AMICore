package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddCommandRole extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_command;
	private String m_role;

	/*---------------------------------------------------------------------*/

	public AddCommandRole(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_command = arguments.get("command");
		m_role = arguments.get("role");
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

		String sql3 = String.format("INSERT INTO `router_command_role` (`commandFK`,`roleFK`) VALUES ('%s','%s')",
			commandID,
			roleID
		);

		transactionalQuerier.executeSQLUpdate(sql3);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Add command role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "-command=\"value\" -role=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
