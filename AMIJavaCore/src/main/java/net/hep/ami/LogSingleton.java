package net.hep.ami;

import java.util.logging.*;

public class LogSingleton
{
	/*---------------------------------------------------------------------*/

	private static class JsonFormatter extends Formatter
	{
		@Override
		public String format(LogRecord record)
		{
			StringBuilder result = new StringBuilder();

			Object[] parameters = record.getParameters();

			result.append("{\n");

			result.append("  \"logger\": \"" + record.getLoggerName() + "\",\n");

			if(parameters.length == 3)
			{
				result.append("  \"thread\": \"" + parameters[0] + "\",\n");

				result.append("  \"file\": \"" + parameters[1] + "\",\n");
				result.append("  \"line\": \"" + parameters[2] + "\",\n");
			}

			result.append("  \"class\": \"" + record.getSourceClassName() + "\",\n");
			result.append("  \"method\": \"" + record.getSourceMethodName() + "\",\n");

			result.append("  \"level\": \"" + record.getLevel() + "\",\n");
			result.append("  \"message\": \"" + record.getMessage() + "\"\n");

			result.append("}\n");

			return result.toString();
		}
	}

	/*---------------------------------------------------------------------*/

	public static enum LogLevel
	{
		INFO,
		WARN,
		ERROR,
		CRITICAL,
	}

	/*---------------------------------------------------------------------*/

	public static class Logger
	{
		/*-----------------------------------------------------------------*/

		private static final String m_from = ConfigSingleton.getProperty("log_from");

		private static final String m_to = ConfigSingleton.getProperty("log_to");

		private static final String m_cc = ConfigSingleton.getProperty("log_cc");

		/*-----------------------------------------------------------------*/

		private static final Integer m_logLevel = ConfigSingleton.getProperty("log_level", 0);

		/*-----------------------------------------------------------------*/

		private java.util.logging.Logger m_logger;

		/*-----------------------------------------------------------------*/

		public Logger(String name)
		{
			try
			{
				/*---------------------------------------------------------*/

				Handler fileHandler = new FileHandler(name + ".log");

				fileHandler.setFormatter(new JsonFormatter());

				/*---------------------------------------------------------*/

				m_logger = java.util.logging.Logger.getLogger(name);

				m_logger.addHandler(fileHandler);

				/*---------------------------------------------------------*/
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		/*-----------------------------------------------------------------*/

		public void log(LogLevel logLevel, String message)
		{
			if(m_logLevel <= logLevel.ordinal())
			{
				/*---------------------------------------------------------*/
				/* GET CONTEXT                                             */
				/*---------------------------------------------------------*/

				StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

				String fileName = stackTraceElements[2].getFileName();
				Integer lineNumber = stackTraceElements[2].getLineNumber();

				String className = stackTraceElements[2].getClassName();
				String methodName = stackTraceElements[2].getMethodName();

				/*---------------------------------------------------------*/

				Object[] parameters = new Object[] {
					Thread.currentThread().getName(),
					fileName,
					lineNumber,
				};

				/*---------------------------------------------------------*/
				/* WRITE MESSAGE                                           */
				/*---------------------------------------------------------*/

				switch(logLevel)
				{
					case INFO:
						m_logger.logp(Level.INFO, className, methodName, message, parameters);
						break;

					case WARN:
						m_logger.logp(Level.WARNING, className, methodName, message, parameters);
						break;

					case ERROR:
						m_logger.logp(Level.SEVERE, className, methodName, message, parameters);
						break;

					case CRITICAL:
						m_logger.logp(Level.SEVERE, className, methodName, message, parameters);

						if(m_to.isEmpty() == false
						   ||
						   m_cc.isEmpty() == false
						 ) {

							try
							{
								MailSingleton.sendMessage(m_from, m_to, m_cc, String.format("CRITICAL ERROR (%s.%s)", className, methodName), message);
							}
							catch(Exception e)
							{
								log(LogLevel.ERROR, e.getMessage());
							}
						}

						break;
				}

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static final Logger defaultLogger = new LogSingleton.Logger("ami.core");

	/*---------------------------------------------------------------------*/
}
