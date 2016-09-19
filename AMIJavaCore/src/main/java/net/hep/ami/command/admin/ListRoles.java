package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;

public class ListRoles extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListRoles(Map<String, String> arguments, long transactionId)
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

		return getQuerier("self").executeQuery("SELECT * FROM `router_role`").toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List roles.";
	}

	/*---------------------------------------------------------------------*/
}
