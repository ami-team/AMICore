package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;

public class Echo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public Echo(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		result.append("<rowset><row>");

		for(Map.Entry<String, String> entry: arguments.entrySet())
		{
			result.append("<field name=\"" + entry.getKey().replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") + "\"><![CDATA[" + entry.getValue() + "]]></field>");
		}

		result.append("</row></rowset>");

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
