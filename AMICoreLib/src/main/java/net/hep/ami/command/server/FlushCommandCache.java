package net.hep.ami.command.server;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
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
	public StringBuilder main(@NotNull Map<String, String> arguments)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		long t1 = System.currentTimeMillis();

		CacheSingleton.flush();

		long t2 = System.currentTimeMillis();

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(String.format(Locale.US, "<info><![CDATA[done with success within %.3f s]]></info>", 0.001f * (t2 - t1)));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Flush the command cache.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
