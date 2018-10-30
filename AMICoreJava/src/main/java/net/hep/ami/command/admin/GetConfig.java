package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = true)
public class GetConfig extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetConfig(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
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
