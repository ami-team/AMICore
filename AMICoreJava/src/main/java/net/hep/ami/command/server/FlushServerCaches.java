package net.hep.ami.command.server;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class FlushServerCaches extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public FlushServerCaches(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments)
	{
		long t1 = System.currentTimeMillis();
		RouterQuerier.reload(arguments.containsKey("full"));
		long t2 = System.currentTimeMillis();

		return new StringBuilder(String.format(Locale.US, "<info><![CDATA[done with success within %.3f s]]></info>", 0.001f * (t2 - t1)));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Flush the server caches.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-full)?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
