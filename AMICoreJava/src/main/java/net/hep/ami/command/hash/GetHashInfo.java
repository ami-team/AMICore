package net.hep.ami.command.hash;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
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
		String hash = arguments.get("hash");

		if(hash == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList = getQuerier("self").executeSQLQuery("router_short_url", "SELECT `id`, `hash`, `name`, `rank`, `json` FROM `router_short_url` WHERE `hash` = ?0 AND (`shared` = 1 OR `owner` = ?1)", hash, m_AMIUser).getAll();

		if(rowList.size() != 1)
		{
				throw new Exception("undefined hash `" + hash + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>")
		                          .append("<row>")
		                          .append("<field name=\"id\"><![CDATA[").append(rowList.get(0).getValue(0)).append("]]></field>")
		                          .append("<field name=\"hash\"><![CDATA[").append(rowList.get(0).getValue(1)).append("]]></field>")
		                          .append("<field name=\"name\"><![CDATA[").append(rowList.get(0).getValue(2)).append("]]></field>")
		                          .append("<field name=\"rank\"><![CDATA[").append(rowList.get(0).getValue(3)).append("]]></field>")
		                          .append("<field name=\"json\"><![CDATA[").append(rowList.get(0).getValue(4)).append("]]></field>")
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
		return "-hash=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
