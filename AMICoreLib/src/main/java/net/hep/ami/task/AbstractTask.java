package net.hep.ami.task;

import java.io.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.utility.*;

import org.slf4j.*;

import org.jetbrains.annotations.*;

public abstract class AbstractTask
{
	/*----------------------------------------------------------------------------------------------------------------*/

	protected static final String s_AMIUser = ConfigSingleton.getProperty("admin_user");

	protected static final String s_timeZone = ConfigSingleton.getProperty("time_zone");

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final Logger m_logger;

	protected final long m_transactionId;

	/**/

	protected final String m_taskCommand;

	protected final String m_taskReportFile;

	/*----------------------------------------------------------------------------------------------------------------*/

	public AbstractTask(String loggerName)
	{
		m_logger = LogSingleton.getLogger(loggerName);

		m_transactionId = TransactionPoolSingleton.bookNewTransactionId();

		/**/

		m_taskCommand = ConfigSingleton.getSystemProperty("ami.task_command");

		m_taskReportFile = ConfigSingleton.getSystemProperty("ami.task_report_file");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public Logger getLogger()
	{
		return m_logger;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public long getTransactionId()
	{
		return m_transactionId;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected String executeCommand(@NotNull String command, @NotNull Map<String, String> arguments) throws Exception
	{
		return CommandSingleton.executeCommand(command, arguments, false, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected String executeCommand(@NotNull String command, @NotNull Map<String, String> arguments, boolean checkRoles) throws Exception
	{
		return CommandSingleton.executeCommand(command, arguments, checkRoles, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected Querier getAdminQuerier(@NotNull String catalog) throws Exception
	{
		return new TransactionalQuerier(catalog, s_AMIUser, s_timeZone, Querier.FLAG_ADMIN, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected Querier getAdminQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass) throws Exception
	{
		return new TransactionalQuerier(externalCatalog, internalCatalog, jdbcUrl, user, pass, s_AMIUser, s_timeZone, Querier.FLAG_ADMIN, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected Querier getQuerier(@NotNull String catalog) throws Exception
	{
		return new TransactionalQuerier(catalog, s_AMIUser, s_timeZone, 0x00, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected Querier getQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass) throws Exception
	{
		return new TransactionalQuerier(externalCatalog, internalCatalog, jdbcUrl, user, pass, s_AMIUser, s_timeZone, 0x00, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected void commitAndRelease() throws Exception
	{
		TransactionPoolSingleton.commitAndRelease(m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected void rollbackAndRelease() throws Exception
	{
		TransactionPoolSingleton.rollbackAndRelease(m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected void writeReportFile(StringBuilder report) throws Exception
	{
		if(!Empty.is(m_taskReportFile, Empty.STRING_NULL_EMPTY_BLANK))
		{
			TextFile.write(new File(m_taskReportFile), report);
		}
		else
		{
			m_logger.warn("`ami.report_file` is empty");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public int execute(@NotNull String[] args)
	{
		int result = 0x00;

		Exception e1 = null;

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			result = task(args);
		}
		catch(Exception e2)
		{
			e1 = e2;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(e1 == null)
		{
			try
			{
				TransactionPoolSingleton.commitAndRelease(m_transactionId);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				m_logger.error(e.getMessage(), e);
			}
		}
		else
		{
			try
			{
				TransactionPoolSingleton.rollbackAndRelease(m_transactionId);
			}
			catch(Exception e2)
			{
				e2.initCause(e1);
				System.out.println(e1.getMessage() + ", " + e2.getMessage());
				m_logger.error(e1.getMessage() + ", " + e2.getMessage(), e2);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public abstract int task(@NotNull String[] args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/
}
