package net.hep.ami.command.server;

import net.hep.ami.CacheSingleton;
import net.hep.ami.command.AbstractCommand;
import net.hep.ami.command.CommandMetadata;
import net.hep.ami.jdbc.Row;
import net.hep.ami.jdbc.RowSet;
import net.hep.ami.jdbc.pool.ConnectionPoolSingleton;
import net.hep.ami.utility.shell.SimpleShell;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class GetNodeStatus extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String s_hostName;

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		String hostName;

		/*------------------------------------------------------------------------------------------------------------*/

		SimpleShell simpleShell = new SimpleShell();

		try
		{
			simpleShell.connect();
			SimpleShell.ShellTuple shellTuple = simpleShell.exec(new String[] {"hostname", "-f"});
			simpleShell.disconnect();

			hostName = (shellTuple.errorCode == 0) ? shellTuple.inputStringBuilder.toString().trim()
					: "N/A"
			;
		}
		catch(Exception e)
		{
			hostName = "N/A";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		s_hostName = hostName;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public GetNodeStatus(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet = getQuerier("self").executeSQLQuery("router_monitoring", "SELECT `node`, `service`, `frequency`, `modified` FROM `router_monitoring` WHERE `node` = ?0", s_hostName);

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		result.append("<rowset>");

		for(Row row: rowSet.iterate())
		{
			result.append("<row>")
			      .append("<field name=\"node\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
			      .append("<field name=\"service\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
			      .append("<field name=\"frequency\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
			      .append("<field name=\"modified\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the status of each node.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
