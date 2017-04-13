package net.hep.ami;

import org.slf4j.*;

public class LogSingleton
{
	/*---------------------------------------------------------------------*/

	public static final org.slf4j.Marker FATAL = org.slf4j.MarkerFactory.getMarker("FATAL");

	/*---------------------------------------------------------------------*/

	private LogSingleton() {}

	/*---------------------------------------------------------------------*/

	public static final Logger defaultLogger = LogSingleton.getLogger("net.hep.ami");

	/*---------------------------------------------------------------------*/

	public static Logger getLogger(String name)
	{
		String logLevel = ConfigSingleton.getProperty("log_level", "WARN");

		Logger result = org.slf4j.LoggerFactory.getLogger(name);

		((ch.qos.logback.classic.Logger) result).setLevel(
			ch.qos.logback.classic.Level.toLevel(
				logLevel
			)
		);

		return result;
	}

	/*---------------------------------------------------------------------*/
}
