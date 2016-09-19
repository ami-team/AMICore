package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddRole extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public AddRole(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String parent = arguments.containsKey("parent") ? arguments.get("parent")
		                                                : "AMI_guest_role"
		;

		String role = arguments.get("role");

		String roleValidatorClass = arguments.containsKey("roleValidatorClass") ? arguments.get("roleValidatorClass")
		                                                                        : ""
		;

		if(role == null)
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
			parent.replace("'", "''")
		);

		List<Row> rowList = transactionalQuerier.executeQuery(sql1).getAll();

		if(rowList.size() != 1)
		{
			throw new Exception("unknown role `" + parent + "`");
		}

		String parentLft = rowList.get(0).getValue(0);
		String parentRgt = rowList.get(0).getValue(1);

		/*-----------------------------------------------------------------*/
		/* UPDATE TREE                                                     */
		/*-----------------------------------------------------------------*/

		String sql2 = String.format("UPDATE `router_role` SET `lft` = `lft` + 2 WHERE (`lft` > %s) ORDER BY `lft` DESC",
			parentLft
		);

		transactionalQuerier.executeUpdate(sql2);

		/*-----------------------------------------------------------------*/

		String sql3 = String.format("UPDATE `router_role` SET `rgt` = `rgt` + 2 WHERE (`rgt` >= %s) OR ((`rgt` > %s + 1) AND (`rgt` < %s)) ORDER BY `rgt` DESC",
			parentRgt,
			parentLft,
			parentRgt
		);

		transactionalQuerier.executeUpdate(sql3);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql4 = String.format("INSERT INTO `router_role` (`lft`,`rgt`,`role`,`validatorClass`) VALUES (%s+1,%s+2,'%s','%s')",
			parentLft,
			parentLft,
			role.replace("'", "''"),
			roleValidatorClass.replace("'", "''")
		);

		transactionalQuerier.executeUpdate(sql4);

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
