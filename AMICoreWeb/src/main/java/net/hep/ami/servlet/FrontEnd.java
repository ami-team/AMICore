package net.hep.ami.servlet;

import java.io.*;
import java.text.*;
import java.util.*;
import java.security.cert.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

@WebServlet(
	name = "FrontEnd",
	urlPatterns = "/FrontEnd"
)

public class FrontEnd extends HttpServlet
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 6325706434625863655L;

	/*---------------------------------------------------------------------*/

	private static final String GUEST_USER = ConfigSingleton.getProperty("guest_user");
	private static final String GUEST_PASS = ConfigSingleton.getProperty("guest_pass");

	/*---------------------------------------------------------------------*/

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	private void doCommand(HttpServletRequest req, HttpServletResponse res)
	{
		/*-----------------------------------------------------------------*/
		/* SET UTF-8 AS DEFAULT ENCODING                                   */
		/*-----------------------------------------------------------------*/

		try
		{
			req.setCharacterEncoding("UTF-8");
			res.setCharacterEncoding("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/
		/* CROSS-ORIGIN RESOURCE SHARING                                   */
		/*-----------------------------------------------------------------*/

		String origin = req.getHeader("Origin");

		if(origin != null)
		{
			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Origin", origin);
		}

		/*-----------------------------------------------------------------*/
		/* VARIABLES FOR LINKS                                             */
		/*-----------------------------------------------------------------*/

		String link = req.getParameter("Link");
		if(link == null) {
			/* berk */ link = req.getParameter("LinkId");
			if(link == null) {
				/* berk */ link = req.getParameter("linkId");
			}
		}

		link = (link != null) ? link.trim() : "";

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES                                              */
		/*-----------------------------------------------------------------*/

		String command = req.getParameter("Command");
		command = (command != null) ? command.trim() : "";

		String converter = req.getParameter("Converter");
		converter = (converter != null) ? converter.trim() : "";

		String textOutput = req.getParameter("TextOutput");
		textOutput = (textOutput != null) ? textOutput.trim() : "";

		/*-----------------------------------------------------------------*/
		/* SET CONTENT DISPOSITION                                         */
		/*-----------------------------------------------------------------*/

		if(textOutput.isEmpty())
		{
			res.setHeader("Content-disposition", "inline; filename=" + ((("AMI"))) );
		}
		else
		{
			res.setHeader("Content-disposition", "attachment; filename=" + textOutput);
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		String data;

		try
		{
			/*---------------------------------------------------------*/
			/* CHECK AMI CONFIG FILE                                   */
			/*---------------------------------------------------------*/

			ConfigSingleton.checkValidAMIConfigFile();

			/*---------------------------------------------------------*/
			/* RESOLVE LINK                                            */
			/*---------------------------------------------------------*/

			if(link.isEmpty() == false)
			{
				if(link.matches("[0-9]+") == false)
				{
					throw new Exception("invalid link format");
				}

				Tuple2<String, String> result = resolveLink(link);

				command = result.x;
				converter = result.y;
			}

			/*---------------------------------------------------------*/
			/* PARSE COMMAND                                           */
			/*---------------------------------------------------------*/

			Command.CommandTuple tuple = Command.parse(command);

			HttpSession session = req.getSession(true);

			updateSessionAndCommandArgs(
				tuple.arguments,
				session,
				req
			);

			/*---------------------------------------------------------*/
			/* EXECUTE COMMAND                                         */
			/*---------------------------------------------------------*/

			data = CommandSingleton.executeCommand(
				tuple.command,
				tuple.arguments
			);

			/*---------------------------------------------------------*/
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

		/*-----------------------------------------------------------------*/
		/* CONVERT RESULT                                                  */
		/*-----------------------------------------------------------------*/

		String mime;

		if(converter.isEmpty() == false)
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

		/*-----------------------------------------------------------------*/
		/* WRITE RESULT                                                    */
		/*-----------------------------------------------------------------*/

		try(PrintWriter writer = res.getWriter())
		{
			writer.print(data);

			res.setContentType(mime);

			res.setStatus(200);
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);

			res.setContentType("text/plain");

			res.setStatus(500);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static Tuple4<String, String, String, String> getDNs(HttpServletRequest req)
	{
		X509Certificate[] certificates = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

		if(certificates != null)
		{
			for(X509Certificate certificate: certificates)
			{
				if(SecuritySingleton.isProxy(certificate) == false)
				{
					return new Tuple4<>(
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

		return new Tuple4<>(
			"",
			"",
			"",
			""
		);
	}

	/*---------------------------------------------------------------------*/

	private Tuple2<String, String> resolveLink(String linkId) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		Router router = new Router();

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = router.executeSQLQuery("router_link", "SELECT `command`, `converter` FROM `router_link` WHERE `id` = ?", linkId).getAll();

			/*-------------------------------------------------------------*/
			/* GET LINK                                                    */
			/*-------------------------------------------------------------*/

			if(rowList.size() != 1)
			{
				throw new Exception("could not resolve link `" + linkId + "`");
			}

			Row row = rowList.get(0);

			/*-------------------------------------------------------------*/

			return new Tuple2<>(
				row.getValue(0),
				row.getValue(1)
			);

			/*-------------------------------------------------------------*/
		}
		finally
		{
			router.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private Tuple2<String, String> resolveUserByCertificate(@Nullable String clientDN, @Nullable String issuerDN, String clientIP) throws Exception
	{
		if(clientDN == null || clientDN.isEmpty()
		   ||
		   issuerDN == null || issuerDN.isEmpty()
		 ) {
			return new Tuple2<>(
				GUEST_USER,
				GUEST_PASS
			);
		}

		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		Router router = new Router();

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = router.executeSQLQuery("router_user", "SELECT `AMIUser`, `AMIPass`, `country` FROM `router_user` WHERE `clientDN` = ? AND `issuerDN` = ?", SecuritySingleton.encrypt(clientDN), SecuritySingleton.encrypt(issuerDN)).getAll();

			/*-------------------------------------------------------------*/
			/* GET CREDENTIALS                                             */
			/*-------------------------------------------------------------*/

			if(rowList.isEmpty())
			{
				return new Tuple2<>(
					GUEST_USER,
					GUEST_PASS
				);
			}

			Row row = rowList.get(0);

			/*-------------------------------------------------------------*/

			Tuple2<String, String> result = new Tuple2<>(
				/*---------------------*/(row.getValue(0)),
				SecuritySingleton.decrypt(row.getValue(1))
			);

			/*-------------------------------------------------------------*/
			/* UPDATE COUNTRY                                              */
			/*-------------------------------------------------------------*/

			try
			{
				String countryCode = LocalizationSingleton.localizeIP(router, clientIP).countryCode;

				if(countryCode.equals(row.getValue(2)) == false)
				{
					router.executeSQLUpdate("UPDATE `router_user` SET `router_user` = ? WHERE `AMIUser` = ?", countryCode, result.x);
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}

			/*-------------------------------------------------------------*/

			return result;
		}
		finally
		{
			router.commitAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private Tuple2<String, String> resolveUserByUserPass(@Nullable String AMIUser, @Nullable String AMIPass, String clientIP) throws Exception
	{
		if(AMIUser == null || AMIUser.isEmpty()
		   ||
		   AMIPass == null || AMIPass.isEmpty()
		 ) {
			return new Tuple2<>(
				GUEST_USER,
				GUEST_PASS
			);
		}

		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		Router router = new Router();

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = router.executeSQLQuery("router_user", "SELECT `AMIPass`, `country` FROM `router_user` WHERE `AMIUser` = ?", AMIUser).getAll();

			/*-------------------------------------------------------------*/
			/* GET CREDENTIALS                                             */
			/*-------------------------------------------------------------*/

			if(rowList.isEmpty())
			{
				return new Tuple2<>(
					GUEST_USER,
					GUEST_PASS
				);
			}

			Row row = rowList.get(0);

			/*-------------------------------------------------------------*/

			Tuple2<String, String> result;

			try
			{
				result = SecuritySingleton.checkPassword(AMIUser, AMIPass, SecuritySingleton.decrypt(row.getValue(0)));
			}
			catch(Exception e)
			{
				result = new Tuple2<>(
					GUEST_USER,
					GUEST_PASS
				);
			}

			/*-------------------------------------------------------------*/
			/* UPDATE COUNTRY                                              */
			/*-------------------------------------------------------------*/

			try
			{
				String countryCode = LocalizationSingleton.localizeIP(router, clientIP).countryCode;

				if(countryCode.equals(row.getValue(1)) == false)
				{
					router.executeSQLUpdate("UPDATE `router_user` SET `router_user` = ? WHERE `AMIUser` = ?", countryCode, result.x);
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}

			/*-------------------------------------------------------------*/

			return result;
		}
		finally
		{
			router.commitAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void updateSessionAndCommandArgs(Map<String, String> arguments, HttpSession session, HttpServletRequest request) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLIENT_DN, ISSUER_DN, AMI_USER, AMI_PASS                    */
		/*-----------------------------------------------------------------*/

		Tuple4<String, String, String, String> dns = getDNs(request);

		String clientDN = dns.x;
		String issuerDN = dns.y;
		String notBefore = dns.z;
		String notAfter = dns.t;

		/*-----------------------------------------------------------------*/

		String AMIUser = arguments.get("AMIUser");
		String AMIPass = arguments.get("AMIPass");

		/*-----------------------------------------------------------------*/
		/* GET NOCERT FLAG                                                 */
		/*-----------------------------------------------------------------*/

		boolean noCert;

		if(request.getParameter("NoCert") == null)
		{
			Boolean attr = (Boolean) session.getAttribute("NoCert");

			noCert = attr != null
			         &&
			         attr != false
			;
		}
		else
		{
			noCert = true;
		}

		/*-----------------------------------------------------------------*/
		/* GET CLIENT IP                                                   */
		/*-----------------------------------------------------------------*/

		String clientIP = request.getRemoteAddr();

		/*-----------------------------------------------------------------*/
		/* UPDATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		if(clientDN.isEmpty() == false && issuerDN.isEmpty() == false && noCert == false)
		{
			/*-------------------------------------------------------------*/
			/* CERTIFICATE LOGIN                                           */
			/*-------------------------------------------------------------*/

			String tmpAMIUser = (String) session.getAttribute("AMIUser");
			String tmpAMIPass = (String) session.getAttribute("AMIPass");

			if(tmpAMIUser == null
			   ||
			   GUEST_USER.equals(tmpAMIUser)
			   ||
			   tmpAMIPass == null
			 ) {
				Tuple2<String, String> result = resolveUserByCertificate(clientDN, issuerDN, clientIP);

				AMIUser = result.x;
				AMIPass = result.y;
			}
			else
			{
				AMIUser = tmpAMIUser;
				AMIPass = tmpAMIPass;
			}

			/*-------------------------------------------------------------*/

			noCert = /*-----------*/ false /*-----------*/;

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/
			/* CREDENTIAL LOGIN                                            */
			/*-------------------------------------------------------------*/

			String tmpAMIUser = (String) session.getAttribute("AMIUser");
			String tmpAMIPass = (String) session.getAttribute("AMIPass");

			if(tmpAMIUser == null || (tmpAMIUser != null && AMIUser != null && tmpAMIUser.equals(AMIUser) == false)
			   ||
			   tmpAMIPass == null || (tmpAMIPass != null && AMIPass != null && tmpAMIPass.equals(AMIPass) == false)
			 ) {
				Tuple2<String, String> result = resolveUserByUserPass(AMIUser, AMIPass, clientIP);

				AMIUser = result.x;
				AMIPass = result.y;
			}
			else
			{
				AMIUser = tmpAMIUser;
				AMIPass = tmpAMIPass;
			}

			/*-------------------------------------------------------------*/

			noCert = GUEST_USER.equals(AMIUser) == false;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/* UPDATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		session.setAttribute("AMIUser", AMIUser);
		session.setAttribute("AMIPass", AMIPass);

		session.setAttribute("NoCert", noCert);

		/*-----------------------------------------------------------------*/
		/* UPDATE ARGUMENTS                                                */
		/*-----------------------------------------------------------------*/

		arguments.put("AMIUser", AMIUser);
		arguments.put("AMIPass", AMIPass);

		arguments.put("clientDN", clientDN);
		arguments.put("issuerDN", issuerDN);
		arguments.put("notBefore", notBefore);
		arguments.put("notAfter", notAfter);

		/*-----------------------------------------------------------------*/

		arguments.put("isSecure", request.isSecure() ? "true"
		                                             : "false"
		);

		/*-----------------------------------------------------------------*/

		String agent = request.getHeader("User-Agent");

		if(agent != null)
		{
			if(agent.startsWith("cami")
			   ||
			   agent.startsWith("jami")
			   ||
			   agent.startsWith("pami")
			   ||
			   agent.startsWith("pyAMI")
			 ) {
				arguments.put("userAgent", agent);
			}
		}
		else
		{
			arguments.put("userAgent", "web");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
