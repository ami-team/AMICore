package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddUserRole extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_user;
	private String m_role;

	/*---------------------------------------------------------------------*/

	public AddUserRole(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);

		m_user = arguments.get("user");
		m_role = arguments.get("role");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_user == null
		   ||
		   m_role == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET USER ID                                                     */
		/*-----------------------------------------------------------------*/

		String sql1 = String.format("SELECT `id` FROM `router_user` WHERE `user`='%s'",
			m_user.replace("'", "''")
		);

		QueryResult queryResult1 = transactionalQuerier.executeSQLQuery(sql1);

		if(queryResult1.getNumberOfRows() != 1)
		{
			throw new Exception("unknown user `" + m_user + "`");
		}

		String userID = queryResult1.getValue(0, 0);

		/*-----------------------------------------------------------------*/
		/* GET ROLE ID                                                     */
		/*-----------------------------------------------------------------*/

		String sql2 = String.format("SELECT `id` FROM `router_role` WHERE `role`='%s'",
			m_role.replace("'", "''")
		);

		QueryResult queryResult2 = transactionalQuerier.executeSQLQuery(sql2);

		if(queryResult2.getNumberOfRows() != 1)
		{
			throw new Exception("unknown role `" + m_role + "`");
		}

		String roleID = queryResult2.getValue(0, 0);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql3 = String.format("INSERT INTO `router_user_role` (`userFK`,`roleFK`) VALUES ('%s','%s')",
			userID,
			roleID
		);

		transactionalQuerier.executeSQLUpdate(sql3);

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
