package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class Encrypt extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public Encrypt(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String string = arguments.get("string");

		if(string == null)
		{
			throw new Exception("invalid usage");
		}

		if("false".equals(m_isSecure))
		{
			throw new Exception("HTTPS connection required"); 
		}

		return new StringBuilder("<info><![CDATA[" + SecuritySingleton.encrypt(string) + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Encrypt a string.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-string=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
