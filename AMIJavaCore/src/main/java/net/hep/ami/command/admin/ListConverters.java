package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.CommandAbstractClass;

public class ListConverters extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListConverters(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
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
