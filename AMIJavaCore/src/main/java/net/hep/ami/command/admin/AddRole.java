package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddRole extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_parent;
	private String m_role;
	private String m_roleValidatorClass;

	/*---------------------------------------------------------------------*/

	public AddRole(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);

		m_parent = arguments.containsKey("parent") ? arguments.get("parent")
		                                           : "AMI_guest_role"
		;

		m_role = arguments.get("role");

		m_roleValidatorClass = arguments.containsKey("roleValidatorClass") ? arguments.get("roleValidatorClass")
		                                                                   : ""
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_role == null)
		{
			throw new Exception("invalid usage");
		}

		/* !!! CHECK VALIDATOR !!! */

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET PARENT ID                                                   */
		/*-----------------------------------------------------------------*/

		String sql1 = String.format("SELECT `lft`,`rgt` FROM `router_role` WHERE `role`='%s'",
			m_parent.replace("'", "''")
		);

		QueryResult queryResult = transactionalQuerier.executeSQLQuery(sql1);

		if(queryResult.getNumberOfRows() != 1)
		{
			throw new Exception("unknown role `" + m_parent + "`");
		}

		String parentLft = queryResult.getValue(0, 0);
		String parentRgt = queryResult.getValue(0, 1);

		/*-----------------------------------------------------------------*/
		/* UPDATE TREE                                                     */
		/*-----------------------------------------------------------------*/

		String sql2 = String.format("UPDATE `router_role` SET `lft` = `lft` + 2 WHERE (`lft` > %s) ORDER BY `lft` DESC",
			parentLft
		);

		transactionalQuerier.executeSQLUpdate(sql2);

		/*-----------------------------------------------------------------*/

		String sql3 = String.format("UPDATE `router_role` SET `rgt` = `rgt` + 2 WHERE (`rgt` >= %s) OR ((`rgt` > %s + 1) AND (`rgt` < %s)) ORDER BY `rgt` DESC",
			parentRgt,
			parentLft,
			parentRgt
		);

		transactionalQuerier.executeSQLUpdate(sql3);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql4 = String.format("INSERT INTO `router_role` (`lft`,`rgt`,`role`,`validatorClass`) VALUES (%s+1,%s+2,'%s','%s')",
			parentLft,
			parentLft,
			m_role.replace("'", "''"),
			m_roleValidatorClass.replace("'", "''")
		);

		transactionalQuerier.executeSQLUpdate(sql4);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-parent=\"value\")? -role=\"value\" (-roleValidatorClass=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
