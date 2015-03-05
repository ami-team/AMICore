package net.hep.ami.servlet;

import java.io.*;
import java.util.*;
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

public class FrontEnd extends HttpServlet {
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 6325706434625863655L;

	/*---------------------------------------------------------------------*/

	private static final String m_guest_user = ConfigSingleton.getProperty("guest_user");
	private static final String m_guest_pass = ConfigSingleton.getProperty("guest_pass");

	/*---------------------------------------------------------------------*/

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
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

		if(ConfigSingleton.hasValidConfFile() != false) {

			try {
				/*---------------------------------------------------------*/
				/* RESOLVE LINK                                            */
				/*---------------------------------------------------------*/

				if(link.isEmpty() == false) {

					Tuple2<String, String> result = resolveLink(link);

					command = result.x;
					converter = result.y;
				}

				/*---------------------------------------------------------*/
				/* EXECUTE COMMAND                                         */
				/*---------------------------------------------------------*/

				CommandParser.Tuple result = CommandParser.parse(command);

				updateSessionAndCommandArgs(
					result.y,
					session,
					req
				);

				data = CommandSingleton.executeCommand(
					result.x,
					result.y
				);

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

	private Tuple2<String, String> resolveLink(String linkId) throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		BasicLoader basicLoader = null;
		QueryResult queryResult = null;

		try {
			basicLoader = new BasicLoader("self");

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

			return new Tuple2<String, String>(
				queryResult.getValue(0,  "command" ),
				queryResult.getValue(0, "converter")
			);
		}

		/*-----------------------------------------------------------------*/
		/* RAISE ERROR                                                     */
		/*-----------------------------------------------------------------*/

		throw new Exception("could not resolve link `" + linkId + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private Tuple2<String, String> resolveCertificate(String clientDN) throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		BasicLoader basicLoader = null;
		QueryResult queryResult = null;

		try {
			basicLoader = new BasicLoader("self");

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

			return new Tuple2<String, String>(
				/******************/(queryResult.getValue(0, "AMIUser")),
				Cryptography.decrypt(queryResult.getValue(0, "AMIPass"))
			);
		}

		/*-----------------------------------------------------------------*/
		/* RAISE ERROR                                                     */
		/*-----------------------------------------------------------------*/

		return new Tuple2<String, String>(
			m_guest_user,
			m_guest_pass
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String _safe(Object s) { return s != null ? (String) s : ""; }

	/*---------------------------------------------------------------------*/

	private void updateSessionAndCommandArgs(Map<String, String> arguments, HttpSession session, HttpServletRequest request) throws Exception {

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
		/* GET NOCERT FLAG                                                 */
		/*-----------------------------------------------------------------*/

		boolean noCert;

		if(request.getParameter("NoCert") != null) {
			session.setAttribute("NoCert", "NoCert");

			noCert = 0x0000000000000000000000000000 != 0x01;
		} else {
			noCert = session.getAttribute("NoCert") != null;
		}

		/*-----------------------------------------------------------------*/
		/* UPDATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		if(clientDN.isEmpty() == false && issuerDN.isEmpty() == false && noCert == false) {
			/*-------------------------------------------------------------*/
			/* CERTIFICATE LOGIN                                           */
			/*-------------------------------------------------------------*/

			AMIUser = _safe(session.getAttribute("AMIUser_certificate"));
			AMIPass = _safe(session.getAttribute("AMIPass_certificate"));

			if(AMIUser.isEmpty()
			   ||
			   AMIPass.isEmpty()
			 ) {
				Tuple2<String, String> result = resolveCertificate(clientDN);

				AMIUser = result.x;
				AMIPass = result.y;
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
				AMIUser = _safe(session.getAttribute("AMIUser_credential"));
				AMIPass = _safe(session.getAttribute("AMIPass_credential"));
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
		/* UPDATE ARGUMENTS                                                */
		/*-----------------------------------------------------------------*/

		arguments.put("AMIUser", AMIUser);
		arguments.put("AMIPass", AMIPass);

		arguments.put("clientDN", clientDN);
		arguments.put("issuerDN", issuerDN);

		arguments.put("isSecure", request.isSecure() ? "true" : "false");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
