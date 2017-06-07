package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class SetConfigProperty extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public SetConfigProperty(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String name = arguments.get("name");
		String value = arguments.get("value");

		if(name == null
		   ||
		   value == null
		 ) {
			throw new Exception("invalid usage");
		}

		if("false".equals(m_isSecure))
		{
			throw new Exception("HTTPS connection required"); 
		}

		/*-----------------------------------------------------------------*/

		ConfigSingleton.setProperty(name, value);

		ConfigSingleton.setPropertyInDataBase(getQuerier("self"), name, value);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Set configuration property.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-name=\"\" -value=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
