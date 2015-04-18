package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

public class AddRole extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_role;
	private String m_parent;

	/*---------------------------------------------------------------------*/

	public AddRole(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_role = arguments.get("role");

		m_parent = arguments.containsKey("parent") ? arguments.get("parent")
		                                           : "AMI_guest_role"
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_role.isEmpty()) {
			throw new Exception("invalid usage");
		}

		String sql;

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET PARENT ID                                                   */
		/*-----------------------------------------------------------------*/

		sql = String.format("SELECT `id` FROM `router_role` WHERE `role`='%s'",
			m_parent.replace("'", "''")
		);

		QueryResult queryResult = transactionalQuerier.executeSQLQuery(sql);

		if(queryResult.getNumberOfRows() != 1) {
			throw new Exception("unknown role `" + m_parent + "`");
		}

		String id = queryResult.getValue(0, 0);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		sql = String.format("INSERT INTO `router_role` (`role`,`parent`) VALUES ('%s',%s)",
			m_role.replace("'", "''"),
			id
		);

		transactionalQuerier.executeUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Add role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "-role=\"value\" (-parent=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
