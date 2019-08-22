package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class RemoveWidget extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RemoveWidget(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String id = arguments.get("id");


		if(id == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return getQuerier("self").executeSQLUpdate("DELETE FROM `router_dashboard` WHERE `id` = ? AND `owner` = ?", id, m_AMIUser).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Remove the given widget.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-id=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
