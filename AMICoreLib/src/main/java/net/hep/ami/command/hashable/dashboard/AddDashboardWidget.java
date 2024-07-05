package net.hep.ami.command.hashable.dashboard;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

import net.hep.ami.data.*;
import net.hep.ami.utility.*;
import net.hep.ami.command.*;
import net.hep.ami.command.hashable.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class AddDashboardWidget extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	static TypeReference<LinkedHashMap<String, Object>> DICT_TYPE_REF = new TypeReference<>() {};

	/*----------------------------------------------------------------------------------------------------------------*/

	public AddDashboardWidget(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String hash = arguments.get("hash");
		String json = arguments.get("json");

		boolean transparent = "0".equals(arguments.getOrDefault("transparent", "0"));
		boolean autoRefresh = "0".equals(arguments.getOrDefault("autoRefresh", "0"));

		if(Empty.is(hash, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		    Empty.is(json, Empty.STRING_NULL_EMPTY_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String dashboard = getQuerier("self").executeSQLQuery("router_dashboard", "SELECT `json` FROM `router_dashboard` WHERE `hash` = ?0", hash).getAll().get(0).getValue(0);

		if(Empty.is(dashboard, Empty.STRING_AMI_NULL))
		{
			String newHash = Utilities.getNewHash();

			String newRank = Utilities.getRank(this, "router_dashboard");

			Update update = getQuerier("self").executeSQLUpdate("router_dashboard", "INSERT INTO `router_dashboard` (`hash`, `name`, `rank`, `json`, `shared`, `archived`, `owner`, `created`, `modified`) VALUES (?0, ?1, ?2, ?3, ?4, ?5, ?6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
				newHash,
				hash,
				newRank,
				dashboard,
				"0",
				"0",
				m_AMIUser
			);

			hash = newHash;
			dashboard = "{}";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String id = Utilities.getNewHash();

		Map<String, Object> dashboardJSON = new ObjectMapper().readValue(dashboard, DICT_TYPE_REF);

		Map<String, Object> controlJSON = new ObjectMapper().readValue(json, DICT_TYPE_REF);

		Map<String, Object> widget = new LinkedHashMap<>();

		widget.put("id", id);
		widget.put("control", controlJSON.get("control"));
		widget.put("params", controlJSON.get("params"));
		widget.put("options", controlJSON.get("options"));
		widget.put("transparent", transparent);
		widget.put("autoRefresh", autoRefresh);
		widget.put("x", 0);
		widget.put("y", 0);
		widget.put("width", 0);
		widget.put("height", 0);

		dashboardJSON.put(id, widget);

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
		return "Add a new dashboard widget.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-hash=\"\" -transparent=\"\" -json=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
