package net.hep.ami.task;

import org.apache.logging.log4j.*;

import net.hep.ami.task.MainServer.*;

public abstract class TaskAbstractClass implements Runnable
{
	/*---------------------------------------------------------------------*/

	private TaskThread m_taskThread = null;

	private String m_argument = null;

	/*---------------------------------------------------------------------*/

	protected final Logger m_logger = LogManager.getLogger(getClass().getSimpleName());

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
		try
		{
			boolean status = main(m_argument);

			if(m_taskThread != null)
			{
				m_taskThread.m_status = status;
			}
		}
		catch(Exception e)
		{
			if(m_taskThread != null)
			{
				m_taskThread.m_status = false;
			}

			throw new RuntimeException(e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	public abstract boolean main(String argument) throws Exception;

	/*---------------------------------------------------------------------*/
}
