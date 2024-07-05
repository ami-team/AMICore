package net.hep.ami.command.hashable.dashboard;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import net.hep.ami.utility.*;
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
		List<Row> rowList;

		/*------------------------------------------------------------------------------------------------------------*/

		String amiLogin = arguments.getOrDefault("amiLogin", m_AMIUser);

		/*------------------------------------------------------------------------------------------------------------*/

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String id = arguments.get("id");

		if(!Empty.is(id, Empty.STRING_NULL_EMPTY_BLANK))
		{
			rowList = getQuerier("self").executeSQLQuery("router_dashboard", "SELECT `id`, `hash`, `name`, `rank`, `json`, `shared`, `archived`, `owner` FROM `router_dashboard` WHERE `id` = ?0 AND `owner` = ?1 AND (`shared` = 1 OR `owner` = ?1)", id, amiLogin, m_AMIUser).getAll();

			if(rowList.size() != 1)
			{
				throw new Exception("undefined id `" + id + "`");
			}
		}
		else
		{
			String hash = arguments.get("hash");

			rowList = getQuerier("self").executeSQLQuery("router_dashboard", "SELECT `id`, `hash`, `name`, `rank`, `json`, `shared`, `archived`, `owner` FROM `router_dashboard` WHERE `hash` = ?0 AND `owner` = ?1 AND (`shared` = 1 OR `owner` = ?1)", hash, amiLogin, m_AMIUser).getAll();

			if(rowList.size() != 1)
			{
				throw new Exception("undefined name `" + hash + "`");
			}
			else
			{
				throw new Exception("invalid usage");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Row row = rowList.get(0);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>")
		                          .append("<field name=\"id\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
		                          .append("<field name=\"hash\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
		                          .append("<field name=\"name\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
		                          .append("<field name=\"rank\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
		                          .append("<field name=\"json\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
		                          .append("<field name=\"shared\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
		                          .append("<field name=\"archived\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
		                          .append("<field name=\"owner\"><![CDATA[").append(row.getValue(5)).append("]]></field>")
		                          .append("</row>")
		;

		/*------------------------------------------------------------------------------------------------------------*/
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
