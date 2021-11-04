package net.hep.ami.command.user;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
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

		Querier querier = getAdminQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/
		/* ATTACH CERTIFICATE                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		if(attachCert)
		{
			return new StringBuilder(changeCert(querier, UserValidator.Mode.ATTACH, amiLogin, amiPassword));
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* DETACH CERTIFICATE                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		if(detachCert)
		{
			return new StringBuilder(changeCert(querier, UserValidator.Mode.DETACH, amiLogin, amiPassword));
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

		/*------------------------------------------------------------------------------------------------------------*/

		if("@NULL".equals(clientDNInAMI)) {
			clientDNInAMI = "";
		}

		if("@NULL".equals(issuerDNInAMI)) {
			issuerDNInAMI = "";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		boolean vomsEnabled = ConfigSingleton.getProperty("has_virtual_organization_management_system", false);

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
		String ssoSignInURL = ConfigSingleton.getProperty("sso_sign_in_url", "N/A");
		String ssoSignOutURL = ConfigSingleton.getProperty("sso_sign_out_url", "N/A");

		Map<String, String> map = new HashMap<>();

		map.put("certEnabled", m_isSecure ? "true" : "false");
		map.put("vomsEnabled", vomsEnabled ? "true" : "false");

		map.put("datetimeFormat", ConfigSingleton.getProperty("datetime_format", "yyyy-MM-dd HH:mm:ss"));
		map.put("timePrecision", ConfigSingleton.getProperty("time_precision", "6"));
		map.put("dateFormat", ConfigSingleton.getProperty("date_format", "yyyy-MM-dd"));
		map.put("timeFormatHMS", ConfigSingleton.getProperty("time_format_hms", "HH:mm:ss"));
		map.put("timeFormatHM", ConfigSingleton.getProperty("time_format_hm", "HH:mm"));

		map.put("ssoLabel", ConfigSingleton.getProperty("sso_label", "SSO"));
		map.put("ssoSignInURL", ConfigSingleton.getProperty("sso_sign_in_url", "N/A"));
		map.put("ssoSignOutURL", ConfigSingleton.getProperty("sso_sign_out_url", "N/A"));

		map.put("termsAndConditions", ConfigSingleton.getProperty("terms_and_conditions", "N/A"));

		String config = Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(map));

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
		/* CONFIG                                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"config\">");

		result.append("<row>")
		      .append("<field name=\"config\"><![CDATA[").append(config).append("]]></field>")
		      .append("</row>")
		;

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

		if(!ssoLabel.isEmpty()
		   &&
		   !"N/A".equals(ssoSignInURL)
		   &&
		   !"N/A".equals(ssoSignOutURL)
		 ) {
			result.append("<row>")
			      .append("<field name=\"label\"><![CDATA[").append(ssoLabel).append("]]></field>")
			      .append("<field name=\"signInURL\"><![CDATA[").append(ssoSignInURL).append("]]></field>")
			      .append("<field name=\"signOutURL\"><![CDATA[").append(ssoSignOutURL).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private String changeCert(Querier querier, UserValidator.Mode mode, String amiLogin, String amiPassword) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(mode == UserValidator.Mode.ATTACH)
		{
			if(!m_isSecure
			   ||
			   Empty.is(m_clientDN, Empty.STRING_NULL_EMPTY_BLANK)
			   ||
			   Empty.is(m_issuerDN, Empty.STRING_NULL_EMPTY_BLANK)
			 ) {
				throw new Exception("You must connect using https and provide a valid certificate");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList = querier.executeSQLQuery("router_user", "SELECT `id`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `ssoUser`, `json` FROM `router_user` WHERE `AMIUser` = ?0 AND `AMIPass` = ?#1", amiLogin, amiPassword).getAll();

		/*------------------------------------------------------------------------------------------------------------*/

		if(rowList.size() != 1)
		{
			throw new Exception("Bad login and/or password");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String _id = rowList.get(0).getValue(0);

		UserValidator.Bean bean = new UserValidator.Bean(
			amiLogin,
			amiPassword,
			amiPassword,
			rowList.get(0).getValue(1),
			rowList.get(0).getValue(2),
			rowList.get(0).getValue(3),
			rowList.get(0).getValue(4),
			rowList.get(0).getValue(5),
			rowList.get(0).getValue(6),
			rowList.get(0).getValue(7)
		);

		/*------------------------------------------------------------------------------------------------------------*/

		if(mode == UserValidator.Mode.ATTACH && (
			!m_clientDN.equals(bean.clientDN)
			||
			!m_issuerDN.equals(bean.issuerDN)
		   )
		   ||
		   mode == UserValidator.Mode.DETACH && (
			!Empty.is(bean.clientDN, Empty.STRING_NULL_EMPTY_BLANK)
			||
			!Empty.is(bean.issuerDN, Empty.STRING_NULL_EMPTY_BLANK)
		   )
		 ) {
			/*--------------------------------------------------------------------------------------------------------*/

			switch(mode)
			{
				case ATTACH:
					bean.clientDN = m_clientDN;
					bean.issuerDN = m_issuerDN;
					break;

				case DETACH:
					bean.clientDN = null;
					bean.issuerDN = null;
					break;

				default:
					throw new Exception("Internal error");
			}

			/*--------------------------------------------------------------------------------------------------------*/

			boolean valid = RoleSingleton.checkUser(
				ConfigSingleton.getProperty("user_cert_validator_class"),
				mode,
				bean
			);

			/*--------------------------------------------------------------------------------------------------------*/

			Update update = querier.executeSQLUpdate("router_user", "UPDATE `router_user` SET `clientDN` = ?#1, `issuerDN` = ?#2, `ssoUser` = ?3, `json` = ?4, `valid` = ?5 WHERE `id` = ?0",
				_id,
				bean.clientDN,
				bean.issuerDN,
				bean.ssoUser,
				bean.json,
				valid ? 1 : 0
			);

			/*--------------------------------------------------------------------------------------------------------*/

			if(update.getNbOfUpdatedRows() != 1)
			{
				return "<error><![CDATA[nothing done]]></error>";
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return "<info><![CDATA[done with success]]></info>";

		/*------------------------------------------------------------------------------------------------------------*/
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
