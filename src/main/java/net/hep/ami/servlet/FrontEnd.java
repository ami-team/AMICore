package net.hep.ami.servlet;

import java.io.*;
import java.util.*;
import java.security.cert.*;

import javax.servlet.http.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

public class FrontEnd extends HttpServlet {
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 6325706434625863655L;

	/*---------------------------------------------------------------------*/

	private static final String m_guest_user = ConfigSingleton.getProperty("guest_user");
	private static final String m_guest_pass = ConfigSingleton.getProperty("guest_pass");

	/*---------------------------------------------------------------------*/

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	private void doCommand(HttpServletRequest req, HttpServletResponse res) throws IOException {
		/*-----------------------------------------------------------------*/
		/* SET DEFAULT ENCODING                                            */
		/*-----------------------------------------------------------------*/

		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");

		/*-----------------------------------------------------------------*/
		/* CROSS-ORIGIN RESOURCE SHARING                                   */
		/*-----------------------------------------------------------------*/

		String origin = req.getHeader("Origin");

		if(origin != null) {
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

		if(command.equals("Ping")) {

			res.setContentType("text/xml");

			writer.print(Templates.info("AMI is alive."));
			writer.close();

			return;
		}

		/*-----------------------------------------------------------------*/
		/* SET CONTENT DISPOSITION                                         */
		/*-----------------------------------------------------------------*/

		if(textOutput.isEmpty()) {
			res.setHeader("Content-disposition", "inline; filename=" + ((("AMI"))) );
		}
		else {
			res.setHeader("Content-disposition", "attachment; filename=" + textOutput);
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		String data;

		if(ConfigSingleton.hasValidConfFile() == false) {

			try {
				/*---------------------------------------------------------*/
				/* RESOLVE LINK                                            */
				/*---------------------------------------------------------*/

				if(link.isEmpty() == false) {

					String[] result = resolveLink(link);

					command = result[0];
					converter = result[1];
				}

				/*---------------------------------------------------------*/
				/* EXECUTE COMMAND                                         */
				/*---------------------------------------------------------*/

				CommandParser.Tuple result = CommandParser.parse(command);

				updateSessionAndCommandArgs(
					req,
					session,
					result.y
				);

				data = CommandSingleton.executeCommand(result.x, result.y);

				/*---------------------------------------------------------*/
			} catch(Exception e) {
				data = Templates.error(
					e.getMessage()
				);
			}
		} else {
			data = Templates.error(
				"config error"
			);
		}

		/*-----------------------------------------------------------------*/
		/* CONVERT COMMAND RESULT                                          */
		/*-----------------------------------------------------------------*/

		String mime;

		if(converter.isEmpty() == false) {

			StringReader stringReader = new StringReader(data);
			StringWriter stringWriter = new StringWriter(/**/);

			try {
				mime = ConverterSingleton.applyConverter(converter, stringReader, stringWriter);

				data = stringWriter.toString();

			} catch(Exception e) {
				data = Templates.error(
					e.getMessage()
				);

				mime = "text/xml";
			}
		} else {
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

	private static String getClientDN(HttpServletRequest req) {

		X509Certificate[] certificates = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

		if(certificates != null && certificates.length > 0) {

			return Cryptography.getAMIShortDN(certificates[0].getSubjectX500Principal());
		}

		return "";
	}

	/*---------------------------------------------------------------------*/

	private static String getIssuerDN(HttpServletRequest req) {

		X509Certificate[] certificates = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

		if(certificates != null && certificates.length > 0) {

			return Cryptography.getAMIShortDN(certificates[0].getIssuerX500Principal());
		}

		return "";
	}

	/*---------------------------------------------------------------------*/

	private String[] resolveLink(String linkId) throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		BasicLoader basicLoader = null;
		QueryResult queryResult = null;

		try {
			basicLoader = new BasicLoader(
				ConfigSingleton.getProperty("jdbc_url"),
				ConfigSingleton.getProperty("router_user"),
				ConfigSingleton.getProperty("router_pass")
			);

			basicLoader.useDB(ConfigSingleton.getProperty("router_name"));

			queryResult = basicLoader.executeQuery("SELECT command, converter FROM router_link WHERE id = '" + linkId + "'");

		} finally {

			if(basicLoader != null) {
				basicLoader.rollbackAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
		/* CHECK AND RETURN RESULT                                         */
		/*-----------------------------------------------------------------*/

		if(queryResult.getNumberOfRows() == 1) {

			return new String[] {
				queryResult.getFieldValueForRow(0,  "command" ),
				queryResult.getFieldValueForRow(0, "converter"),
			};
		}

		/*-----------------------------------------------------------------*/
		/* RAISE ERROR                                                     */
		/*-----------------------------------------------------------------*/

		throw new Exception("could not resolve link `" + linkId + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String[] resolveCertificate(String clientDN) throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		BasicLoader basicLoader = null;
		QueryResult queryResult = null;

		try {
			basicLoader = new BasicLoader(
				ConfigSingleton.getProperty("jdbc_url"),
				ConfigSingleton.getProperty("router_user"),
				ConfigSingleton.getProperty("router_pass")
			);

			basicLoader.useDB(ConfigSingleton.getProperty("router_name"));

			queryResult = basicLoader.executeQuery("SELECT AMIUser, AMIPass FROM router_user WHERE clientDN = '" + clientDN.replaceAll("'", "''") + "'");

		} finally {

			if(basicLoader != null) {
				basicLoader.rollbackAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
		/* CHECK AND RETURN RESULT                                         */
		/*-----------------------------------------------------------------*/

		if(queryResult.getNumberOfRows() == 1) {

			return new String[] {
				/******************/(queryResult.getFieldValueForRow(0, "AMIUser")),
				Cryptography.decrypt(queryResult.getFieldValueForRow(0, "AMIPass")),
			};
		}

		/*-----------------------------------------------------------------*/
		/* RAISE ERROR                                                     */
		/*-----------------------------------------------------------------*/

		return new String[] {
			m_guest_user,
			m_guest_pass,
		};

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String _safe(String s) { return s == null ? "" : s; }

	/*---------------------------------------------------------------------*/

	private void updateSessionAndCommandArgs(HttpServletRequest request, HttpSession session, HashMap<String, String> arguments) throws Exception {

		String AMIUser;
		String AMIPass;

		/*-----------------------------------------------------------------*/
		/* GET CLIENT DN                                                   */
		/*-----------------------------------------------------------------*/

		String clientDN = getClientDN(request);
		session.setAttribute("clientDN", clientDN);

		/*-----------------------------------------------------------------*/
		/* GET ISSUER DN                                                   */
		/*-----------------------------------------------------------------*/

		String issuerDN = getIssuerDN(request);
		session.setAttribute("issuerDN", issuerDN);

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		boolean noCert;

		noCert = request.getParameter("NoCert") != null;

		if(noCert == false) {
			noCert = session.getAttribute("NoCert") != null;

		} else {
			session.setAttribute("NoCert", "true");
		}

		/*-----------------------------------------------------------------*/

		if(clientDN.isEmpty() == false && issuerDN.isEmpty() == false && noCert) {
			/*-------------------------------------------------------------*/
			/* CERTIFICATE LOGIN                                           */
			/*-------------------------------------------------------------*/

			AMIUser = _safe((String) session.getAttribute("AMIUser_certificate"));
			AMIPass = _safe((String) session.getAttribute("AMIPass_certificate"));

			if(AMIUser.isEmpty()
			   ||
			   AMIPass.isEmpty()
			 ) {
				String[] result = resolveCertificate(clientDN);

				AMIUser = result[0];
				AMIPass = result[1];
			}

			if(!AMIUser.equals(m_guest_user)) {
				session.setAttribute("AMIUser_certificate", AMIUser);
				session.setAttribute("AMIPass_certificate", AMIPass);

				session.removeAttribute("AMIUser_credential");
				session.removeAttribute("AMIPass_credential");
			} else {
				session.setAttribute("AMIUser_credential", AMIUser);
				session.setAttribute("AMIPass_credential", AMIPass);

				session.removeAttribute("AMIUser_certificate");
				session.removeAttribute("AMIPass_certificate");
			}

			/*-------------------------------------------------------------*/
		} else {
			/*-------------------------------------------------------------*/
			/* CREDENTIAL LOGIN                                            */
			/*-------------------------------------------------------------*/

			if(arguments.containsKey("AMIUser")
			   &&
			   arguments.containsKey("AMIPass")
			 ) {
				AMIUser = arguments.get("AMIUser");
				AMIPass = arguments.get("AMIPass");
			} else {
				AMIUser = _safe((String) session.getAttribute("AMIUser_credential"));
				AMIPass = _safe((String) session.getAttribute("AMIPass_credential"));
			}

			if(AMIUser.isEmpty()
			   ||
			   AMIPass.isEmpty()
			 ) {
				AMIUser = m_guest_user;
				AMIPass = m_guest_pass;
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
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		arguments.put("AMIUser", AMIUser);
		arguments.put("AMIPass", AMIPass);

		arguments.put("clientDN", clientDN);
		arguments.put("issuerDN", issuerDN);

		arguments.put("isSecure", request.isSecure() ? "1" : "0");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
