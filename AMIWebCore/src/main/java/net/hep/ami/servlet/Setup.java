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

	private String level1(HttpServletRequest req)
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
		/* VARIABLES (DATABASE)                                            */
		/*-----------------------------------------------------------------*/

		String jdbc_url = ConfigSingleton.getProperty("jdbc_url");

		String router_user = ConfigSingleton.getProperty("router_user");

		String router_pass = ConfigSingleton.getProperty("router_pass");

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			TextFile.read(stringBuilder, Setup.class.getResourceAsStream("/html/setup_level1.html"));

			return stringBuilder.toString().replace("%%HOST%%", host).replace("%%AGENT%%", agent).replace("%%ADMIN_USER%%", admin_user).replace("%%ADMIN_PASS%%", admin_pass).replace("%%GUEST_USER%%", guest_user).replace("%%GUEST_PASS%%", guest_pass).replace("%%ENCRYPTION_KEY%%", encryption_key).replace("%%JDBC_URL%%", jdbc_url).replace("%%ROUTER_USER%%", router_user).replace("%%ROUTER_PASS%%", router_pass);
		}
		catch(Exception e)
		{
			return "<html><body><![CDATA[" + e.getMessage() + "]]></body></html>";
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String level2(HttpServletRequest req)
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

		/*-----------------------------------------------------------------*/
		/* PATCH HOST                                                      */
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
		                 "  <property name=\"jdbc_url\"><![CDATA[" + jdbc_url + "]]></property>\n" +
		                 "  <property name=\"router_user\"><![CDATA[" + router_user + "]]></property>\n" +
		                 "  <property name=\"router_pass\"><![CDATA[" + router_pass + "]]></property>\n" +
		                 "</properties>\n"
		;

		/*-----------------------------------------------------------------*/
		/* CHECK AND SAVE CONFIG FILE                                      */
		/*-----------------------------------------------------------------*/

		String fileName = ConfigSingleton.getConfigFileName();

		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = null;
		BufferedWriter bufferedWriter = null;
		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			basicQuerier = new BasicQuerier(jdbc_url, router_user, router_pass);

			bufferedWriter = new BufferedWriter(new FileWriter(fileName));
			bufferedWriter.write(content);

			try
			{
				TextFile.read(stringBuilder, Setup.class.getResourceAsStream("/html/setup_level2_success.html"));

				return stringBuilder.toString().replace("%%HOST%%", host).replace("%%ADMIN_USER%%", admin_user).replace("%%ADMIN_PASS%%", admin_pass);
			}
			catch(Exception f)
			{
				return "<html><body><![CDATA[" + f.getMessage() + "]]></body></html>";
			}

		}
		catch(Exception e)
		{
			try
			{
				TextFile.read(stringBuilder, Setup.class.getResourceAsStream("/html/setup_level2_error.html"));

				return stringBuilder.toString().replace("%%MESSAGE%%", e.getMessage());
			}
			catch(Exception f)
			{
				return "<html><body><![CDATA[" + f.getMessage() + "]]></body></html>";
			}

		}
		finally
		{
			/*-------------------------------------------------------------*/

			if(basicQuerier != null)
			{
				try
				{
					basicQuerier.rollbackAndRelease();
				}
				catch(Exception e)
				{
					/* IGNORE */
				}
			}

			/*-------------------------------------------------------------*/

			if(bufferedWriter != null)
			{
				try
				{
					bufferedWriter.close();
				}
				catch(Exception e)
				{
					/* IGNORE */
				}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
