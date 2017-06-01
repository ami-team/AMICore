package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class GetUserInfo extends AbstractCommand
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
		String amiLogin = arguments.get("amiLogin");

		if(amiLogin == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		List<Row> rowList = querier.executeQuery("SELECT `AMIUser`, `lastName`, `firstName`, `email`, `country`, `valid` FROM `router_user` WHERE `AMIUser` = '" + amiLogin + "'").getAll();

		if(rowList.isEmpty())
		{
			throw new Exception("invalid user `" + amiLogin + "`");
		}

		Row row1 = rowList.get(0);

		/*-----------------------------------------------------------------*/

		String AMIUser = row1.getValue("AMIUser");
		String firstName = row1.getValue("firstName");
		String lastName = row1.getValue("lastName");
		String email = row1.getValue("email");
		String valid = row1.getValue("valid");

		/*-----------------------------------------------------------------*/

		boolean VALID = valid.equals("0") == false;

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		RowSet rowSet2 = querier.executeQuery("SELECT `router_role`.`role` FROM `router_role`, `router_user_role` WHERE `router_user_role`.`userFK` = (SELECT MAX(`id`) FROM `router_user` WHERE `AMIUser` = '" + m_AMIUser + "' OR `AMIUser` = '" + m_guestUser + "') AND `router_user_role`.`roleFK` = `router_role`.`id`");

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

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

		result.append("<rowset type=\"role\">");

		for(Row row2: rowSet2.iterate())
		{
			result.append(
				"<row>"
				+
				"<field name=\"role\"><![CDATA[" + row2.getValue("role") + "]]></field>"
				+
				"</row>"
			);
		}

		result.append("</rowset>");

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
		return "-amiLogin=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
