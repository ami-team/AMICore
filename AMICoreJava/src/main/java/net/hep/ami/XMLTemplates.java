package net.hep.ami;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public class XMLTemplates
{
	/*---------------------------------------------------------------------*/

	private XMLTemplates() {}

	/*---------------------------------------------------------------------*/

	private static String format(String tag, String message)
	{
		return new StringBuilder().append("<").append(tag).append(">")
		                          .append("<![CDATA[").append(message.replace("]]>", "))>")).append("]]>")
		                          .append("</").append(tag).append(">")
		                          .toString()
		;
	}

	/*---------------------------------------------------------------------*/

	public static String info(@Nullable Object object)
	{
		if(object == null)
		{
			object = "null";
		}

		String xml = object.getClass().isArray() ? Arrays.stream((Object[]) object).map(OBJECT -> format("info", OBJECT.toString())).collect(Collectors.joining(""))
		                                         : /*------------------------------------------*/ format("info", object.toString()) /*----------------------------*/
		;

		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		                          .append("<AMIMessage>").append(xml).append("<executionTime>0.0</executionTime></AMIMessage>")
		                          .toString()
		;
	}

	/*---------------------------------------------------------------------*/

	public static String error(@Nullable Object object)
	{
		if(object == null)
		{
			object = "null";
		}

		String xml = object.getClass().isArray() ? Arrays.stream((Object[]) object).map(OBJECT -> format("error", OBJECT.toString())).collect(Collectors.joining(""))
		                                         : /*------------------------------------------*/ format("error", object.toString()) /*----------------------------*/
		;

		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		                          .append("<AMIMessage>").append(xml).append("<executionTime>0.0</executionTime></AMIMessage>")
		                          .toString()
		;
	}

	/*---------------------------------------------------------------------*/
}
