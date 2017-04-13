package net.hep.ami;

public class LogSingleton
{
	/*---------------------------------------------------------------------*/

	public static final org.slf4j.Marker FATAL = org.slf4j.MarkerFactory.getMarker("FATAL");

	/*---------------------------------------------------------------------*/

	private LogSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{

	}

	/*---------------------------------------------------------------------*/

	public static final org.slf4j.Logger defaultLogger = org.slf4j.LoggerFactory.getLogger("net.hep.ami");

	/*---------------------------------------------------------------------*/

	public static org.slf4j.Logger getLogger(String name)
	{
		return org.slf4j.LoggerFactory.getLogger(name);
	}

	/*---------------------------------------------------------------------*/
}
