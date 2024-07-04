package net.hep.ami.command.hashable.dashboard;

import java.util.*;

import net.hep.ami.command.hashable.Utilities;
import net.hep.ami.data.*;
import net.hep.ami.command.*;

import net.hep.ami.utility.*;
import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class AddDashboard extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddDashboard(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String name = arguments.get("name");
		String json = arguments.get("json");

		String shared = arguments.getOrDefault("shared", "0");
		String archived = arguments.getOrDefault("archived", "0");

		if(Empty.is(json, Empty.STRING_NULL_EMPTY_BLANK))
		{
			throw new Exception("invalid usage");
		}

		/*----------------------------------------------------------------------------------------------------------------*/

		String hash = Utilities.getNewHash();

		String rank = Utilities.getRank(this, "router_dashboard");

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_dashboard", "INSERT INTO `router_dashboard` (`hash`, `name`, `rank`, `json`, `shared`, `archived`, `owner`, `created`, `modified`) VALUES (?0, ?1, ?2, ?3, ?4, ?5, ?6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
			hash,
			name,
			rank,
			json,
			shared,
			archived,
			m_AMIUser
		);

		/*------------------------------------------------------------------------------------------------------------*/

		if(update.getNbOfUpdatedRows() == 1)
		{
			return new StringBuilder().append("<info><![CDATA[done with success]]></info>")
			                          .append("<rowset>")
			                          .append("<row>")
			                          .append("<field name=\"hash\"><![CDATA[").append(hash).append("]]></field>")
			                          .append("</row>")
			                          .append("</rowset>")
			;
		}
		else
		{
			return new StringBuilder("<error><![CDATA[nothing done]]></error>");
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Add a new dashboard.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-name=\"\" -json=\"\" (-shared=\"\")? (-archived=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
