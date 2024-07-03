package net.hep.ami.command.dashboard;

import net.hep.ami.command.AbstractCommand;
import net.hep.ami.command.CommandMetadata;
import net.hep.ami.data.Update;
import net.hep.ami.utility.Empty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@CommandMetadata(role = "AMI_USER", visible = true)
public class AddDashboardWidget extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddDashboardWidget(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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
		return "Add a new dashboard widget.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-hash=\"\" -transparent=\"\" -json=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
