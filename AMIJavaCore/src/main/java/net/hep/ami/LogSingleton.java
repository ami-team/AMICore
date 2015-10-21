package net.hep.ami;

import java.util.*;
import java.util.Map.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;

public class LogSingleton
{

	/*---------------------------------------------------------------------*/

	public static org.apache.logging.log4j.Logger getLogger(String name)
	{
		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		org.apache.logging.log4j.Logger result = LogManager.getLogger(name);

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		Map<String, Appender> appenders = ((org.apache.logging.log4j.core.Logger) result).getAppenders();

		for(Entry<String, Appender> entry: appenders.entrySet())
		{
			System.out.println(entry.getKey());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static final org.apache.logging.log4j.Logger defaultLogger = getLogger("ami.core");

	/*---------------------------------------------------------------------*/
}
