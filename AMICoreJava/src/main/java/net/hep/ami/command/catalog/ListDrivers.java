package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class ListDrivers extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ListDrivers(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments)
	{
		return DriverSingleton.listDrivers();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "List the available drivers.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
