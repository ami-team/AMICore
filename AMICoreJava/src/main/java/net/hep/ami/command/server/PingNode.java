package net.hep.ami.command.server;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class PingNode extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public PingNode(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String hostName = arguments.getOrDefault("hostName", CommandSingleton.HOSTNAME);
		String service = arguments.getOrDefault("service", "web");

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rows = querier.executeSQLQuery("router_monitoring", "SELECT `id` FROM `router_monitoring` WHERE `node` = ?0", hostName).getAll();

		Update update = (rows.size() == 1) ? querier.executeSQLUpdate("router_monitoring", "UPDATE `router_monitoring` SET `modified` = CURRENT_TIMESTAMP WHERE `id` = ?0", rows.get(0).getValue(0))
		                                   : querier.executeSQLUpdate("router_monitoring", "INSERT INTO `router_monitoring` (`node`, `service`, `frequency`, `endpoint`) VALUES (?0, ?1, ?2, ?3)", hostName, service, ConfigSingleton.getProperty("monitoring_frequency", 30), "N/A")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return update.toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Ping an AMI node.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-hostName=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
