package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", secured = false)
public class ListRoles extends AbstractCommand
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
		if(m_isSecure == false)
		{
			throw new Exception("HTTPS connection required"); 
		}

		return getQuerier("self").executeSQLQuery("SELECT role, validatorClass FROM `router_role`").toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the available roles.";
	}

	/*---------------------------------------------------------------------*/
}
