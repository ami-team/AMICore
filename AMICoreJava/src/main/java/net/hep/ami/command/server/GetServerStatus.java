package net.hep.ami.command.server;

import java.io.*;
import java.util.*;

import net.hep.ami.ConfigSingleton;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.utility.shell.SimpleShell;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class GetServerStatus extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetServerStatus(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		SimpleShell simpleShell = new SimpleShell();

		simpleShell.connect();
		SimpleShell.ShellTuple shellTuple = simpleShell.exec(new String[] {"hostname"});
		simpleShell.disconnect();

		String hostName = (shellTuple.errorCode == 0) ? shellTuple.inputStringBuilder.toString().trim() : "N/A";

		/*-----------------------------------------------------------------*/

		Runtime runtime = java.lang.Runtime.getRuntime();

		File file = new File(System.getProperty("catalina.base", GetServerStatus.class.getProtectionDomain().getCodeSource().getLocation().getPath()));

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"system\">")
		      .append("<row>")
		      .append("<field name=\"hostName\"><![CDATA[").append(hostName).append("]]></field>")
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
