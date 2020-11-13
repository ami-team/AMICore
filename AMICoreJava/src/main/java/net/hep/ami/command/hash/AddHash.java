package net.hep.ami.command.hash;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class AddHash extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddHash(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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
		String expire = arguments.getOrDefault("expire", "0");

		if(json == null)
		{
			throw new Exception("invalid usage");
		}

		/*----------------------------------------------------------------------------------------------------------------*/

		long uuid;

		String hash;

		for(;;)
		{
			uuid = UUID.randomUUID().getMostSignificantBits();

			hash = Base64.getEncoder().encodeToString(Long.toString(uuid < 0 ? -uuid : +uuid).getBytes());

			if(hash.length() >= 8)
			{
				hash = hash.substring(0, 8);

				break;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String rank = getQuerier("self").executeSQLQuery("router_short_url", "SELECT max(`rank`) + 1 FROM `router_short_url` WHERE `owner` = ?0", m_AMIUser).getAll().get(0).getValue(0);

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_short_url", "INSERT INTO `router_short_url` (`hash`, `name`, `rank`, `json`, `shared`, `expire`, `owner`, `created`, `modified`) VALUES (?0, ?1, ?2, ?3, ?4, ?5, ?6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
			hash,
			name,
			rank,
			json,
			shared,
			expire,
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
		return "Add a hash.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-name=\"\" -json=\"\" (-shared=\"\")? (-expire=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
