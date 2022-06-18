package net.hep.ami.command.server;

import java.io.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.pool.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = false)
public class GetServerStatus extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String BUILD_VERSION = System.getProperty("java.version");

	/*----------------------------------------------------------------------------------------------------------------*/

	public GetServerStatus(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"java\">")
		      .append("<row>")
		      .append("<field name=\"buildVersion\"><![CDATA[").append(BUILD_VERSION).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		String tags;
		String buildVersion;
		String branch;
		String commitId;
		String commitIdAbbrev;
		String remoteOriginURL;

		try(final InputStream inputStream = GetServerStatus.class.getClassLoader().getResourceAsStream("/git.properties"))
		{
			if(inputStream != null)
			{
				Properties properties = new Properties();

				properties.load(inputStream);

				tags = properties.getProperty("git.tags");
				buildVersion = properties.getProperty("git.build.version");
				branch = properties.getProperty("git.branch");
				commitId = properties.getProperty("git.commit.id");
				commitIdAbbrev = properties.getProperty("git.commit.id.abbrev");
				remoteOriginURL = properties.getProperty("git.remote.origin.url");
			}
			else
			{
				tags = "N/A";
				buildVersion = "N/A";
				branch = "N/A";
				commitId = "N/A";
				commitIdAbbrev = "N/A";
				remoteOriginURL = "N/A";
			}
		}
		catch(Exception e)
		{
			tags = "N/A";
			buildVersion = "N/A";
			branch = "N/A";
			commitId = "N/A";
			commitIdAbbrev = "N/A";
			remoteOriginURL = "N/A";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"ami\">")
		      .append("<row>")
		      .append("<field name=\"tags\"><![CDATA[").append(tags).append("]]></field>")
		      .append("<field name=\"buildVersion\"><![CDATA[").append(buildVersion).append("]]></field>")
		      .append("<field name=\"branch\"><![CDATA[").append(branch).append("]]></field>")
		      .append("<field name=\"commitId\"><![CDATA[").append(commitId).append("]]></field>")
		      .append("<field name=\"commitIdAbbrev\"><![CDATA[").append(commitIdAbbrev).append("]]></field>")
		      .append("<field name=\"remoteOriginURL\"><![CDATA[").append(remoteOriginURL).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		Runtime runtime = java.lang.Runtime.getRuntime();

		File file = new File(System.getProperty("catalina.base", GetServerStatus.class.getProtectionDomain().getCodeSource().getLocation().getPath()));

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"system\">")
		      .append("<row>")
		      .append("<field name=\"hostName\"><![CDATA[").append(CommandSingleton.HOSTNAME).append("]]></field>")
		      .append("<field name=\"nbOfCores\"><![CDATA[").append(runtime.availableProcessors()).append("]]></field>")
		      .append("<field name=\"freeDisk\"><![CDATA[").append(file.getFreeSpace()).append("]]></field>")
		      .append("<field name=\"totalDisk\"><![CDATA[").append(file.getTotalSpace()).append("]]></field>")
		      .append("<field name=\"freeMem\"><![CDATA[").append(runtime.freeMemory()).append("]]></field>")
		      .append("<field name=\"totalMem\"><![CDATA[").append(runtime.totalMemory()).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		result.append(ConnectionPoolSingleton.getStatus());

		/*------------------------------------------------------------------------------------------------------------*/

		result.append(CacheSingleton.getStatus());

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the server status.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
