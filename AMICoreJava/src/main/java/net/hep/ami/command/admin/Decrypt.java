package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class Decrypt extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public Decrypt(Map<String, String> arguments, long transactionId)
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

		if(m_isSecure.equals("false"))
		{
			throw new Exception("HTTPS connection required"); 
		}

		return new StringBuilder("<info><![CDATA[" + SecuritySingleton.decrypt(string) + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Decrypt a string.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-string=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
