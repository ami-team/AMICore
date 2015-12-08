package net.hep.ami.task;

import org.apache.logging.log4j.*;

import net.hep.ami.task.MainServer.*;

public abstract class TaskAbstractClass implements Runnable
{
	/*---------------------------------------------------------------------*/

	protected final Logger m_logger = LogManager.getLogger(getClass().getSimpleName());

	/*---------------------------------------------------------------------*/

	private TaskThread m_taskThread = null;

	private String m_argument = null;

	/*---------------------------------------------------------------------*/

	public static TaskThread getInstance(String clazz, String argument) throws Exception
	{
		TaskThread taskThread = new TaskThread((TaskAbstractClass) Class.forName(clazz).getConstructor().newInstance());

		taskThread.m_task.m_taskThread = taskThread;
		taskThread.m_task.m_argument = argument;

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

		boolean status = false;

		try
		{
			status = main(m_argument);
		}
		catch(Exception e)
		{
			m_logger.error(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		if(m_taskThread != null)
		{
			m_taskThread.m_status = status;
		}

		/*-----------------------------------------------------------------*/

		m_logger.info("Task '" + getTaskName() + "': stop");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public abstract boolean main(String argument) throws Exception;

	/*---------------------------------------------------------------------*/
}
