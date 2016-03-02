package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.TransactionalQuerier;

public class GetUserStats extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public GetUserStats(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		result.append(transactionalQuerier.executeSQLQuery("SELECT (SELECT COUNT(`id`) FROM `router_user` WHERE `valid`=1) AS `valid`, (SELECT COUNT(`id`) FROM `router_user` WHERE `valid`=0) AS `invalid`").toStringBuilder("users"));

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		result.append(transactionalQuerier.executeSQLQuery("SELECT `country` AS `code`, COUNT(`country`) AS `z` FROM `router_user` WHERE `valid`=1 GROUP BY `country`").toStringBuilder("countries"));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get user stats.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "";
	}

	/*---------------------------------------------------------------------*/
}
