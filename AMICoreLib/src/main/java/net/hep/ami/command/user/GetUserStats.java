package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = false)
public class GetUserStats extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetUserStats(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		String sql1 = "SELECT"
		              + "(SELECT COUNT(`id`) FROM `router_user` WHERE `valid` = 1) AS `valid`"
		              + ","
		              + "(SELECT COUNT(`id`) FROM `router_user` WHERE `valid` = 0) AS `invalid`"
		;

		/*------------------------------------------------------------------------------------------------------------*/

		String sql2 = "SELECT `country` AS `code`, COUNT(`country`) AS `z` FROM `router_user` WHERE `valid` = 1 GROUP BY `country`";

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append(querier.executeSQLQuery("router_user", sql1).toStringBuilder(  "users"  ))
		                          .append(querier.executeSQLQuery("router_user", sql2).toStringBuilder("countries"))
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the user stats.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
