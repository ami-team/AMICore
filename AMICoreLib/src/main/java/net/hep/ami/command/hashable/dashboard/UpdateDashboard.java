package net.hep.ami.command.hashable.dashboard;

import java.util.*;

import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class UpdateDashboard extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public UpdateDashboard(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String id = arguments.get("id");
		String transparent = arguments.get("transparent");
		String x = arguments.get("x");
		String y = arguments.get("y");
		String width = arguments.get("width");
		String height = arguments.get("height");

		if(id == null || transparent == null || x == null || y == null || width == null || height == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		return getQuerier("self").executeSQLUpdate("router_dashboard", "UPDATE `router_dashboard` SET `transparent` = ?0, `x` = ?1, `y` = ?2, `width` = ?3, `height` = ?4 WHERE `id` = ?5 AND `owner` = ?6", transparent, x, y, width, height, id, m_AMIUser).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Update the given widget.";
	}

	/*---------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-id=\"\" -transparent=\"\" -x=\"\"-y=\"\" -width=\"\" -height=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
