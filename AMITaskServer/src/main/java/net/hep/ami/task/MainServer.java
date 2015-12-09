package net.hep.ami.task;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

public class MainServer
{
	/*---------------------------------------------------------------------*/

	public static class TaskThread extends Thread
	{
		/*-----------------------------------------------------------------*/

		public TaskAbstractClass m_task;

		/*-----------------------------------------------------------------*/

		public TaskThread(TaskAbstractClass task, String name)
		{
			super(task, name);

			m_task = task;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String m_wdogFileName;
	private String m_stopFileName;

	private String m_taskServerName;

	private BasicQuerier m_taskServerQuerier;

	/*---------------------------------------------------------------------*/

	private final int m_maxTasks = ConfigSingleton.getProperty("max_tasks", 10);

	/*---------------------------------------------------------------------*/

	private final HashMap<String, TaskThread> m_threadMap = new HashMap<String, TaskThread>();

	/*---------------------------------------------------------------------*/

	private static final int[] s_priorityArray = new int[] {
		15,
		14,14,
		13,13,13,
		12,12,12,12,
		11,11,11,11,11,
		10,10,10,10,10,10,
		9,9,9,9,9,9,9,9,
		8,8,8,8,8,8,8,8,8,8,8,
		7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
		6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
		5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,
		4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
		3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
		1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
	};

	/*---------------------------------------------------------------------*/

	public int run()
	{
		/*-----------------------------------------------------------------*/

		LogSingleton.defaultLogger.info("TaskServer: start");

		/*-----------------------------------------------------------------*/

		m_taskServerName = ConfigSingleton.getProperty("task_server_name");

		if(m_taskServerName.isEmpty())
		{
			LogSingleton.defaultLogger.fatal("Property 'task_server_name' not defined");

			return 1;
		}

		/*-----------------------------------------------------------------*/

		String amiHome = ConfigSingleton.getSystemProperty("AMI_HOME");

		if(amiHome.isEmpty())
		{
			LogSingleton.defaultLogger.fatal("System property 'AMI_HOME' not defined");

			return 1;
		}

		/*-----------------------------------------------------------------*/

		m_wdogFileName = amiHome.concat(File.separator).concat("wdogFile");
		m_stopFileName = amiHome.concat(File.separator).concat("stopFile");

		/*-----------------------------------------------------------------*/

		try
		{
			m_taskServerQuerier = new BasicQuerier("self");
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.fatal(e.getMessage());

			return 1;
		}

		/*-----------------------------------------------------------------*/

		removeAllTasks();

		/*-----------------------------------------------------------------*/

		Random random = new Random();

		for(;;)
		{
			/*-------------------------------------------------------------*/

			if(isFinish())
			{
				removeAllTasks();
				break;
			}
			else
			{
				removeFinishTasks();
				;;;;;;
			}

			/*-------------------------------------------------------------*/

			try { Thread.sleep(1000); } catch(Exception e) { /* IGNORE */ }

			/*-------------------------------------------------------------*/

			if(m_threadMap.size() <= m_maxTasks)
			{
				startTask(random);
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		try
		{
			m_taskServerQuerier.rollbackAndRelease();
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.error(e.getMessage());

			return 1;
		}

		/*-----------------------------------------------------------------*/

		LogSingleton.defaultLogger.info("TaskServer: stop");

		/*-----------------------------------------------------------------*/

		return 0;
	}

	/*---------------------------------------------------------------------*/

	private long m_cnt = 0;

	private boolean isFinish()
	{
		/*-----------------------------------------------------------------*/
		/* WDOG FILE                                                       */
		/*-----------------------------------------------------------------*/

		if((m_cnt++) % 10 == 0)
		{
			File file = new File(m_wdogFileName);

			try
			{
				if(file.exists() && file.setLastModified(new Date().getTime()) == false)
				{
					LogSingleton.defaultLogger.error("Watch dog error");

					return true;
				}
			}
			catch(Exception e)
			{
				LogSingleton.defaultLogger.error("Watch dog error");

				return true;
			}
		}

		/*-----------------------------------------------------------------*/
		/* STOP FILE                                                       */
		/*-----------------------------------------------------------------*/

		return new File(m_stopFileName).exists();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void removeAllTasks()
	{
		/*-----------------------------------------------------------------*/

		m_threadMap.clear();

		/*-----------------------------------------------------------------*/

		try
		{
			if(m_taskServerQuerier.executeSQLUpdate("UPDATE router_task SET status = (status & ~1) WHERE taskServerName = '" + m_taskServerName.replace("'", "''") + "'") > 0)
			{
				m_taskServerQuerier.commit();
			}
		}
		catch(Exception e1)
		{
			LogSingleton.defaultLogger.error(e1.getMessage());

			try
			{
				m_taskServerQuerier.rollback();
			}
			catch(Exception e2)
			{
				LogSingleton.defaultLogger.error(e2.getMessage());
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void removeFinishTasks()
	{
		/*-----------------------------------------------------------------*/

		List<String> toBeRemoved = new ArrayList<String>();

		for(Entry<String, TaskThread> entry: m_threadMap.entrySet())
		{
			if(entry.getValue().isAlive() == false)
			{
				toBeRemoved.add(entry.getKey());
			}
		}

		/*-----------------------------------------------------------------*/

		for(String taskId: toBeRemoved)
		{
			Boolean status = m_threadMap.remove(taskId).m_task.m_status;

			try
			{
				int nb = status ? m_taskServerQuerier.executeSQLUpdate("UPDATE router_task SET status = ((status & ~3) | 2) WHERE id = '" + taskId + "'")
				                : m_taskServerQuerier.executeSQLUpdate("UPDATE router_task SET status = ((status & ~3) | 0) WHERE id = '" + taskId + "'")
				;

				if(nb > 0)
				{
					m_taskServerQuerier.commit();
				}
			}
			catch(Exception e1)
			{
				LogSingleton.defaultLogger.error(e1.getMessage());

				try
				{
					m_taskServerQuerier.rollback();
				}
				catch(Exception e2)
				{
					LogSingleton.defaultLogger.error(e2.getMessage());
				}
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void startTask(Random random)
	{
		/*-----------------------------------------------------------------*/

		Date date = new Date();

		/*-----------------------------------------------------------------*/

		int priority = s_priorityArray[random.nextInt(s_priorityArray.length)];

		/*-----------------------------------------------------------------*/

		try
		{
			List<Row> rows = m_taskServerQuerier.executeSQLQuery("SELECT id, class, argument, lockNames FROM router_task WHERE taskServerName = '" + m_taskServerName.replace("'", "''") + "' AND priority = '" + priority + "' AND (lastRunTime + step) < '" + date.getTime() + "' AND (status & '1') = '0'").getAll();

			if(rows.isEmpty() == false)
			{
				/*---------------------------------------------------------*/

				Row row = rows.get(random.nextInt(rows.size()));

				String taskId = row.getValue("id");
				String taskClass = row.getValue("class");
				String taskArgument = row.getValue("argument");
				String lockNames = row.getValue("lockNames");

				/*---------------------------------------------------------*/

				Set<String> lockNameSet = (lockNames == null) ? new HashSet<String>(/*-------------------------------------------*/)
				                                              : new HashSet<String>(Arrays.asList(lockNames.split("[^a-zA-Z0-9_]")))
				;

				/*---------------------------------------------------------*/

				if(isLocked(lockNameSet) == false)
				{
					/*-----------------------------------------------------*/

					TaskThread taskThread = TaskAbstractClass.getInstance(taskClass, taskArgument, lockNameSet);
					m_threadMap.put(taskId, taskThread);
					taskThread.start();

					/*-----------------------------------------------------*/

					if(m_taskServerQuerier.executeSQLUpdate("UPDATE router_task SET status = (status | 1), lastRunTime = '" + date.getTime() + "', lastRunDate = '" + DateFormater.format(date) + "' WHERE id = '" + taskId + "'") > 0)
					{
						m_taskServerQuerier.commit();
					}

					/*-----------------------------------------------------*/
				}

				/*---------------------------------------------------------*/
			}
		}
		catch(Exception e1)
		{
			LogSingleton.defaultLogger.error(e1.getMessage());

			try
			{
				m_taskServerQuerier.rollback();
			}
			catch(Exception e2)
			{
				LogSingleton.defaultLogger.error(e2.getMessage());
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private boolean isLocked(Set<String> lockNameSet)
	{
		/*-----------------------------------------------------------------*/

		if(lockNameSet.isEmpty())
		{
			return false;
		}

		/*-----------------------------------------------------------------*/

		for(TaskThread taskThread: m_threadMap.values())
		{
			Set<String> lockNameSET = taskThread.m_task.m_lockNameSet;

			/*-------------------------------------------------------------*/

			for(String lockNAME: lockNameSET)
			{
				for(String lockName: lockNameSet)
				{
					if(lockNAME.equalsIgnoreCase(lockName))
					{
						return true;
					}
				}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		System.exit(new MainServer().run());
	}

	/*---------------------------------------------------------------------*/
}
