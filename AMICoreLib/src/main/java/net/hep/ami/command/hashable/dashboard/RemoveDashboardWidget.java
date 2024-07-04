package net.hep.ami.command.hashable.dashboard;

import net.hep.ami.command.AbstractCommand;
import net.hep.ami.command.CommandMetadata;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

@CommandMetadata(role = "AMI_USER", visible = true)
public class RemoveDashboardWidget extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RemoveDashboardWidget(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		return new StringBuilder("<error><![CDATA[nothing done]]></error>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Remove a dashboard widget.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-id=\"\" | -hash=\"\") -???=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
