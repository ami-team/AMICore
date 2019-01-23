package net.hep.ami.command.hash;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListHashes extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListHashes(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		RowSet rowSet = querier.executeSQLQuery(true, "SELECT `hash`, `name`, `rank` FROM `router_short_url` WHERE `createdBy` = ?", m_AMIUser);

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		result.append("<rowset>");

		for(Row row: rowSet.iterate())
		{
			result.append("<row>")
			      .append("<field name=\"hash\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
			      .append("<field name=\"name\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
			      .append("<field name=\"rank\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the user hashes.";
	}

	/*---------------------------------------------------------------------*/
}
