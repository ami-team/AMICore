package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.Update;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class AddWidget extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddWidget(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String control = arguments.get("control");
		String params = arguments.get("params");
		String settings = arguments.get("settings");

		if(control == null || params == null || settings == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("INSERT INTO `router_dashboard` (`control`, `params`, `settings`, `transparent`, `autoRefresh`, `owner`, `created`, `modified`) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", control, params, settings, arguments.containsKey("transparent"), arguments.containsKey("autoRefresh"), m_AMIUser);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add a new widget in the user dashboard.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-control=\"\" -params=\"\" -settings=\"\" (-transparent)? (-autoRefresh)?";
	}

	/*---------------------------------------------------------------------*/
}
