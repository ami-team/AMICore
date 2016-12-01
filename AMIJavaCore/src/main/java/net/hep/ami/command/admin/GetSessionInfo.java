package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class GetSessionInfo extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public GetSessionInfo(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		boolean attachCert = arguments.containsKey("attachCert");
		boolean detachCert = arguments.containsKey("detachCert");

		String amiLogin = arguments.containsKey("amiLogin") ? arguments.get("amiLogin")
		                                                    : ""
		;

		String amiPassword = arguments.containsKey("amiPassword") ? arguments.get("amiPassword")
		                                                          : ""
		;

		if(attachCert
		   &&
		   detachCert
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
			throw new Exception("invalid user `" + amiLogin + "`");
		}

		Row row1 = rowList.get(0);

		/*-----------------------------------------------------------------*/

		String AMIUser = row1.getValue("AMIUser");
		String clientDNInAMI = row1.getValue("clientDN");
		String issuerDNInAMI = row1.getValue("issuerDN");
		String firstName = row1.getValue("firstName");
		String lastName = row1.getValue("lastName");
		String email = row1.getValue("email");
		String valid = row1.getValue("valid");

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

		if(attachCert)
		{
			amiPassword = CryptographySingleton.encrypt(amiPassword);

			String clientDN = CryptographySingleton.encrypt(m_clientDN);
			String issuerDN = CryptographySingleton.encrypt(m_issuerDN);

			String sql;

			if(VOMS_ENABLED == false)
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "' WHERE AMIUser='" + amiLogin + "' AND AMIPass='" + amiPassword + "'";
			}
			else
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "',`valid`='1' WHERE AMIUser='" + amiLogin + "' AND AMIPass='" + amiPassword + "'";
			}

			if(transactionalQuerier.executeUpdate(sql) != 1)
			{
				throw new Exception("wrong authentication");
			}
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		if(detachCert)
		{
			amiPassword = CryptographySingleton.encrypt(amiPassword);

			String clientDN = CryptographySingleton.encrypt("");
			String issuerDN = CryptographySingleton.encrypt("");

			String sql;

			if(VOMS_ENABLED == false)
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "' WHERE AMIUser='" + amiLogin + "' AND AMIPass='" + amiPassword + "'";
			}
			else
			{
				sql = "UPDATE `router_user` SET `clientDN`='" + clientDN + "',`issuerDN`='" + issuerDN + "',`valid`='0' WHERE AMIUser='" + amiLogin + "' AND AMIPass='" + amiPassword + "'";
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
			"<field name=\"clientDNInAMI\"><![CDATA[" + CryptographySingleton.decrypt(clientDNInAMI) + "]]></field>"
			+
			"<field name=\"issuerDNInAMI\"><![CDATA[" + CryptographySingleton.decrypt(issuerDNInAMI) + "]]></field>"
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
