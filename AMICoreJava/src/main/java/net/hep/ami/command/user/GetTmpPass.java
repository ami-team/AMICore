package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = true)
public class GetTmpPass extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	static final String s_guest = ConfigSingleton.getProperty("guest_user");

	/*---------------------------------------------------------------------*/

	public GetTmpPass(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String ssoLogin = arguments.get("ssoLogin");
		String amiLogin = arguments.get("amiLogin");

		int mode;

		/**/ if(ssoLogin != null
		        &&
		        amiLogin == null
		 ) {
			if(/*-----------*/ true /*-----------*/ && m_userRoles.contains("AMI_SSO") == false && m_userRoles.contains("AMI_ADMIN") == false)
			{
				throw new Exception("wrong role for user `" + m_AMIUser + "`");
			}

			mode = 0;
		}
		else if(ssoLogin == null
		        &&
		        amiLogin != null
		 ) {
			if(m_AMIUser.equals(amiLogin) == false && m_userRoles.contains("AMI_SSO") == false && m_userRoles.contains("AMI_ADMIN") == false)
			{
				throw new Exception("wrong role for user `" + m_AMIUser + "`");
			}

			mode = 1;
		}
		else if(ssoLogin == null
		        &&
		        amiLogin == null
		 ) {
			amiLogin = m_AMIUser;

			mode = 1;
		}
		else 
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		List<Row> rowList;

		switch(mode)
		{
			case 0:
				rowList = getQuerier("self").executeSQLQuery("SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `ssoUser` = ? AND `valid` != 0", ssoLogin).getAll(10, 0);
				break;

			case 1:
				rowList = getQuerier("self").executeSQLQuery("SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `AMIUser` = ? AND `valid` != 0", amiLogin).getAll(10, 0);
				break;

			default:
				throw new Exception("internal error");
		}

		/*-----------------------------------------------------------------*/

		String tmpUser;
		String tmpPass;

		if(rowList.size() > 0)
		{
			Row row = rowList.get(0);

			tmpUser = /*---------------------*/(row.getValue(0));
			tmpPass = SecuritySingleton.decrypt(row.getValue(1));

			tmpPass = SecuritySingleton.buildTmpPassword(tmpUser, tmpPass);
		}
		else
		{
			tmpUser = ConfigSingleton.getProperty("guest_user");
			tmpPass = ConfigSingleton.getProperty("guest_user");
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>").append("<row>")
		                          .append("<field name=\"tmpUser\"><![CDATA[").append(tmpUser).append("]]></field>")
		                          .append("<field name=\"tmpPass\"><![CDATA[").append(tmpPass).append("]]></field>")
		                          .append("</row>").append("</rowset>")
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get a temporary password.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-ssoLogin=\"\" | -amiLogin=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
