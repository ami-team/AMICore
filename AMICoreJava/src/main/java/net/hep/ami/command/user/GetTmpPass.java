package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
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
		String amiLogin = arguments.containsKey("amiLogin") ? arguments.get("amiLogin")
		                                                    : m_AMIUser
		;

		if(m_AMIUser.equals(amiLogin) == false && m_userRoles.contains("AMI_SSO") == false && m_userRoles.contains("AMI_ADMIN"))
		{
			throw new Exception("wrong role for user `" + m_AMIUser + "`");
		}

		/*-----------------------------------------------------------------*/
		/* GET USER INFO                                                   */
		/*-----------------------------------------------------------------*/

		List<Row> rowList = getQuerier("self").executeSQLQuery("SELECT `AMIPass` FROM `router_user` WHERE `AMIUser` = ?", amiLogin).getAll(10, 0);

		if(rowList.size() != 1)
		{
			throw new Exception("invalid user `" + amiLogin + "`");
		}

		Row row = rowList.get(0);

		/*-----------------------------------------------------------------*/

		String password = SecuritySingleton.buildTmpPassword(amiLogin, SecuritySingleton.decrypt(row.getValue(0)));

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>").append("<row>")
		                          .append("<field name=\"tmpUser\"><![CDATA[").append(amiLogin).append("]]></field>")
		                          .append("<field name=\"tmpPass\"><![CDATA[").append(password).append("]]></field>")
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
		return "(-amiLogin=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
