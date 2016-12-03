package net.hep.ami;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.filter.*;
import org.apache.logging.log4j.core.appender.*;

public class LogSingleton
{
	/*---------------------------------------------------------------------*/

	public static final org.apache.logging.log4j.Logger defaultLogger = LogManager.getLogger("net.hep.ami");

	/*---------------------------------------------------------------------*/

	private static final class AMIFatalAppender extends AbstractAppender
	{
		/*-----------------------------------------------------------------*/

		private static final long serialVersionUID = 2755767477764994797L;

		/*-----------------------------------------------------------------*/

		public AMIFatalAppender(String name)
		{
			super(name, LevelRangeFilter.createFilter(Level.FATAL, Level.FATAL, null, null), null, false);
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
				if(ignoreExceptions() == false)
				{
					throw new AppenderLoggingException(e);
				}
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	static
	{
		/*-----------------------------------------------------------------*/
		/* CREATE APPENDER                                                 */
		/*-----------------------------------------------------------------*/

		AMIFatalAppender amiFatalAppender = new AMIFatalAppender("AMIFatalAppender");

		/*-----------------------------------------------------------------*/
		/* START APPENDER                                                  */
		/*-----------------------------------------------------------------*/

		LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);

		Configuration configuration = (Configuration) loggerContext.getConfiguration();

		configuration.addAppender(amiFatalAppender);

		configuration.start();

		/*-----------------------------------------------------------------*/
		/* ADD APPENDER                                                    */
		/*-----------------------------------------------------------------*/

		/* NOT MUTABLE */
		Level level = Level.toLevel(ConfigSingleton.getProperty("log_level"));
		/* NOT MUTABLE */

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
}
