package net.hep.ami;

import java.io.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

public class ConfigSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_properties = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

	/*---------------------------------------------------------------------*/

	private static final Set<String> s_reserved = new HashSet<>();

	/*---------------------------------------------------------------------*/

	private static String s_configPathName;
	private static String s_configFileName;

	/*---------------------------------------------------------------------*/

	private static boolean s_isValidConfFile;

	/*---------------------------------------------------------------------*/

	private ConfigSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		s_reserved.add("host");
		s_reserved.add("admin_user");
		s_reserved.add("admin_pass");
		s_reserved.add("admin_email");
		s_reserved.add("guest_user");
		s_reserved.add("guest_pass");
		s_reserved.add("encryption_key");
		s_reserved.add("router_catalog");
		s_reserved.add("router_schema");
		s_reserved.add("router_url");
		s_reserved.add("router_user");
		s_reserved.add("router_pass");

		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		s_properties.clear();

		try
		{
			s_isValidConfFile = false;
			loadConfFile();
			s_isValidConfFile = true;

			SecuritySingleton.init(s_properties.get("encryption_key"));
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not read configuration", e);
		}
	}

	/*---------------------------------------------------------------------*/

	private static boolean isValid()
	{
		return getProperty("host").isEmpty() == false
		       &&
		       getProperty("admin_user").isEmpty() == false
		       &&
		       getProperty("admin_pass").isEmpty() == false
		       &&
		       getProperty("admin_email").isEmpty() == false
		       &&
		       getProperty("encryption_key").isEmpty() == false
		       &&
		       getProperty("router_catalog").isEmpty() == false
		       &&
		       getProperty("router_url").isEmpty() == false
		       &&
		       getProperty("router_user").isEmpty() == false
		       &&
		       getProperty("router_pass").isEmpty() == false
		;
	}

	/*---------------------------------------------------------------------*/

	private static File toFile(String configPathName)
	{
		/*-----------------------------------------------------------------*/

		if(configPathName.toLowerCase().endsWith(".xml") == false)
		{
			configPathName = configPathName + File.separator + "AMI.xml";
		}

		/*-----------------------------------------------------------------*/

		return new File(configPathName);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void loadConfFile() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* FIND CONFIG FILE                                                */
		/*-----------------------------------------------------------------*/

		File file;
		String path;

		path = System.getProperty("ami.conffile");

		if(path == null
		   ||
		   (file = toFile(path)).exists() == false
		 ) {

			path = System.getProperty("catalina.base");

			if(path == null
			   ||
			   (file = toFile(path + File.separator + "conf")).exists() == false
			 ) {

				path = System.getProperty((("user.home")));

				if(path == null
				   ||
				   (file = toFile(path + File.separator + ".ami")).exists() == false
				 ) {
					/*----------------------------*/
					/* DEFAULT FOR DEBs/RPMs      */
					/*----------------------------*/

					file = toFile("/etc/ami");

					/*----------------------------*/
				}
			}
		}

		/*-----------------------------------------------------------------*/

		s_configPathName = file.getParentFile().getPath();
		s_configFileName = file        .        getPath();

		/*-----------------------------------------------------------------*/

		try(InputStream inputStream = new FileInputStream(file))
		{
			/*-------------------------------------------------------------*/
			/* PARSE CONFIG FILE                                           */
			/*-------------------------------------------------------------*/

			org.w3c.dom.Document document = XMLFactory.newDocument(inputStream);

			/*-------------------------------------------------------------*/
			/* READ CONFIG FILE                                            */
			/*-------------------------------------------------------------*/

			org.w3c.dom.NodeList nodeList = document.getElementsByTagName("property");

			/*-------------------------------------------------------------*/
			/* ADD PROPERTIES                                              */
			/*-------------------------------------------------------------*/

			for(org.w3c.dom.Node node: XMLFactory.toIterable(nodeList))
			{
				s_properties.put(
					XMLFactory.getAttribute(node,
					                         "name"),
					XMLFactory.getContent(node)
				);
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/* GUEST USER                                                      */
		/*-----------------------------------------------------------------*/

		s_properties.put("guest_user", "guest");
		s_properties.put("guest_pass", "guest");

		s_properties.put("sso_user", "sso");
		s_properties.put("sso_pass", "sso");

		/*-----------------------------------------------------------------*/
		/* CHECK CONFIG                                                    */
		/*-----------------------------------------------------------------*/

		if(isValid() == false)
		{
			throw new Exception("invalid configuration file");
		}

		/*-----------------------------------------------------------------*/
		/* RESET LOGGERS                                                   */
		/*-----------------------------------------------------------------*/

		LogSingleton.reset("WARN");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void readDataBase() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = DriverSingleton.getConnection(
			"self",
			getProperty("router_catalog"),
			getProperty("router_url"),
			getProperty("router_user"),
			getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = driver.executeSQLQuery("SELECT `paramName`, `paramValue` FROM `router_config`");

			/*-------------------------------------------------------------*/
			/* ADD PROPERTIES                                              */
			/*-------------------------------------------------------------*/

			String name;
			String value;

			for(Row row: rowSet.iterate())
			{
				name = row.getValue(0);
				value = row.getValue(1);

				if(s_reserved.contains(name) == false)
				{
					s_properties.put(name, value);
				}
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* RESET LOGGERS                                                   */
		/*-----------------------------------------------------------------*/

		LogSingleton.reset("WARN");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static int setPropertyInDataBase(Querier querier, String name, String value, String user) throws Exception
	{
		int result;

		name = SecuritySingleton.encrypt(name);
		value = SecuritySingleton.encrypt(value);

		/*------------------------------------------------------------------*/

		List<Row> rows = querier.executeSQLQuery("SELECT `paramValue` FROM `router_config` WHERE `paramName` = ?", name).getAll();

		/*------------------------------------------------------------------*/

		if(rows.size() == 0)
		{
			result = querier.executeSQLUpdate("INSERT INTO `router_config` (`paramName`, `paramValue`, `createdBy`, `modifiedBy`) VALUES (?, ?, ?, ?)", name, value, user, user).getNbOfUpdatedRows();
		}
		else
		{
			if(rows.get(0).getValue(0).equals(value) == false)
			{
				result = querier.executeSQLUpdate("UPDATE `router_config` SET `paramValue` = ?, `modified` = CURRENT_TIMESTAMP, `modifiedBy` = ? WHERE `paramName` = ?", value, user, name).getNbOfUpdatedRows();
			}
			else
			{
				result = 0x0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000;
			}
		}

		/*------------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static int removePropertyInDataBase(Querier querier, String name) throws Exception
	{
		Update update = querier.executeSQLUpdate("DELETE FROM `router_config` WHERE `paramName` = ?",
			SecuritySingleton.encrypt(name)
		);

		return update.getNbOfUpdatedRows();
	}

	/*---------------------------------------------------------------------*/

	public static String getConfigPathName()
	{
		return s_configPathName;
	}

	/*---------------------------------------------------------------------*/

	public static String getConfigFileName()
	{
		return s_configFileName;
	}

	/*---------------------------------------------------------------------*/

	public static boolean isValidConfFile()
	{
		return s_isValidConfFile;
	}

	/*---------------------------------------------------------------------*/
	/* SYSTEM                                                              */
	/*---------------------------------------------------------------------*/

	public static String setSystemProperty(String key, String value)
	{
		return System.setProperty(key, value);
	}

	/*---------------------------------------------------------------------*/

	public static String removeSystemProperty(String key)
	{
		return System.clearProperty(key);
	}

	/*---------------------------------------------------------------------*/

	public static String getSystemProperty(String key)
	{
		String result = System.getProperty(key);

		return result != null ? result : "";
	}

	/*---------------------------------------------------------------------*/

	public static String getSystemProperty(String key, String defaultValue)
	{
		String result = System.getProperty(key);

		return result != null ? result : defaultValue;
	}
	/*---------------------------------------------------------------------*/

	public static boolean getSystemProperty(String key, boolean defaultValue)
	{
		boolean result;

		String tmpValue = System.getProperty(key);

		if(tmpValue != null)
		{
			tmpValue = tmpValue.trim().toLowerCase();

			result = "1".equals(tmpValue)
			         ||
			         "on".equals(tmpValue)
			         ||
			         "yes".equals(tmpValue)
			         ||
			         "true".equals(tmpValue)
			;
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static int getSystemProperty(String key, int defaultValue)
	{
		int result;

		String tmpValue = System.getProperty(key);

		if(tmpValue != null)
		{
			try
			{
				result = Integer.parseInt(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static float getSystemProperty(String key, float defaultValue)
	{
		float result;

		String tmpValue = System.getProperty(key);

		if(tmpValue != null)
		{
			try
			{
				result = Float.parseFloat(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static double getSystemProperty(String key, double defaultValue)
	{
		double result;

		String tmpValue = System.getProperty(key);

		if(tmpValue != null)
		{
			try
			{
				result = Double.parseDouble(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
	/* AMI                                                                 */
	/*---------------------------------------------------------------------*/

	public static String setProperty(String key, String value)
	{
		return s_properties.put(key, value);
	}

	/*---------------------------------------------------------------------*/

	public static String removeProperty(String key)
	{
		return s_properties.remove(key);
	}

	/*---------------------------------------------------------------------*/

	public static String getProperty(String key)
	{
		String result = s_properties.get(key);

		return result != null ? result : "";
	}

	/*---------------------------------------------------------------------*/

	public static String getProperty(String key, String defaultValue)
	{
		String result = s_properties.get(key);

		return result != null ? result : defaultValue;
	}

	/*---------------------------------------------------------------------*/

	public static boolean getProperty(String key, boolean defaultValue)
	{
		boolean result;

		String tmpValue = s_properties.get(key);

		if(tmpValue != null)
		{
			tmpValue = tmpValue.trim().toLowerCase();

			result = "1".equals(tmpValue)
			         ||
			         "on".equals(tmpValue)
			         ||
			         "yes".equals(tmpValue)
			         ||
			         "true".equals(tmpValue)
			;
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static int getProperty(String key, int defaultValue)
	{
		int result;

		String tmpValue = s_properties.get(key);

		if(tmpValue != null)
		{
			try
			{
				result = Integer.parseInt(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static float getProperty(String key, float defaultValue)
	{
		float result;

		String tmpValue = s_properties.get(key);

		if(tmpValue != null)
		{
			try
			{
				result = Float.parseFloat(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static double getProperty(String key, double defaultValue)
	{
		double result;

		String tmpValue = s_properties.get(key);

		if(tmpValue != null)
		{
			try
			{
				result = Double.parseDouble(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder showConfig()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"paths\">")
		      .append("<row>")
		      .append("<field name=\"configPathName\"><![CDATA[").append(s_configPathName).append("]]></field>")
		      .append("<field name=\"configFileName\"><![CDATA[").append(s_configFileName).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"config\">")
		      .append("<row>")
		;

		for(Map.Entry<String, String> entry: s_properties.entrySet())
		{
			result.append("<field name=\"").append(entry.getKey()).append("\"><![CDATA[").append(entry.getValue()).append("]]></field>");
		}

		result.append("</row>")
		      .append("</rowset>")
		;

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
