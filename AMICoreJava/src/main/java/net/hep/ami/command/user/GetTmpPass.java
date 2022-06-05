package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = true)
public class GetTmpPass extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetTmpPass(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");
		String ssoLogin = arguments.get("ssoLogin");

		int mode;

		/**/ if(ssoLogin == null
		        &&
			    amiLogin != null
		 ) {
			if(!m_AMIUser.equals(amiLogin) && !m_userRoles.contains("AMI_SSO") && !m_userRoles.contains("AMI_ADMIN"))
			{
				throw new Exception("wrong role for user `" + m_AMIUser + "`");
			}

			mode = 0;
		}
		else if(ssoLogin != null
		        &&
		        amiLogin == null
		 ) {
			if(/*--------------------------*/ !m_userRoles.contains("AMI_SSO") && !m_userRoles.contains("AMI_ADMIN"))
			{
				throw new Exception("wrong role for user `" + m_AMIUser + "`");
			}

			mode = 1;
		}
		else if(ssoLogin == null)
		{
			amiLogin = m_AMIUser;

			mode = 0;
		}
		else
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList;

		switch(mode)
		{
			case 0:
				rowList = getAdminQuerier("self").executeSQLQuery("router_user", "SELECT `AMIUser` FROM `router_user` WHERE `AMIUser` = ?0 AND `valid` != 0", amiLogin).getAll(10, 0);
				break;

			case 1:
				rowList = getAdminQuerier("self").executeSQLQuery("router_user", "SELECT `AMIUser` FROM `router_user` WHERE `ssoUser` = ?0 AND `valid` != 0", ssoLogin).getAll(10, 0);
				break;

			default:
				throw new Exception("internal error");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String tmpUsername;
		String tmpPassword;

		if(rowList.size() > 0)
		{
			tmpUsername = /*-------*/ rowList.get(0).getValue(0) /*-------*/;
			tmpPassword = SecuritySingleton.generateTmpPassword(tmpUsername);
		}
		else
		{
			tmpUsername = ConfigSingleton.getProperty("guest_user");
			tmpPassword = ConfigSingleton.getProperty("guest_pass");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>").append("<row>")
		                          .append("<field name=\"username\"><![CDATA[").append(tmpUsername).append("]]></field>")
		                          .append("<field name=\"password\"><![CDATA[").append(tmpPassword).append("]]></field>")
		                          .append("</row>").append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get a temporary password.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-amiLogin=\"\" | -ssoLogin=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
