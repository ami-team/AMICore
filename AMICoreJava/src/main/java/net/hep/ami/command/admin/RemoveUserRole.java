package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", secured = false)
public class RemoveUserRole extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveUserRole(Map<String, String> arguments, long transactionId)
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

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET USER ID                                                     */
		/*-----------------------------------------------------------------*/

		List<Row> rowList1 = querier.executeSQLQuery("SELECT `id` FROM `router_user` WHERE `AMIUser` = ?", user).getAll();

		if(rowList1.size() != 1)
		{
			throw new Exception("unknown user `" + user + "`");
		}

		String userID = rowList1.get(0).getValue(0);

		/*-----------------------------------------------------------------*/
		/* GET ROLE ID                                                     */
		/*-----------------------------------------------------------------*/

		List<Row> rowList2 = querier.executeSQLQuery("SELECT `id` FROM `router_role` WHERE `role` = ?", role).getAll();

		if(rowList2.size() != 1)
		{
			throw new Exception("unknown role `" + role + "`");
		}

		String roleID = rowList2.get(0).getValue(0);

		/*-----------------------------------------------------------------*/
		/* REMOVE ROLE                                                     */
		/*-----------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("DELETE FROM `router_user_role` WHERE `commandFK` = ? AND `roleFK` = ?",
			userID,
			roleID
		);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove a user role.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-user=\"\" -role=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
