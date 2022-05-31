package net.hep.ami;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class ConfigSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Pattern s_envVarPattern = Pattern.compile(
			"\\$\\{\\s*([a-zA-Z-_.]+)\\s*}"
	);

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, String> s_properties = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Set<String> s_reservedParams = new HashSet<>();
	private static final Set<String> s_neededParams = new HashSet<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	private static String s_configPathName = "N/A";
	private static String s_configFileName = "N/A";

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private ConfigSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		/*------------------------------------------------------------------------------------------------------------*/

		s_reservedParams.add("base_url");
		s_reservedParams.add("admin_user");
		s_reservedParams.add("admin_pass");
		s_reservedParams.add("admin_email");
		s_reservedParams.add("guest_user");
		s_reservedParams.add("guest_pass");
		s_reservedParams.add("encryption_key");
		s_reservedParams.add("router_catalog");
		s_reservedParams.add("router_schema");
		s_reservedParams.add("router_url");
		s_reservedParams.add("router_user");
		s_reservedParams.add("router_pass");
		s_reservedParams.add("time_zone");

		/*------------------------------------------------------------------------------------------------------------*/

		s_neededParams.add("base_url");
		s_neededParams.add("admin_user");
		s_neededParams.add("admin_pass");
		s_neededParams.add("admin_email");
		s_neededParams.add("encryption_key");
		s_neededParams.add("router_catalog");
		s_neededParams.add("router_url");
		s_neededParams.add("router_user");
		s_neededParams.add("router_pass");
		s_neededParams.add("time_zone");

		/*------------------------------------------------------------------------------------------------------------*/

		reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload()
	{
		s_properties.clear();

		try
		{
			loadConfigFile();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not read configuration", e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	private static File toFile(@NotNull String configPathName)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(!configPathName.toLowerCase().endsWith(".xml"))
		{
			configPathName = configPathName + File.separator + "AMI.xml";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new File(configPathName);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void loadConfigFile() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* FIND CONFIG FILE                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		File file;
		String path;

		path = System.getProperty("ami.conffile");

		if(path == null
		   ||
		   !(file = toFile(path)).exists()
		 ) {

			path = System.getProperty("catalina.base");

			if(path == null
			   ||
			   !(file = toFile(path + File.separator + "conf")).exists()
			 ) {

				path = System.getProperty((("user.home")));

				if(path == null
				   ||
				   !(file = toFile(path + File.separator + ".ami")).exists()
				 ) {
					/*----------------------------*/
					/* DEFAULT FOR DEBs/RPMs      */
					/*----------------------------*/

					file = toFile("/etc/ami");

					/*----------------------------*/
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		s_configPathName = file.getParentFile().getPath();
		s_configFileName = file        .        getPath();

		/*------------------------------------------------------------------------------------------------------------*/

		try(InputStream inputStream = new FileInputStream(file))
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* PARSE CONFIG FILE                                                                                      */
			/*--------------------------------------------------------------------------------------------------------*/

			org.w3c.dom.Document document = XMLFactory.newDocument(inputStream);

			/*--------------------------------------------------------------------------------------------------------*/
			/* READ CONFIG FILE                                                                                       */
			/*--------------------------------------------------------------------------------------------------------*/

			org.w3c.dom.NodeList nodeList = document.getElementsByTagName("property");

			/*--------------------------------------------------------------------------------------------------------*/
			/* ADD PROPERTIES                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			String key;
			String val;

			for(org.w3c.dom.Node node: XMLFactory.nodeListToIterable(nodeList))
			{
				key = XMLFactory.getNodeAttribute(node,
						                          "name");
				val = XMLFactory.getNodeContent(node);

				s_properties.put(key, s_envVarPattern.matcher(val).replaceAll(m -> System.getProperty(m.group(1), "")));
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* DEFAULT USERS                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		s_properties.put("sudoer_user", "sudoer");
		s_properties.put("sudoer_pass", s_properties.get("admin_pass"));

		s_properties.put("sso_user", "sso");
		s_properties.put("sso_pass", s_properties.get("admin_pass"));

		s_properties.put("guest_user", "guest");
		s_properties.put("guest_pass", "guest");

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK CONFIG                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		checkValidAMIConfigFile();

		/*------------------------------------------------------------------------------------------------------------*/
		/* RESET LOGGERS                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.reset("WARN");

		/*------------------------------------------------------------------------------------------------------------*/
		/* SET ENCRYPTION KEY                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		SecuritySingleton.setEncryptionKey(s_properties.get("encryption_key"));

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void readDataBase() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE QUERIER                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		Router router = new Router();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet = router.executeSQLQuery("router_config", "SELECT `paramName`, `paramValue` FROM `router_config`");

			/*--------------------------------------------------------------------------------------------------------*/
			/* ADD PROPERTIES                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			String key;
			String val;

			for(Row row: rowSet.iterate())
			{
				key = row.getValue(0);
				val = row.getValue(1);

				if(!s_reservedParams.contains(key))
				{
					s_properties.put(key, s_envVarPattern.matcher(val).replaceAll(m -> System.getProperty(m.group(1), "")));
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		finally
		{
			router.rollbackAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* RESET LOGGERS                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.reset("WARN");

		/*------------------------------------------------------------------------------------------------------------*/
		/* SET OIDC CHECK URL                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			s_properties.put("sso_auth_url", SecuritySingleton.setupOIDC(
				s_properties.getOrDefault("sso_client_id", null),
					s_properties.getOrDefault("sso_conf_url", null)
			));
		}
		catch(Exception e)
		{
			s_properties.put("sso_auth_url", "@NULL");

			LogSingleton.root.warn(e.getMessage());
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static int setPropertyInDataBase(@NotNull Querier querier, @NotNull String name, @Nullable String value, @NotNull String AMIUser) throws Exception
	{
		int result;

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rows = querier.executeSQLQuery("router_config", "SELECT `id`, `paramValue` FROM `router_config` WHERE `paramName` = ?#0", name).getAll();

		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(rows.isEmpty())
		{
			result = querier.executeSQLUpdate("router_config", "INSERT INTO `router_config` (`paramName`, `paramValue`, `createdBy`, `modifiedBy`) VALUES (?#0, ?#1, ?2, ?2)",
				name,
				value,
				AMIUser
			).getNbOfUpdatedRows();
		}
		else
		{
			String id = rows.get(0).getValue(0);
			String eulav = rows.get(0).getValue(1);

			if(!SecuritySingleton.encrypt(eulav).equals(value))
			{
				result = querier.executeSQLUpdate("router_config", "UPDATE `router_config` SET `paramValue` = ?#1, `modified` = CURRENT_TIMESTAMP, `modifiedBy` = ?2 WHERE `id` = ?0",
					id,
					value,
					AMIUser
				).getNbOfUpdatedRows();
			}
			else
			{
				result = 0;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static int removePropertyInDataBase(@NotNull Querier querier, @NotNull String name) throws Exception
	{
		return querier.executeSQLUpdate("router_config", "DELETE FROM `router_config` WHERE `paramName` = ?#0", name).getNbOfUpdatedRows();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public static String getConfigPathName()
	{
		return s_configPathName;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public static String getConfigFileName()
	{
		return s_configFileName;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void checkValidAMIConfigFile() throws Exception
	{
		for(String key: s_neededParams)
		{
			if(getProperty(key, (String) null) == null)
			{
				throw new Exception("invalid AMI configuration file");
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* HELPERS                                                                                                        */
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(value = "null, _ -> param2", pure = true)
	public static String checkString(@Nullable String currentValue, @Nullable String defaultValue)
	{
		if(currentValue != null)
		{
			String tempValue = currentValue.trim();

			if(!tempValue.isEmpty() && !"@NULL".equalsIgnoreCase(tempValue))
			{
				return currentValue;
			}
		}

		return defaultValue;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* SYSTEM PROPERTIES                                                                                              */
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String setSystemProperty(@NotNull String key, @Nullable String value)
	{
		value = checkString(value, null);

		if(value != null)
		{
			return checkString(System.setProperty(key, value), "");
		}
		else
		{
			return checkString(System.clearProperty(key), "");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String removeSystemProperty(@NotNull String key)
	{
		return checkString(System.clearProperty(key), "");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getSystemProperty(@NotNull String key)
	{
		return checkString(System.getProperty(key), "");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(value = "_, !null -> !null", pure = true)
	public static String getSystemProperty(@NotNull String key, @Nullable String defaultValue)
	{
		return checkString(System.getProperty(key), defaultValue);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Boolean getSystemProperty(@NotNull String key, Boolean defaultValue)
	{
		try
		{
			return Bool.valueOf(checkString(System.getProperty(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Integer getSystemProperty(@NotNull String key, Integer defaultValue)
	{
		try
		{
			return Integer.valueOf(checkString(System.getProperty(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Float getSystemProperty(@NotNull String key, Float defaultValue)
	{
		try
		{
			return Float.valueOf(checkString(System.getProperty(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Double getSystemProperty(@NotNull String key, Double defaultValue)
	{
		try
		{
			return Double.valueOf(checkString(System.getProperty(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* AMI PROPERTIES                                                                                                 */
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String setProperty(@NotNull String key, @Nullable String value)
	{
		value = checkString(value, null);

		if(value != null)
		{
			return checkString(s_properties.put(key, value), "");
		}
		else
		{
			return checkString(s_properties.remove(key), "");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String removeProperty(@NotNull String key)
	{
		return checkString(s_properties.remove(key), "");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getProperty(@NotNull String key)
	{
		return checkString(s_properties.get(key), "");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static String getProperty(@NotNull String key, @Nullable String defaultValue)
	{
		return checkString(s_properties.get(key), defaultValue);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Boolean getProperty(@NotNull String key, Boolean defaultValue)
	{
		try
		{
			return Bool.valueOf(checkString(s_properties.get(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Integer getProperty(@NotNull String key, Integer defaultValue)
	{
		try
		{
			return Integer.valueOf(checkString(s_properties.get(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Float getProperty(@NotNull String key, Float defaultValue)
	{
		try
		{
			return Float.valueOf(checkString(s_properties.get(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public static Double getProperty(@NotNull String key, Double defaultValue)
	{
		try
		{
			return Double.valueOf(checkString(s_properties.get(key), ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder showConfig()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"paths\">")
		      .append("<row>")
		      .append("<field name=\"configPathName\"><![CDATA[").append(s_configPathName).append("]]></field>")
		      .append("<field name=\"configFileName\"><![CDATA[").append(s_configFileName).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"params\">");

		for(Map.Entry<String, String> entry: s_properties.entrySet())
		{
			result.append("<row>")
			      .append("<field name=\"").append(entry.getKey()).append("\"><![CDATA[").append(entry.getValue()).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
