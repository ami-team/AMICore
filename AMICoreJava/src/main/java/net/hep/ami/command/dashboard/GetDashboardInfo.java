package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetDashboardInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetDashboardInfo(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		RowSet rowSet = querier.executeSQLQuery("router_dashboard", "SELECT `id`, `control`, `params`, `settings`, `autoRefresh`, `x`, `y`, `width`, `height` FROM `router_dashboard` WHERE `owner` = ?", m_AMIUser);

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		result.append("<rowset>");

		for(Row row: rowSet.iterate())
		{
			result.append("<row>")
			      .append("<field name=\"id\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
			      .append("<field name=\"control\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
			      .append("<field name=\"params\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
			      .append("<field name=\"settings\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
			      .append("<field name=\"autoRefresh\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
			      .append("<field name=\"x\"><![CDATA[").append(row.getValue(5)).append("]]></field>")
			      .append("<field name=\"y\"><![CDATA[").append(row.getValue(6)).append("]]></field>")
			      .append("<field name=\"width\"><![CDATA[").append(row.getValue(7)).append("]]></field>")
			      .append("<field name=\"height\"><![CDATA[").append(row.getValue(8)).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the dashboard content information.";
	}

	/*---------------------------------------------------------------------*/
}
