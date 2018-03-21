package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListConverters extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListConverters(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		return ConverterSingleton.listConverters();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the available converters.";
	}

	/*---------------------------------------------------------------------*/
}
