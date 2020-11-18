package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetUserInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	static final String GUEST_USER = ConfigSingleton.getProperty("guest_user");

	/*----------------------------------------------------------------------------------------------------------------*/

	public GetUserInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		boolean exception = arguments.containsKey("exception");
		boolean attachCert = arguments.containsKey("attachCert");
		boolean detachCert = arguments.containsKey("detachCert");

		String amiLogin = arguments.getOrDefault("amiLogin", m_AMIUser);
		String amiPassword = arguments.getOrDefault("amiPassword", m_AMIPass);

		if(attachCert
		   &&
		   detachCert
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		boolean vomsEnabled = ConfigSingleton.getProperty("has_virtual_organization_management_system", false);

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getAdminQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/
		/* ATTACH CERTIFICATE                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		if(attachCert)
		{
			if(!m_isSecure
			   ||
			   Empty.is(m_clientDN, Empty.STRING_JAVA_NULL | Empty.STRING_AMI_NULL | Empty.STRING_EMPTY | Empty.STRING_BLANK)
			   ||
			   Empty.is(m_issuerDN, Empty.STRING_JAVA_NULL | Empty.STRING_AMI_NULL | Empty.STRING_EMPTY | Empty.STRING_BLANK)
			 ) {
				throw new Exception("You must connect using https and provide a valid certificate");
			}

			String commandName = ConfigSingleton.getProperty("user_override_info");

			if(Empty.is(commandName, Empty.STRING_JAVA_NULL | Empty.STRING_AMI_NULL | Empty.STRING_EMPTY | Empty.STRING_BLANK))
			{
				String sql;

				if(!vomsEnabled)
				{
					sql = "UPDATE `router_user` SET `clientDN` = ?#0, `issuerDN` = ?#1 WHERE `AMIUser` = ?2 AND `AMIPass` = ?#3";
				}
				else
				{
					sql = "UPDATE `router_user` SET `clientDN` = ?#0, `issuerDN` = ?#1, `valid` = '1' WHERE `AMIUser` = ?2 AND `AMIPass` = ?#3";
				}

				Update update = querier.executeSQLUpdate("router_user", sql, m_clientDN, m_issuerDN, amiLogin, amiPassword);

				return new StringBuilder(
					update.getNbOfUpdatedRows() == 1 ? "<info><![CDATA[done with success]]></info>"
					                                 : "<error><![CDATA[nothing done]]></error>"
				);
			}
			else
			{
				return executeCommand(commandName, arguments);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* DETACH CERTIFICATE                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		if(detachCert)
		{
			String commandName = ConfigSingleton.getProperty("user_override_info");

			if(Empty.is(commandName, Empty.STRING_JAVA_NULL | Empty.STRING_AMI_NULL | Empty.STRING_EMPTY | Empty.STRING_BLANK))
			{
				String sql;

				if(!vomsEnabled)
				{
					sql = "UPDATE `router_user` SET `clientDN` = ?#0, `issuerDN` = ?#1 WHERE `AMIUser` = ?2 AND `AMIPass` = ?#3";
				}
				else
				{
					sql = "UPDATE `router_user` SET `clientDN` = ?#0, `issuerDN` = ?#1, `valid` = '0' WHERE `AMIUser` = ?2 AND `AMIPass` = ?#3";
				}

				Update update = querier.executeSQLUpdate("router_user", sql, "", "", amiLogin, amiPassword);

				return new StringBuilder(
					update.getNbOfUpdatedRows() == 1 ? "<info><![CDATA[done with success]]></info>"
					                                 : "<error><![CDATA[nothing done]]></error>"
				);
			}
			else
			{
				return executeCommand(commandName, arguments);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET USER INFO                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList = querier.executeSQLQuery("router_user", "SELECT `AMIUser`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `country`, `valid` FROM `router_user` WHERE `AMIUser` = ?0", amiLogin).getAll(10, 0);

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET USER ROLES                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet2 = querier.executeSQLQuery("router_role", "SELECT `router_role`.`role`, `router_role`.`description` FROM `router_user_role`, `router_user`, `router_role` WHERE `router_user_role`.`userFK` = `router_user`.`id` AND `router_user_role`.`roleFK` = `router_role`.`id` AND `AMIUser` = ?0", amiLogin);

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET USER BOOKMARKS                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet3 = getQuerier("self").executeSQLQuery("router_short_url", "SELECT `id`, `hash`, `name`, `rank`, `json`, `shared`, `expire` FROM `router_short_url` WHERE `owner` = ?0 ORDER BY `rank`", amiLogin);

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET OTHER INFO                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		String termsAndConditions = ConfigSingleton.getProperty("terms_and_conditions", "N/A");

		String ssoLabel = ConfigSingleton.getProperty("sso_label", "SSO");

		String ssoURL = ConfigSingleton.getProperty("sso_url", "N/A");

		/*------------------------------------------------------------------------------------------------------------*/
		/* USER INFO                                                                                                  */
		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* ROLES                                                                                                      */
		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* BOOKMARKS                                                                                                  */
		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"bookmark\">");

		for(Row row: rowSet3.iterate())
		{
			result.append("<row>")
					.append("<field name=\"id\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
					.append("<field name=\"hash\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
					.append("<field name=\"name\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
					.append("<field name=\"rank\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
					.append("<field name=\"json\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
					.append("<field name=\"shared\"><![CDATA[").append(row.getValue(5)).append("]]></field>")
					.append("<field name=\"expire\"><![CDATA[").append(row.getValue(6)).append("]]></field>")
					.append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/
		/* UDP                                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"udp\">");

		if(!termsAndConditions.isEmpty()
		   &&
		   !"N/A".equals(termsAndConditions)
		 ) {
			result.append("<row>")
			      .append("<field name=\"termsAndConditions\"><![CDATA[").append(termsAndConditions).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/
		/* SSO                                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"sso\">");

		if(!ssoURL.isEmpty()
		   &&
		   !"N/A".equals(ssoURL)
		 ) {
			result.append("<row>")
			      .append("<field name=\"label\"><![CDATA[").append(ssoLabel).append("]]></field>")
			      .append("<field name=\"url\"><![CDATA[").append(ssoURL).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the user information.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-amiLogin=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
