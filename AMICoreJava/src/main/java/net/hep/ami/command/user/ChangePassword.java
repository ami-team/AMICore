package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class ChangePassword extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ChangePassword(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");
		String amiPasswordOld = arguments.get("amiPasswordOld");
		String amiPasswordNew = arguments.get("amiPasswordNew");

		if(amiLogin == null || amiPasswordOld == null || amiPasswordNew == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate(m_AMIUser, true, "UPDATE `router_user` SET `AMIPass` = ? WHERE `AMIUser` = ? AND `AMIPass` = ?",
			SecuritySingleton.encrypt(amiPasswordOld),
			/*---------------------*/(   amiLogin   ),
			SecuritySingleton.encrypt(amiPasswordOld)
		);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[bad user or password]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Change password.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"\" -amiPasswordOld=\"\" -amiPasswordNew=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
