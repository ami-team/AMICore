package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@Role(role = "AMI_ADMIN", secured = true)
public class GetConfig extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetConfig(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		return ConfigSingleton.showConfig();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the global configuration.";
	}

	/*---------------------------------------------------------------------*/
}
