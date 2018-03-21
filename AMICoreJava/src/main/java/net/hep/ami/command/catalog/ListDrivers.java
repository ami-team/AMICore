package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListDrivers extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListDrivers(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		return DriverSingleton.listDrivers();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the available drivers.";
	}

	/*---------------------------------------------------------------------*/
}
