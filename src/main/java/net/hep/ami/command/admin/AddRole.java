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
		m_parent = arguments.get("parent");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_role.isEmpty()
		   ||
		   m_parent.isEmpty()
		 ) {
			throw new Exception("invalid usage");
		}

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		String sql = String.format("INSERT INTO `router_role` (`role`,`parent`) VALUES ('%s',(SELECT `id` FROM `router_role` WHERE `role`='%s'))",
			m_role.replace("'", "''"),
			m_parent.replace("'", "''")
		);

		transactionalQuerier.executeUpdate(sql);

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Add role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "-role=\"value\" -parent=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
