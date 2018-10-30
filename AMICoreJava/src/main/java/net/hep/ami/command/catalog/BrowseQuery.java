package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class BrowseQuery extends SearchQuery
{
	/*---------------------------------------------------------------------*/

	public BrowseQuery(Set<String> roles, Map<String, String> arguments, long transactionId)
	{
		super(roles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		return super.main(arguments);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple SQL or MQL query.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" (-sql=\"\" | (-entity=\"\" -mql=\"\"))";
	}

	/*---------------------------------------------------------------------*/
}
