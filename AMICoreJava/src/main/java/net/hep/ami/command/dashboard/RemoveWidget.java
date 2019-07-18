package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class RemoveWidget extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveWidget(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String id = arguments.get("id");


		if(id == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		return getQuerier("self").executeSQLUpdate("DELETE FROM `router_dashboard` WHERE `id` = ? AND `owner = ?`", id, m_AMIUser).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove the given widget.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-id=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
