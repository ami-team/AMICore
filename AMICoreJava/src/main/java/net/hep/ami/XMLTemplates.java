package net.hep.ami;

import net.hep.ami.utility.*;

public class XMLTemplates
{
	/*---------------------------------------------------------------------*/

	private XMLTemplates() {}

	/*---------------------------------------------------------------------*/

	private static void format(StringBuilder result, String tag, @Nullable String message)
	{
		if(message == null)
		{
			message = "null";
		}

		result.append("<").append(tag).append(">")
		      .append("<![CDATA[").append(message.replace("]]>", "))>")).append("]]>")
		      .append("</").append(tag).append(">")
		;
	}

	/*---------------------------------------------------------------------*/

	public static String info(Object... args)
	{
		StringBuilder xml = new StringBuilder();

		for(Object arg: args)
		{
			for(Object ARG: arg.getClass().isArray() == false ? new Object[] {arg} : (Object[]) arg)
			{
				format(xml, "info", ARG.toString());
			}
		}

		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		                          .append("<AMIMessage>").append(xml).append("<executionTime>0.0</executionTime></AMIMessage>")
		                          .toString()
		;
	}

	/*---------------------------------------------------------------------*/

	public static String error(Object... args)
	{
		StringBuilder xml = new StringBuilder();

		for(Object arg: args)
		{
			for(Object ARG: arg.getClass().isArray() == false ? new Object[] {arg} : (Object[]) arg)
			{
				format(xml, "error", ARG.toString());
			}
		}

		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		                          .append("<AMIMessage>").append(xml).append("<executionTime>0.0</executionTime></AMIMessage>")
		                          .toString()
		;
	}

	/*---------------------------------------------------------------------*/
}
