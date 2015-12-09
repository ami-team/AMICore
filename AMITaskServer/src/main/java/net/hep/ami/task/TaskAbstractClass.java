package net.hep.ami.task;

import java.util.*;

import org.apache.logging.log4j.*;

import net.hep.ami.task.MainServer.*;

public abstract class TaskAbstractClass implements Runnable
{
	/*---------------------------------------------------------------------*/

	protected final Logger m_logger = LogManager.getLogger(getClass().getSimpleName());

	/*---------------------------------------------------------------------*/

	public TaskThread m_thread = null;
	public String m_argument = null;
	public boolean m_status = true;

	/*---------------------------------------------------------------------*/

	public final Set<String> m_lockNameSet = new HashSet<String>();

	/*---------------------------------------------------------------------*/

	public static TaskThread getInstance(String taskClass, String taskArgument, Set<String> lockNameSet) throws Exception
	{
		TaskThread taskThread = new TaskThread((TaskAbstractClass) Class.forName(taskClass).getConstructor().newInstance(), taskClass);

		taskThread.m_task.m_thread = taskThread;
		taskThread.m_task.m_argument = taskArgument;

		if(lockNameSet != null)
		{
			taskThread.m_task.m_lockNameSet.addAll(lockNameSet);
		}

		return taskThread;
	}

	/*---------------------------------------------------------------------*/

	public final String getTaskName()
	{
		return getClass().getSimpleName();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public final void run()
	{
		/*-----------------------------------------------------------------*/

		m_logger.info("Task '" + getTaskName() + "': start");

		/*-----------------------------------------------------------------*/

		try
		{
			m_status = main(m_argument != null ? m_argument : "");
		}
		catch(Exception e)
		{
			m_status = false;

			m_logger.error(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		m_logger.info("Task '" + getTaskName() + "': stop");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public abstract boolean main(String argument) throws Exception;

	/*---------------------------------------------------------------------*/
}
