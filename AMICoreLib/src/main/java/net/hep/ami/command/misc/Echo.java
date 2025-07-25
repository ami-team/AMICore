package net.hep.ami.command.misc;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true)
public class Echo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public Echo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments)
	{
		StringBuilder result = new StringBuilder();

		result.append("<rowset>")
		      .append("<row>")
		;

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			result.append("<field name=\"").append(Utility.escapeHTML(entry.getKey())).append("\"><![CDATA[").append(entry.getValue()).append("]]></field>");
		}

		result.append("</row>")
		      .append("</rowset>")
		;

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Dump arguments.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(.)*";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
