package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class Decrypt extends CommandAbstractClass
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
			throw new Exception("https connection required"); 
		}

		return new StringBuilder("<info><![CDATA[" + Cryptography.decrypt(string) + "]]></info>");
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
