package net.hep.ami.command.server;

import java.io.*;
import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.pool.*;

@Role(role = "AMI_GUEST", secured = false)
public class GetServerStatus extends AbstractCommand
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
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		Runtime runtime = java.lang.Runtime.getRuntime();

		File file = new File(System.getProperty("catalina.base", "/"));

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
		return "Get the server status.";
	}

	/*---------------------------------------------------------------------*/
}
