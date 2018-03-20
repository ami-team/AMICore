package net.hep.ami.command.user;

import java.util.*;

public class GetUserInfo extends GetSessionInfo
{
	/*---------------------------------------------------------------------*/

	public GetUserInfo(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		if(arguments.containsKey("amiLogin") == false)
		{
			throw new Exception("invalid usage");
		}

		arguments.put("exception", "");

		return super.main(arguments);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the user information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
