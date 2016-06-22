package net.hep.ami;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.filter.*;
import org.apache.logging.log4j.core.appender.*;

public class LogSingleton
{
	/*---------------------------------------------------------------------*/

	private static final class AMIFatalAppender extends AbstractAppender
	{
		/*-----------------------------------------------------------------*/

		private static final long serialVersionUID = 2755767477764994797L;

		/*-----------------------------------------------------------------*/

		public AMIFatalAppender(String name)
		{
			super(name, LevelRangeFilter.createFilter(Level.FATAL, Level.FATAL, null, null), null, true);
		}

		/*-----------------------------------------------------------------*/

		@Override
		public void append(LogEvent event)
		{
			String title = "AMI FATAL ERROR - " + event.getLoggerName() + " :: " + event.getSource().getClassName()
			                                                            + " :: " + event.getSource().getMethodName()
			;

			try
			{
				MailSingleton.sendMessage(
					ConfigSingleton.getProperty("log_from"),
					ConfigSingleton.getProperty("log_to"),
					ConfigSingleton.getProperty("log_cc"),
					title, title + "\n\n" + event.getMessage().getFormattedMessage()
				);
			}
			catch(Exception e)
			{
				error(e.getMessage());

				throw new AppenderLoggingException(e.getMessage());
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	static
	{
		/*-----------------------------------------------------------------*/

		AMIFatalAppender amiFatalAppender = new AMIFatalAppender("AMIFatalAppender");

		/*-----------------------------------------------------------------*/

		LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);

		Configuration configuration = (Configuration) loggerContext.getConfiguration();

		configuration.addAppender(amiFatalAppender);

		configuration.start();

		/*-----------------------------------------------------------------*/

		Level level = Level.toLevel(ConfigSingleton.getProperty("log_level"));

		/*-----------------------------------------------------------------*/

		LoggerConfig loggerConfig = configuration.getRootLogger();

		loggerConfig.addAppender(amiFatalAppender, null, null);

		loggerConfig.setLevel(level);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static org.apache.logging.log4j.Logger getLogger(String name)
	{
		return LogManager.getLogger(name);
	}

	/*---------------------------------------------------------------------*/

	public static final org.apache.logging.log4j.Logger defaultLogger = LogManager.getLogger("ami.core");

	/*---------------------------------------------------------------------*/
}
