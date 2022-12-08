package net.hep.ami.log;

import org.jetbrains.annotations.*;

abstract public class AbstractLogAppender
{
	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public void append(
		@NotNull String loggerName,
		@NotNull String loggerLevel,
		@NotNull String loggerMarker,
		long timeStamp,
		@NotNull String thread,
		@NotNull String message,
		@NotNull StackTraceElement[] stackTraceElements

	) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/
}
