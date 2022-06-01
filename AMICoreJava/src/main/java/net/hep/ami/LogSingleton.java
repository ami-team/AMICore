package net.hep.ami;

import ch.qos.logback.core.*;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.*;

import java.io.*;

import org.jetbrains.annotations.*;

public class LogSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public static final org.slf4j.Marker FATAL = org.slf4j.MarkerFactory.getMarker("FATAL");

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger("ROOT");

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private LogSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final class FatalAppender extends AppenderBase<ILoggingEvent>
	{
		/*------------------------------------------------------------------------------------------------------------*/

		@Override
		protected void append(@NotNull ILoggingEvent event)
		{
			if(FATAL.equals(event.getMarker()))
			{
				String title = "AMI FATAL ERROR - " + event.getLoggerName() + " :: " + event.getCallerData()[0].getClassName()
				                                                            + " :: " + event.getCallerData()[0].getMethodName()
				;

				String message = event.getFormattedMessage();

				try
				{
					MailSingleton.sendMessage(
						ConfigSingleton.getProperty("admin_email"),
						ConfigSingleton.getProperty("log_to"),
						ConfigSingleton.getProperty("log_cc"),
						title, title + "\n\n" + message
					);
				}
				catch(Exception e)
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);

					e.printStackTrace(pw);

					root.error("could not send emails: " + sw, e);
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reset(@NotNull String defaultLogLevel)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		boolean devMode = ConfigSingleton.getProperty("dev_mode", false);

		String logLevel = ConfigSingleton.getProperty("log_level", defaultLogLevel);

		/*------------------------------------------------------------------------------------------------------------*/

		LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();

		/*------------------------------------------------------------------------------------------------------------*/

		String name;

		Level level = Level.toLevel(logLevel);

		for(Logger logger: context.getLoggerList())
		{
			name = logger.getName().toLowerCase();

			/**/ if(name.contains("hikari")) {
				logger.setLevel(devMode ? Level.DEBUG : Level.OFF);
			}
			else if(name.contains("memcached")) {
				logger.setLevel(devMode ? Level.DEBUG : Level.OFF);
			}
			else {
				logger.setLevel(level);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static org.slf4j.Logger getLogger(@NotNull String name)
	{
		return getLogger(name, "WARN");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static org.slf4j.Logger getLogger(@NotNull String name, @NotNull String defaultLogLevel)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Level level = Level.toLevel(ConfigSingleton.getProperty("log_level", defaultLogLevel));

		/*------------------------------------------------------------------------------------------------------------*/

		Logger result = (Logger) org.slf4j.LoggerFactory.getLogger(name);

		result.setLevel(
			level
		);

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
