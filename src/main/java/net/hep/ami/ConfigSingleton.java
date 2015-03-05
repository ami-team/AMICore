package net.hep.ami;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.w3c.dom.*;

public class ConfigSingleton {
	/*---------------------------------------------------------------------*/

	private static String m_configFileName = System.getProperty("user.home");

	/*---------------------------------------------------------------------*/

	private static boolean m_hasValidConfFile = false;
	private static boolean m_hasValidDataBase = false;

	/*---------------------------------------------------------------------*/

	private static Map<String, String> m_properties = new HashMap<String, String>();

	/*---------------------------------------------------------------------*/

	static {

		try {
			/*-------------------------------------------------------------*/
			/* READ FROM CONF FILE                                         */
			/*-------------------------------------------------------------*/

			readFromConfFile();
			m_hasValidConfFile = true;

			/*-------------------------------------------------------------*/
			/* READ FROM DATA BASE                                         */
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
			   ||
			   getProperty("router_name").isEmpty()
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

	private static void readFromConfFile() throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET CONFIG FILE NAME                                            */
		/*-----------------------------------------------------------------*/

		String tomcatPath = System.getProperty("catalina.base");
		if(tomcatPath != null) {
			m_configFileName = tomcatPath.trim() + File.separator + "conf";

		} else {

			String customPath = System.getProperty("ami.conf_path");
			if(customPath != null) {
				m_configFileName = customPath.trim() /* pathname or filename */;
			}
		}

		/*-----------------------------------------------------------------*/

		if(m_configFileName.endsWith(".xml") == false) {
			m_configFileName = m_configFileName.concat(File.separator + "AMI.xml");
		}

		/*-----------------------------------------------------------------*/
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = new FileInputStream(m_configFileName);

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

		final int nr = nodeList.getLength();

		/*-----------------------------------------------------------------*/
		/* ADD PROPERTIES                                                  */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < nr; i++) {

			Node node = nodeList.item(i);

			m_properties.put(
				XMLFactories.getAttribute(node, "name")
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

		BasicLoader basicLoader = null;
		QueryResult queryResult = null;

		try {
			basicLoader = new BasicLoader(
				ConfigSingleton.getProperty("jdbc_url"),
				ConfigSingleton.getProperty("router_user"),
				ConfigSingleton.getProperty("router_pass"),
				ConfigSingleton.getProperty("router_name")
			);

			queryResult = basicLoader.executeQuery("SELECT `name`, `value` FROM `router_config`");

		} finally {

			if(basicLoader != null) {
				basicLoader.rollbackAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF PROPERTIES                                        */
		/*-----------------------------------------------------------------*/

		final int nr = queryResult.getNumberOfRows();

		/*-----------------------------------------------------------------*/
		/* ADD PROPERTIES                                                  */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < nr; i++) {

			m_properties.put(
				queryResult.getValue(i, "name").trim()
				,
				queryResult.getValue(i, "value").trim()
			);
		}

		/*-----------------------------------------------------------------*/
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

	public static String getProperty(String key) {
		return m_properties.containsKey(key) ? m_properties.get(key) : "";
	}

	/*---------------------------------------------------------------------*/

	public static String getProperty(String key, String defaultValue) {
		return m_properties.containsKey(key) ? m_properties.get(key) : defaultValue;
	}

	/*---------------------------------------------------------------------*/

	public static int getProperty(String key, int defaultValue) {

		int result;

		if(m_properties.containsKey(key)) {

			try {
				result = Integer.parseInt(m_properties.get(key));
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

		if(m_properties.containsKey(key)) {

			try {
				result = Float.parseFloat(m_properties.get(key));
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

		if(m_properties.containsKey(key)) {

			try {
				result = Double.parseDouble(m_properties.get(key));
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

		result.append("<rowset type=\"status\">");
		result.append("<row>");

		result.append("<field name=\"validConfFile\"><![CDATA[" + m_hasValidConfFile + "]]></field>");
		result.append("<field name=\"validDataBase\"><![CDATA[" + m_hasValidDataBase + "]]></field>");

		result.append("</row>");
		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"config\">");
		result.append("<row>");

		for(Entry<String, String> entry: m_properties.entrySet()) {

			String key = entry.getKey();
			String value = entry.getValue();

			result.append("<field name=\"" + key + "\"><![CDATA[" + value + "]]></field>");
		}

		result.append("</row>");
		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("</Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
