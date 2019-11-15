package net.hep.ami;

import org.jetbrains.annotations.*;

public class XMLTemplates
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private XMLTemplates() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static Object[] asArray(@NotNull Object arg)
	{
		return (Object[]) (
			arg.getClass().isArray() ?  /*--------*/ (arg)
			                         :  new Object[] {arg}
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static String format(@NotNull String tag, @NotNull Object[] args)
	{
		StringBuilder xml = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		for(Object arg: args)
		{
			for(Object ARG: asArray(arg))
			{
				xml.append("<").append(tag).append(">")
				   .append("<![CDATA[").append(ARG.toString().replace("]]>", "))>")).append("]]>")
				   .append("</").append(tag).append(">")
				;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		                          .append("<AMIMessage>").append(xml).append("<executionTime>0.0</executionTime></AMIMessage>")
		                          .toString()
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String info(@NotNull Object... args)
	{
		return format("info", args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String error(@NotNull Object... args)
	{
		return format("error", args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
