package net.hep.ami;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.w3c.dom.*;

public class ConfigSingleton {
	/*---------------------------------------------------------------------*/

	private static String m_configPathName = "/etc/ami";	/* for DEBs/RPMs */
	private static String m_configFileName = "/etc/ami/AMI.xml";	/* for DEBs/RPMs */

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

	private static void readFromConfFile() throws Exception {

		String path;

		/*-----------------------------------------------------------------*/
		/* FIND CONFFILE                                                   */
		/*-----------------------------------------------------------------*/

		path = System.getProperty("catalina.base");

		if(path != null) {
			m_configFileName = path.trim()
			                   +
			                   File.separator
			                   +
			                   "conf"
			;

		} else {

			path = System.getProperty("conf.file");
			if(path != null) {
				m_configFileName = path.trim();

			} else {

				path = System.getProperty("user.home");
				if(path != null) {
					m_configFileName = path.trim();

				}
			}
		}

		/*-----------------------------------------------------------------*/

		if(m_configFileName.endsWith(".xml") == false)
		{
			m_configFileName = m_configFileName.concat(File.separator + "AMI.xml");
		}

		/*-----------------------------------------------------------------*/

		File file = new File(m_configFileName).getAbsoluteFile();

		m_configPathName = file.getParentFile().getPath();
		m_configFileName = file.getPath();

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

	public static String getProperty(String key) {

		String value = m_properties.get(key);

		return value != null ? value : "";
	}

	/*---------------------------------------------------------------------*/

	public static String getProperty(String key, String defaultValue) {

		String value = m_properties.get(key);

		return value != null ? value : defaultValue;
	}

	/*---------------------------------------------------------------------*/

	public static int getProperty(String key, int defaultValue) {

		int result;

		String value = m_properties.get(key);

		if(value != null) {

			try {
				result = Integer.parseInt(value);
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

		String value = m_properties.get(key);

		if(value != null) {

			try {
				result = Float.parseFloat(value);
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

		String value = m_properties.get(key);

		if(value != null) {

			try {
				result = Double.parseDouble(value);
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

		result.append("<field name=\"configFileName\"><![CDATA[" + m_configFileName + "]]></field>");
		result.append("<field name=\"validConfFile\"><![CDATA[" + m_hasValidConfFile + "]]></field>");
		result.append("<field name=\"validDataBase\"><![CDATA[" + m_hasValidDataBase + "]]></field>");

		result.append("</row></rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"config\"><row>");

		for(Entry<String, String> entry: m_properties.entrySet()) {

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
