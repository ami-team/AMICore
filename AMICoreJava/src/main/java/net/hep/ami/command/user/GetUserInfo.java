package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetUserInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	static final String GUEST_USER = ConfigSingleton.getProperty("guest_user");

	/*---------------------------------------------------------------------*/

	public GetUserInfo(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		boolean exception = arguments.containsKey("exception");
		boolean attachCert = arguments.containsKey("attachCert");
		boolean detachCert = arguments.containsKey("detachCert");

		String amiLogin = arguments.containsKey("amiLogin") ? arguments.get("amiLogin")
		                                                    : m_AMIUser
		;

		String amiPassword = arguments.containsKey("amiPassword") ? arguments.get("amiPassword")
		                                                          : m_AMIPass
		;

		if(attachCert
		   &&
		   detachCert
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getAdminQuerier("self");

		/*-----------------------------------------------------------------*/
		/* GET USER INFO                                                   */
		/*-----------------------------------------------------------------*/

		List<Row> rowList = querier.executeSQLQuery("router_user", "SELECT `AMIUser`, `clientDN`, `issuerDN`, `lastName`, `firstName`, `email`, `country`, `valid` FROM `router_user` WHERE `AMIUser` = ?", amiLogin).getAll(10, 0);

		String AMIUser;
		String clientDNInAMI;
		String issuerDNInAMI;
		String firstName;
		String lastName;
		String email;
		String country;
		boolean valid;

		if(rowList.size() == 1)
		{
			Row row1 = rowList.get(0);

			AMIUser = row1.getValue(0);
			clientDNInAMI = row1.getValue(1);
			issuerDNInAMI = row1.getValue(2);
			firstName = row1.getValue(3);
			lastName = row1.getValue(4);
			email = row1.getValue(5);
			country = row1.getValue(6);
			valid = row1.getValue(7, false);
		}
		else
		{
			if(exception)
			{
				throw new Exception("invalid user `" + amiLogin + "`");
			}

			AMIUser = GUEST_USER;
			clientDNInAMI = "";
			issuerDNInAMI = "";
			firstName = GUEST_USER;
			lastName = GUEST_USER;
			email = "N/A";
			country = "N/A";
			valid = true;
		}

		if("@NULL".equals(clientDNInAMI)) {
			clientDNInAMI = "";
		}

		if("@NULL".equals(issuerDNInAMI)) {
			issuerDNInAMI = "";
		}

		boolean vomsEnabled = ConfigSingleton.getProperty("has_virtual_organization_management_system", false);

		/*-----------------------------------------------------------------*/
		/* ATTACH CERTIFICATE                                              */
		/*-----------------------------------------------------------------*/

		if(attachCert)
		{
			amiPassword = SecuritySingleton.encrypt(amiPassword);

			String clientDN = SecuritySingleton.encrypt(m_clientDN);
			String issuerDN = SecuritySingleton.encrypt(m_issuerDN);

			String sql;

			if(vomsEnabled == false)
			{
				sql = "UPDATE `router_user` SET `clientDN` = ?, `issuerDN` = ? WHERE `AMIUser` = ? AND `AMIPass` = ?";
			}
			else
			{
				sql = "UPDATE `router_user` SET `clientDN` = ?, `issuerDN` = ?, `valid` = '1' WHERE `AMIUser` = ? AND `AMIPass` = ?";
			}

			Update update = querier.executeSQLUpdate(sql, clientDN, issuerDN, amiLogin, amiPassword);

			return new StringBuilder(
				update.getNbOfUpdatedRows() == 1 ? "<info><![CDATA[done with success]]></info>"
				                                 : "<error><![CDATA[nothing done]]></error>"
			);
		}

		/*-----------------------------------------------------------------*/
		/* DETACH CERTIFICATE                                              */
		/*-----------------------------------------------------------------*/

		if(detachCert)
		{
			amiPassword = SecuritySingleton.encrypt(amiPassword);

			String clientDN = SecuritySingleton.encrypt("");
			String issuerDN = SecuritySingleton.encrypt("");

			String sql;

			if(vomsEnabled == false)
			{
				sql = "UPDATE `router_user` SET `clientDN` = ?, `issuerDN` = ? WHERE `AMIUser` = ? AND `AMIPass` = ?";
			}
			else
			{
				sql = "UPDATE `router_user` SET `clientDN` = ?, `issuerDN` = ?, `valid` = '0' WHERE `AMIUser` = ? AND `AMIPass` = ?";
			}

			Update update = querier.executeSQLUpdate(sql, clientDN, issuerDN, amiLogin, amiPassword);

			return new StringBuilder(
				update.getNbOfUpdatedRows() == 1 ? "<info><![CDATA[done with success]]></info>"
				                                 : "<error><![CDATA[nothing done]]></error>"
			);
		}

		/*-----------------------------------------------------------------*/
		/* GET USER ROLES                                                  */
		/*-----------------------------------------------------------------*/

		RowSet rowSet2 = querier.executeSQLQuery("router_role", "SELECT `router_role`.`role`, `router_role`.`description` FROM `router_user_role`, `router_user`, `router_role` WHERE `router_user_role`.`userFK` = `router_user`.`id` AND `router_user_role`.`roleFK` = `router_role`.`id` AND `AMIUser` = ?", amiLogin);

		/*-----------------------------------------------------------------*/
		/* GET OTHER INFO                                                  */
		/*-----------------------------------------------------------------*/

		String termsAndConditions = ConfigSingleton.getProperty("terms_and_conditions", null);

		if(termsAndConditions == null || termsAndConditions.isEmpty()) {
			termsAndConditions = "N/A";
		}

		String ssoLabel = ConfigSingleton.getProperty("sso_label", null);

		if(ssoLabel == null || ssoLabel.isEmpty()) {
			ssoLabel = "SSO";
		}

		String ssoURL = ConfigSingleton.getProperty("sso_url", null);

		if(ssoURL == null || ssoURL.isEmpty()) {
			ssoURL = "N/A";
		}

		/*-----------------------------------------------------------------*/
		/* USER                                                            */
		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"user\">")
		      .append("<row>")
		      .append("<field name=\"AMIUser\"><![CDATA[").append(AMIUser).append("]]></field>")
		      .append("<field name=\"guestUser\"><![CDATA[").append(GUEST_USER).append("]]></field>")
		      .append("<field name=\"clientDNInAMI\"><![CDATA[").append(clientDNInAMI).append("]]></field>")
		      .append("<field name=\"issuerDNInAMI\"><![CDATA[").append(issuerDNInAMI).append("]]></field>")
		      .append("<field name=\"clientDNInSession\"><![CDATA[").append(m_clientDN).append("]]></field>")
		      .append("<field name=\"issuerDNInSession\"><![CDATA[").append(m_issuerDN).append("]]></field>")
		      .append("<field name=\"notBefore\"><![CDATA[").append(m_notBefore).append("]]></field>")
		      .append("<field name=\"notAfter\"><![CDATA[").append(m_notAfter).append("]]></field>")
		      .append("<field name=\"firstName\"><![CDATA[").append(firstName).append("]]></field>")
		      .append("<field name=\"lastName\"><![CDATA[").append(lastName).append("]]></field>")
		      .append("<field name=\"email\"><![CDATA[").append(email).append("]]></field>")
		      .append("<field name=\"country\"><![CDATA[").append(country).append("]]></field>")
		      .append("<field name=\"valid\"><![CDATA[").append(valid).append("]]></field>")
		      .append("<field name=\"certEnabled\"><![CDATA[").append(m_isSecure).append("]]></field>")
		      .append("<field name=\"vomsEnabled\"><![CDATA[").append(vomsEnabled).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*-----------------------------------------------------------------*/
		/* ROLES                                                           */
		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"role\">");

		for(Row row2: rowSet2.iterate())
		{
			result.append("<row>")
			      .append("<field name=\"name\"><![CDATA[").append(row2.getValue(0)).append("]]></field>")
			      .append("<field name=\"description\"><![CDATA[").append(row2.getValue(1)).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/
		/* UDP                                                             */
		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"udp\">");

		if(termsAndConditions.isEmpty() == false
		   &&
		   "N/A".equals(termsAndConditions) == false
		 ) {
			result.append("<row>")
			      .append("<field name=\"termsAndConditions\"><![CDATA[").append(termsAndConditions).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/
		/* SSO                                                            */
		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"sso\">");

		if(ssoURL.isEmpty() == false
		   &&
		   "N/A".equals(ssoURL) == false
		 ) {
			result.append("<row>")
			      .append("<field name=\"label\"><![CDATA[").append(ssoLabel).append("]]></field>")
			      .append("<field name=\"url\"><![CDATA[").append(ssoURL).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the user information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-amiLogin=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
