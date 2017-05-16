package net.hep.ami.servlet;

import java.io.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

@WebServlet(
	name = "Setup",
	urlPatterns = "/Setup"
)

public class Setup extends HttpServlet
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -7394712866072360297L;

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
		/* GET/POST VARIABLES                                              */
		/*-----------------------------------------------------------------*/

		String level = req.getParameter("Level");
		level = (level != null) ? level.trim() : "1";

		/*-----------------------------------------------------------------*/
		/* WRITE HTML                                                      */
		/*-----------------------------------------------------------------*/

		res.setStatus(200);

		res.setContentType("text/html");

		/*-----------------------------------------------------------------*/

		PrintWriter writer = res.getWriter();

		try
		{
			/****/ if(level.equals("1")) {
				writer.write(level1(req));
			} else if(level.equals("2")) {
				writer.write(level2(req));
			}
		}
		catch(Exception e)
		{
			writer.write("<html><body><![CDATA[" + e.getMessage() + "]]></body></html>");
		}

		writer.close();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String level1(HttpServletRequest req) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* VARIABLES (SERVER)                                              */
		/*-----------------------------------------------------------------*/

		String host = ConfigSingleton.getProperty("host");

		String agent = ConfigSingleton.getProperty("agent");

		String admin_user = ConfigSingleton.getProperty("admin_user");

		String admin_pass = ConfigSingleton.getProperty("admin_pass");

		String guest_user = ConfigSingleton.getProperty("guest_user");

		String guest_pass = ConfigSingleton.getProperty("guest_pass");

		String encryption_key = ConfigSingleton.getProperty("encryption_key");

		/*-----------------------------------------------------------------*/
		/* VARIABLES (ROUTER DATABASE)                                     */
		/*-----------------------------------------------------------------*/

		String router = ConfigSingleton.getProperty("router");

		String router_url = ConfigSingleton.getProperty("router_url");

		String router_user = ConfigSingleton.getProperty("router_user");

		String router_pass = ConfigSingleton.getProperty("router_pass");

		/*-----------------------------------------------------------------*/
		/* BUILD HTML                                                      */
		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		TextFile.read(stringBuilder, Setup.class.getResourceAsStream("/html/setup_level1.twig"));

		return stringBuilder.toString()
		                    .replace("{{HOST}}", host)
		                    .replace("{{AGENT}}", agent)
		                    .replace("{{ADMIN_USER}}", admin_user)
		                    .replace("{{ADMIN_PASS}}", admin_pass)
		                    .replace("{{GUEST_USER}}", guest_user)
		                    .replace("{{GUEST_PASS}}", guest_pass)
		                    .replace("{{ENCRYPTION_KEY}}", encryption_key)
		                    /**/
		                    .replace("{{ROUTER}}", router)
		                    .replace("{{ROUTER_URL}}", router_url)
		                    .replace("{{ROUTER_USER}}", router_user)
		                    .replace("{{ROUTER_PASS}}", router_pass)
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String level2(HttpServletRequest req) throws Exception
	{
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

		String encryption_key = req.getParameter("encryption_key");
		encryption_key = (encryption_key != null) ? encryption_key.trim() : "";

		/*-----------------------------------------------------------------*/

		String guest_user = "guest";
		String guest_pass =   ""   ;

		String catalina_base = System.getProperty("catalina.base");

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (ROUTER DATABASE)                            */
		/*-----------------------------------------------------------------*/

		String router = req.getParameter("router");
		router = (router != null) ? router.trim() : "";

		String router_url = req.getParameter("router_url");
		router_url = (router_url != null) ? router_url.trim() : "";

		String router_user = req.getParameter("router_user");
		router_user = (router_user != null) ? router_user.trim() : "";

		String router_pass = req.getParameter("router_pass");
		router_pass = (router_pass != null) ? router_pass.trim() : "";

		/*-----------------------------------------------------------------*/

		while(host.endsWith("/"))
		{
			host = host.substring(0, host.length() - 1);
		}

		/*-----------------------------------------------------------------*/
		/* BUILD CONFIG FILE                                               */
		/*-----------------------------------------------------------------*/

		String content = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
		                 "\n" +
		                 "<properties>\n" +
		                 "  <property name=\"host\"><![CDATA[" + host + "]]></property>\n" +
		                 "  <property name=\"agent\"><![CDATA[" + agent + "]]></property>\n" +
		                 "  <property name=\"admin_user\"><![CDATA[" + admin_user + "]]></property>\n" +
		                 "  <property name=\"admin_pass\"><![CDATA[" + admin_pass + "]]></property>\n" +
		                 "  <property name=\"guest_user\"><![CDATA[" + guest_user + "]]></property>\n" +
		                 "  <property name=\"guest_pass\"><![CDATA[" + guest_pass + "]]></property>\n" +
		                 "  <property name=\"encryption_key\"><![CDATA[" + encryption_key + "]]></property>\n" +
		                 "\n" +
		                 "  <property name=\"router\"><![CDATA[" + router + "]]></property>\n" +
		                 "  <property name=\"router_url\"><![CDATA[" + router_url + "]]></property>\n" +
		                 "  <property name=\"router_user\"><![CDATA[" + router_user + "]]></property>\n" +
		                 "  <property name=\"router_pass\"><![CDATA[" + router_pass + "]]></property>\n" +
		                 "</properties>\n"
		;

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			/*-------------------------------------------------------------*/
			/* CHECK ROUTER DATABASE                                       */
			/*-------------------------------------------------------------*/

			new SimpleQuerier("self", router, router_url, router_user, router_pass).rollbackAndRelease();

			/*-------------------------------------------------------------*/
			/* WRITE CONFIG FILE                                           */
			/*-------------------------------------------------------------*/

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ConfigSingleton.getConfigFileName()));

			try
			{
				bufferedWriter.write(content);
			}
			finally
			{
				bufferedWriter.close();
			}

			/*-------------------------------------------------------------*/
			/* BUILD HTML                                                  */
			/*-------------------------------------------------------------*/

			TextFile.read(stringBuilder, Setup.class.getResourceAsStream("/html/setup_level2_success.twig"));

			return stringBuilder.toString()
			                    .replace("{{HOST}}", host)
			                    .replace("{{ADMIN_USER}}", admin_user)
			                    .replace("{{ADMIN_PASS}}", admin_pass)
			                    .replace("{{CATALINA_BASE}}", catalina_base)
			;

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			/*-------------------------------------------------------------*/
			/* BUILD HTML                                                  */
			/*-------------------------------------------------------------*/

			TextFile.read(stringBuilder, Setup.class.getResourceAsStream("/html/setup_level2_error.twig"));

			return stringBuilder.toString()
			                    .replace("{{MESSAGE}}", e.getMessage())
			;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
