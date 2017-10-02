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
		String hash = generateHash();

		String json = arguments.get("json");

		boolean shared = arguments.containsKey("shared");
		boolean expire = arguments.containsKey("expire");

		if(json == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		int nb = querier.executeSQLUpdate("INSERT INTO `router_short_url` (`hash`, `json`, `owner`, `shared`, `expire`, `created`) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
			hash,
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

	private static String generateHash()
	{
		long hash;

		String result;

		for(;;)
		{
			hash = UUID.randomUUID().getMostSignificantBits();

			result = Base64.getEncoder().encodeToString(Long.toString(hash < 0 ? -hash : +hash).getBytes());

			if(result.length() >= 8)
			{
				break;
			}
		}

		return result.substring(0, 8);
	}

	/*---------------------------------------------------------------------*/
}
