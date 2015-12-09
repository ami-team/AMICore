package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class GetConfig extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public GetConfig(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_isSecure.equals("false"))
		{
			throw new Exception("https connection required"); 
		}

		return ConfigSingleton.showConfig();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Show configuration.";
	}

	/*---------------------------------------------------------------------*/
}
