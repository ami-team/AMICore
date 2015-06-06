package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class GetUserInfo extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_amiLogin;

	/*---------------------------------------------------------------------*/

	public GetUserInfo(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);

		m_amiLogin = arguments.get("amiLogin");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_amiLogin == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		QueryResult queryResult1 = transactionalQuerier.executeSQLQuery("SELECT `AMIUser`,`lastName`,`firstName`,`email`,`valid` FROM `router_user` WHERE `AMIUser`='" + m_amiLogin + "'");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		String AMIUser = queryResult1.getValue(0, "AMIUser");
		String firstName = queryResult1.getValue(0, "firstName");
		String lastName = queryResult1.getValue(0, "lastName");
		String email = queryResult1.getValue(0, "email");
		String valid = queryResult1.getValue(0, "valid");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		boolean VALID = valid.equals("0") == false;

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		QueryResult queryResult2 = transactionalQuerier.executeSQLQuery("SELECT `router_role`.`role` FROM `router_role`, `router_user_role` WHERE `router_user_role`.`userFK`=(SELECT MAX(`id`) FROM `router_user` WHERE `AMIUser`='" + m_AMIUser + "' OR `AMIUser`='" + m_guestUser + "') AND `router_user_role`.`roleFK`=`router_role`.`id`");

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result>");

		/*-----------------------------------------------------------------*/
		/* USER                                                            */
		/*-----------------------------------------------------------------*/

		result.append(
			"<rowset type=\"user\">"
			+
			"<row>"
			+
			"<field name=\"AMIUser\"><![CDATA[" + AMIUser + "]]></field>"
			+
			"<field name=\"firstName\"><![CDATA[" + firstName + "]]></field>"
			+
			"<field name=\"lastName\"><![CDATA[" + lastName + "]]></field>"
			+
			"<field name=\"email\"><![CDATA[" + email + "]]></field>"
			+
			"<field name=\"valid\"><![CDATA[" + VALID + "]]></field>"
			+
			"</row>"
			+
			"</rowset>"
		);

		/*-----------------------------------------------------------------*/
		/* ROLE                                                            */
		/*-----------------------------------------------------------------*/

		final int numberOfRows = queryResult2.getNumberOfRows();

		result.append("<rowset type=\"role\">");

		for(int i = 0; i < numberOfRows; i++)
		{
			result.append(
				"<row>"
				+
				"<field name=\"role\"><![CDATA[" + queryResult2.getValue(i, "role") + "]]></field>"
				+
				"</row>"
			);
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("</Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get user information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-amiLogin=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
