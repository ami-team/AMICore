package net.hep.ami.command.server;

import java.io.*;
import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.pool.*;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class GetServerStatus extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetServerStatus(Set<String> roles, Map<String, String> arguments, long transactionId)
	{
		super(roles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		Runtime runtime = java.lang.Runtime.getRuntime();

		File file = new File(System.getProperty("catalina.base", GetServerStatus.class.getProtectionDomain().getCodeSource().getLocation().getPath()));

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"system\">")
		      .append("<row>")
		      .append("<field name=\"freeDisk\"><![CDATA[").append(file.getFreeSpace()).append("]]></field>")
		      .append("<field name=\"totalDisk\"><![CDATA[").append(file.getTotalSpace()).append("]]></field>")
		      .append("<field name=\"freeMem\"><![CDATA[").append(runtime.freeMemory()).append("]]></field>")
		      .append("<field name=\"totalMem\"><![CDATA[").append(runtime.totalMemory()).append("]]></field>")
		      .append("<field name=\"nbOfCPUs\"><![CDATA[").append(runtime.availableProcessors()).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

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
