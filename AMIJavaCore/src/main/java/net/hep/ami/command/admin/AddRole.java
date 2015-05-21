package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class AddRole extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_parent;
	private String m_role;
	private String m_roleValidatorClass;

	/*---------------------------------------------------------------------*/

	public AddRole(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_parent = arguments.containsKey("parent") ? arguments.get("parent")
		                                           : "AMI_guest_role"
		;

		m_role = arguments.get("role");

		m_roleValidatorClass = arguments.get("roleValidatorClass");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_role == null) {
			throw new Exception("invalid usage");
		}

		if(m_roleValidatorClass != null) {

			Class<?> clazz = Class.forName(m_roleValidatorClass);

			if(ClassFinder.extendsClass(clazz, RoleValidatorInterface.class) == false) {

				throw new Exception("class `" + m_roleValidatorClass + "` must implement `" + RoleValidatorInterface.class.getName() + "`");
			}
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET PARENT ID                                                   */
		/*-----------------------------------------------------------------*/

		String sql1 = String.format("SELECT `id` FROM `router_role` WHERE `role`='%s'",
			m_parent.replace("'", "''")
		);

		QueryResult queryResult = transactionalQuerier.executeSQLQuery(sql1);

		if(queryResult.getNumberOfRows() != 1) {
			throw new Exception("unknown role `" + m_parent + "`");
		}

		String parentID = queryResult.getValue(0, 0);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql2;

		if(m_roleValidatorClass == null) {

			sql2 = String.format("INSERT INTO `router_role` (`parentFK`,`role`) VALUES ('%s','%s')",
					parentID.replace("'", "''"),
					m_role.replace("'", "''")
			);

		} else {

			sql2 = String.format("INSERT INTO `router_role` (`parentFK`,`role`,`roleValidatorClass`) VALUES ('%s','%s','%s')",
					parentID.replace("'", "''"),
					m_role.replace("'", "''"),
					m_roleValidatorClass.replace("'", "''")
			);
		}

		transactionalQuerier.executeSQLUpdate(sql2);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Add role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "(-parent=\"value\")? -role=\"value\" (-roleValidatorClass=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
