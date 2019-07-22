package net.hep.ami.command.misc;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.parser.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class Echo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public Echo(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
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

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Dump arguments.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(.)*";
	}

	/*---------------------------------------------------------------------*/
}
