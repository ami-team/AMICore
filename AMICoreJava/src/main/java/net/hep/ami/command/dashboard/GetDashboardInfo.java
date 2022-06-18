package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class GetDashboardInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetDashboardInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String amiLogin = arguments.getOrDefault("amiLogin", m_AMIUser);

		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet = getQuerier("self").executeSQLQuery("router_dashboard", "SELECT `id`, `control`, `params`, `settings`, `transparent`, `autoRefresh`, `x`, `y`, `width`, `height` FROM `router_dashboard` WHERE `owner` = ?0", amiLogin);

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		result.append("<rowset>");

		for(Row row: rowSet.iterate())
		{
			result.append("<row>")
			      .append("<field name=\"id\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
			      .append("<field name=\"control\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
			      .append("<field name=\"params\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
			      .append("<field name=\"settings\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
			      .append("<field name=\"transparent\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
			      .append("<field name=\"autoRefresh\"><![CDATA[").append(row.getValue(5)).append("]]></field>")
			      .append("<field name=\"x\"><![CDATA[").append(row.getValue(6)).append("]]></field>")
			      .append("<field name=\"y\"><![CDATA[").append(row.getValue(7)).append("]]></field>")
			      .append("<field name=\"width\"><![CDATA[").append(row.getValue(8)).append("]]></field>")
			      .append("<field name=\"height\"><![CDATA[").append(row.getValue(9)).append("]]></field>")
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
		return "Get the dashboard content information.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
