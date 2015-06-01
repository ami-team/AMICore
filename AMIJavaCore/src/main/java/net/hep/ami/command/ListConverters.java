package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;

public class ListConverters extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListConverters(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		return ConverterSingleton.listConverters();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List converters.";
	}

	/*---------------------------------------------------------------------*/
}
