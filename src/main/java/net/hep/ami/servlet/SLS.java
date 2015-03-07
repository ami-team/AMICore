package net.hep.ami.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

@WebServlet(
	name = "SLS",
	urlPatterns = "/SLS"
)

public class SLS extends HttpServlet {
	/*---------------------------------------------------------------------*/

	private static class SLSTuple extends Tuple3<Integer, String, String> {

		public SLSTuple(int _x, String _y, String _z) {
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -5592991531794327107L;

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
		/* GET/POST VARIABLES                                              */
		/*-----------------------------------------------------------------*/

		String service = req.getParameter("Service");
		service = (service != null) ? service.trim() : "";

		String mode = req.getParameter("Mode");
		mode = (mode != null) ? mode.trim() : "";

		/*-----------------------------------------------------------------*/
		/* GET AND WRITE STATUS                                            */
		/*-----------------------------------------------------------------*/

		res.setContentType("text/xml");

		SLSTuple tuple;

		try {
			tuple = getStatus(service, mode);

		} catch(Exception e) {
			tuple = new SLSTuple(0, "", e.getMessage());
		}

		PrintWriter writer = res.getWriter();

		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<serviceupdate xmlns=\"http://sls.cern.ch/SLS/XML/update\"><id>" + service + "</id><availability>" + tuple.x + "</availability><data>" + tuple.y + "</data><notes><![CDATA[" + tuple.z + "]]></notes><timestamp>" +  DateFormater.formatSLS(new Date()) + "</timestamp></serviceupdate>");
		writer.close();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private SLSTuple getStatus(String service, String mode) throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier("self");

		QueryResult queryResult;

		try {
			queryResult = basicQuerier.executeSQLQuery("SELECT `name`,`url` FROM `router_node` WHERE `service` LIKE '" + service + "'");

		} finally {
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF NODES                                             */
		/*-----------------------------------------------------------------*/

		final int nr = queryResult.getNumberOfRows();

		/*-----------------------------------------------------------------*/
		/* GET SCORE OF NODES                                              */
		/*-----------------------------------------------------------------*/

		int total = 0;

		String data = "";
		String notes = "";

		for(int i = 0; i < nr; i++) {

			int score;

			String nodeName = queryResult.getValue(i, "name");
			String nodeURL = queryResult.getValue(i, "url");

			try {
				checkAvailability(mode, nodeName, nodeURL);
				score = 0x64;

			} catch(Exception e) {
				notes += ", node error: " + e.getMessage();
				score = 0x00;
			}

			total += score;

			data += "<numericvalue name=\"" + nodeName + "\">" + score + "</numericvalue>";
		}

		/*-----------------------------------------------------------------*/
		/* GET TOTAL                                                       */
		/*-----------------------------------------------------------------*/

		if(nr > 0) total /= nr;

		/*-----------------------------------------------------------------*/

		if(!notes.isEmpty()) {
			notes = notes.substring(2);
		}

		return new SLSTuple(total, data, notes);
	}

	/*---------------------------------------------------------------------*/

	private void checkAvailability(String mode, String nodeName, String nodeURL) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET HTTP URL CONNECTION                                         */
		/*-----------------------------------------------------------------*/

		HttpURLConnection httpURLConnection;

		if(mode.equals("soft")) {
			httpURLConnection = HttpConnectionFactory.openTLSConnection(nodeURL.trim() + "?Command=" + "Ping" + "");
		} else {
			httpURLConnection = HttpConnectionFactory.openTLSConnection(nodeURL.trim() + "?Command=GetSessionInfo");
		}

		httpURLConnection.setRequestMethod("GET");

		/*-----------------------------------------------------------------*/
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = httpURLConnection.getInputStream();

		/*-----------------------------------------------------------------*/
		/* GET NODE SCORE                                                  */
		/*-----------------------------------------------------------------*/

		try {
			XMLFactories.newDocument(inputStream);

		} finally {
			inputStream.close();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
