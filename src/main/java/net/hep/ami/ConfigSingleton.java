package net.hep.ami;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.w3c.dom.*;

public class ConfigSingleton {
	/*---------------------------------------------------------------------*/

	private static boolean m_validConfFile = false;
	private static boolean m_validDataBase = false;

	/*---------------------------------------------------------------------*/

	private static HashMap<String, String> m_properties = new HashMap<String, String>();

	/*---------------------------------------------------------------------*/

	static {

		try {
			/*-------------------------------------------------------------*/
			/* READ FROM CONF FILE                                         */
			/*-------------------------------------------------------------*/

			readFromConfFile();
			m_validConfFile = true;

			/*-------------------------------------------------------------*/
			/* READ FROM DATA BASE                                         */
			/*-------------------------------------------------------------*/

			readFromDataBase();
			m_validDataBase = true;

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
		}

		/*-----------------------------------------------------------------*/
		/* SET ENCRYPTION KEY                                              */
		/*-----------------------------------------------------------------*/

		Cryptography.init(getProperty("encryption_key"));

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readFromConfFile() throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = ConfigSingleton.class.getResourceAsStream("/AMI.xml");

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
				ConfigSingleton.getProperty("router_pass")
			);

			basicLoader.useDB(ConfigSingleton.getProperty("router_name"));

			queryResult = basicLoader.executeQuery("SELECT `name`, `value` FROM router_config");

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
				queryResult.getFieldValueForRow(i, "name").trim()
				,
				queryResult.getFieldValueForRow(i, "value").trim()
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Boolean hasValidConfFile() {
		return m_validConfFile;
	}

	/*---------------------------------------------------------------------*/

	public static Boolean hasValidDataBase() {
		return m_validDataBase;
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

		result.append("<field name=\"validConfFile\"><![CDATA[" + m_validConfFile + "]]></field>");
		result.append("<field name=\"validDataBase\"><![CDATA[" + m_validDataBase + "]]></field>");

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
