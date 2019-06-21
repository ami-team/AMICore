package net.hep.ami.command.server;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class FlushCommandCache extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public FlushCommandCache(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String delay = arguments.get("delay");

		/*-----------------------------------------------------------------*/

		long t1 = System.currentTimeMillis();

		if(delay == null) {
			CacheSingleton.flush(/*-------------------*/);
		}
		else {
			CacheSingleton.flush(Integer.parseInt(delay));
		}

		long t2 = System.currentTimeMillis();

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success within " + String.format(Locale.US, "%.3f", 0.001f * (t2 - t1)) + "s]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Flush the command cache.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-delay=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
