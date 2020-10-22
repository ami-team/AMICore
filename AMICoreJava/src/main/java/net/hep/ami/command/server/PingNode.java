package net.hep.ami.command.server;

import net.hep.ami.ConfigSingleton;
import net.hep.ami.command.AbstractCommand;
import net.hep.ami.command.CommandMetadata;
import net.hep.ami.jdbc.Querier;
import net.hep.ami.jdbc.Row;
import net.hep.ami.jdbc.RowSet;
import net.hep.ami.jdbc.Update;
import net.hep.ami.utility.shell.SimpleShell;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class PingNode extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String s_hostName;

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		String shell_hostName;

		/*------------------------------------------------------------------------------------------------------------*/

		SimpleShell simpleShell = new SimpleShell();

		try
		{
			simpleShell.connect();
			SimpleShell.ShellTuple shellTuple = simpleShell.exec(new String[] {"hostname", "-f"});
			simpleShell.disconnect();

			shell_hostName = (shellTuple.errorCode == 0) ? shellTuple.inputStringBuilder.toString().trim()
			                                       : "N/A"
			;
		}
		catch(Exception e)
		{
			shell_hostName = "N/A";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		s_hostName = shell_hostName;

		/*------------------------------------------------------------------------------------------------------------*/
	}

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

		String hostName = arguments.getOrDefault("hostName", s_hostName);

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rows = querier.executeSQLQuery("router_monitoring", "SELECT `id` FROM `router_monitoring` WHERE `node` = ?0", hostName).getAll();

		Update update = (rows.size() == 1) ? querier.executeSQLUpdate("router_monitoring", "UPDATE `router_monitoring` SET `modified` = CURRENT_TIMESTAMP WHERE `id` = ?0", rows.get(0).getValue(0))
		                                   : querier.executeSQLUpdate("router_monitoring", "INSERT INTO `router_monitoring` (`node`, `service`, `frequency`) VALUES (?0, 'web', ?1)", hostName, ConfigSingleton.getProperty("monitoring_frequency", 30))
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return update.toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the status of each node.";
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
