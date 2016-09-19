package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class SetConfig extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public SetConfig(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		if(m_isSecure.equals("false"))
		{
			throw new Exception("https connection required"); 
		}

		ConfigSingleton.writeToDataBase(arguments);

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Set configuration.";
	}

	/*---------------------------------------------------------------------*/
}
