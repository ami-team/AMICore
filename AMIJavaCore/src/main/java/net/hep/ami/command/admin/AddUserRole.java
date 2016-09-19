package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddUserRole extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public AddUserRole(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String user = arguments.get("user");
		String role = arguments.get("role");

		if(user == null
		   ||
		   role == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET USER ID                                                     */
		/*-----------------------------------------------------------------*/

		String sql1 = String.format("SELECT `id` FROM `router_user` WHERE `user`='%s'",
			user.replace("'", "''")
		);

		List<Row> rowList1 = transactionalQuerier.executeQuery(sql1).getAll();

		if(rowList1.size() != 1)
		{
			throw new Exception("unknown user `" + user + "`");
		}

		String userID = rowList1.get(0).getValue(0);

		/*-----------------------------------------------------------------*/
		/* GET ROLE ID                                                     */
		/*-----------------------------------------------------------------*/

		String sql2 = String.format("SELECT `id` FROM `router_role` WHERE `role`='%s'",
			role.replace("'", "''")
		);

		List<Row> rowList2 = transactionalQuerier.executeQuery(sql2).getAll();

		if(rowList2.size() != 1)
		{
			throw new Exception("unknown role `" + role + "`");
		}

		String roleID = rowList2.get(0).getValue(0);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql3 = String.format("INSERT INTO `router_user_role` (`userFK`,`roleFK`) VALUES ('%s','%s')",
			userID,
			roleID
		);

		transactionalQuerier.executeUpdate(sql3);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add user role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-user=\"value\" -role=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
