package net.hep.ami.command.hash;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class GetHashInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetHashInfo(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String hash = arguments.get("hash");

		if(hash == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		List<Row> rowList = querier.executeSQLQuery("SELECT `name`, `json` FROM `router_short_url` WHERE `hash` = ? AND (`shared` = 1 OR `owner` = ?)", hash, m_AMIUser).getAll();

		if(rowList.size() != 1)
		{
				throw new Exception("undefined hash `" + hash + "`");
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>")
		                          .append("<row>")
		                          .append("<field name=\"name\"><![CDATA[").append(rowList.get(0).getValue(0)).append("]]></field>")
		                          .append("<field name=\"json\"><![CDATA[").append(rowList.get(0).getValue(1)).append("]]></field>")
		                          .append("</row>")
		                          .append("</rowset>")
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the hash information.";
	}

	/*---------------------------------------------------------------------*/
}
