package net.hep.ami.command.user;

import java.io.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import com.fasterxml.jackson.databind.*;

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
		String amiPassword = arguments.getOrDefault("amiPassword", "@NULL");

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

		String tags;
		String buildVersion;
		String branch;
		String commitId;
		String commitIdAbbrev;
		String remoteOriginURL;

		try(final InputStream inputStream = GetUserInfo.class.getClassLoader().getResourceAsStream("/git.properties"))
		{
			if(inputStream != null)
			{
				Properties properties = new Properties();

				properties.load(inputStream);

				tags = properties.getProperty("git.tags");
				buildVersion = properties.getProperty("git.build.version");
				branch = properties.getProperty("git.branch");
				commitId = properties.getProperty("git.commit.id");
				commitIdAbbrev = properties.getProperty("git.commit.id.abbrev");
				remoteOriginURL = properties.getProperty("git.remote.origin.url");
			}
			else
			{
				tags = "N/A";
				buildVersion = "N/A";
				branch = "N/A";
				commitId = "N/A";
				commitIdAbbrev = "N/A";
				remoteOriginURL = "N/A";
			}
		}
		catch(Exception e)
		{
			tags = "N/A";
			buildVersion = "N/A";
			branch = "N/A";
			commitId = "N/A";
			commitIdAbbrev = "N/A";
			remoteOriginURL = "N/A";
		}

		String termsAndConditions = ConfigSingleton.getProperty("terms_and_conditions", "N/A");

		String ssoLabel = ConfigSingleton.getProperty("sso_label", "SSO");
		String ssoSignInURL = ConfigSingleton.getProperty("sso_sign_in_url", "N/A");
		String ssoSignOutURL = ConfigSingleton.getProperty("sso_sign_out_url", "N/A");

		Map<String, Object> map = new HashMap<>();

		map.put("tags", tags);
		map.put("buildVersion", buildVersion);
		map.put("branch", branch);
		map.put("commitId", commitId);
		map.put("commitIdAbbrev", commitIdAbbrev);
		map.put("remoteOriginURL", remoteOriginURL);

		map.put("certEnabled", m_isSecure ? "true" : "false");
		map.put("vomsEnabled", vomsEnabled ? "true" : "false");

		map.put("datetimePrecision", ConfigSingleton.getProperty("datetime_precision", 6));
		map.put("datetimeFormat", ConfigSingleton.getProperty("datetime_format", "yyyy-MM-dd HH:mm:ss"));
		map.put("dateFormat", ConfigSingleton.getProperty("date_format", "yyyy-MM-dd"));

		map.put("timePrecision", ConfigSingleton.getProperty("time_precision", 6));
		map.put("timeHMSFormat", ConfigSingleton.getProperty("time_hms_format", "HH:mm:ss"));
		map.put("timeHMFormat", ConfigSingleton.getProperty("time_hm_format", "HH:mm"));

		map.put("ssoLabel", ConfigSingleton.getProperty("sso_label", "SSO"));
		map.put("ssoAuthURL", ConfigSingleton.getProperty("sso_auth_url", "N/A"));
		map.put("ssoSignOutURL", ConfigSingleton.getProperty("sso_sign_out_url", "N/A"));
		map.put("ssoRealm", ConfigSingleton.getProperty("sso_realm", "N/A"));
		map.put("ssoClientId", ConfigSingleton.getProperty("sso_client_id", "N/A"));
		map.put("ssoSecret", ConfigSingleton.getProperty("sso_secret", "N/A"));

		map.put("termsAndConditions", ConfigSingleton.getProperty("terms_and_conditions", "N/A"));

		map.put("nodeRedURL", ConfigSingleton.getProperty("node_red_url", "N/A"));

		String config = Base64.getUrlEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(map));

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

		result.append("<rowset type=\"awf\">");

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

	private String changeCert(Querier querier, UserValidator.Mode mode, @Nullable String amiLogin, @Nullable String amiPassword) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(mode == UserValidator.Mode.ATTACH)
		{
			if(Empty.is(m_clientDN, Empty.STRING_NULL_EMPTY_BLANK)
			   ||
			   Empty.is(m_issuerDN, Empty.STRING_NULL_EMPTY_BLANK)
			 ) {
				throw new Exception("you must provide a valid certificate");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rowList;

		if(Empty.is(amiPassword, Empty.STRING_NULL_EMPTY_BLANK) && !m_AMIUser.equals(GUEST_USER))
		{
			rowList = querier.executeSQLQuery("router_user", "SELECT `id`, `AMIUser`, `ssoUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `json` FROM `router_user` WHERE `AMIUser` = ?0", m_AMIUser).getAll();

			if(rowList.size() != 1)
			{
				throw new Exception("internal error, contact your administrator");
			}
		}
		else
		{
			rowList = querier.executeSQLQuery("router_user", "SELECT `id`, `AMIUser`, `ssoUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `json` FROM `router_user` WHERE `AMIUser` = ?0", amiLogin).getAll();

			if(rowList.size() != 1)
			{
				throw new Exception("bad username");
			}

			SecuritySingleton.checkPassword(
				rowList.get(0).getValue(1),
				amiPassword,
				rowList.get(0).getValue(3)
			);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String _id = rowList.get(0).getValue(0);

		UserValidator.Bean bean = new UserValidator.Bean(
			rowList.get(0).getValue(1),
			rowList.get(0).getValue(2),
			amiPassword,
			amiPassword,
			rowList.get(0).getValue(4),
			rowList.get(0).getValue(5),
			rowList.get(0).getValue(6),
			rowList.get(0).getValue(7),
			rowList.get(0).getValue(8),
			rowList.get(0).getValue(9)
		);

		/*------------------------------------------------------------------------------------------------------------*/

		if(mode == UserValidator.Mode.ATTACH && (
			!m_clientDN.equals(bean.getClientDN())
			||
			!m_issuerDN.equals(bean.getIssuerDN())
		   )
		   ||
		   mode == UserValidator.Mode.DETACH && (
			!Empty.is(bean.getClientDN(), Empty.STRING_NULL_EMPTY_BLANK)
			||
			!Empty.is(bean.getIssuerDN(), Empty.STRING_NULL_EMPTY_BLANK)
		   )
		 ) {
			/*--------------------------------------------------------------------------------------------------------*/

			if(!querier.executeSQLQuery("router_user", "SELECT `AMIUser` FROM `router_user` WHERE `id` != ?0 AND `clientDN` = ?#1 AND `issuerDN` = ?#2", _id, m_clientDN, m_issuerDN).getAll().isEmpty())
			{
				throw new Exception("this certificate is already associated to another account");
			}

			/*--------------------------------------------------------------------------------------------------------*/

			switch(mode)
			{
				case ATTACH:
					bean.setClientDN(m_clientDN);
					bean.setIssuerDN(m_issuerDN);
					break;

				case DETACH:
					bean.setClientDN(null);
					bean.setIssuerDN(null);
					break;

				default:
					throw new Exception("internal error, contact your administrator");
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
				bean.getClientDN(),
				bean.getIssuerDN(),
				bean.getSsoUsername(),
				bean.getJson(),
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
