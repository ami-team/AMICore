package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class UpdateWidget extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public UpdateWidget(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String id = arguments.get("id");
		String x = arguments.get("x");
		String y = arguments.get("y");
		String width = arguments.get("width");
		String height = arguments.get("height");

		if(id == null || x == null || y == null || width == null || height == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		return getQuerier("self").executeSQLUpdate("UPDATE `router_dashboard` SET `x` = ?, `y` = ?, `width` = ?, `height` = ? WHERE `id` = ?", x, y, width, height, id).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Update the given widget.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-id=\"\" -x=\"\"-y=\"\" -width=\"\" -height=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
