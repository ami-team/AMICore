package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

public class GetSessionInfo extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	static final String m_useVOMS = ConfigSingleton.getProperty("use_voms");

	/*---------------------------------------------------------------------*/

	private boolean m_attachCert;
	private boolean m_detachCert;

	private String m_amiLogin;
	private String m_amiPassword;

	/*---------------------------------------------------------------------*/

	public GetSessionInfo(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);

		m_attachCert = arguments.containsKey("attachCert");
		m_detachCert = arguments.containsKey("detachCert");

		m_amiLogin = arguments.containsKey("amiLogin") ? arguments.get("amiLogin")
		                                               : ""
		;

		m_amiPassword = arguments.containsKey("amiPassword") ? arguments.get("amiPassword")
		                                                     : ""
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		if(m_attachCert
		   &&
		   m_detachCert
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		QueryResult queryResult1 = transactionalQuerier.executeSQLQuery("SELECT `AMIUser`,`clientDN`,`issuerDN`,`lastName`,`firstName`,`email`,`valid` FROM `router_user` WHERE `id`=(SELECT MAX(`id`) FROM `router_user` WHERE `AMIUser`='" + m_AMIUser + "' OR `AMIUser`='" + m_guestUser + "')");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		String AMIUser = queryResult1.getValue(0, "AMIUser");
		String clientDNInAMI = queryResult1.getValue(0, "clientDN");
		String issuerDNInAMI = queryResult1.getValue(0, "issuerDN");
		String firstName = queryResult1.getValue(0, "firstName");
		String lastName = queryResult1.getValue(0, "lastName");
		String email = queryResult1.getValue(0, "email");
		String valid = queryResult1.getValue(0, "valid");

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
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		if(m_attachCert)
		{
			m_amiPassword = Cryptography.encrypt(m_amiPassword);

			String clientDN = Cryptography.encrypt(m_clientDN);
			String issuerDN = Cryptography.encrypt(m_issuerDN);

			String sql;

			if(VOMS_ENABLED == false)
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "' WHERE AMIUser='" + m_amiLogin + "' AND AMIPass='" + m_amiPassword + "'";
			}
			else
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "',`valid`='1' WHERE AMIUser='" + m_amiLogin + "' AND AMIPass='" + m_amiPassword + "'";
			}

			if(transactionalQuerier.executeSQLUpdate(sql) != 1)
			{
				throw new Exception("authentication error");
			}
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		if(m_detachCert)
		{
			m_amiPassword = Cryptography.encrypt(m_amiPassword);

			String clientDN = Cryptography.encrypt("");
			String issuerDN = Cryptography.encrypt("");

			String sql;

			if(VOMS_ENABLED == false)
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "' WHERE AMIUser='" + m_amiLogin + "' AND AMIPass='" + m_amiPassword + "'";
			}
			else
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "',`valid`='0' WHERE AMIUser='" + m_amiLogin + "' AND AMIPass='" + m_amiPassword + "'";
			}

			if(transactionalQuerier.executeSQLUpdate(sql) != 1)
			{
				throw new Exception("authentication error");
			}
		}

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
			"<field name=\"guestUser\"><![CDATA[" + m_guestUser + "]]></field>"
			+
			"<field name=\"clientDNInAMI\"><![CDATA[" + Cryptography.decrypt(clientDNInAMI) + "]]></field>"
			+
			"<field name=\"issuerDNInAMI\"><![CDATA[" + Cryptography.decrypt(issuerDNInAMI) + "]]></field>"
			+
			"<field name=\"clientDNInSession\"><![CDATA[" + m_clientDN + "]]></field>"
			+
			"<field name=\"issuerDNInSession\"><![CDATA[" + m_issuerDN + "]]></field>"
			+
			"<field name=\"firstName\"><![CDATA[" + firstName + "]]></field>"
			+
			"<field name=\"lastName\"><![CDATA[" + lastName + "]]></field>"
			+
			"<field name=\"email\"><![CDATA[" + email + "]]></field>"
			+
			"<field name=\"valid\"><![CDATA[" + VALID + "]]></field>"
			+
			"<field name=\"certEnabled\"><![CDATA[" + CERT_ENABLED + "]]></field>"
			+
			"<field name=\"vomsEnabled\"><![CDATA[" + VOMS_ENABLED + "]]></field>"
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
		return "Get session information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "((-attachCert | -detachCert) -AMIUser=\"value\" -AMIPass=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
