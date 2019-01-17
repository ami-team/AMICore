package net.hep.ami.command.user;

import java.util.*;

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

		List<Row> rowList = getQuerier("self").executeSQLQuery("SELECT `AMIUser`, `AMIPass`, `email` FROM `router_user` WHERE `AMIUser` = ? AND `valid` != 0", amiLogin).getAll(10, 0);

		if(rowList.size() > 0)
		{

		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[email sent with success]]></info>");
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
