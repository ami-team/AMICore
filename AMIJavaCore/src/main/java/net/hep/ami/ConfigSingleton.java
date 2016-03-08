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

	private static boolean m_hasValidConfFile;
	private static boolean m_hasValidDataBase;

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> m_properties = new HashMap<String, String>();

	/*---------------------------------------------------------------------*/

	static
	{
		m_hasValidConfFile = false;
		m_hasValidDataBase = false;

		try
		{
			/*-------------------------------------------------------------*/
			/* READ FROM CONFFILE                                          */
			/*-------------------------------------------------------------*/

			readFromConfFile(m_properties);
			m_hasValidConfFile = true;

			/*-------------------------------------------------------------*/
			/* READ FROM DATABASE                                          */
			/*-------------------------------------------------------------*/

			readFromDataBase(m_properties);
			m_hasValidDataBase = true;

			/*-------------------------------------------------------------*/
			/* CHECK IF VALID                                              */
			/*-------------------------------------------------------------*/

			if(isValid())
			{
				Cryptography.init(getProperty("encryption_key"));
			}
			else
			{
				m_hasValidConfFile = false;
				m_hasValidDataBase = false;
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.error(e.getMessage());
		}

		/*-----------------------------------------------------------------*/
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
		return new File(
			configPathName.endsWith(".xml") == false ? configPathName + File.separator + "AMI.xml"
			                                         : configPathName
		);
	}

	/*---------------------------------------------------------------------*/

	private static void readFromConfFile(Map<String, String> properties) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* FIND CONFFILE                                                   */
		/*-----------------------------------------------------------------*/

		File file;
		String path;

		path = System.getProperty("ami.conffile");

		if(path == null
		   ||
		   (file = _toFile(path.trim())).exists() == false
		 ) {

			path = System.getProperty("catalina.base");

			if(path == null
			   ||
			   (file = _toFile(path.trim() + File.separator + "conf")).exists() == false
			 ) {

				path = System.getProperty((("user.home")));

				if(path == null
				   ||
				   (file = _toFile(path.trim() + File.separator + ".ami")).exists() == false
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

			properties.put(
				XMLFactories.getAttribute(node, "name")
				,
				XMLFactories.getContent(node)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readFromDataBase(Map<String, String> properties) throws Exception
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

			RowSet rowSet = basicQuerier.executeSQLQuery("SELECT `paramName`,`paramValue` FROM `router_config`");

			/*-------------------------------------------------------------*/
			/* ADD PROPERTIES                                              */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet)
			{
				try
				{
					properties.put(
						row.getValue("paramName"),
						row.getValue("paramValue")
					);
			//		m_properties.put(
			//			Cryptography.decrypt(queryResult.getValue(i, "paramName"))
			//			,
			//			Cryptography.decrypt(queryResult.getValue(i, "paramValue"))
			//		);
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
		/* EXECUTE QUERIES                                                 */
		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/

			basicQuerier.executeSQLUpdate("DELETE FROM `router_config`");

			/*-------------------------------------------------------------*/

			PreparedStatement preparedStatement = basicQuerier.sqlPrepareStatement("INSERT INTO `router_config` VALUES (?, ?)");

			/*-------------------------------------------------------------*/

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

			/*-------------------------------------------------------------*/

			preparedStatement.executeBatch();

			/*-------------------------------------------------------------*/
		}
		finally
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

		/*-----------------------------------------------------------------*/
		/* COMMIT AND RELEASE                                              */
		/*-----------------------------------------------------------------*/

		basicQuerier.commitAndRelease();

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

	public static boolean hasValidConfFile()
	{
		return m_hasValidConfFile;
	}

	/*---------------------------------------------------------------------*/

	public static boolean hasValidDataBase()
	{
		return m_hasValidDataBase;
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
			"<rowset type=\"status\">"
			+
			"<row>"
			+
			"<field name=\"configPathName\"><![CDATA[" + m_configPathName + "]]></field>"
			+
			"<field name=\"configFileName\"><![CDATA[" + m_configFileName + "]]></field>"
			+
			"<field name=\"hasValidConfFile\"><![CDATA[" + m_hasValidConfFile + "]]></field>"
			+
			"<field name=\"hasValidDataBase\"><![CDATA[" + m_hasValidDataBase + "]]></field>"
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
