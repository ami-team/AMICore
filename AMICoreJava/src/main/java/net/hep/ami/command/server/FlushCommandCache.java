package net.hep.ami.command.server;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class FlushCommandCache extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public FlushCommandCache(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String delay = arguments.get("delay");

		/*------------------------------------------------------------------------------------------------------------*/

		long t1 = System.currentTimeMillis();

		if(delay == null) {
			CacheSingleton.flush(/*-------------------*/);
		}
		else {
			CacheSingleton.flush(Integer.parseInt(delay));
		}

		long t2 = System.currentTimeMillis();

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success within " + String.format(Locale.US, "%.3f", 0.001f * (t2 - t1)) + "s]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Flush the command cache.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "(-delay=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
