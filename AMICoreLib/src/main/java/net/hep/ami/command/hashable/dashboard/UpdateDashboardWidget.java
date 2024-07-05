package net.hep.ami.command.hashable.dashboard;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class UpdateDashboardWidget extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	static TypeReference<LinkedHashMap<String, Object>> DICT_TYPE_REF = new TypeReference<>() {};

	/*----------------------------------------------------------------------------------------------------------------*/

	public UpdateDashboardWidget(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
	@Override
	@SuppressWarnings("unchecked")
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String hash = arguments.get("hash");
		String widgetId = arguments.get("widget-id");
		String transparent = arguments.get("transparent");
		String autoRefresh = arguments.get("autoRefresh");
		String x = arguments.get("x");
		String y = arguments.get("y");
		String width = arguments.get("width");
		String height = arguments.get("height");

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

		Map<String, Object> widget = (Map<String, Object>) dashboardJSON.get(widgetId);

		if(widget != null)
		{
			if(transparent != null) {
				widget.put("transparent", "0".equals(transparent));
			}

			if(autoRefresh != null) {
				widget.put("autoRefresh", "0".equals(autoRefresh));
			}

			if(autoRefresh != null) {
				widget.put("x", Integer.valueOf(x));
			}

			if(autoRefresh != null) {
				widget.put("y", Integer.valueOf(y));
			}

			if(autoRefresh != null) {
				widget.put("width", Integer.valueOf(width));
			}

			if(autoRefresh != null) {
				widget.put("height", Integer.valueOf(height));
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		dashboard = new ObjectMapper().writeValueAsString(dashboardJSON);

		/*------------------------------------------------------------------------------------------------------------*/

		return getQuerier("self").executeSQLUpdate("router_dashboard", "UPDATE `router_dashboard` SET `json` = ?0 WHERE `hash` = ?1 AND `owner` = ?1", dashboard, hash, m_AMIUser).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Update the given dashboard widget.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-hash=\"\" -widget-hash=\"\" (-transparent=\"\")? (-autoRefresh=\"\")? (-x=\"\")? (-y=\"\")? (-width=\"\")? (-height=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
