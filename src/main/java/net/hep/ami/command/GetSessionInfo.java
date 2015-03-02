package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

public class GetSessionInfo extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public GetSessionInfo(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		QueryResult queryResult = getRouterLoader().executeQuery("SELECT `clientDN`, `issuerDN`, `lastName`, `firstName`, `email`, `valid` FROM `router_user` WHERE `AMIUser` = '" + m_AMIUser + "' || `AMIUser` = '" + m_guestUser + "'");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		String clientDNInAMI = queryResult.getValue(0, "clientDN");
		String issuerDNInAMI = queryResult.getValue(0, "issuerDN");
		String firstName = queryResult.getValue(0, "firstName");
		String lastName = queryResult.getValue(0, "lastName");
		String email = queryResult.getValue(0, "email");
		String valid = queryResult.getValue(0, "valid");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		boolean VALID = valid.equals("0") == false;

		boolean CERT_ENABLED = m_isSecure.equals("0") == false;

		boolean VOMS_ENABLED = m_useVOMS.equals("yes")
		                       ||
		                       m_useVOMS.equals("true")
		;

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result>");

		/*-----------------------------------------------------------------*/
		/* USER                                                            */
		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"user\">");
		result.append("<row>");

		result.append("<field name=\"AMIUser\"><![CDATA[" + m_AMIUser + "]]></field>");
		result.append("<field name=\"guestUser\"><![CDATA[" + m_guestUser + "]]></field>");
		result.append("<field name=\"clientDNInAMI\"><![CDATA[" + clientDNInAMI + "]]></field>");
		result.append("<field name=\"issuerDNInAMI\"><![CDATA[" + issuerDNInAMI + "]]></field>");
		result.append("<field name=\"clientDNInSession\"><![CDATA[" + m_clientDN + "]]></field>");
		result.append("<field name=\"issuerDNInSession\"><![CDATA[" + m_issuerDN + "]]></field>");
		result.append("<field name=\"firstName\"><![CDATA[" + firstName + "]]></field>");
		result.append("<field name=\"lastName\"><![CDATA[" + lastName + "]]></field>");
		result.append("<field name=\"email\"><![CDATA[" + email + "]]></field>");
		result.append("<field name=\"valid\"><![CDATA[" + VALID + "]]></field>");
		result.append("<field name=\"certEnabled\"><![CDATA[" + CERT_ENABLED + "]]></field>");
		result.append("<field name=\"vomsEnabled\"><![CDATA[" + VOMS_ENABLED + "]]></field>");

		result.append("</row>");
		result.append("</rowset>");

		/*-----------------------------------------------------------------*/
		/* ROLE                                                            */
		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"role\">");

		/* TODO */

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("</Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Get session information.";
	}

	/*---------------------------------------------------------------------*/
}
