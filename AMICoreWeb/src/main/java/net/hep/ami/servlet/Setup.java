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

	private static String getConfigPath()
	{
		String result;

		/*-----------------------------------------------------------------*/

		result = System.getProperty("user.home", "/fake1357") + File.separator + ".ami";

		if(new File(result).exists() == false)
		{
			result = System.getProperty("catalina.base", "/fake1357") + File.separator + "conf";

			if(new File(result).exists() == false)
			{
				result = "/etc/ami";
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static String getRootPath()
	{
		return System.getProperty("catalina.base", "/fake1357") + File.separator + "webapps" + File.separator + "ROOT";
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

		String admin_user = req.getParameter("admin_user");
		admin_user = (admin_user != null) ? admin_user.trim()
		                                  : ConfigSingleton.getProperty("admin_user")
		;

		String admin_pass = req.getParameter("admin_pass");
		admin_pass = (admin_pass != null) ? admin_pass.trim()
		                                  : ConfigSingleton.getProperty("admin_pass")
		;

		String admin_email = req.getParameter("admin_email");
		admin_email = (admin_email != null) ? admin_email.trim()
		                                    : ConfigSingleton.getProperty("admin_email")
		;

		String encryption_key = req.getParameter("encryption_key");
		encryption_key = (encryption_key != null) ? encryption_key.trim()
		                                          : ConfigSingleton.getProperty("encryption_key")
		;

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (AMI DATABASE)                               */
		/*-----------------------------------------------------------------*/

		String router_catalog = req.getParameter("router_catalog");
		router_catalog = (router_catalog != null) ? router_catalog.trim()
		                                          : ConfigSingleton.getProperty("router_catalog")
		;

		String router_schema = req.getParameter("router_schema");
		router_schema = (router_schema != null) ? router_schema.trim()
		                                        : ConfigSingleton.getProperty("router_schema")
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
		/* GET/POST VARIABLES (USER EXTENTIONS)                            */
		/*-----------------------------------------------------------------*/

		String class_path = req.getParameter("class_path");
		class_path = (class_path != null) ? class_path.trim()
		                                  : ConfigSingleton.getProperty("class_path")
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
		                    .replace("{{ADMIN_USER}}", admin_user)
		                    .replace("{{ADMIN_PASS}}", admin_pass)
		                    .replace("{{ADMIN_EMAIL}}", admin_email)
		                    .replace("{{ENCRYPTION_KEY}}", encryption_key)
		                    /**/
		                    .replace("{{ROUTER_CATALOG}}", router_catalog)
		                    .replace("{{ROUTER_SCHEMA}}", router_schema)
		                    .replace("{{ROUTER_URL}}", router_url)
		                    .replace("{{ROUTER_USER}}", router_user)
		                    .replace("{{ROUTER_PASS}}", router_pass)
		                    /**/
		                    .replace("{{CLASS_PATH}}", class_path)
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String level3(HttpServletRequest req, String year) throws Exception
	{
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();
		StringBuilder stringBuilder3 = new StringBuilder();

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (SERVER)                                     */
		/*-----------------------------------------------------------------*/

		String host = req.getParameter("host");
		host = (host != null) ? host.trim() : "";

		String admin_user = req.getParameter("admin_user");
		admin_user = (admin_user != null) ? admin_user.trim() : "";

		String admin_pass = req.getParameter("admin_pass");
		admin_pass = (admin_pass != null) ? admin_pass.trim() : "";

		String admin_email = req.getParameter("admin_email");
		admin_email = (admin_email != null) ? admin_email.trim() : "";

		String encryption_key = req.getParameter("encryption_key");
		encryption_key = (encryption_key != null) ? encryption_key.trim() : "";

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (AMI DATABASE)                               */
		/*-----------------------------------------------------------------*/

		String router_catalog = req.getParameter("router_catalog");
		router_catalog = (router_catalog != null) ? router_catalog.trim() : "";

		String router_schema = req.getParameter("router_schema");
		router_schema = (router_schema != null) ? router_schema.trim() : "";

		String router_url = req.getParameter("router_url");
		router_url = (router_url != null) ? router_url.trim() : "";

		String router_user = req.getParameter("router_user");
		router_user = (router_user != null) ? router_user.trim() : "";

		String router_pass = req.getParameter("router_pass");
		router_pass = (router_pass != null) ? router_pass.trim() : "";

		/*-----------------------------------------------------------------*/
		/* GET/POST VARIABLES (USER EXTENTIONS)                            */
		/*-----------------------------------------------------------------*/

		String class_path = req.getParameter("class_path");
		class_path = (class_path != null) ? class_path.trim()
		                                  : ConfigSingleton.getProperty("class_path")
		;

		/*-----------------------------------------------------------------*/

		while(host.endsWith("/"))
		{
			host = host.substring(0, host.length() - 1);
		}

		/*-----------------------------------------------------------------*/
		/* BUILD CONFIG FILE                                               */
		/*-----------------------------------------------------------------*/

		stringBuilder1.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
		              .append("\n")
		              .append("<properties>\n")
		              .append("  <property name=\"host\"><![CDATA[" + host + "]]></property>\n")
		              .append("  <property name=\"admin_user\"><![CDATA[" + admin_user + "]]></property>\n")
		              .append("  <property name=\"admin_pass\"><![CDATA[" + admin_pass + "]]></property>\n")
		              .append("  <property name=\"admin_email\"><![CDATA[" + admin_email + "]]></property>\n")
		              .append("  <property name=\"encryption_key\"><![CDATA[" + encryption_key + "]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"router_catalog\"><![CDATA[" + router_catalog + "]]></property>\n")
		              .append("  <property name=\"router_schema\"><![CDATA[" + router_schema + "]]></property>\n")
		              .append("  <property name=\"router_url\"><![CDATA[" + router_url + "]]></property>\n")
		              .append("  <property name=\"router_user\"><![CDATA[" + router_user + "]]></property>\n")
		              .append("  <property name=\"router_pass\"><![CDATA[" + router_pass + "]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"class_path\"><![CDATA[" + class_path + "]]></property>\n")
		              .append("</properties>\n")
		;

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* SETUP SERVER CONFIG                                         */
			/*-------------------------------------------------------------*/

			Router db = new Router("self", router_catalog, router_url, router_user, router_pass);

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

					db.fill(router_schema);
				}

				/*---------------------------------------------------------*/

				db.commitAndRelease();
			}
			catch(Exception e)
			{
				db.rollbackAndRelease();

				throw e;
			}

			/*-------------------------------------------------------------*/
			/* LOAD SERVER CONFIG                                          */
			/*-------------------------------------------------------------*/

			Router.reload();

			/*-------------------------------------------------------------*/
			/* PATCH ROOT SERVLET                                          */
			/*-------------------------------------------------------------*/

			try
			{
				/*---------------------------------------------------------*/

				try(InputStream inputStream = new FileInputStream(getRootPath() + File.separator + "index.html"))
				{
					TextFile.read(stringBuilder2, inputStream);
				}

				/*---------------------------------------------------------*/

				String stringContent2 = stringBuilder2.toString().replaceAll("endpoint_url\\s*:\\s*[\'\"][^\'\"]*[\'\"]", "endpoint_url: '" + host + "/AMI/FrontEnd'");

				/*---------------------------------------------------------*/

				try(OutputStream outputStream = new FileOutputStream(getRootPath() + File.separator + "index.html"))
				{
					TextFile.write(outputStream, stringContent2);
				}

				/*---------------------------------------------------------*/
			}
			catch(Exception e)
			{
				/* IGNORE */
			}

			/*-------------------------------------------------------------*/
			/* BUILD HTML                                                  */
			/*-------------------------------------------------------------*/

			try(InputStream inputStream = Setup.class.getResourceAsStream("/twig/setup_level3_success.twig"))
			{
				TextFile.read(stringBuilder3, inputStream);
			}

			/*-------------------------------------------------------------*/

			return stringBuilder3.toString()
			                     .replace("{{YEAR}}", year)
			                     /**/
			                     .replace("{{HOST}}", host)
			                     .replace("{{ADMIN_USER}}", admin_user)
			                     .replace("{{ADMIN_PASS}}", admin_pass)
			                     .replace("{{ADMIN_EMAIL}}", admin_email)
			                     .replace("{{ENCRYPTION_KEY}}", encryption_key)
			                     /**/
			                     .replace("{{ROUTER_CATALOG}}", router_catalog)
			                     .replace("{{ROUTER_SCHEMA}}", router_schema)
			                     .replace("{{ROUTER_URL}}", router_url)
			                     .replace("{{ROUTER_USER}}", router_user)
			                     .replace("{{ROUTER_PASS}}", router_pass)
			                     /**/
			                     .replace("{{CLASS_PATH}}", class_path)
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
				TextFile.read(stringBuilder3, inputStream);
			}

			/*-------------------------------------------------------------*/

			return stringBuilder3.toString()
			                     .replace("{{YEAR}}", year)
			                     /**/
			                     .replace("{{HOST}}", host)
			                     .replace("{{ADMIN_USER}}", admin_user)
			                     .replace("{{ADMIN_PASS}}", admin_pass)
			                     .replace("{{ADMIN_EMAIL}}", admin_email)
			                     .replace("{{ENCRYPTION_KEY}}", encryption_key)
			                     /**/
			                     .replace("{{ROUTER_CATALOG}}", router_catalog)
			                     .replace("{{ROUTER_SCHEMA}}", router_schema)
			                     .replace("{{ROUTER_URL}}", router_url)
			                     .replace("{{ROUTER_USER}}", router_user)
			                     .replace("{{ROUTER_PASS}}", router_pass)
			                     /**/
			                     .replace("{{CLASS_PATH}}", class_path)
			                     /**/
			                     .replace("{{MESSAGE}}", e.getMessage())
			;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
