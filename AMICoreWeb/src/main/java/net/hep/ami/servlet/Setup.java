package net.hep.ami.servlet;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.slf4j.*;

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

	private static final long serialVersionUID = 5570607624197246874L;

	/*---------------------------------------------------------------------*/

	public static final Logger logger = LoggerFactory.getLogger("SETUP");

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
		/* SET DEFAULT ENCODING                                            */
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
		/* GET CURRENT YEAR                                                */
		/*-----------------------------------------------------------------*/

		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

		/*-----------------------------------------------------------------*/
		/* WRITE HTML                                                      */
		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/

			String data;

			String level = req.getParameter("Level");

			/****/ if("3".equals(level)) {
				data = level3(req, year);
			} else if("2".equals(level)) {
				data = level2(req, year);
			} else {
				data = level1(req, year);
			}

			/*-------------------------------------------------------------*/

			try(PrintWriter writer = res.getWriter())
			{
				res.setStatus(HttpServletResponse.SC_OK);

				res.setContentType("text/html");

				writer.write(data);
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);

			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	static String getConfigPath()
	{
		String result;

		/*-----------------------------------------------------------------*/

		result = System.getProperty("user.home", "/fake1357") + File.separator + ".ami";

		if(new File(result).exists() == false)
		{
			result = System.getProperty("catalina.base", "/fake1357");

			if(new File(result).exists() == false)
			{
				result = "/etc/ami";
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private String level1(HttpServletRequest req, String year) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/
		/* BUILD HTML                                                      */
		/*-----------------------------------------------------------------*/

		TextFile.read(stringBuilder, Setup.class.getResourceAsStream("/twig/setup_level1.twig"));

		return stringBuilder.toString()
		                    .replace("{{YEAR}}", year)
		                    .replace("{{AMI_CONFIG_PATH}}", getConfigPath())
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String level2(HttpServletRequest req, String year) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (SERVER)                                     */
		/*-----------------------------------------------------------------*/

		String host = req.getParameter("host");
		host = (host != null) ? host.trim()
		                      : ConfigSingleton.getProperty("host")
		;

		String agent = req.getParameter("agent");
		agent = (agent != null) ? agent.trim()
		                        : ConfigSingleton.getProperty("agent")
		;

		String admin_user = req.getParameter("admin_user");
		admin_user = (admin_user != null) ? admin_user.trim()
		                                  : ConfigSingleton.getProperty("admin_user")
		;

		String admin_pass = req.getParameter("admin_pass");
		admin_pass = (admin_pass != null) ? admin_pass.trim()
		                                  : ConfigSingleton.getProperty("admin_pass")
		;

		String encryption_key = req.getParameter("encryption_key");
		encryption_key = (encryption_key != null) ? encryption_key.trim()
		                                          : ConfigSingleton.getProperty("encryption_key")
		;

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (ROUTER DATABASE)                            */
		/*-----------------------------------------------------------------*/

		String router = req.getParameter("router");
		router = (router != null) ? router.trim()
		                          : ConfigSingleton.getProperty("router")
		;

		String router_url = req.getParameter("router_url");
		router_url = (router_url != null) ? router_url.trim()
		                                  : ConfigSingleton.getProperty("router_url")
		;

		String router_user = req.getParameter("router_user");
		router_user = (router_user != null) ? router_user.trim()
		                                    : ConfigSingleton.getProperty("router_user")
		;

		String router_pass = req.getParameter("router_pass");
		router_pass = (router_pass != null) ? router_pass.trim()
		                                    : ConfigSingleton.getProperty("router_pass")
		;

		/*-----------------------------------------------------------------*/
		/* BUILD HTML                                                      */
		/*-----------------------------------------------------------------*/

		try(InputStream inputStream = Setup.class.getResourceAsStream("/twig/setup_level2.twig"))
		{
			TextFile.read(stringBuilder, inputStream);
		}

		/*-----------------------------------------------------------------*/

		return stringBuilder.toString()
		                    .replace("{{YEAR}}", year)
		                    /**/
		                    .replace("{{HOST}}", host)
		                    .replace("{{AGENT}}", agent)
		                    .replace("{{ADMIN_USER}}", admin_user)
		                    .replace("{{ADMIN_PASS}}", admin_pass)
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

	private String level3(HttpServletRequest req, String year) throws Exception
	{
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();

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

		String guest_user = "guest";
		String guest_pass =   ""   ;

		/*-----------------------------------------------------------------*/
		/* BUILD CONFIG FILE                                               */
		/*-----------------------------------------------------------------*/

		stringBuilder1.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
		              .append("\n")
		              .append("<properties>\n")
		              .append("  <property name=\"host\"><![CDATA[" + host + "]]></property>\n")
		              .append("  <property name=\"agent\"><![CDATA[" + agent + "]]></property>\n")
		              .append("  <property name=\"admin_user\"><![CDATA[" + admin_user + "]]></property>\n")
		              .append("  <property name=\"admin_pass\"><![CDATA[" + admin_pass + "]]></property>\n")
		              .append("  <property name=\"guest_user\"><![CDATA[" + guest_user + "]]></property>\n")
		              .append("  <property name=\"guest_pass\"><![CDATA[" + guest_pass + "]]></property>\n")
		              .append("  <property name=\"encryption_key\"><![CDATA[" + encryption_key + "]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"router\"><![CDATA[" + router + "]]></property>\n")
		              .append("  <property name=\"router_url\"><![CDATA[" + router_url + "]]></property>\n")
		              .append("  <property name=\"router_user\"><![CDATA[" + router_user + "]]></property>\n")
		              .append("  <property name=\"router_pass\"><![CDATA[" + router_pass + "]]></property>\n")
		              .append("</properties>\n")
		;

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* SETUP SERVER CONFIG                                         */
			/*-------------------------------------------------------------*/

			Router db = new Router("self", router, router_url, router_user, router_pass);

			try
			{
				/*---------------------------------------------------------*/

				try(OutputStream outputStream = new FileOutputStream(getConfigPath() + File.separator + "AMI.xml"))
				{
					TextFile.write(outputStream, stringBuilder1);
				}

				ConfigSingleton.reload();

				/*---------------------------------------------------------*/

				if("on".equals(req.getParameter("router_reset")))
				{
					db.create();
					db.fill();
				}

				/*---------------------------------------------------------*/

				db.commitAndRelease();
			}
			catch(Exception e)
			{
				logger.error(e.getMessage(), e);

				db.rollbackAndRelease();
			}

			/*-------------------------------------------------------------*/
			/* LOAD SERVER CONFIG                                          */
			/*-------------------------------------------------------------*/

			Router.reload();

			/*-------------------------------------------------------------*/
			/* BUILD HTML                                                  */
			/*-------------------------------------------------------------*/

			try(InputStream inputStream = Setup.class.getResourceAsStream("/twig/setup_level3_success.twig"))
			{
				TextFile.read(stringBuilder2, inputStream);
			}

			/*-------------------------------------------------------------*/

			return stringBuilder2.toString()
			                     .replace("{{YEAR}}", year)
			                     /**/
			                     .replace("{{HOST}}", host)
			                     .replace("{{AGENT}}", agent)
			                     .replace("{{ADMIN_USER}}", admin_user)
			                     .replace("{{ADMIN_PASS}}", admin_pass)
			                     .replace("{{ENCRYPTION_KEY}}", encryption_key)
			                     /**/
			                     .replace("{{ROUTER}}", router)
			                     .replace("{{ROUTER_URL}}", router_url)
			                     .replace("{{ROUTER_USER}}", router_user)
			                     .replace("{{ROUTER_PASS}}", router_pass)
			                     /**/
			                     .replace("{{CATALINA_BASE}}", System.getProperty("catalina.base", "?"))
			;

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			/*-------------------------------------------------------------*/
			/* LOG ERROR                                                   */
			/*-------------------------------------------------------------*/

			logger.error(e.getMessage(), e);

			/*-------------------------------------------------------------*/
			/* BUILD HTML                                                  */
			/*-------------------------------------------------------------*/

			try(InputStream inputStream = Setup.class.getResourceAsStream("/twig/setup_level3_error.twig"))
			{
				TextFile.read(stringBuilder2, inputStream);
			}

			/*-------------------------------------------------------------*/

			return stringBuilder2.toString()
			                     .replace("{{YEAR}}", year)
			                     /**/
			                     .replace("{{HOST}}", host)
			                     .replace("{{AGENT}}", agent)
			                     .replace("{{ADMIN_USER}}", admin_user)
			                     .replace("{{ADMIN_PASS}}", admin_pass)
			                     .replace("{{ENCRYPTION_KEY}}", encryption_key)
			                     /**/
			                     .replace("{{ROUTER}}", router)
			                     .replace("{{ROUTER_URL}}", router_url)
			                     .replace("{{ROUTER_USER}}", router_user)
			                     .replace("{{ROUTER_PASS}}", router_pass)
			                     /**/
			                     .replace("{{MESSAGE}}", e.getMessage())
			;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
