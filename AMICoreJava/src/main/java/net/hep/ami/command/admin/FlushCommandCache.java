package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class FlushCommandCache extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public FlushCommandCache(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String delay = arguments.get("delay");

		if(delay == null)
		{
			CacheSingleton.flush();
		}
		else
		{
			CacheSingleton.flush(Integer.parseInt(delay));
		}

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
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
