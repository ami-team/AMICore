package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class RemoveConfigProperty extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public RemoveConfigProperty(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String name = arguments.get("name");

		if(name == null)
		{
			throw new Exception("invalid usage");
		}

		if(m_isSecure.equals("false"))
		{
			throw new Exception("HTTPS connection required"); 
		}

		/*-----------------------------------------------------------------*/

		ConfigSingleton.removeProperty(name);

		ConfigSingleton.removePropertyFromDataBase(getQuerier("self"), name);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove configuration property.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-name=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
