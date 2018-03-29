package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = false, secured = false)
public class GetUserStats extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetUserStats(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		String sql1 = "SELECT"
		              + "(SELECT COUNT(`id`) FROM `router_user` WHERE `valid` = 1) AS `valid`"
		              + ","
		              + "(SELECT COUNT(`id`) FROM `router_user` WHERE `valid` = 0) AS `invalid`"
		;

		/*-----------------------------------------------------------------*/

		String sql2 = "SELECT `country` AS `code`, COUNT(`country`) AS `z` FROM `router_user` WHERE `valid` = 1 GROUP BY `country`";

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append(querier.executeSQLQuery(sql1).toStringBuilder(  "users"  ))
		                          .append(querier.executeSQLQuery(sql2).toStringBuilder("countries"))
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the user stats.";
	}

	/*---------------------------------------------------------------------*/
}
