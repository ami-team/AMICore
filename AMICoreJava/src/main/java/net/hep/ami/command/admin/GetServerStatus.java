package net.hep.ami.command.admin;

import java.io.*;
import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.pool.*;

public class GetServerStatus extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public GetServerStatus(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Runtime runtime = Runtime.getRuntime();

		File file = new File(System.getProperty("catalina.base", "/"));

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(
			"<rowset type=\"system\">"
			+
			"<row>"
			+
			"<field name=\"freeDisk\">" + file.getFreeSpace() + "</field>"
			+
			"<field name=\"totalDisk\">" + file.getTotalSpace() + "</field>"
			+
			"<field name=\"freeMem\">" + runtime.freeMemory() + "</field>"
			+
			"<field name=\"totalMem\">" + runtime.totalMemory() + "</field>"
			+
			"<field name=\"nbOfCPUs\">" + runtime.availableProcessors() + "</field>"
			+
			"</row>"
			+
			"</rowset>"
		);

		/*-----------------------------------------------------------------*/

		result.append(ConnectionPoolSingleton.getStatus());

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get connection pool status.";
	}

	/*---------------------------------------------------------------------*/
}
