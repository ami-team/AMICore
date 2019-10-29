package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class GetSessionInfo extends GetUserInfo
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetSessionInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		if(!arguments.containsKey("attachCert")
		   &&
		   !arguments.containsKey("detachCert")
		 ) {
			arguments.put("amiLogin", m_AMIUser);
		}

		arguments.put("exception", null);

		return super.main(arguments);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the session information.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "((-attachCert | -detachCert) -amiLogin=\"\" -amiPassword=\"\")? (-AMIUser=\"\" -AMIPass=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
