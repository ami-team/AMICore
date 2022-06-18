package net.hep.ami.servlet;

import lombok.extern.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@Slf4j
@WebServlet(
	name = "Setup",
	urlPatterns = "/Setup"
)
@SuppressWarnings({"unused", "DuplicatedCode", "DuplicateExpressions"})
public class Setup extends HttpServlet
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = 5570607624197246874L;

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	protected void doGet(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res)
	{
		doCommand(req, res);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	protected void doPost(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res)
	{
		doCommand(req, res);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void doCommand(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res)
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* SET DEFAULT ENCODING                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			req.setCharacterEncoding("UTF-8");
			res.setContentType("text/html; charset=UTF-8");
			res.setCharacterEncoding("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			/* IGNORE */
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK IPS                                                                                                  */
		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> ips = Arrays.stream(ConfigSingleton.getProperty("authorized_ips").split(",", -1)).map(String::trim).filter(x -> !x.isEmpty()).collect(Collectors.toSet());

		if(!ips.isEmpty() && !ips.contains(req.getRemoteAddr()))
		{
			res.setStatus(404);

			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET CURRENT YEAR                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

		/*------------------------------------------------------------------------------------------------------------*/
		/* WRITE HTML                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/

			String data;

			String level = req.getParameter("Level");

			/*--*/ if("3".equals(level)) {
				data = level3(req, year);
			} else if("2".equals(level)) {
				data = level2(req, year);
			} else {
				data = level1(req, year);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			try(PrintWriter writer = res.getWriter())
			{
				res.setStatus(HttpServletResponse.SC_OK);

				writer.write(data);
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, e.getMessage(), e);

			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static String getConfigPath()
	{
		String result;

		/*------------------------------------------------------------------------------------------------------------*/

		result = System.getProperty("user.home", "/fake1357") + File.separator + ".ami";

		if(!new File(result).exists())
		{
			result = System.getProperty("catalina.base", "/fake1357") + File.separator + "conf";

			if(!new File(result).exists())
			{
				result = "/etc/ami";
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static String getRootPath()
	{
		return System.getProperty("catalina.base", "/fake1357") + File.separator + "webapps" + File.separator + "ROOT";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String level1(@NotNull HttpServletRequest req, @NotNull String year) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
		/* BUILD HTML                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		TextFile.read(stringBuilder, Objects.requireNonNull(Setup.class.getResourceAsStream("/twig/setup_level1.twig")));

		return stringBuilder.toString()
		                    .replace("{{YEAR}}", year)
		                    .replace("{{AMI_CONFIG_PATH}}", getConfigPath())
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String level2(@NotNull HttpServletRequest req, @NotNull String year) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET/POST VARIABLES (SERVER)                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		String base_url = req.getParameter("base_url");
		base_url = (base_url != null) ? base_url.trim()
		                              : ConfigSingleton.getProperty("base_url")
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

		String authorized_ips = req.getParameter("authorized_ips");
		authorized_ips = (authorized_ips != null) ? authorized_ips.trim()
		                                          : ConfigSingleton.getProperty("authorized_ips")
		;

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET/POST VARIABLES (AMI DATABASE)                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		String router_catalog = req.getParameter("router_catalog");
		router_catalog = (router_catalog != null) ? router_catalog.trim()
		                                          : ConfigSingleton.getProperty("router_catalog")
		;

		String router_schema = req.getParameter("router_schema");
		router_schema = (router_schema != null) ? router_schema.trim()
		                                        : ConfigSingleton.getProperty("router_schema", "@NULL")
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

		/*------------------------------------------------------------------------------------------------------------*/

		String time_zone = req.getParameter("time_zone");
		time_zone = (time_zone != null) ? time_zone.trim()
		                                : ConfigSingleton.getProperty("time_zone")
		;

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET/POST VARIABLES (USER EXTENSIONS)                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		String class_path = req.getParameter("class_path");
		class_path = (class_path != null) ? class_path.trim()
		                                  : ConfigSingleton.getProperty("class_path")
		;

		/*------------------------------------------------------------------------------------------------------------*/
		/* BUILD HTML                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		try(InputStream inputStream = Setup.class.getResourceAsStream("/twig/setup_level2.twig"))
		{
			TextFile.read(stringBuilder, Objects.requireNonNull(inputStream));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return stringBuilder.toString()
		                    .replace("{{YEAR}}", year)
		                    /**/
		                    .replace("{{BASE_URL}}", base_url)
		                    .replace("{{ADMIN_USER}}", admin_user)
		                    .replace("{{ADMIN_PASS}}", admin_pass)
		                    .replace("{{ADMIN_EMAIL}}", admin_email)
		                    /**/
		                    .replace("{{ENCRYPTION_KEY}}", encryption_key)
		                    .replace("{{AUTHORIZED_IPS}}", authorized_ips)
		                    /**/
		                    .replace("{{ROUTER_CATALOG}}", router_catalog)
		                    .replace("{{ROUTER_SCHEMA}}", router_schema)
		                    .replace("{{ROUTER_URL}}", router_url)
		                    .replace("{{ROUTER_USER}}", router_user)
		                    .replace("{{ROUTER_PASS}}", router_pass)
		                     /**/
		                    .replace("{{TIME_ZONE}}", time_zone)
		                    /**/
		                    .replace("{{CLASS_PATH}}", class_path)
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String level3(@NotNull HttpServletRequest req, @NotNull  String year) throws Exception
	{
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();
		StringBuilder stringBuilder3 = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET/POST VARIABLES (SERVER)                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		String base_url = req.getParameter("base_url");
		base_url = (base_url != null) ? base_url.trim() : "";

		String admin_user = req.getParameter("admin_user");
		admin_user = (admin_user != null) ? admin_user.trim() : "";

		String admin_pass = req.getParameter("admin_pass");
		admin_pass = (admin_pass != null) ? admin_pass.trim() : "";

		String admin_email = req.getParameter("admin_email");
		admin_email = (admin_email != null) ? admin_email.trim() : "";

		String encryption_key = req.getParameter("encryption_key");
		encryption_key = (encryption_key != null) ? encryption_key.trim() : "";

		String authorized_ips = req.getParameter("authorized_ips");
		authorized_ips = (authorized_ips != null) ? authorized_ips.trim() : "";

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET/POST VARIABLES (AMI DATABASE)                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		String time_zone = req.getParameter("time_zone");
		time_zone = (time_zone != null) ? time_zone.trim() : "";

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET/POST VARIABLES (USER EXTENSIONS)                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		String class_path = req.getParameter("class_path");
		class_path = (class_path != null) ? class_path.trim()
		                                  : ConfigSingleton.getProperty("class_path")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		while(base_url.endsWith("/"))
		{
			base_url = base_url.substring(0, base_url.length() - 1);
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* BUILD CONFIG FILE                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		stringBuilder1.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
		              .append("\n")
		              .append("<properties>\n")
		              .append("  <property name=\"base_url\"><![CDATA[").append(base_url).append("]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"admin_user\"><![CDATA[").append(admin_user).append("]]></property>\n")
		              .append("  <property name=\"admin_pass\"><![CDATA[").append(admin_pass).append("]]></property>\n")
		              .append("  <property name=\"admin_email\"><![CDATA[").append(admin_email).append("]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"encryption_key\"><![CDATA[").append(encryption_key).append("]]></property>\n")
		              .append("  <property name=\"authorized_ips\"><![CDATA[").append(authorized_ips).append("]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"router_catalog\"><![CDATA[").append(router_catalog).append("]]></property>\n")
		              .append("  <property name=\"router_schema\"><![CDATA[").append(router_schema).append("]]></property>\n")
		              .append("  <property name=\"router_url\"><![CDATA[").append(router_url).append("]]></property>\n")
		              .append("  <property name=\"router_user\"><![CDATA[").append(router_user).append("]]></property>\n")
		              .append("  <property name=\"router_pass\"><![CDATA[").append(router_pass).append("]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"time_zone\"><![CDATA[").append(time_zone).append("]]></property>\n")
		              .append("\n")
		              .append("  <property name=\"class_path\"><![CDATA[").append(class_path).append("]]></property>\n")
		              .append("</properties>\n")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* PATCH ROOT SERVLET                                                                                     */
			/*--------------------------------------------------------------------------------------------------------*/

			try(InputStream inputStream = new FileInputStream(getRootPath() + File.separator + "index.html"))
			{
				TextFile.read(stringBuilder2, inputStream);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			String stringContent2 = stringBuilder2.toString().replaceAll("endpoint_url\\s*:\\s*['\"][^'\"]*['\"]", "endpoint_url: '" + base_url + "/AMI/FrontEnd'");

			/*--------------------------------------------------------------------------------------------------------*/

			try(OutputStream outputStream = new FileOutputStream(getRootPath() + File.separator + "index.html"))
			{
				TextFile.write(outputStream, stringContent2);
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			log.info(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* SETUP SERVER CONFIG                                                                                    */
			/*--------------------------------------------------------------------------------------------------------*/

			RouterQuerier db = new RouterQuerier("self", router_catalog, router_url, router_user, router_pass, "UTC");

			try
			{
				/*----------------------------------------------------------------------------------------------------*/

				try(OutputStream outputStream = new FileOutputStream(getConfigPath() + File.separator + "AMI.xml"))
				{
					TextFile.write(outputStream, stringBuilder1);
				}

				ConfigSingleton.reload();

				/*----------------------------------------------------------------------------------------------------*/

				if("on".equals(req.getParameter("router_reset")))
				{
					db.create();

					db.fill(router_schema);
				}

				/*----------------------------------------------------------------------------------------------------*/

				db.commitAndRelease();
			}
			catch(Exception e)
			{
				db.rollbackAndRelease();

				throw e;
			}

			/*--------------------------------------------------------------------------------------------------------*/
			/* LOAD SERVER CONFIG                                                                                     */
			/*--------------------------------------------------------------------------------------------------------*/

			RouterQuerier.reload(true);

			/*--------------------------------------------------------------------------------------------------------*/
			/* BUILD HTML                                                                                             */
			/*--------------------------------------------------------------------------------------------------------*/

			try(InputStream inputStream = Setup.class.getResourceAsStream("/twig/setup_level3_success.twig"))
			{
				TextFile.read(stringBuilder3, Objects.requireNonNull(inputStream));
			}

			/*--------------------------------------------------------------------------------------------------------*/

			return stringBuilder3.toString()
			                     .replace("{{YEAR}}", year)
			                     /**/
			                     .replace("{{BASE_URL}}", base_url)
			                     /**/
			                     .replace("{{ADMIN_USER}}", admin_user)
			                     .replace("{{ADMIN_PASS}}", admin_pass)
			                     .replace("{{ADMIN_EMAIL}}", admin_email)
			                     /**/
			                     .replace("{{ENCRYPTION_KEY}}", encryption_key)
			                     .replace("{{AUTHORIZED_IPS}}", authorized_ips)
			                     /**/
			                     .replace("{{ROUTER_CATALOG}}", router_catalog)
			                     .replace("{{ROUTER_SCHEMA}}", router_schema)
			                     .replace("{{ROUTER_URL}}", router_url)
			                     .replace("{{ROUTER_USER}}", router_user)
			                     .replace("{{ROUTER_PASS}}", router_pass)
			                     /**/
			                     .replace("{{TIME_ZONE}}", time_zone)
			                     /**/
			                     .replace("{{CLASS_PATH}}", class_path)
			;

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* LOG ERROR                                                                                              */
			/*--------------------------------------------------------------------------------------------------------*/

			log.error(e.getMessage(), e);

			/*--------------------------------------------------------------------------------------------------------*/
			/* BUILD HTML                                                                                             */
			/*--------------------------------------------------------------------------------------------------------*/

			try(InputStream inputStream = Setup.class.getResourceAsStream("/twig/setup_level3_error.twig"))
			{
				TextFile.read(stringBuilder3, Objects.requireNonNull(inputStream));
			}

			/*--------------------------------------------------------------------------------------------------------*/

			return stringBuilder3.toString()
			                     .replace("{{YEAR}}", year)
			                     /**/
			                     .replace("{{BASE_URL}}", base_url)
			                     /**/
			                     .replace("{{ADMIN_USER}}", admin_user)
			                     .replace("{{ADMIN_PASS}}", admin_pass)
			                     .replace("{{ADMIN_EMAIL}}", admin_email)
			                     /**/
			                     .replace("{{ENCRYPTION_KEY}}", encryption_key)
			                     .replace("{{AUTHORIZED_IPS}}", authorized_ips)
			                     /**/
			                     .replace("{{ROUTER_CATALOG}}", router_catalog)
			                     .replace("{{ROUTER_SCHEMA}}", router_schema)
			                     .replace("{{ROUTER_URL}}", router_url)
			                     .replace("{{ROUTER_USER}}", router_user)
			                     .replace("{{ROUTER_PASS}}", router_pass)
			                     /**/
			                     .replace("{{TIME_ZONE}}", time_zone)
			                     /**/
			                     .replace("{{CLASS_PATH}}", class_path)
			                     /**/
			                     .replace("{{MESSAGE}}", e.getMessage())
			;

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
