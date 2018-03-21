package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", secured = false)
public class ListUsers extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListUsers(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		if(m_isSecure == false)
		{
			throw new Exception("HTTPS connection required"); 
		}

		return getQuerier("self").executeSQLQuery("SELECT `AMIUser`, `firstName`, `lastName`, `email`, `country`, `valid` FROM `router_user`").toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the registered users.";
	}

	/*---------------------------------------------------------------------*/
}
