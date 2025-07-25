package net.hep.ami.servlet;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.security.cert.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.mqtt.*;
import net.hep.ami.role.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@WebServlet(
	name = "FrontEnd",
	urlPatterns = "/FrontEnd"
)
@SuppressWarnings("DuplicatedCode")
public class FrontEnd extends HttpServlet
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Serial
	private static final long serialVersionUID = 6325706434625863655L;

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(FrontEnd.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final String GUEST_USER = ConfigSingleton.getProperty("guest_user");

	/*----------------------------------------------------------------------------------------------------------------*/

	public void init(@NotNull ServletConfig config) throws ServletException
	{
		super.init(config);

		new MQTT().init();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		super.doOptions(req, res);

		String origin = req.getHeader("Origin");

		if(origin != null)
		{
			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Origin", origin);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	protected void doGet(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res)
	{
		doCommand(req, res);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	protected void doPost(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res)
	{
		doCommand(req, res);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void doCommand(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res)
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* SET UTF-8 AS DEFAULT ENCODING                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			req.setCharacterEncoding("UTF-8");
			res.setCharacterEncoding("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			/* IGNORE */
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CROSS-ORIGIN RESOURCE SHARING                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		String origin = req.getHeader("Origin");

		if(origin != null)
		{
			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Origin", origin);
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET/POST VARIABLES                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		String command = req.getParameter("Command");
		command = (command != null) ? command.trim() : "";

		String converter = req.getParameter("Converter");
		converter = (converter != null) ? converter.trim() : "";

		String textOutput = req.getParameter("TextOutput");
		textOutput = (textOutput != null) ? textOutput.trim() : "";

		/*------------------------------------------------------------------------------------------------------------*/
		/* SET CONTENT DISPOSITION                                                                                    */
		/*------------------------------------------------------------------------------------------------------------*/

		if(textOutput.isEmpty())
		{
			res.setHeader("Content-disposition", "inline; filename=" + ((("AMI"))) );
		}
		else
		{
			res.setHeader("Content-disposition", "attachment; filename=" + textOutput);
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		String data;

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CHECK AMI CONFIG FILE                                                                                  */
			/*--------------------------------------------------------------------------------------------------------*/

			ConfigSingleton.checkValidAMIConfigFile();

			/*--------------------------------------------------------------------------------------------------------*/
			/* PARSE COMMAND                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			Command.CommandAndArguments commandAndArguments = Command.parse(command);

			/*--------------------------------------------------------------------------------------------------------*/

			updateSessionAndCommandArguments(
				commandAndArguments.getArguments(),
				req
			);

			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE COMMAND                                                                                        */
			/*--------------------------------------------------------------------------------------------------------*/

			data = CommandSingleton.executeCommand(
				commandAndArguments.getCommand(),
				commandAndArguments.getArguments()
			);

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			if(ConfigSingleton.getProperty("dev_mode", false))
			{
				data = XMLTemplates.error(
					e.getMessage(), e.getStackTrace()
				);
			}
			else
			{
				data = XMLTemplates.error(
					e.getMessage()
				);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CONVERT RESULT                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		String mime;

		if(!converter.isEmpty())
		{
			StringReader stringReader = new StringReader(data);
			StringWriter stringWriter = new StringWriter(/**/);

			try
			{
				mime = ConverterSingleton.convert(converter, stringReader, stringWriter);

				data = stringWriter.toString();
			}
			catch(Exception e)
			{
				data = XMLTemplates.error(
					e.getMessage()
				);

				mime = "text/xml";
			}
		}
		else
		{
			mime = "text/xml";
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* WRITE RESULT                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		try(PrintWriter writer = res.getWriter())
		{
			res.setStatus(200);

			res.setContentType(mime);

			writer.print(data);
		}
		catch(Exception e)
		{
			res.setStatus(500);

			res.setContentType("text/plain");

			LOG.error(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private record CertInfo(
		@NotNull String clientDN,
		@NotNull String issuerDN,
		@NotNull String notBefore,
		@NotNull String notAfter
	) {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static CertInfo getCertInfo(@NotNull Map<String, String> arguments, @NotNull HttpServletRequest req)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(arguments.containsKey("clientDN")
		   &&
		   arguments.containsKey("issuerDN")
		   &&
		   arguments.containsKey("notBefore")
		   &&
		   arguments.containsKey("notAfter")
		 ) {
			try
			{
				return new CertInfo(
					SecuritySingleton.decrypt(arguments.getOrDefault("clientDN", "")),
					SecuritySingleton.decrypt(arguments.getOrDefault("issuerDN", "")),
					SecuritySingleton.decrypt(arguments.getOrDefault("notBefore", "")),
					SecuritySingleton.decrypt(arguments.getOrDefault("notAfter", ""))
				);
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		X509Certificate[] certificates = (X509Certificate[]) req.getAttribute("jakarta.servlet.request.X509Certificate");

		if(certificates != null)
		{
			for(X509Certificate certificate: certificates)
			{
				if(!SecuritySingleton.isProxy(certificate))
				{
					return new CertInfo(
						SecuritySingleton.getDN(certificate.getSubjectX500Principal()),
						SecuritySingleton.getDN(certificate.getIssuerX500Principal()),
						new SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(
							certificate.getNotBefore()
						),
						new SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(
							certificate.getNotAfter()
						)
					);
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new CertInfo(
			"",
			"",
			"",
			""
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String resolveUserByCertificate(@Nullable String clientDN, @Nullable String issuerDN, String clientIP) throws Exception
	{
		if(Empty.is(clientDN, Empty.STRING_JAVA_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(issuerDN, Empty.STRING_JAVA_NULL | Empty.STRING_BLANK)
		 ) {
			return GUEST_USER;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE QUERIER                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		RouterQuerier querier = new RouterQuerier();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			List<Row> rowList = querier.executeSQLQuery("router_user", "SELECT `AMIUser`, `country` FROM `router_user` WHERE `clientDN` = ?#0 AND `issuerDN` = ?#1", clientDN, issuerDN).getAll();

			/*--------------------------------------------------------------------------------------------------------*/
			/* GET CREDENTIALS                                                                                        */
			/*--------------------------------------------------------------------------------------------------------*/

			if(rowList.size() != 1)
			{
				return GUEST_USER;
			}

			Row row = rowList.get(0);

			String result = row.getValue(0);
			String oldCountryCode = row.getValue(1);

			/*--------------------------------------------------------------------------------------------------------*/
			/* UPDATE COUNTRY                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			updateCountry(querier, result, oldCountryCode, clientIP);

			/*--------------------------------------------------------------------------------------------------------*/

			return result;
		}
		finally
		{
			querier.commitAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String resolveUserByUserPass(@Nullable String AMIUser, @Nullable String AMIPass, @NotNull String clientIP, @NotNull String clientOrigin) throws Exception
	{
		if(Empty.is(AMIUser, Empty.STRING_JAVA_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(AMIPass, Empty.STRING_JAVA_NULL | Empty.STRING_BLANK)
		 ) {
			return GUEST_USER;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CONNECTION WITH OIDC                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		UserInfoAndUsername userInfoAndUsername;

		try
		{
			/**/ if("__oidc_code__".equals(AMIUser))
			{
				/*----------------------------------------------------------------------------------------------------*/

				String redirectURL;

				try
				{
					URL url = new URL(clientOrigin);

					redirectURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), "/docs/sso.html").toString();
				}
				catch(MalformedURLException e)
				{
					return GUEST_USER;
				}

				/*----------------------------------------------------------------------------------------------------*/

				SecuritySingleton.MapAndJSON mapAndJSON = SecuritySingleton.validateOIDCCodeAndParseTokens(redirectURL, AMIPass);

				if(mapAndJSON.map().containsKey("access_token"))
				{
					AMIPass = (String) mapAndJSON.map().get("access_token");
				}
				else
				{
					return GUEST_USER;
				}

				/*----------------------------------------------------------------------------------------------------*/

				userInfoAndUsername = getUserInfoFromToken(AMIPass);

				AMIUser = userInfoAndUsername.username();

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("__oidc_token__".equals(AMIUser))
			{
				/*----------------------------------------------------------------------------------------------------*/

				userInfoAndUsername = getUserInfoFromToken(AMIPass);

				AMIUser = userInfoAndUsername.username();

				/*----------------------------------------------------------------------------------------------------*/
			}
			else
			{
				userInfoAndUsername = null;
			}
		}
		catch(Exception e)
		{
			return GUEST_USER;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE QUERIER                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		RouterQuerier querier = new RouterQuerier();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			String sql = userInfoAndUsername != null ? "SELECT `AMIUser`, `AMIPass`, `country` FROM `router_user` WHERE `ssoUser` = ?0"
			                                         : "SELECT `AMIUser`, `AMIPass`, `country` FROM `router_user` WHERE `AMIUser` = ?0"
			;

			List<Row> rowList = querier.executeSQLQuery("router_user", sql, AMIUser).getAll();

			/*--------------------------------------------------------------------------------------------------------*/
			/* GET CREDENTIALS                                                                                        */
			/*--------------------------------------------------------------------------------------------------------*/

			if(rowList.size() == 0)
			{
				return userInfoAndUsername != null ? createNewUser(querier, userInfoAndUsername, clientIP) : GUEST_USER;
			}

			Row row = rowList.get(0);

			String result = row.getValue(0);
			String hashed = row.getValue(1);
			String oldCountryCode = row.getValue(2);

			if(userInfoAndUsername == null)
			{
				try
				{
					SecuritySingleton.checkPassword(result, AMIPass, hashed);
				}
				catch(Exception e)
				{
					return GUEST_USER;
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
			/* UPDATE COUNTRY                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			updateCountry(querier, result, oldCountryCode, clientIP);

			/*--------------------------------------------------------------------------------------------------------*/

			return result;
		}
		finally
		{
			querier.commitAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public record UserInfoAndUsername(
		@NotNull Map<String, Object> userInfoMap,
		@NotNull String userInfoJson,
		@NotNull String username
	) {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private UserInfoAndUsername getUserInfoFromToken(@NotNull String token) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		SecuritySingleton.MapAndJSON mapAndJSON = SecuritySingleton.validateOIDCTokenAndParseUserInfo(token);

		/*------------------------------------------------------------------------------------------------------------*/

		String usernameKey = ConfigSingleton.getProperty("sso_userinfo_username_key", "@NULL");

		/*------------------------------------------------------------------------------------------------------------*/

		if(mapAndJSON.map().containsKey(usernameKey))
		{
			String username = (String) mapAndJSON.map().get(usernameKey);

			return new UserInfoAndUsername(mapAndJSON.map(), mapAndJSON.json(), username);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		throw new Exception("OpenID Connect not properly configured");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String createNewUser(@NotNull RouterQuerier querier, @NotNull UserInfoAndUsername userInfoAndUsername, @NotNull String clientIP) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String username = userInfoAndUsername.username();

		String password = SecuritySingleton.generatePassword();

		String firstnameKey = ConfigSingleton.getProperty("sso_userinfo_firstname_key", "@NULL");
		String firstname = (String) userInfoAndUsername.userInfoMap().getOrDefault(firstnameKey, "Unknown");

		String lastnameKey = ConfigSingleton.getProperty("sso_userinfo_lastname_key", "@NULL");
		String lastname = (String) userInfoAndUsername.userInfoMap().getOrDefault(lastnameKey, "Unknown");

		String emailKey = ConfigSingleton.getProperty("sso_userinfo_email_key", "@NULL");
		String email = (String) userInfoAndUsername.userInfoMap().getOrDefault(emailKey, "x@y.z");

		/*------------------------------------------------------------------------------------------------------------*/

		UserValidator.Bean bean = new UserValidator.Bean(
			username, username,
			password, password,
			null, null,
			firstname, lastname, email,
			userInfoAndUsername.userInfoJson()
		);

		RoleSingleton.checkUser(
			ConfigSingleton.getProperty("new_user_validator_class"),
			UserValidator.Mode.ADD,
			bean
		);

		/*------------------------------------------------------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("router_user", "INSERT INTO `router_user` (`AMIUser`, `ssoUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `json`, `valid`) VALUES (?0, ?1, ?^2, ?#3, ?#4, ?5, ?6, ?7, ?8, ?9)",
			username,
			username,
			password,
			SecuritySingleton.encrypt(bean.getClientDN()),
			SecuritySingleton.encrypt(bean.getIssuerDN()),
			!Empty.is(bean.getFirstName(), Empty.STRING_NULL_EMPTY_BLANK) ? bean.getFirstName() : "Unknown",
			!Empty.is(bean.getLastName(), Empty.STRING_NULL_EMPTY_BLANK) ? bean.getLastName() : "Unknown",
			!Empty.is(bean.getEmail(), Empty.STRING_NULL_EMPTY_BLANK) ? bean.getEmail() : "x@y.z",
			bean.getJson(),
			1
		);

		if(update.getNbOfUpdatedRows() == 1)
		{
			Update update2 = querier.executeSQLUpdate("router_user_role", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?0), (SELECT `id` FROM `router_role` WHERE `role` = ?1))",
				username,
				"AMI_USER"
			);

			if(update2.getNbOfUpdatedRows() == 1)
			{
				updateCountry(querier, bean.getAmiUsername(), "N/A", clientIP);

				querier.commit();

				return username;
			}
			else
			{
				querier.rollback();

				return GUEST_USER;
			}
		}
		else
		{
			querier.rollback();

			return GUEST_USER;
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void updateCountry(@NotNull RouterQuerier querier, @NotNull String AMIUser, @NotNull String oldCountryCode, @NotNull String clientIP)
	{
		try
		{
			String newCountryCode = LocalizationSingleton.localizeIP(querier, clientIP).countryCode();

			if(!oldCountryCode.equals(newCountryCode))
			{
				querier.executeSQLUpdate("UPDATE `router_user` SET `country` = ?0 WHERE `AMIUser` = ?1", newCountryCode, AMIUser);
			}
		}
		catch(Exception e)
		{
			LOG.debug(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void updateSessionAndCommandArguments(@NotNull Map<String, String> arguments, @NotNull HttpServletRequest request) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET CLIENT_DN, ISSUER_DN, AMI_USER, AMI_PASS                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		CertInfo certInfo = getCertInfo(arguments, request);

		String clientDN = certInfo.clientDN();
		String issuerDN = certInfo.issuerDN();
		String notBefore = certInfo.notBefore();
		String notAfter = certInfo.notAfter();

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET ARGUMENT AND SESSION PARAMETERS                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		String AMIUser = arguments.get("AMIUser");
		String AMIPass = arguments.get("AMIPass");

		/*------------------------------------------------------------------------------------------------------------*/

		HttpSession session = request.getSession(true);

		String sessionAMIUser = (String) session.getAttribute("AMIUser");

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET NO CERT FLAG                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		boolean noCert;

		if(request.getParameter("NoCert") == null)
		{
			Boolean attr = (Boolean) session.getAttribute("NoCert");

			noCert = (attr != null) && attr;
		}
		else
		{
			noCert = /*----*/ true /*----*/;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET CLIENT IP                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		String clientIP = request.getRemoteAddr();

		String clientOrigin = request.getHeader("Origin");

		/*------------------------------------------------------------------------------------------------------------*/
		/* UPDATE SESSION                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		if(!clientDN.isEmpty() && !issuerDN.isEmpty() && !noCert)
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CERTIFICATE LOGIN                                                                                      */
			/*--------------------------------------------------------------------------------------------------------*/

			if(sessionAMIUser == null || GUEST_USER.equals(sessionAMIUser))
			{
				AMIUser = resolveUserByCertificate(clientDN, issuerDN, clientIP);
			}
			else
			{
				AMIUser = sessionAMIUser;
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		else
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* CREDENTIAL LOGIN                                                                                       */
			/*--------------------------------------------------------------------------------------------------------*/

			if(sessionAMIUser == null || (AMIUser != null && AMIPass != null && !sessionAMIUser.equals(AMIUser)))
			{
				AMIUser = resolveUserByUserPass(AMIUser, AMIPass, clientIP, clientOrigin);
			}
			else
			{
				AMIUser = sessionAMIUser;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			noCert = !GUEST_USER.equals(AMIUser);

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* UPDATE SESSION                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		session.setAttribute("AMIUser", AMIUser);
		session.setAttribute("NoCert", noCert);

		/*------------------------------------------------------------------------------------------------------------*/
		/* UPDATE ARGUMENTS                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		arguments.put("AMIUser", AMIUser);
		arguments.put("clientDN", clientDN);
		arguments.put("issuerDN", issuerDN);
		arguments.put("notBefore", notBefore);
		arguments.put("notAfter", notAfter);

		/*------------------------------------------------------------------------------------------------------------*/

		arguments.put("isSecure", request.isSecure() ? "true"
		                                             : "false"
		);

		/*------------------------------------------------------------------------------------------------------------*/

		String agent = request.getHeader("User-Agent");

		if(agent != null
		   &&
		   (
		     agent.startsWith("cami")
		     ||
		     agent.startsWith("jami")
		     ||
		     agent.startsWith("pami")
		     ||
		     agent.startsWith("pyAMI")
		   )
		 ) {
			arguments.put("userAgent", agent);
		}
		else
		{
			arguments.put("userAgent", "web");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		arguments.put("userSession", session.getId());

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
