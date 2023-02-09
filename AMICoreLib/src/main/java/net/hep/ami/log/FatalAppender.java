package net.hep.ami.log;

import java.util.*;

import net.hep.ami.*;

import org.jetbrains.annotations.*;

public class FatalAppender extends AbstractLogAppender
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void append(@NotNull String loggerName, @NotNull ch.qos.logback.classic.Level loggerLevel, @NotNull List<org.slf4j.Marker> loggerMarkers, long timeStamp, @NotNull String thread, @NotNull String message, @NotNull StackTraceElement[] stackTraceElements) throws Exception
	{
		if(loggerMarkers.contains(LogSingleton.FATAL))
		{
			String title = "AMI FATAL ERROR - " + loggerName + " :: " + stackTraceElements[0].getClassName()
			                                                 + " :: " + stackTraceElements[0].getMethodName()
			;

			MailSingleton.sendMessage(
				ConfigSingleton.getProperty("admin_email"),
				ConfigSingleton.getProperty("log_to"),
				ConfigSingleton.getProperty("log_cc"),
				title, title + "\n\n" + message
			);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Send an email if the `net.hep.ami.LogSingleton.FATAL` marker is used";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
