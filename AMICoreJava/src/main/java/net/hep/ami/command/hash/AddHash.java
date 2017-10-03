package net.hep.ami.command.hash;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class AddHash extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddHash(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String name = arguments.get("name");
		String json = arguments.get("json");

		boolean shared = arguments.containsKey("shared");
		boolean expire = arguments.containsKey("expire");

		if(json == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		int nb = querier.executeSQLUpdate("INSERT INTO `router_short_url` (`hash`, `name`, `json`, `owner`, `shared`, `expire`, `created`) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
			hash,
			name,
			json,
			m_AMIUser,
			shared ? 1 : 0,
			expire ? 1 : 0
		);

		/*-----------------------------------------------------------------*/

		if(nb == 1)
		{
			return new StringBuilder().append("<rowset>")
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
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add a hash.";
	}

	/*---------------------------------------------------------------------*/
}
