package net.hep.ami;

import java.io.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

import org.w3c.dom.*;

public class ConfigSingleton
{
	/*---------------------------------------------------------------------*/

	private static String s_configPathName;
	private static String s_configFileName;

	/*---------------------------------------------------------------------*/

	private static boolean s_hasValidConfFile;

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_properties = new AMIMap<>();

	/*---------------------------------------------------------------------*/

	private ConfigSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		s_properties.clear();

		try
		{
			s_hasValidConfFile = false;
			loadConfFile();
			s_hasValidConfFile = true;

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
		       getProperty("agent").isEmpty() == false
		       &&
		       getProperty("admin_user").isEmpty() == false
		       &&
		       getProperty("guest_user").isEmpty() == false
		       &&
		       getProperty("encryption_key").isEmpty() == false
		       &&
		       getProperty("router").isEmpty() == false
		       &&
		       getProperty("router_url").isEmpty() == false
		       &&
		       getProperty("router_user").isEmpty() == false
		       &&
		       getProperty("router_pass").isEmpty() == false
		;
	}

	/*---------------------------------------------------------------------*/

	private static boolean isReserved(String name)
	{
		return "host".equals(name)
		       ||
		       "agent".equals(name)
		       ||
		       "admin_user".equals(name)
		       ||
		       "guest_user".equals(name)
		       ||
		       "encryption_key".equals(name)
		       ||
		       "router".equals(name)
		       ||
		       "router_url".equals(name)
		       ||
		       "router_user".equals(name)
		       ||
		       "router_pass".equals(name)
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

			Document document = XMLFactories.newDocument(inputStream);

			/*-------------------------------------------------------------*/
			/* READ CONFIG FILE                                            */
			/*-------------------------------------------------------------*/

			NodeList nodeList = document.getElementsByTagName("property");

			/*-------------------------------------------------------------*/
			/* GET NUMBER OF PROPERTIES                                    */
			/*-------------------------------------------------------------*/

			final int numberOfNodes = nodeList.getLength();

			/*-------------------------------------------------------------*/
			/* ADD PROPERTIES                                              */
			/*-------------------------------------------------------------*/

			Node node;

			for(int i = 0; i < numberOfNodes; i++)
			{
				node = nodeList.item(i);

				s_properties.put(
					XMLFactories.getAttribute(node,
					                          "name"),
					XMLFactories.getContent(node)
				);
			}

			/*-------------------------------------------------------------*/
		}

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

		LogSingleton.reset();

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
			getProperty("router"),
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

			RowSet rowSet = driver.executeQuery("SELECT `paramName`, `paramValue` FROM `router_config`");

			/*-------------------------------------------------------------*/
			/* ADD PROPERTIES                                              */
			/*-------------------------------------------------------------*/

			String name;
			String value;

			for(Row row: rowSet.iter())
			{
				name = SecuritySingleton.decrypt(row.getValue(0));
				value = SecuritySingleton.decrypt(row.getValue(1));

				if(isReserved(name) == false)
				{
					s_properties.put(
						name
						,
						value
					);
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

		LogSingleton.reset();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void setPropertyToDataBase(String name, String value) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = DriverSingleton.getConnection(
			"self",
			getProperty("router"),
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

			driver.executeUpdate(String.format("INSERT INTO `router_config` (`paramName`, `paramValue`) VALUES ('%s', '%s') ON DUPLICATE KEY UPDATE `paramName`='%s'",
				SecuritySingleton.encrypt(name).replace("'", "''"),
				SecuritySingleton.encrypt(value).replace("'", "''"),
				SecuritySingleton.encrypt(name).replace("'", "''")
			));

			/*-------------------------------------------------------------*/
		}
		finally
		{
			driver.commitAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void removePropertyFromDataBase(String name) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = DriverSingleton.getConnection(
			"self",
			getProperty("router"),
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

			driver.executeUpdate(String.format("DELETE FROM `router_config` WHERE `paramName` = '%s'",
				SecuritySingleton.encrypt(name).replace("'", "''")
			));

			/*-------------------------------------------------------------*/
		}
		finally
		{
			driver.commitAndRelease();
		}

		/*-----------------------------------------------------------------*/
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

	public static boolean hasValidConfFile()
	{
		return s_hasValidConfFile;
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
