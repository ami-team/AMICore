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

	private static final Map<String, String> s_properties = new ConcurrentHashMap<>();

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

		s_configPathName = "";
		s_configFileName = "";

		try
		{
			s_hasValidConfFile = false;
			loadConfFile();
			s_hasValidConfFile = true;

			SecuritySingleton.init(s_properties.get("encryption_key"));
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.fatal(e);
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
		       getProperty("jdbc_url").isEmpty() == false
		       &&
		       getProperty("router_user").isEmpty() == false
		       &&
		       getProperty("router_pass").isEmpty() == false
		;
	}

	/*---------------------------------------------------------------------*/

	private static boolean isReserved(String name)
	{
		return name.equals("host")
		       ||
		       name.equals("agent")
		       ||
		       name.equals("admin_user")
		       ||
		       name.equals("guest_user")
		       ||
		       name.equals("encryption_key")
		       ||
		       name.equals("jdbc_url")
		       ||
		       name.equals("router_user")
		       ||
		       name.equals("router_pass")
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
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = new FileInputStream(file);

		/*-----------------------------------------------------------------*/
		/* PARSE CONFIG FILE                                               */
		/*-----------------------------------------------------------------*/

		Document document = XMLFactories.newDocument(inputStream);

		/*-----------------------------------------------------------------*/
		/* READ CONFIG FILE                                                */
		/*-----------------------------------------------------------------*/

		NodeList nodeList = document.getElementsByTagName("property");

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF PROPERTIES                                        */
		/*-----------------------------------------------------------------*/

		final int numberOfNodes = nodeList.getLength();

		/*-----------------------------------------------------------------*/
		/* ADD PROPERTIES                                                  */
		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/
		/* CHECK CONFIG                                                    */
		/*-----------------------------------------------------------------*/

		if(isValid() == false)
		{
			throw new Exception("invalid configuration file");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void readDataBase() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
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
	}

	/*---------------------------------------------------------------------*/

	public static void setPropertyToDataBase(String name, String value) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			driver.executeUpdate("INSERT INTO `router_config` (`paramName`, `paramValue`) VALUES ('" + SecuritySingleton.encrypt(name) + "', '" + SecuritySingleton.encrypt(value) + "')");

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

		DriverAbstractClass driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			driver.executeUpdate("DELETE FROM `router_config` WHERE `paramName` = '" + SecuritySingleton.encrypt(name) + "'");

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

			result = tmpValue.equals("1")
			         ||
			         tmpValue.equals("on")
			         ||
			         tmpValue.equals("yes")
			         ||
			         tmpValue.equals("true")
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

			result = tmpValue.equals("1")
			         ||
			         tmpValue.equals("on")
			         ||
			         tmpValue.equals("yes")
			         ||
			         tmpValue.equals("true")
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

		result.append("<rowset type=\"paths\"><row>")
		      .append("<field name=\"configPathName\"><![CDATA[").append(s_configPathName).append("]]></field>")
		      .append("<field name=\"configFileName\"><![CDATA[").append(s_configFileName).append("]]></field>")
		      .append("</row></rowset>")
		;

		/*-----------------------------------------------------------------*/

		String name;
		String value;

		result.append("<rowset type=\"config\"><row>");

		for(Map.Entry<String, String> entry: s_properties.entrySet())
		{
			name = entry.getKey();
			value = entry.getValue();

			result.append("<field name=\"").append(name).append("\"><![CDATA[").append(value).append("]]></field>");
		}

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
