package net.hep.ami;

import ch.qos.logback.core.*;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.*;

public class LogSingleton
{
	/*---------------------------------------------------------------------*/

	public static final org.slf4j.Marker FATAL = org.slf4j.MarkerFactory.getMarker("FATAL");

	/*---------------------------------------------------------------------*/

	public static final org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger("ROOT");

	/*---------------------------------------------------------------------*/

	private LogSingleton() {}

	/*---------------------------------------------------------------------*/

	public static class FatalAppender extends AppenderBase<ILoggingEvent>
	{
		/*-----------------------------------------------------------------*/

		@Override
		protected void append(ILoggingEvent event)
		{
			if(event.getMarker() == FATAL)
			{
				String title = "AMI FATAL ERROR - " + event.getLoggerName() + " :: " + event.getCallerData()[0].getClassName()
				                                                            + " :: " + event.getCallerData()[0].getMethodName()
				;

				String message = event.getFormattedMessage();

				try
				{
					MailSingleton.sendMessage(
						ConfigSingleton.getProperty("log_from"),
						ConfigSingleton.getProperty("log_to"),
						ConfigSingleton.getProperty("log_cc"),
						title, title + "\n\n" + message
					);
				}
				catch(Exception e)
				{
					root.error("could not send emails", e);
				}
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void reset()
	{
		/*-----------------------------------------------------------------*/

		Level level = Level.toLevel(ConfigSingleton.getProperty("log_level", "WARN"));

		/*-----------------------------------------------------------------*/

		LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();

		for(Logger logger: context.getLoggerList())
		{
			logger.setLevel(
				logger.getName().startsWith("com.zaxxer.hikari") ? Level
				                                                     .WARN
				                                                 : level
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static org.slf4j.Logger getLogger(String name)
	{
		/*-----------------------------------------------------------------*/

		Level level = Level.toLevel(ConfigSingleton.getProperty("log_level", "WARN"));

		/*-----------------------------------------------------------------*/

		Logger result = (Logger) org.slf4j.LoggerFactory.getLogger(name);

		result.setLevel(
			level
		);

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
