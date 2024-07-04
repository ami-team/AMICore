package net.hep.ami.command.hashable.hash;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class UpdateHash extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public UpdateHash(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		String id = arguments.get("id");

		if(id != null)
		{
			rowList = getQuerier("self").executeSQLQuery("router_short_url", "SELECT `id`, `name`, `rank`, `json`, `shared`, `expire` FROM `router_short_url` WHERE `id` = ?0 AND (`shared` = 1 OR `owner` = ?1)", id, m_AMIUser).getAll();

			if(rowList.size() != 1)
			{
				throw new Exception("undefined id `" + id + "`");
			}
		}
		else
		{
			String hash = arguments.get("hash");

			if(hash != null)
			{
				rowList = getQuerier("self").executeSQLQuery("router_short_url", "SELECT `id`, `name`, `rank`, `json`, `shared`, `expire` FROM `router_short_url` WHERE `hash` = ?0 AND (`shared` = 1 OR `owner` = ?1)", hash, m_AMIUser).getAll();

				if(rowList.size() != 1)
				{
					throw new Exception("undefined hash `" + hash + "`");
				}
			}
			else
			{
				throw new Exception("invalid usage");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Row row = rowList.get(0);

		id = row.getValue(0);

		String hash = arguments.getOrDefault("name", row.getValue(1));
		String rank = arguments.getOrDefault("rank", row.getValue(2));
		String json = arguments.getOrDefault("json", row.getValue(3));
		String shared = arguments.getOrDefault("shared", row.getValue(4));
		String expire = arguments.getOrDefault("expire", row.getValue(5));

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("router_short_url", "UPDATE `router_short_url` SET `name` = ?1, `rank` = ?2, `json` = ?3, `shared` = ?4, `expire` = ?5 WHERE `id` = ?0", id, hash, rank, json, shared, expire);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Update a hash.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-id=\"\" | -hash=\"\") (-name=\"\")? (-rank=\"\")? (-json=\"\")? (-shared=\"\")? (-expire=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
