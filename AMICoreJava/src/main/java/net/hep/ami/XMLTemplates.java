package net.hep.ami;

import org.jetbrains.annotations.*;

public class XMLTemplates
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private XMLTemplates() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static Object @NotNull [] asArray(@Nullable Object arg)
	{
		if(arg == null)
		{
			return new Object[] {"null"};
		}

		return arg.getClass().isArray() ? ( Object[] ) (arg)
			                            : new Object[] {arg}
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@SuppressWarnings("StringBufferReplaceableByString")
	private static String format(@NotNull String tag, @NotNull Object[] args)
	{
		StringBuilder xml = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		for(Object arg: args)
		{
			for(Object ARG: asArray(arg))
			{
				xml.append("<").append(tag).append(">")
				   .append("<![CDATA[").append(ARG.toString().replace("<![CDATA[", "<!(CDATA(").replace("]]>", "))>")).append("]]>")
				   .append("</").append(tag).append(">")
				;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		                          .append("<AMIMessage>")
		                          .append(xml)
		                          .append("<node><![CDATA[").append(CommandSingleton.HOSTNAME).append("]]></node>")
		                          .append("<executionTime><![CDATA[0.000]]></executionTime>")
		                          .append("</AMIMessage>")
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
