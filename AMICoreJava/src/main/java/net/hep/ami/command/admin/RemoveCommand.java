package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class RemoveCommand extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveCommand(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String command = arguments.get("command");

		if(command == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("DELETE FROM `router_command` WHERE `command` = ?",
			command
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
		return "Remove a command.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-command=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
