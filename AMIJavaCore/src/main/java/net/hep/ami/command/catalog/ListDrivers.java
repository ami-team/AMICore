package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class ListDrivers extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListDrivers(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		return DriverSingleton.listDrivers();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List drivers.";
	}

	/*---------------------------------------------------------------------*/
}
