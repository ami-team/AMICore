package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class RemoveUser extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveUser(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");

		if(amiLogin == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("DELETE FROM `router_user` WHERE `AMIUser` = ?",
			amiLogin
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
		return "Remove a user.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
