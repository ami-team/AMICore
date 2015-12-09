package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddCommandRole extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_command;
	private String m_role;

	/*---------------------------------------------------------------------*/

	public AddCommandRole(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);

		m_command = arguments.get("command");
		m_role = arguments.get("role");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
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

		List<Row> rowList1 = transactionalQuerier.executeSQLQuery(sql1).getAll();

		if(rowList1.size() != 1)
		{
			throw new Exception("unknown command `" + m_command + "`");
		}

		String commandID = rowList1.get(0).getValue(0);

		/*-----------------------------------------------------------------*/
		/* GET ROLE ID                                                     */
		/*-----------------------------------------------------------------*/

		String sql2 = String.format("SELECT `id` FROM `router_role` WHERE `role`='%s'",
			m_role.replace("'", "''")
		);

		List<Row> rowList2 = transactionalQuerier.executeSQLQuery(sql2).getAll();

		if(rowList2.size() != 1)
		{
			throw new Exception("unknown role `" + m_role + "`");
		}

		String roleID = rowList2.get(0).getValue(0);

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

	public static String help()
	{
		return "Add command role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-command=\"value\" -role=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
