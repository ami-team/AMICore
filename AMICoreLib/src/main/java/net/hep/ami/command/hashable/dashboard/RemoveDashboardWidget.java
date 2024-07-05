package net.hep.ami.command.hashable.dashboard;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class RemoveDashboardWidget extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	static TypeReference<LinkedHashMap<String, Object>> DICT_TYPE_REF = new TypeReference<>() {};

	/*----------------------------------------------------------------------------------------------------------------*/

	public RemoveDashboardWidget(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String hash = arguments.get("hash");
		String widgetId = arguments.get("widgetId");

		if(Empty.is(hash, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(widgetId, Empty.STRING_NULL_EMPTY_BLANK)
		) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String dashboard = getQuerier("self").executeSQLQuery("router_dashboard", "SELECT `json` FROM `router_dashboard` WHERE `hash` = ?0 AND `owner` = ?1", hash, m_AMIUser).getAll().get(0).getValue(0);

		if(Empty.is(dashboard, Empty.STRING_AMI_NULL))
		{
			throw new Exception("invalid dashboard");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Map<String, Object> dashboardJSON = new ObjectMapper().readValue(dashboard, DICT_TYPE_REF);

		dashboardJSON.remove(widgetId);

		/*------------------------------------------------------------------------------------------------------------*/

		dashboard = new ObjectMapper().writeValueAsString(dashboardJSON);

		/*------------------------------------------------------------------------------------------------------------*/

		return getQuerier("self").executeSQLUpdate("router_dashboard", "UPDATE `router_dashboard` SET `json` = ?0 WHERE `hash` = ?1 AND `owner` = ?2", dashboard, hash, m_AMIUser).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Remove a dashboard widget.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-hash=\"\" -widgetId=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
