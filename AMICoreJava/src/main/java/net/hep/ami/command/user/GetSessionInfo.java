package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class GetSessionInfo extends GetUserInfo
{
	/*---------------------------------------------------------------------*/

	public GetSessionInfo(Set<String> roles, Map<String, String> arguments, long transactionId)
	{
		super(roles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		arguments.put("amiLogin", m_AMIUser);

		arguments.put("exception", null);

		return super.main(arguments);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the session information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "((-attachCert | -detachCert) -amiLogin=\"\" -amiPassword=\"\")? (-AMIUser=\"\" -AMIPass=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
