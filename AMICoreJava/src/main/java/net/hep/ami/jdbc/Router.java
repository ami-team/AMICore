package net.hep.ami.jdbc;

import java.io.*;
import java.sql.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;

public class Router implements Querier
{
	/*---------------------------------------------------------------------*/

	private final AbstractDriver m_driver;

	/*---------------------------------------------------------------------*/

	public Router() throws Exception
	{
		this(
			"self",
			ConfigSingleton.getProperty("router_catalog"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass"),
			ConfigSingleton.getProperty("time_zone", "UTC")
		);
	}

	/*---------------------------------------------------------------------*/

	public Router(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String timeZone) throws Exception
	{
		String AMIUser = ConfigSingleton.getProperty("admin_user", "admin");

		m_driver = DriverSingleton.getConnection(
			externalCatalog,
			internalCatalog,
			jdbcUrl,
			user,
			pass,
			AMIUser,
			timeZone,
			true,
			false
		);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setReadOnly(boolean readOnly) throws Exception
	{
		m_driver.setReadOnly(readOnly);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToSQL(String entity, String mql) throws Exception
	{
		return m_driver.mqlToSQL(entity, mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToAST(String entity, String mql) throws Exception
	{
		return m_driver.mqlToAST(entity, mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String entity, String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLQuery(entity, mql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeSQLQuery(@Nullable String entity, String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLQuery(entity, sql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeRawQuery(@Nullable String entity, String raw, Object... args) throws Exception
	{
		return m_driver.executeRawQuery(entity, raw, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Update executeMQLUpdate(String entity, String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLUpdate(entity, mql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Update executeSQLUpdate(String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLUpdate(sql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Update executeRawUpdate(String raw, Object... args) throws Exception
	{
		return m_driver.executeRawUpdate(raw, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement preparedStatement(String sql, boolean isRawQuery, boolean returnGeneratedKeys, @Nullable String[] columnNames) throws Exception
	{
		return m_driver.preparedStatement(sql, isRawQuery, returnGeneratedKeys, columnNames);
	}

	/*---------------------------------------------------------------------*/

	public void commit() throws Exception
	{
		m_driver.commit();
	}

	/*---------------------------------------------------------------------*/

	public void rollback() throws Exception
	{
		m_driver.rollback();
	}

	/*---------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception
	{
		m_driver.commitAndRelease();
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception
	{
		m_driver.rollbackAndRelease();
	}

	/*---------------------------------------------------------------------*/

	@Override
	@Deprecated
	public Connection getConnection()
	{
		return m_driver.getConnection();
	}

	/*---------------------------------------------------------------------*/

	@Override
	@Deprecated
	public Statement getStatement()
	{
		return m_driver.getStatement();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getInternalCatalog()
	{
		return m_driver.getInternalCatalog();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getExternalCatalog()
	{
		return m_driver.getExternalCatalog();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public DriverMetadata.Type getJdbcType()
	{
		return m_driver.getJdbcType();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcProto()
	{
		return m_driver.getJdbcProto();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcClass()
	{
		return m_driver.getJdbcClass();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean getBackslashEscapes()
	{
		return m_driver.getBackslashEscapes();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcUrl()
	{
		return m_driver.getJdbcUrl();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getUser()
	{
		return m_driver.getUser();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getPass()
	{
		return m_driver.getPass();
	}

	/*---------------------------------------------------------------------*/

	/**
	 * Create the AMI router tables.
	 */

	@SuppressWarnings("deprecation")
	public void create() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* SELECT PROFILE                                                  */
		/*-----------------------------------------------------------------*/

		String path;

		String jdbcUrl = getJdbcUrl();

		/**/ if(jdbcUrl.contains("jdbc:mysql")) {
			path = "/sql/mysql.sql";
		}
		else if(jdbcUrl.contains("jdbc:mariadb")) {
			path = "/sql/mysql.sql";
		}
		else if(jdbcUrl.contains("jdbc:oracle")) {
			path = "/sql/oracle.sql";
		}
		else if(jdbcUrl.contains("jdbc:postgresql")) {
			path = "/sql/postgresql.sql";
		}
		else {
			throw new Exception("only `mysql`, `mariadb`, `oracle` and `postgresql` are supported");
		}

		/*-----------------------------------------------------------------*/
		/* GET INPUT STREAM                                                */
		/*-----------------------------------------------------------------*/

		InputStream inputStream = Router.class.getResourceAsStream(path);

		/*-----------------------------------------------------------------*/
		/* EXECUTE SQL QUERIES                                             */
		/*-----------------------------------------------------------------*/

		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)))
		{
			String line = "";
			String query = "";

			while((line = bufferedReader.readLine()) != null)
			{
				line = line.trim();

				if(line.isEmpty() == false
				   &&
				   line.startsWith("-") == false
				 ) {
					query += line + " ";

					if(line.endsWith(";;"))
					{
						LogSingleton.root.info(query);

						try
						{
							m_driver.getStatement().executeUpdate(query.replace(";;", ""));
						}
						catch(SQLException e)
						{
							throw new SQLException(e.getMessage() + " for SQL query: " + query.replace(";;", ""), e);
						}

						query = "";
					}
				}
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	/**
	 * Fill the AMI routeur tables.
	 */

	public void fill() throws Exception
	{
		fill(null);
	}

	/*---------------------------------------------------------------------*/

	/**
	 * Fill the AMI routeur tables.
	 *
	 * @param schema The schema name, an empty string or <code>null</code>;
	 */

	public void fill(@Nullable String schema) throws Exception
	{
		String admin_user = ConfigSingleton.getProperty("admin_user");
		String admin_pass = ConfigSingleton.getProperty("admin_pass");
		String admin_email = ConfigSingleton.getProperty("admin_email");
		String sudoer_user = ConfigSingleton.getProperty("sudoer_user");
		String sudoer_pass = ConfigSingleton.getProperty("sudoer_pass");
		String sso_user = ConfigSingleton.getProperty("sso_user");
		String sso_pass = ConfigSingleton.getProperty("sso_pass");
		String guest_user = ConfigSingleton.getProperty("guest_user");
		String guest_pass = ConfigSingleton.getProperty("guest_pass");

		/*-----------------------------------------------------------------*/
		/* CATALOGS                                                        */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup catalogs...");

		executeSQLUpdate("INSERT INTO `router_catalog` (`externalCatalog`, `internalCatalog`, `internalSchema`, `jdbcUrl`, `user`, `pass`, `json`, `description`, `archived`, `createdBy`, `modifiedBy`) VALUES (?, ?, ?, ?, ?, ?, ?, 'AMI configuration catalog', '0', 'admin', 'admin');",
			getExternalCatalog(),
			getInternalCatalog(),
			(schema != null) ? schema : "",
			getJdbcUrl(),
			SecuritySingleton.encrypt(getUser()),
			SecuritySingleton.encrypt(getPass()),
			"{\"router_authority\":{\"x\":250,\"y\":370,\"color\":\"#1494CC\"},\"router_catalog\":{\"x\":0,\"y\":0,\"color\":\"#2BBB88\"},\"router_command\":{\"x\":0,\"y\":370,\"color\":\"#0066CC\"},\"router_command_role\":{\"x\":0,\"y\":270,\"color\":\"#0066CC\"},\"router_config\":{\"x\":750,\"y\":240,\"color\":\"#FF0000\"},\"router_converter\":{\"x\":750,\"y\":400,\"color\":\"#FF0000\"},\"router_dashboard\":{\"x\":0,\"y\":640,\"color\":\"#CCCC33\"},\"router_entity\":{\"x\":250,\"y\":0,\"color\":\"#2BBB88\"},\"router_field\":{\"x\":500,\"y\":0,\"color\":\"#2BBB88\"},\"router_foreign_key\":{\"x\":750,\"y\":0,\"color\":\"#2BBB88\"},\"router_ipv4_blocks\":{\"x\":0,\"y\":890,\"color\":\"#CCAC81\"},\"router_ipv6_blocks\":{\"x\":500,\"y\":890,\"color\":\"#CCAA88\"},\"router_locations\":{\"x\":250,\"y\":905,\"color\":\"#CCAA88\"},\"router_role\":{\"x\":250,\"y\":270,\"color\":\"#0066CC\"},\"router_search_interface\":{\"x\":500,\"y\":640,\"color\":\"#CCCC33\"},\"router_short_url\":{\"x\":250,\"y\":640,\"color\":\"#CCCC33\"},\"router_user\":{\"x\":500,\"y\":370,\"color\":\"#0066CC\"},\"router_user_role\":{\"x\":500,\"y\":270,\"color\":\"#0066CC\"}}"
		);

		/*-----------------------------------------------------------------*/
		/* CONVERTERS                                                      */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup converters...");

		executeSQLUpdate(
			"INSERT INTO `router_converter` (`xslt`, `mime`) VALUES" +
			" ('/xslt/AMIXmlToText.xsl', 'text/plain')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_converter` (`xslt`, `mime`) VALUES" +
			" ('/xslt/AMIXmlToCsv.xsl', 'text/csv')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_converter` (`xslt`, `mime`) VALUES" +
			" ('/xslt/AMIXmlToJson.xsl', 'application/json')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_converter` (`xslt`, `mime`) VALUES" +
			" ('/xslt/AMIXmlToXml.xsl', 'application/xml')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* ROLES                                                           */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup roles...");

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_ADMIN')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_SUDOER')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_SSO')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_CERT')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_USER')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_GUEST')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* USERS                                                           */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup users...");

		/**/

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, ?, ?, ?, 'N/A', '1');",
			admin_user,
			SecuritySingleton.encrypt(admin_pass),
			admin_user,
			admin_user,
			admin_email
		);

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, ?, ?, ?, 'N/A', '1');",
			sudoer_user,
			SecuritySingleton.encrypt(sudoer_pass),
			sudoer_user,
			sudoer_user,
			admin_email
		);

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, ?, ?, ?, 'N/A', '1');",
			sso_user,
			SecuritySingleton.encrypt(sso_pass),
			sso_user,
			sso_user,
			admin_email
		);

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, ?, ?, ?, 'N/A', '1');",
			guest_user,
			SecuritySingleton.encrypt(guest_pass),
			guest_user,
			guest_user,
			admin_email
		);

		/**/

		executeSQLUpdate(
			"INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?));",
			admin_user,
			"AMI_ADMIN"
		);

		executeSQLUpdate(
			"INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?));",
			sudoer_user,
			"AMI_USER"
		);

		executeSQLUpdate(
			"INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?));",
			sudoer_user,
			"AMI_SUDOER"
		);

		executeSQLUpdate(
			"INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?));",
			sso_user,
			"AMI_USER"
		);

		executeSQLUpdate(
			"INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?));",
			sso_user,
			"AMI_SSO"
		);

		executeSQLUpdate(
			"INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?));",
			guest_user,
			"AMI_GUEST"
		);

		/*-----------------------------------------------------------------*/
		/* COMMANDS                                                        */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup commands...");

		/*-----------------------------------------------------------------*/

		PreparedStatement statement1 = preparedStatement("INSERT INTO `router_command` (`command`, `class`, `visible`, `secured`) VALUES (?, ?, ?, ?)", false, false, null);

		PreparedStatement statement2 = preparedStatement("INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", false, false, null);

		try
		{
			String commandName;
			String commandRole;

			int commandVisible;
			int commandSecured;

			for(String commandClass: ClassSingleton.findClassNames("net.hep.ami.command"))
			{
				Class<?> clazz = ClassSingleton.forName(commandClass);

				CommandMetadata commandMetadata = clazz.getAnnotation(CommandMetadata.class);

				if(commandMetadata != null
				   &&
				   (clazz.getModifiers() & Modifier.ABSTRACT) == 0x00
				   &&
				   ClassSingleton.extendsClass(clazz, AbstractCommand.class)
				 ) {
					/*-----------------------------------------------------*/

					commandName = clazz.getSimpleName();

					commandRole = commandMetadata.role();

					commandVisible = commandMetadata.visible() ? 1 : 0;
					commandSecured = commandMetadata.secured() ? 1 : 0;

					/*-----------------------------------------------------*/

					statement1.setString(1, commandName);
					statement1.setString(2, commandClass);
					statement1.setInt(3, commandVisible);
					statement1.setInt(4, commandSecured);

					statement2.setString(1, commandName);
					statement2.setString(2, commandRole);

					statement1.addBatch();
					statement2.addBatch();

					/*-----------------------------------------------------*/
				}
			}

			statement1.executeBatch();
			statement2.executeBatch();
		}
		finally
		{
			statement2.close();
			statement1.close();
		}

		/*-----------------------------------------------------------------*/
		/* LOCALIZATION                                                    */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup localization...");

		//LocalizationSingleton.importCSVToAMI(this);

		/*-----------------------------------------------------------------*/
		/* DONE                                                            */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("done");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void patchSchemaSingleton() throws Exception
	{
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_config = SchemaSingleton.getEntityInfo("self", "router_config");

		router_config.columns.get("paramName").crypted = true;
		router_config.columns.get("paramValue").crypted = true;
		router_config.columns.get("created").created = true;
		router_config.columns.get("createdBy").createdBy = true;
		router_config.columns.get("modified").modified = true;
		router_config.columns.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_catalog = SchemaSingleton.getEntityInfo("self", "router_catalog");

		router_catalog.columns.get("internalCatalog").hidden = true;
		router_catalog.columns.get("internalSchema").hidden = true;
		router_catalog.columns.get("jdbcUrl").adminOnly = true;
		router_catalog.columns.get("user").crypted = true;
		router_catalog.columns.get("pass").crypted = true;
		router_catalog.columns.get("archived").groupable = true;
		router_catalog.columns.get("created").created = true;
		router_catalog.columns.get("createdBy").createdBy = true;
		router_catalog.columns.get("modified").modified = true;
		router_catalog.columns.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_entity = SchemaSingleton.getEntityInfo("self", "router_entity");

		router_entity.columns.get("created").created = true;
		router_entity.columns.get("createdBy").createdBy = true;
		router_entity.columns.get("modified").modified = true;
		router_entity.columns.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_field = SchemaSingleton.getEntityInfo("self", "router_field");

		router_field.columns.get("created").created = true;
		router_field.columns.get("createdBy").createdBy = true;
		router_field.columns.get("modified").modified = true;
		router_field.columns.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_foreign_key = SchemaSingleton.getEntityInfo("self", "router_foreign_key");

		router_foreign_key.columns.get("created").created = true;
		router_foreign_key.columns.get("createdBy").createdBy = true;
		router_foreign_key.columns.get("modified").modified = true;
		router_foreign_key.columns.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_command_role = SchemaSingleton.getEntityInfo("self", "router_command_role");

		router_command_role.bridge = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_user_role = SchemaSingleton.getEntityInfo("self", "router_user_role");

		router_user_role.bridge = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_command = SchemaSingleton.getEntityInfo("self", "router_command");

		router_command.columns.get("visible").groupable = true;
		router_command.columns.get("secured").groupable = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_user = SchemaSingleton.getEntityInfo("self", "router_user");

		router_user.columns.get("AMIPass").adminOnly = true;
		router_user.columns.get("clientDN").crypted = true;
		router_user.columns.get("issuerDN").crypted = true;
		router_user.columns.get("country").adminOnly = true;
		router_user.columns.get("ssoUser").adminOnly = true;
		router_user.columns.get("json").adminOnly = true;
		router_user.columns.get("valid").groupable = true;
		router_user.columns.get("created").created = true;
		router_user.columns.get("modified").modified = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_short_url = SchemaSingleton.getEntityInfo("self", "router_short_url");

		router_short_url.columns.get("owner").createdBy = true;
		router_short_url.columns.get("created").created = true;
		router_short_url.columns.get("modified").modified = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_dashboard = SchemaSingleton.getEntityInfo("self", "router_dashboard");

		router_dashboard.columns.get("owner").createdBy = true;
		router_dashboard.columns.get("created").created = true;
		router_dashboard.columns.get("modified").modified = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_authority = SchemaSingleton.getEntityInfo("self", "router_authority");

		router_authority.columns.get("vo").adminOnly = true;
		router_authority.columns.get("clientDN").adminOnly = true;
		router_authority.columns.get("issuerDN").adminOnly = true;
		router_authority.columns.get("notBefore").adminOnly = true;
		router_authority.columns.get("notAfter").adminOnly = true;
		router_authority.columns.get("email").adminOnly = true;
		router_authority.columns.get("reason").adminOnly = true;
		router_authority.columns.get("created").created = true;
		router_authority.columns.get("createdBy").createdBy = true;
		router_authority.columns.get("modified").modified = true;
		router_authority.columns.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Table router_search_interface = SchemaSingleton.getEntityInfo("self", "router_search_interface");

		router_search_interface.columns.get("archived").groupable = true;
		router_search_interface.columns.get("created").created = true;
		router_search_interface.columns.get("createdBy").createdBy = true;
		router_search_interface.columns.get("modified").modified = true;
		router_search_interface.columns.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void reload(boolean full) throws Exception
	{
		ClassSingleton.reload(/**/);
		ConfigSingleton.reload(/**/);

		DriverSingleton.reload(/**/);
		CatalogSingleton.reload(full);

		CommandSingleton.reload(/**/);
		ConverterSingleton.reload(/**/);
	}

	/*---------------------------------------------------------------------*/
}
