package net.hep.ami.command.hash;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import net.hep.ami.utility.Empty;
import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class GetHashInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetHashInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		if(!Empty.is(id, Empty.STRING_NULL_EMPTY_BLANK))
		{
			rowList = getQuerier("self").executeSQLQuery("router_short_url", "SELECT `id`, `hash`, `name`, `rank`, `json`, `shared`, `expire` FROM `router_short_url` WHERE `id` = ?0 AND (`shared` = 1 OR `owner` = ?1)", id, m_AMIUser).getAll();

			if(rowList.size() != 1)
			{
				throw new Exception("undefined id `" + id + "`");
			}
		}
		else
		{
			String hash = arguments.get("hash");

			if(!Empty.is(hash, Empty.STRING_NULL_EMPTY_BLANK))
			{
				rowList = getQuerier("self").executeSQLQuery("router_short_url", "SELECT `id`, `hash`, `name`, `rank`, `json`, `shared`, `expire` FROM `router_short_url` WHERE `hash` = ?0 AND (`shared` = 1 OR `owner` = ?1)", hash, m_AMIUser).getAll();

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

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>")
		                          .append("<row>")
		                          .append("<field name=\"id\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
		                          .append("<field name=\"hash\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
		                          .append("<field name=\"name\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
		                          .append("<field name=\"rank\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
		                          .append("<field name=\"json\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
		                          .append("<field name=\"shared\"><![CDATA[").append(row.getValue(5)).append("]]></field>")
		                          .append("<field name=\"expire\"><![CDATA[").append(row.getValue(6)).append("]]></field>")
		                          .append("</row>")
		                          .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the hash information.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-id=\"\" | -hash=\"\")";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
