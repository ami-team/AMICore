package net.hep.ami.servlet;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.security.cert.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

@WebServlet(
	name = "FrontEnd",
	urlPatterns = "/FrontEnd"
)

public class FrontEnd extends HttpServlet
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 6325706434625863655L;

	/*---------------------------------------------------------------------*/

	private static Pattern m_xml10Pattern = Pattern.compile(
	      "[^"
	    + "\u0009\r\n"
	    + "\u0020-\uD7FF"
	    + "\uE000-\uFFFD"
	    + "\uD800\uDC00-\uDBFF\uDFFF"
	    + "]+"
	);

	/*---------------------------------------------------------------------*/

	private static final String m_guest_user = ConfigSingleton.getProperty("guest_user");
	private static final String m_guest_pass = ConfigSingleton.getProperty("guest_pass");

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

	private void doCommand(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		/*-----------------------------------------------------------------*/
		/* SET DEFAULT ENCODING                                            */
		/*-----------------------------------------------------------------*/

		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");

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
		/* GET WRITER                                                      */
		/*-----------------------------------------------------------------*/

		PrintWriter writer = res.getWriter();

		/*-----------------------------------------------------------------*/
		/* CREATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		HttpSession session = req.getSession(true);

		/*-----------------------------------------------------------------*/
		/* PING                                                            */
		/*-----------------------------------------------------------------*/

		if(command.equals("Ping"))
		{
			res.setContentType("text/xml");

			writer.print(XMLTemplates.info("AMI is alive."));

			writer.close();

			return;
		}

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

		if(ConfigSingleton.hasValidConfFile() != false)
		{
			try
			{
				/*---------------------------------------------------------*/
				/* RESOLVE LINK                                            */
				/*---------------------------------------------------------*/

				if(link.isEmpty() == false)
				{
					Tuple2<String, String> result = resolveLink(link);

					command = result.x;
					converter = result.y;
				}

				/*---------------------------------------------------------*/
				/* EXECUTE COMMAND                                         */
				/*---------------------------------------------------------*/

				CommandParser.CommandParserTuple result = CommandParser.parse(command);

				updateSessionAndCommandArgs(
					result.arguments,
					session,
					req
				);

				data = CommandSingleton.executeCommand(
					result.command,
					result.arguments
				);

				data = m_xml10Pattern.matcher(data).replaceAll("?");

				/*---------------------------------------------------------*/
			}
			catch(Exception e)
			{
				data = XMLTemplates.error(
					e.getMessage()
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
		/* CONVERT COMMAND RESULT                                          */
		/*-----------------------------------------------------------------*/

		String mime;

		if(converter.isEmpty() == false)
		{
			StringReader stringReader = new StringReader(data);
			StringWriter stringWriter = new StringWriter(/**/);

			try
			{
				mime = ConverterSingleton.applyConverter(converter, stringReader, stringWriter);

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

		res.setContentType(mime);

		writer.print(data);

		/*-----------------------------------------------------------------*/
		/* CLOSE WRITER                                                    */
		/*-----------------------------------------------------------------*/

		writer.close();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static Tuple2<String, String> getDNs(HttpServletRequest req)
	{
		X509Certificate[] certificates = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

		if(certificates != null)
		{
			for(X509Certificate certificate: certificates)
			{
				if(Cryptography.isProxy(certificate) == false)
				{
					return new Tuple2<String, String>(
						Cryptography.getAMIName(certificate.getSubjectX500Principal()),
						Cryptography.getAMIName(certificate.getIssuerX500Principal())
					);
				}
			}
		}

		return new Tuple2<String, String>(
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

		BasicQuerier basicQuerier = new BasicQuerier("self");

		/*-----------------------------------------------------------------*/

		Row row;

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = basicQuerier.executeSQLQuery("SELECT `command`,`converter` FROM `router_link` WHERE `id`='" + linkId.replace("'", "''") + "'").getAll();

			/*-------------------------------------------------------------*/
			/* GET LINK                                                    */
			/*-------------------------------------------------------------*/

			if(rowList.size() != 1)
			{
				throw new Exception("could not resolve link `" + linkId + "`");
			}

			row = rowList.get(0);

			/*-------------------------------------------------------------*/
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* RETURN LINK                                                     */
		/*-----------------------------------------------------------------*/

		return new Tuple2<String, String>(
			row.getValue( "command" ),
			row.getValue("converter")
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private Tuple2<String, String> resolveCertificate(String clientDN) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier("self");

		/*-----------------------------------------------------------------*/

		Row row;

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			List<Row> rowList = basicQuerier.executeSQLQuery("SELECT `AMIUser`,`AMIPass` FROM `router_user` WHERE `clientDN`='" + Cryptography.encrypt(clientDN).replace("'", "''") + "'").getAll();

			/*-------------------------------------------------------------*/
			/* GET CREDENTIALS                                             */
			/*-------------------------------------------------------------*/

			if(rowList.size() == 0)
			{
				return new Tuple2<String, String>(
					m_guest_user,
					m_guest_pass
				);
			}

			row = rowList.get(0);

			/*-------------------------------------------------------------*/
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* RETURN CREDENTIALS                                              */
		/*-----------------------------------------------------------------*/

		return new Tuple2<String, String>(
			/******************/(row.getValue("AMIUser")),
			Cryptography.decrypt(row.getValue("AMIPass"))
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void updateSessionAndCommandArgs(Map<String, String> arguments, HttpSession session, HttpServletRequest request) throws Exception {

		String AMIUser;
		String AMIPass;

		/*-----------------------------------------------------------------*/
		/* GET DNs                                                         */
		/*-----------------------------------------------------------------*/

		Tuple2<String, String> dns = getDNs(request);

		String clientDN = dns.x;
		session.setAttribute("clientDN", clientDN);

		String issuerDN = dns.y;
		session.setAttribute("issuerDN", issuerDN);

		/*-----------------------------------------------------------------*/
		/* GET NOCERT FLAG                                                 */
		/*-----------------------------------------------------------------*/

		boolean noCert;

		if(request.getParameter("NoCert") != null)
		{
			session.setAttribute("NoCert", "NoCert");

			noCert = 0x0000000000000000000000000000 != 0x01;
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

			AMIUser = (String) session.getAttribute("AMIUser_certificate");
			AMIPass = (String) session.getAttribute("AMIPass_certificate");

			if(AMIUser == null || AMIUser.isEmpty()
			   ||
			   AMIPass == null || AMIPass.isEmpty()
			 ) {
				Tuple2<String, String> result = resolveCertificate(clientDN);

				AMIUser = result.x;
				AMIPass = result.y;
			}

			if(AMIUser.equals(m_guest_user) == false)
			{
				session.setAttribute("AMIUser_certificate", AMIUser);
				session.setAttribute("AMIPass_certificate", AMIPass);

				session.removeAttribute("AMIUser_credential");
				session.removeAttribute("AMIPass_credential");
			}
			else
			{
				session.setAttribute("AMIUser_credential", AMIUser);
				session.setAttribute("AMIPass_credential", AMIPass);

				session.removeAttribute("AMIUser_certificate");
				session.removeAttribute("AMIPass_certificate");
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/
			/* CREDENTIAL LOGIN                                            */
			/*-------------------------------------------------------------*/

			AMIUser = arguments.get("AMIUser");
			AMIPass = arguments.get("AMIPass");

			if(AMIUser == null
			   ||
			   AMIPass == null
			 ) {
				AMIUser = (String) session.getAttribute("AMIUser_credential");
				AMIPass = (String) session.getAttribute("AMIPass_credential");

				if(AMIUser == null || AMIUser.isEmpty()
				   ||
				   AMIPass == null || AMIPass.isEmpty()
				 ) {
					AMIUser = m_guest_user;
					AMIPass = m_guest_pass;
				}
			}
			else
			{
				if(AMIUser.isEmpty()
				   ||
				   AMIPass.isEmpty()
				 ) {
					AMIUser = m_guest_user;
					AMIPass = m_guest_pass;
				}
			}

			session.setAttribute("AMIUser_credential", AMIUser);
			session.setAttribute("AMIPass_credential", AMIPass);

			session.removeAttribute("AMIUser_certificate");
			session.removeAttribute("AMIPass_certificate");

			/*-------------------------------------------------------------*/
		}

		if(AMIUser.equals(m_guest_user)
		   &&
		   AMIPass.equals(m_guest_pass)
		 ) {
			session.removeAttribute("NoCert");
		}

		/*-----------------------------------------------------------------*/
		/* UPDATE ARGUMENTS                                                */
		/*-----------------------------------------------------------------*/

		arguments.put("AMIUser", AMIUser);
		arguments.put("AMIPass", AMIPass);

		arguments.put("clientDN", clientDN);
		arguments.put("issuerDN", issuerDN);

		/*-----------------------------------------------------------------*/

		arguments.put("isSecure", request.isSecure() ? "true"
		                                             : "false"
		);

		/*-----------------------------------------------------------------*/

		String agent = request.getHeader("User-Agent");

		arguments.put("AMIAgent", agent.startsWith("pyAMI") ? agent
		                                                    : "web"
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
