package net.hep.ami.command.server;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = false)
public class GetNodeStatus extends AbstractCommand
{
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

		RowSet rowSet = getQuerier("self").executeSQLQuery("router_monitoring", "SELECT `node`, `service`, `frequency`, `modified` FROM `router_monitoring`");

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
