package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class GetSessionInfo extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private boolean m_attachCert;
	private boolean m_detachCert;

	private String m_amiLogin;
	private String m_amiPassword;

	/*---------------------------------------------------------------------*/

	public GetSessionInfo(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);

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

		List<Row> rowList = transactionalQuerier.executeQuery("SELECT `AMIUser`,`clientDN`,`issuerDN`,`lastName`,`firstName`,`email`,`valid` FROM `router_user` WHERE `id`=(SELECT MAX(`id`) FROM `router_user` WHERE `AMIUser`='" + m_AMIUser + "' OR `AMIUser`='" + m_guestUser + "')").getAll();

		if(rowList.size() == 0)
		{
			throw new Exception("invalid user `" + m_amiLogin + "`");
		}

		Row row1 = rowList.get(0);

		String AMIUser = row1.getValue("AMIUser");
		String clientDNInAMI = row1.getValue("clientDN");
		String issuerDNInAMI = row1.getValue("issuerDN");
		String firstName = row1.getValue("firstName");
		String lastName = row1.getValue("lastName");
		String email = row1.getValue("email");
		String valid = row1.getValue("valid");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		String useVOMS = ConfigSingleton.getProperty("use_voms");

		/*-----------------------------------------------------------------*/

		boolean VALID = valid.equals("0") == false;

		boolean CERT_ENABLED = m_isSecure.equals("0") == false;

		boolean VOMS_ENABLED = !(
				useVOMS.equals("0") == false
				&&
				useVOMS.equals("no") == false
				&&
				useVOMS.equals("false") == false
		);

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

			if(transactionalQuerier.executeUpdate(sql) != 1)
			{
				throw new Exception("wrong authentication");
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

			if(transactionalQuerier.executeUpdate(sql) != 1)
			{
				throw new Exception("wrong authentication");
			}
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		RowSet rowSet2 = transactionalQuerier.executeQuery("SELECT `router_role`.`role` FROM `router_role`, `router_user_role` WHERE `router_user_role`.`userFK`=(SELECT MAX(`id`) FROM `router_user` WHERE `AMIUser`='" + m_AMIUser + "' OR `AMIUser`='" + m_guestUser + "') AND `router_user_role`.`roleFK`=`router_role`.`id`");

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

		result.append("<rowset type=\"role\">");

		for(Row row: rowSet2.iter())
		{
			result.append(
				"<row>"
				+
				"<field name=\"name\"><![CDATA[" + row.getValue("role") + "]]></field>"
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
		return "Get session information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "((-attachCert | -detachCert) -amiLogin=\"value\" -amiPassword=\"value\")? (-AMIUser=\"value\" -AMIPass=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
