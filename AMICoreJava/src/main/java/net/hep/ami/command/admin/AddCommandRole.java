package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddCommandRole extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddCommandRole(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String command = arguments.get("command");
		String role = arguments.get("role");

		if(command == null
		   ||
		   role == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET COMMAND ID                                                  */
		/*-----------------------------------------------------------------*/

		String sql1 = String.format("SELECT `id` FROM `router_command` WHERE `command`='%s'",
			command.replace("'", "''")
		);

		List<Row> rowList1 = querier.executeQuery(sql1).getAll();

		if(rowList1.size() != 1)
		{
			throw new Exception("unknown command `" + command + "`");
		}

		String commandID = rowList1.get(0).getValue(0);

		/*-----------------------------------------------------------------*/
		/* GET ROLE ID                                                     */
		/*-----------------------------------------------------------------*/

		String sql2 = String.format("SELECT `id` FROM `router_role` WHERE `role`='%s'",
			role.replace("'", "''")
		);

		List<Row> rowList2 = querier.executeQuery(sql2).getAll();

		if(rowList2.size() != 1)
		{
			throw new Exception("unknown role `" + role + "`");
		}

		String roleID = rowList2.get(0).getValue(0);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql3 = String.format("INSERT INTO `router_command_role` (`commandFK`,`roleFK`) VALUES ('%s','%s')",
			commandID,
			roleID
		);

		int nb = querier.executeUpdate(sql3);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			nb > 0 ? "<info><![CDATA[done with success]]></info>"
			       : "<error><![CDATA[nothing done]]></error>"
		);
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
