package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
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

		return getQuerier("self").executeSQLUpdate("router_dashboard", "DELETE FROM `router_dashboard` WHERE `id` = ?0 AND `owner` = ?1", id, m_AMIUser).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Remove the given widget.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-id=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
