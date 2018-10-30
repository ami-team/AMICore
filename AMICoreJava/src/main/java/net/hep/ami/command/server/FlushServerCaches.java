package net.hep.ami.command.server;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class FlushServerCaches extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public FlushServerCaches(Set<String> roles, Map<String, String> arguments, long transactionId)
	{
		super(roles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		long t1 = System.currentTimeMillis();
		Router.reload();
		long t2 = System.currentTimeMillis();

		return new StringBuilder("<info><![CDATA[done with success in " + String.format(Locale.US, "%.3f", 0.001f * (t2 - t1)) + "s]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Flush the server caches.";
	}

	/*---------------------------------------------------------------------*/
}
