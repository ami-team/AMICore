package net.hep.ami.log;

import java.util.*;

import org.jetbrains.annotations.*;

abstract public class AbstractLogAppender
{
	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public void append(
		@NotNull String loggerName,
		@NotNull String loggerLevel,
		@NotNull Set<String> loggerMarkers,
		long timeStamp,
		@NotNull String thread,
		@NotNull String message,
		@NotNull StackTraceElement[] stackTraceElements

	) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
