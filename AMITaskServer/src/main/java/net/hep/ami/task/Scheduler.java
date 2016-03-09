package net.hep.ami.task;

import java.sql.*;

public class Scheduler extends Thread
{
	/*---------------------------------------------------------------------*/

	private String m_jdbc_url;
	private String m_router_user;
	private String m_router_pass;

	/*---------------------------------------------------------------------*/

	private int[] m_priorityTable = null;

	private Connection m_connection = null;

	/*---------------------------------------------------------------------*/

	public Scheduler(int max_tasks, int[] priorityTable, String jdbc_url, String router_user, String router_pass)
	{
		super();

		m_jdbc_url = jdbc_url;
		m_router_user = router_user;
		m_router_pass = router_pass;

		m_priorityTable = priorityTable;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void run()
	{

	}

	/*---------------------------------------------------------------------*/

	private Connection getRouterConnection() throws SQLException
	{
		if(m_connection == null
		   ||
		   m_connection.isClosed()
		 ) {
			m_connection = DriverManager.getConnection(
				m_jdbc_url,
				m_router_user,
				m_router_pass
			);
		}

		return m_connection;
	}

	/*---------------------------------------------------------------------*/
}
