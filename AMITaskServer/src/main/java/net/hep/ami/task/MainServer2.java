package net.hep.ami.task;

import java.util.*;

import net.hep.ami.mini.*;

public class MainServer2 implements Handler
{
	/*---------------------------------------------------------------------*/

	private static final int s_max_tasks_default = 10;

	private static final int s_max_priority_levels_default = 5;

	private static final float s_priority_compression_default = 1.5f;

	/*---------------------------------------------------------------------*/

	@Override
	public void init(Server server, Map<String, String> config) throws Exception
	{
		String s;

		/*-----------------------------------------------------------------*/
		/* GET CONNECTION INFORMATION                                      */
		/*-----------------------------------------------------------------*/

		String jdbc_url = config.get("jdbc_url");
		String router_user = config.get("router_user");
		String router_pass = config.get("router_pass");

		if(jdbc_url == null
		   ||
		   router_user == null
		   ||
		   router_pass == null
		 ) {
			throw new Exception("config error");
		}

		/*-----------------------------------------------------------------*/
		/* LOAD JDBC DRIVER                                                */
		/*-----------------------------------------------------------------*/

		String clazz;

		/**/ if(jdbc_url.startsWith("jdbc:mysql")) {
			clazz = "org.gjt.mm.mysql.Driver";
		}
		else if(jdbc_url.startsWith("jdbc:oracle")) {
			clazz = "oracle.jdbc.driver.OracleDriver";
		}
		else if(jdbc_url.startsWith("jdbc:postgresql")) {
			clazz = "org.postgresql.Driver";
		}
		else if(jdbc_url.startsWith("jdbc:sqlite")) {
			clazz = "org.sqlite.JDBC";
		}
		else {
			throw new Exception("unknown JDBC protocol");
		}

		Class.forName(clazz);

		/*-----------------------------------------------------------------*/
		/* SERVER INFORMATION                                              */
		/*-----------------------------------------------------------------*/

		s = config.get("max_tasks");

		int max_tasks = (s != null) ? Integer.parseInt(s) : s_max_tasks_default;

		if(max_tasks < 1)
		{
			throw new Exception("`max_tasks` out of range");
		}

		/*-----------------------------------------------------------------*/

		s = config.get("max_priority_levels");

		int m_max_priority_levels = (s != null) ? Integer.parseInt(s) : s_max_priority_levels_default;

		if(m_max_priority_levels < 1)
		{
			throw new Exception("`max_priority_levels` out of range");
		}

		/*-----------------------------------------------------------------*/

		s = config.get("priority_compression");

		Float m_priority_compression = (s != null) ? Float.parseFloat(s) : s_priority_compression_default;

		if(m_priority_compression < 1.0)
		{
			throw new Exception("`priority_compression` out of range");
		}

		/*-----------------------------------------------------------------*/
		/* BUILD PRIORITY TABLE                                            */
		/*-----------------------------------------------------------------*/

		/* dim = 1 + sum_{n=2}^{n=m_max_priority_levels} m_priority_compression * (n - 1)
		 *     = 1 + m_priority_compression * sum_{n=2}^{n=m_max_priority_levels} (n - 1)
		 *     = 1 + m_priority_compression * m_max_priority_levels * (m_max_priority_levels - 1) / 2
		 */

		int[] priorityTable = new int[1 + (int) (m_priority_compression * (m_max_priority_levels * (m_max_priority_levels - 1)) / 2.0f)];

		/*-----------------------------------------------------------------*/

		float number_of_entries = 1;

		int k = priorityTable.length - 1;

		for(int j = 0; j < m_max_priority_levels; j++)
		{
			for(int i = 0; i < (int) number_of_entries; i++)
			{
				priorityTable[k--] = j;
			}

			number_of_entries *= m_priority_compression;
		}

		/*-----------------------------------------------------------------*/
		/* RUN SCHEDULER                                                   */
		/*-----------------------------------------------------------------*/

		new Scheduler(max_tasks, priorityTable, jdbc_url, router_user, router_pass).run();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder exec(Server server, Map<String, String> config, String command, Map<String, String> arguments, String ip) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/**/ if(command.equals("GetSessionInfo"))
		{

		}
		else if(command.equals("Stop"))
		{
			server.stop();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder help(Server server, Map<String, String> config, String command, Map<String, String> arguments, String ip) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/**/ if(command.equals("GetSessionInfo"))
		{
			result.append("Gives session info");
		}
		else if(command.equals("Stop"))
		{
			result.append("Stops the server");
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		try
		{
			Server server = new Server(9090, new MainServer2());

			server.start();
			server.join();
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
