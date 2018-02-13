package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
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

		if(m_isSecure == false)
		{
			throw new Exception("HTTPS connection required"); 
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		ConfigSingleton.removeProperty(name);

		ConfigSingleton.removePropertyInDataBase(querier, name);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Remove a global configuration property.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-name=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
