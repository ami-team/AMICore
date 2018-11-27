package net.hep.ami.servlet;

import java.io.*;
import java.text.*;
import java.util.*;
import java.security.cert.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
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

	private static final String s_guest_user = ConfigSingleton.getProperty("guest_user");
	private static final String s_guest_pass = ConfigSingleton.getProperty("guest_pass");

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

		if(ConfigSingleton.isValidConfFile())
		{
			try
			{
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
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
e.printStackTrace(pw);
				data = XMLTemplates.error(
						sw.toString()//e.getMessage()
				);
			}
		}
		else
		{
			data = XMLTemplates.error(
				"config error"
			);
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
			res.setStatus(HttpServletResponse.SC_OK);

			res.setContentType(mime);

			writer.print(data);
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);

			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

		AbstractDriver basicQuerier = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("router_catalog"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = basicQuerier.executeSQLQuery("SELECT `command`,`converter` FROM `router_link` WHERE `id` = ?", linkId).getAll();

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
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private Tuple2<String, String> resolveUserByCertificate(@Nullable String clientDN, @Nullable String issuerDN) throws Exception
	{
		if(clientDN == null || clientDN.isEmpty()
		   ||
		   issuerDN == null || issuerDN.isEmpty()
		 ) {
			return new Tuple2<>(
				s_guest_user,
				s_guest_pass
			);
		}

		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver basicQuerier = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("router_catalog"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = basicQuerier.executeSQLQuery("SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `clientDN` = ? AND `issuerDN` = ?", SecuritySingleton.encrypt(clientDN), SecuritySingleton.encrypt(issuerDN)).getAll();

			/*-------------------------------------------------------------*/
			/* GET CREDENTIALS                                             */
			/*-------------------------------------------------------------*/

			if(rowList.isEmpty())
			{
				return new Tuple2<>(
					s_guest_user,
					s_guest_pass
				);
			}

			Row row = rowList.get(0);

			/*-------------------------------------------------------------*/

			return new Tuple2<>(
				/*---------------------*/(row.getValue(0)),
				SecuritySingleton.decrypt(row.getValue(1))
			);

			/*-------------------------------------------------------------*/
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private Tuple2<String, String> resolveUserByUserPass(@Nullable String AMIUser, @Nullable String AMIPass) throws Exception
	{
		if(AMIUser == null || AMIUser.isEmpty()
		   ||
		   AMIPass == null || AMIPass.isEmpty()
		 ) {
			return new Tuple2<>(
				s_guest_user,
				s_guest_pass
			);
		}

		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver basicQuerier = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("router_catalog"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = basicQuerier.executeSQLQuery("SELECT `AMIPass` FROM `router_user` WHERE `AMIUser` = ?", AMIUser).getAll();

			/*-------------------------------------------------------------*/
			/* GET CREDENTIALS                                             */
			/*-------------------------------------------------------------*/

			if(rowList.isEmpty())
			{
				return new Tuple2<>(
					s_guest_user,
					s_guest_pass
				);
			}

			Row row = rowList.get(0);

			/*-------------------------------------------------------------*/

			try
			{
				return SecuritySingleton.checkPassword(AMIUser, AMIPass, SecuritySingleton.decrypt(row.getValue(0)));
			}
			catch(Exception e)
			{
				return new Tuple2<>(
					s_guest_user,
					s_guest_pass
				);
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
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
		session.setAttribute("clientDN", clientDN);
		String issuerDN = dns.y;
		session.setAttribute("issuerDN", issuerDN);

		String notBefore = dns.z;
		String notAfter = dns.t;

		/*-----------------------------------------------------------------*/

		String AMIUser = arguments.get("AMIUser");
		String AMIPass = arguments.get("AMIPass");

		/*-----------------------------------------------------------------*/
		/* GET NOCERT FLAG                                                 */
		/*-----------------------------------------------------------------*/

		boolean noCert;

		if(request.getParameter("NoCert") != null)
		{
			session.setAttribute("NoCert", "1");

			noCert = /*------------*/ true /*------------*/;
		}
		else
		{
			noCert = session.getAttribute("NoCert") != null;
		}

		/*-----------------------------------------------------------------*/
		/* UPDATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		if(clientDN.isEmpty() == false && issuerDN.isEmpty() == false && noCert == false)
		{
			/*-------------------------------------------------------------*/
			/* CERTIFICATE LOGIN                                           */
			/*-------------------------------------------------------------*/

			String tmpAMIUser = (String) session.getAttribute("AMIUser_certificate");
			String tmpAMIPass = (String) session.getAttribute("AMIPass_certificate");

			if(tmpAMIUser == null
			   ||
			   tmpAMIPass == null
			 ) {
				Tuple2<String, String> result = resolveUserByCertificate(clientDN, issuerDN);

				AMIUser = result.x;
				AMIPass = result.y;
			}
			else
			{
				AMIUser = tmpAMIUser;
				AMIPass = tmpAMIPass;
			}

			/*-------------------------------------------------------------*/

			if(AMIUser.equals(s_guest_user) == false)
			{
				session.setAttribute("AMIUser_certificate", AMIUser);
				session.setAttribute("AMIPass_certificate", AMIPass);

				session.removeAttribute("AMIUser_credential");
				session.removeAttribute("AMIPass_credential");

				session.removeAttribute("NoCert");
			}
			else
			{
				session.setAttribute("AMIUser_credential", s_guest_user);
				session.setAttribute("AMIPass_credential", s_guest_pass);

				session.removeAttribute("AMIUser_certificate");
				session.removeAttribute("AMIPass_certificate");

				session.removeAttribute("NoCert");
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/
			/* CREDENTIAL LOGIN                                            */
			/*-------------------------------------------------------------*/

			String tmpAMIUser = (String) session.getAttribute("AMIUser_credential");
			String tmpAMIPass = (String) session.getAttribute("AMIPass_credential");

			if(tmpAMIUser == null || tmpAMIUser.equals(AMIUser) == false
			   ||
			   tmpAMIPass == null || tmpAMIPass.equals(AMIPass) == false
			 ) {
				Tuple2<String, String> result = resolveUserByUserPass(AMIUser, AMIPass);

				AMIUser = result.x;
				AMIPass = result.y;
			}
			else
			{
				AMIUser = tmpAMIUser;
				AMIPass = tmpAMIPass;
			}

			/*-------------------------------------------------------------*/

			if(AMIUser.equals(s_guest_user) == false)
			{
				session.setAttribute("AMIUser_credential", AMIUser);
				session.setAttribute("AMIPass_credential", AMIPass);

				session.removeAttribute("AMIUser_certificate");
				session.removeAttribute("AMIPass_certificate");

				session.setAttribute("NoCert", "1");
			}
			else
			{
				session.setAttribute("AMIUser_credential", s_guest_user);
				session.setAttribute("AMIPass_credential", s_guest_pass);

				session.removeAttribute("AMIUser_certificate");
				session.removeAttribute("AMIPass_certificate");

				session.removeAttribute("NoCert");
			}

			/*-------------------------------------------------------------*/
		}

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
