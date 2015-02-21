package net.hep.ami.servlet;

import java.io.*;

import javax.servlet.http.*;

import net.hep.ami.jdbc.*;

public class Setup extends HttpServlet {
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -7394712866072360297L;

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
		/* GET/POST VARIABLES                                              */
		/*-----------------------------------------------------------------*/

		String level = req.getParameter("Level");
		level = (level != null) ? level.trim() : "1";

		/*-----------------------------------------------------------------*/
		/* WRITE FORM                                                      */
		/*-----------------------------------------------------------------*/

		res.setContentType("text/html");

		PrintWriter writer = res.getWriter();

		/****/ if(level.equals("1")) {
			writer.write(level1(req));
		} else if(level.equals("2")) {
			writer.write(level2(req));
		}

		writer.close();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder readHtmlFile(InputStream inputStream) {

		StringBuilder result = new StringBuilder();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try {

			String line;

			while((line = bufferedReader.readLine()) != null) {
				result.append(line);
				result.append('\n');
			}

		} catch(IOException e) {
			/* IGNORE */
		} finally {

				try {
					bufferedReader.close();

				} catch(IOException e) {
					/* IGNORE */
				}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private String level1(HttpServletRequest req) {

		return readHtmlFile(Setup.class.getResourceAsStream("/html/setup_level1.html")).toString();
	}

	/*---------------------------------------------------------------------*/

	private String level2(HttpServletRequest req) {
		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (SERVER)                                     */
		/*-----------------------------------------------------------------*/

		String host = req.getParameter("host");
		host = (host != null) ? host.trim() : "";

		String agent = req.getParameter("agent");
		agent = (agent != null) ? agent.trim() : "";

		String admin_user = req.getParameter("admin_user");
		admin_user = (admin_user != null) ? admin_user.trim() : "";

		String admin_pass = req.getParameter("admin_pass");
		admin_pass = (admin_pass != null) ? admin_pass.trim() : "";

		String guest_user = req.getParameter("guest_user");
		guest_user = (guest_user != null) ? guest_user.trim() : "";

		String guest_pass = req.getParameter("guest_pass");
		guest_pass = (guest_pass != null) ? guest_pass.trim() : "";

		String encryption_key = req.getParameter("encryption_key");
		encryption_key = (encryption_key != null) ? encryption_key.trim() : "";

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (ROUTER DATABASE)                            */
		/*-----------------------------------------------------------------*/

		String jdbc_url = req.getParameter("jdbc_url");
		jdbc_url = (jdbc_url != null) ? jdbc_url.trim() : "";

		String router_user = req.getParameter("router_user");
		router_user = (router_user != null) ? router_user.trim() : "";

		String router_pass = req.getParameter("router_pass");
		router_pass = (router_pass != null) ? router_pass.trim() : "";

		String router_name = req.getParameter("router_name");
		router_name = (router_name != null) ? router_name.trim() : "";

		/*-----------------------------------------------------------------*/
		/* PATCH HOST                                                      */
		/*-----------------------------------------------------------------*/

		while(host.endsWith("/")) {
			host = host.substring(0, host.length() - 1);
		}

		/*-----------------------------------------------------------------*/
		/* BUILD CONFIG FILE                                               */
		/*-----------------------------------------------------------------*/

		String content = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
		                 "\n" +
		                 "<properties>\n" +
		                 "  <property name=\"host\">" + host + "</property>\n" +
		                 "  <property name=\"agent\">" + agent + "</property>\n" +
		                 "  <property name=\"admin_user\">" + admin_user + "</property>\n" +
		                 "  <property name=\"admin_pass\">" + admin_pass + "</property>\n" +
		                 "  <property name=\"guest_user\">" + guest_user + "</property>\n" +
		                 "  <property name=\"guest_pass\">" + guest_pass + "</property>\n" +
		                 "  <property name=\"encryption_key\">" + encryption_key + "</property>\n" +
		                 "\n" +
		                 "  <property name=\"jdbc_url\">" + jdbc_url + "</property>\n" +
		                 "  <property name=\"router_user\">" + router_user + "</property>\n" +
		                 "  <property name=\"router_pass\">" + router_pass + "</property>\n" +
		                 "  <property name=\"router_name\">" + router_name + "</property>\n" +
		                 "</properties>\n"
		;

		/*-----------------------------------------------------------------*/
		/* CHECK AND SAVE CONFIG FILE                                      */
		/*-----------------------------------------------------------------*/

		String path = this.getClass().getResource("/AMI.xml").getPath();

		/*-----------------------------------------------------------------*/

		BasicLoader    basicLoader    = null;
		BufferedWriter bufferedWriter = null;

		try {
			basicLoader = new BasicLoader(jdbc_url, router_user, router_pass);
			basicLoader.useDB(router_name);

			bufferedWriter = new BufferedWriter(new FileWriter(path));
			bufferedWriter.write(content);

			return readHtmlFile(Setup.class.getResourceAsStream("/html/setup_level2_success.html")).toString().replace("%%HOST%%", host).replace("%%ADMIN_USER%%", admin_user).replace("%%ADMIN_PASS%%", admin_pass);

		} catch(Exception e) {

			return readHtmlFile(Setup.class.getResourceAsStream("/html/setup_level2_error.html")).toString().replace("%%MESSAGE%%", e.getMessage());

		} finally {
			/*-------------------------------------------------------------*/

			if(basicLoader != null) {

				try {
					basicLoader.rollbackAndRelease();

				} catch(Exception e) {
					/* IGNORE */
				}
			}

			/*-------------------------------------------------------------*/

			if(bufferedWriter != null) {

				try {
					bufferedWriter.close();

				} catch(Exception e) {
					/* IGNORE */
				}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
