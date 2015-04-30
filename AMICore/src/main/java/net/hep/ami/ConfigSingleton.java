package net.hep.ami;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.w3c.dom.*;

public class ConfigSingleton {
	/*---------------------------------------------------------------------*/

	private static String m_configPathName;
	private static String m_configFileName;

	/*---------------------------------------------------------------------*/

	private static boolean m_hasValidConfFile;
	private static boolean m_hasValidDataBase;

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> m_properties = new HashMap<String, String>();

	/*---------------------------------------------------------------------*/

	static {

		m_hasValidConfFile = false;
		m_hasValidDataBase = false;

		try {
			/*-------------------------------------------------------------*/
			/* READ FROM CONFFILE                                          */
			/*-------------------------------------------------------------*/

			readFromConfFile();
			m_hasValidConfFile = true;

			/*-------------------------------------------------------------*/
			/* READ FROM DATABASE                                          */
			/*-------------------------------------------------------------*/

			readFromDataBase();
			m_hasValidDataBase = true;

			/*-------------------------------------------------------------*/
			/* CHECK IF VALID                                              */
			/*-------------------------------------------------------------*/

			if(getProperty("host").isEmpty()
			   ||
			   getProperty("agent").isEmpty()
			   ||
			   getProperty("admin_user").isEmpty()
			   ||
			   getProperty("guest_user").isEmpty()
			   ||
			   getProperty("encryption_key").isEmpty()
			   ||
			   getProperty("jdbc_url").isEmpty()
			   ||
			   getProperty("router_user").isEmpty()
			 ) {
				m_hasValidConfFile = false;
				m_hasValidDataBase = false;

			} else {
				Cryptography.init(getProperty("encryption_key"));
			}

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static File _toFile(String configFileName) {
		/*-----------------------------------------------------------------*/
		/* CHECK FILENAME                                                  */
		/*-----------------------------------------------------------------*/

		if(configFileName.endsWith(".xml") == false)
		{
			configFileName = configFileName.concat(File.separator + "AMI.xml");
		}

		/*-----------------------------------------------------------------*/
		/* CREATE FILE                                                     */
		/*-----------------------------------------------------------------*/

		return new File(configFileName);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readFromConfFile() throws Exception {

		String path;

		/*-----------------------------------------------------------------*/
		/* FIND CONFFILE                                                   */
		/*-----------------------------------------------------------------*/

		File file;

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

				path = System.getProperty("user.home");
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

		for(int i = 0; i < numberOfNodes; i++) {

			Node node = nodeList.item(i);

			m_properties.put(
				XMLFactories.getAttribute(node, "name", "")
				,
				XMLFactories.getContent(node)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readFromDataBase() throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier(
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		QueryResult queryResult;

		try {
			queryResult = basicQuerier.executeSQLQuery("SELECT `name`,`value` FROM `router_config`");

		} finally {
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF PROPERTIES                                        */
		/*-----------------------------------------------------------------*/

		final int numberOfRows = queryResult.getNumberOfRows();

		/*-----------------------------------------------------------------*/
		/* ADD PROPERTIES                                                  */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < numberOfRows; i++) {

			m_properties.put(
				queryResult.getValue(i, "name").trim()
				,
				queryResult.getValue(i, "value").trim()
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String getConfigPathName() {

		return m_configPathName;
	}

	/*---------------------------------------------------------------------*/

	public static String getConfigFileName() {

		return m_configFileName;
	}

	/*---------------------------------------------------------------------*/

	public static Boolean hasValidConfFile() {

		return m_hasValidConfFile;
	}

	/*---------------------------------------------------------------------*/

	public static Boolean hasValidDataBase() {

		return m_hasValidDataBase;
	}

	/*---------------------------------------------------------------------*/

	public static String setProperty(String key, String value) {

		String result;

		synchronized(ConfigSingleton.class) {
			result = m_properties.put(key, value);
		}

		return result != null ? result : "";
	}

	/*---------------------------------------------------------------------*/

	public static String getProperty(String key) {

		String result;

		synchronized(ConfigSingleton.class) {
			result = m_properties.get(key);
		}

		return result != null ? result : "";
	}

	/*---------------------------------------------------------------------*/

	public static String getProperty(String key, String defaultValue) {

		String result;

		synchronized(ConfigSingleton.class) {
			result = m_properties.get(key);
		}

		return result != null ? result : defaultValue;
	}

	/*---------------------------------------------------------------------*/

	public static int getProperty(String key, int defaultValue) {

		int result;
		String tmpValue;

		synchronized(ConfigSingleton.class) {
			tmpValue = m_properties.get(key);
		}

		if(tmpValue != null) {

			try {
				result = Integer.parseInt(tmpValue);
			} catch(NumberFormatException e) {
				result = defaultValue;
			}

		} else {
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static float getProperty(String key, float defaultValue) {

		float result;
		String tmpValue;

		synchronized(ConfigSingleton.class) {
			tmpValue = m_properties.get(key);
		}

		if(tmpValue != null) {

			try {
				result = Float.parseFloat(tmpValue);
			} catch(NumberFormatException e) {
				result = defaultValue;
			}

		} else {
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static double getProperty(String key, double defaultValue) {

		double result;
		String tmpValue;

		synchronized(ConfigSingleton.class) {
			tmpValue = m_properties.get(key);
		}

		if(tmpValue != null) {

			try {
				result = Double.parseDouble(tmpValue);
			} catch(NumberFormatException e) {
				result = defaultValue;
			}

		} else {
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder showConfig() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"status\"><row>");

		result.append("<field name=\"configPathName\"><![CDATA[" + m_configPathName + "]]></field>");
		result.append("<field name=\"configFileName\"><![CDATA[" + m_configFileName + "]]></field>");
		result.append("<field name=\"hasValidConfFile\"><![CDATA[" + m_hasValidConfFile + "]]></field>");
		result.append("<field name=\"hasValidDataBase\"><![CDATA[" + m_hasValidDataBase + "]]></field>");

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		Map<String, String> properties;

		synchronized(ConfigSingleton.class) {
			properties = new HashMap<String, String>(m_properties);
		}

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"config\"><row>");

		for(Entry<String, String> entry: properties.entrySet()) {

			String key = entry.getKey();
			String value = entry.getValue();

			result.append("<field name=\"" + key + "\"><![CDATA[" + value + "]]></field>");
		}

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		result.append("</Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
