package net.hep.ami.command.hashable.hash;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class ListHashes extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ListHashes(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet = getQuerier("self").executeSQLQuery("router_short_url", "SELECT `id`, `hash`, `name`, `rank`, `json`, `shared`, `expire` FROM `router_short_url` WHERE `owner` = ?0  AND (`shared` = 1 OR `owner` = ?1) ORDER BY `rank`", amiLogin, m_AMIUser);

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		result.append("<rowset>");

		for(Row row: rowSet.iterate())
		{
			result.append("<row>")
			      .append("<field name=\"id\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
			      .append("<field name=\"hash\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
			      .append("<field name=\"name\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
			      .append("<field name=\"rank\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
			      .append("<field name=\"json\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
			      .append("<field name=\"shared\"><![CDATA[").append(row.getValue(5)).append("]]></field>")
			      .append("<field name=\"expire\"><![CDATA[").append(row.getValue(6)).append("]]></field>")
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
		return "List the user hashes.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-amiLogin=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
