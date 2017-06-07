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

		String sql = String.format("DELETE FROM `router_user` WHERE `AMIUser`='%s'",
			amiLogin.replace("'", "''")
		);

		int nb = querier.executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			nb > 0 ? "<info><![CDATA[done with success]]></info>"
			       : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove user.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
