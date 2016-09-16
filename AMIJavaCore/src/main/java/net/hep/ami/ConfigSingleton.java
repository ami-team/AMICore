package net.hep.ami;

import java.io.*;
import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.w3c.dom.*;

public class ConfigSingleton
{
	/*---------------------------------------------------------------------*/

	private static String m_configPathName;
	private static String m_configFileName;

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> m_properties = new java.util.concurrent.ConcurrentHashMap<String, String>();

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		m_configPathName = "";
		m_configFileName = "";

		m_properties.clear();

		try
		{
			readFromConfFile();
			Cryptography.init(getProperty("encryption_key"));
			readFromDataBase();
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.fatal(e.getMessage());
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

	private static File _toFile(String configPathName)
	{
		/*-----------------------------------------------------------------*/

		if(configPathName.endsWith(".xml") == false)
		{
			configPathName = configPathName + File.separator + "AMI.xml";
		}

		/*-----------------------------------------------------------------*/

		return new File(configPathName);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readFromConfFile() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* FIND FILE                                                       */
		/*-----------------------------------------------------------------*/

		File file;
		String path;

		path = System.getProperty("ami.conffile");

		if(path == null
		   ||
		   (file = _toFile(path)).exists() == false
		 ) {

			path = System.getProperty("catalina.base");

			if(path == null
			   ||
			   (file = _toFile(path + File.separator + "conf")).exists() == false
			 ) {

				path = System.getProperty((("user.home")));

				if(path == null
				   ||
				   (file = _toFile(path + File.separator + ".ami")).exists() == false
				 ) {
					/*----------------------------*/
					/* DEFAULT FOR DEBs/RPMs      */
					/*----------------------------*/

					file = _toFile("/etc/ami");

					/*----------------------------*/
				}
			}
		}

		/*-----------------------------------------------------------------*/

		m_configPathName = file.getParentFile().getPath();
		m_configFileName = file        .        getPath();

		/*-----------------------------------------------------------------*/
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = new FileInputStream(file);

		/*-----------------------------------------------------------------*/
		/* PARSE FILE                                                      */
		/*-----------------------------------------------------------------*/

		Document document = XMLFactories.newDocument(inputStream);

		/*-----------------------------------------------------------------*/
		/* READ FILE                                                       */
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

			m_properties.put(
				XMLFactories.getAttribute(node, "name"),
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

	private static void readFromDataBase() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier(
			getProperty("jdbc_url"),
			getProperty("router_user"),
			getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = basicQuerier.executeQuery("SELECT `paramName`, `paramValue` FROM `router_config`");

			/*-------------------------------------------------------------*/
			/* ADD PROPERTIES                                              */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet.iter())
			{
				try
				{
					m_properties.put(
						row.getValue("paramName"),
						row.getValue("paramValue")
					);
/*
					m_properties.put(
						Cryptography.decrypt(row.getValue("paramName"))
						,
						Cryptography.decrypt(row.getValue("paramValue"))
					);
*/
				}
				catch(org.bouncycastle.util.encoders.DecoderException e)
				{
					LogSingleton.defaultLogger.error(e.getMessage());
				}
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void writeToDataBase(Map<String, String> properties) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier(
			getProperty("jdbc_url"),
			getProperty("router_user"),
			getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		boolean success = false;

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY 1                                             */
			/*-------------------------------------------------------------*/

			basicQuerier.executeUpdate("DELETE FROM `router_config`");

			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY 2                                             */
			/*-------------------------------------------------------------*/

			PreparedStatement preparedStatement = basicQuerier.sqlPrepareStatement("INSERT INTO `router_config` VALUES (?, ?)");

			/*-------------------------------------------------------------*/

			try
			{
				String name;
				String value;

				for(Map.Entry<String, String> entry: properties.entrySet())
				{
					name = entry.getKey();
					value = entry.getValue();

					if(isReserved(name) == false)
					{
						preparedStatement.setString(1, Cryptography.encrypt(name));
						preparedStatement.setString(2, Cryptography.encrypt(value));
						preparedStatement.addBatch();
					}
				}

				preparedStatement.executeBatch();
			}
			finally
			{
				preparedStatement.close();
			}

			/*-------------------------------------------------------------*/
			/* SUCCESS                                                     */
			/*-------------------------------------------------------------*/

			success = true;

			/*-------------------------------------------------------------*/
		}
		finally
		{
			if(success)
			{
				basicQuerier.commitAndRelease();
			}
			else
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
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String getConfigPathName()
	{
		return m_configPathName;
	}

	/*---------------------------------------------------------------------*/

	public static String getConfigFileName()
	{
		return m_configFileName;
	}

	/*---------------------------------------------------------------------*/
	/* SYSTEM                                                              */
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

	public static String getProperty(String key)
	{
		String result = m_properties.get(key);

		return result != null ? result : "";
	}

	/*---------------------------------------------------------------------*/

	public static String getProperty(String key, String defaultValue)
	{
		String result = m_properties.get(key);

		return result != null ? result : defaultValue;
	}

	/*---------------------------------------------------------------------*/

	public static boolean getProperty(String key, boolean defaultValue)
	{
		boolean result;

		String tmpValue = m_properties.get(key);

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

		String tmpValue = m_properties.get(key);

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

		String tmpValue = m_properties.get(key);

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

		String tmpValue = m_properties.get(key);

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

		result.append(
			"<rowset type=\"paths\">"
			+
			"<row>"
			+
			"<field name=\"configPathName\"><![CDATA[" + m_configPathName + "]]></field>"
			+
			"<field name=\"configFileName\"><![CDATA[" + m_configFileName + "]]></field>"
			+
			"</row>"
			+
			"</rowset>"
		);

		/*-----------------------------------------------------------------*/

		String name;
		String value;

		result.append("<rowset type=\"config\"><row>");

		for(Map.Entry<String, String> entry: m_properties.entrySet())
		{
			name = entry.getKey();
			value = entry.getValue();

			result.append("<field name=\"" + name + "\"><![CDATA[" + value + "]]></field>");
		}

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
