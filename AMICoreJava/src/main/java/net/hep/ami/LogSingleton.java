package net.hep.ami;

import lombok.*;

import java.util.*;
import java.lang.reflect.*;

import ch.qos.logback.core.*;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.*;

import net.hep.ami.log.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class LogSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LogSingleton.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	private static final class LogAppenderDescr
	{
		@NotNull private final String name;
		@NotNull private final String help;
		@NotNull private final AppenderBase<ILoggingEvent> instance;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, LogAppenderDescr> s_logAppenders = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final org.slf4j.Marker FATAL = org.slf4j.MarkerFactory.getMarker("FATAL");

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private LogSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reset(@NotNull String defaultLogLevel)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		addLogAppenders();

		/*------------------------------------------------------------------------------------------------------------*/

		setupLoggers(defaultLogLevel);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addLogAppenders()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		for(String className: ClassSingleton.findClassNames("net.hep.ami.log"))
		{
			try
			{
				addLogAppender(className);
			}
			catch(Exception e)
			{
				LOG.error(LogSingleton.FATAL, "for log appender `{}`", className, e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addLogAppender(@NotNull String className) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET LOG APPENDER                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		if(s_logAppenders.containsKey(className))
		{
			return;
		}

		Class<?> clazz = ClassSingleton.forName(className);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00)
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD LOGBACK APPENDER                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		if(ClassSingleton.extendsClass(clazz, AbstractLogAppender.class))
		{
			s_logAppenders.put(className, new LogAppenderDescr(
				className,
				clazz.getMethod("help").invoke(null).toString(),
				new AppenderBase<>() {

					/*------------------------------------------------------------------------------------------------*/

					private final AbstractLogAppender m_logAppender = (AbstractLogAppender) clazz.getConstructor().newInstance();

					/*------------------------------------------------------------------------------------------------*/

					@Override
					protected void append(ILoggingEvent event)
					{
						try
						{
							m_logAppender.append(
								event.getLoggerName(),
								event.getLevel().toString(),
								event.getMarker().getName(),
								event.getTimeStamp(),
								event.getThreadName(),
								event.getFormattedMessage(),
								event./**/getCallerData/**/()
							);
						}
						catch(Exception e)
						{
							LOG.error(e.getMessage(), e);
						}
					}

					/*------------------------------------------------------------------------------------------------*/
				}
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void setupLoggers(@NotNull String defaultLogLevel)
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
			else
			{
				/*----------------------------------------------------------------------------------------------------*/

				for(LogAppenderDescr logAppenderDescr: s_logAppenders.values())
				{
					if(logger.getAppender(logAppenderDescr.getName()) == null)
					{
						logger.addAppender(logAppenderDescr.getInstance());
					}
				}

				/*----------------------------------------------------------------------------------------------------*/

				logger.setLevel(level);

				/*----------------------------------------------------------------------------------------------------*/
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

		/*------------------------------------------------------------------------------------------------------------*/

		for(LogAppenderDescr logAppenderDescr: s_logAppenders.values())
		{
			if(result.getAppender(logAppenderDescr.getName()) == null)
			{
				result.addAppender(logAppenderDescr.getInstance());
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		result.setLevel(level);

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}


	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder listLogAppenders()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"appenders\">");

		for(LogAppenderDescr logAppenderDescr: s_logAppenders.values())
		{
			result.append("<row>")
			      .append("<field name=\"class\"><![CDATA[").append(logAppenderDescr.getName()).append("]]></field>")
			      .append("<field name=\"help\"><![CDATA[").append(logAppenderDescr.getHelp()).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
