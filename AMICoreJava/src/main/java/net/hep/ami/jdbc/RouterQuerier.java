package net.hep.ami.jdbc;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class RouterQuerier implements Querier
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final AbstractDriver m_driver;

	/*----------------------------------------------------------------------------------------------------------------*/

	public RouterQuerier() throws Exception
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

	/*----------------------------------------------------------------------------------------------------------------*/

	public RouterQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, @NotNull String timeZone) throws Exception
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
			Querier.FLAG_ADMIN
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setReadOnly(boolean readOnly) throws Exception
	{
		m_driver.setReadOnly(readOnly);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String mqlToSQL(@NotNull String entity, @NotNull String mql) throws Exception
	{
		return m_driver.mqlToSQL(entity, mql);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String mqlToAST(@NotNull String entity, @NotNull String mql) throws Exception
	{
		return m_driver.mqlToAST(entity, mql);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public RowSet executeMQLQuery(@NotNull String entity, @NotNull String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLQuery(entity, mql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public RowSet executeSQLQuery(@NotNull String entity, @NotNull String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLQuery(entity, sql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public RowSet executeRawQuery(@NotNull String entity, @NotNull String raw, Object... args) throws Exception
	{
		return m_driver.executeRawQuery(entity, raw, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Update executeMQLUpdate(@NotNull String entity, @NotNull String mql, Object... args) throws Exception
	{
		return m_driver.executeMQLUpdate(entity, mql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Update executeSQLUpdate(@NotNull String entity, @NotNull String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLUpdate(entity, sql, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Update executeRawUpdate(@NotNull String entity, @NotNull String raw, Object... args) throws Exception
	{
		return m_driver.executeRawUpdate(entity, raw, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public PreparedStatement sqlPreparedStatement(@NotNull String entity, @NotNull String mql, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, Object... args) throws Exception
	{
		return m_driver.sqlPreparedStatement(entity, mql, returnGeneratedKeys, columnNames, injectArgs, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public PreparedStatement mqlPreparedStatement(@NotNull String entity, @NotNull String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, Object... args) throws Exception
	{
		return m_driver.mqlPreparedStatement(entity, sql, returnGeneratedKeys, columnNames, injectArgs, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public PreparedStatement rawPreparedStatement(@NotNull String entity, @NotNull String raw, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, Object... args) throws Exception
	{
		return m_driver.rawPreparedStatement(entity, raw, returnGeneratedKeys, columnNames, injectArgs, args);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void commit() throws Exception
	{
		m_driver.commit();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void rollback() throws Exception
	{
		m_driver.rollback();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception
	{
		m_driver.commitAndRelease();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception
	{
		m_driver.rollbackAndRelease();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Connection getConnection()
	{
		return m_driver.getConnection();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getInternalCatalog()
	{
		return m_driver.getInternalCatalog();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getExternalCatalog()
	{
		return m_driver.getExternalCatalog();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public DriverMetadata.Type getJdbcType()
	{
		return m_driver.getJdbcType();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getJdbcProto()
	{
		return m_driver.getJdbcProto();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getJdbcClass()
	{
		return m_driver.getJdbcClass();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	////////
	@Override
	public int getJdbcFlags()
	{
		return m_driver.getJdbcFlags();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getJdbcUrl()
	{
		return m_driver.getJdbcUrl();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getUser()
	{
		return m_driver.getUser();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String getPass()
	{
		return m_driver.getPass();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Create the AMI router tables.
	 */

	public void create() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* SELECT PROFILE                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

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
		else if(jdbcUrl.contains("jdbc:sqlite")) {
			path = "/sql/sqlite.sql";
		}
		else if(jdbcUrl.contains("jdbc:h2")) {
			path = "/sql/h2.sql";
		}
		else {
			throw new Exception("only `mysql`, `mariadb`, `oracle`, `postgresql`, `sqlite` and `h2` are supported");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE SQL QUERIES                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		List<String> query = new ArrayList<>();

		try(InputStream inputStream = Objects.requireNonNull(RouterQuerier.class.getResourceAsStream(path)))
		{
			for(String line: TextFile.inputStreamToIterable(inputStream, true))
			{
				if(!line.isEmpty()
				   &&
				   !line.startsWith("-")
				 ) {
					query.add(line);

					if(line.endsWith(";;"))
					{
						/*--------------------------------------------------------------------------------------------*/

						String sql = String.join(" ", query);

						sql = sql.substring(0, sql.length() - 2);

						/*--------------------------------------------------------------------------------------------*/

						try
						{
							try(Statement statement = m_driver.getConnection().createStatement())
							{
								statement.executeUpdate(sql);
							}
						}
						catch(SQLException e)
						{
							throw new SQLException(e.getMessage() + " for SQL query: " + sql, e);
						}

						/*--------------------------------------------------------------------------------------------*/

						query.clear();

						/*--------------------------------------------------------------------------------------------*/
					}
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Fill the AMI router tables.
	 */

	public void fill() throws Exception
	{
		fill(null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Fill the AMI router tables.
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
		String guest_user = ConfigSingleton.getProperty("guest_user");
		String guest_pass = ConfigSingleton.getProperty("guest_pass");

		/*------------------------------------------------------------------------------------------------------------*/
		/* CATALOGS                                                                                                   */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.root.info("setup catalogs...");

		executeSQLUpdate("router_catalog", "INSERT INTO `router_catalog` (`externalCatalog`, `internalCatalog`, `internalSchema`, `jdbcUrl`, `user`, `pass`, `json`, `description`, `archived`, `createdBy`, `modifiedBy`) VALUES (?0, ?1, ?2, ?3, ?#4, ?#5, ?6, 'AMI configuration catalog', '0', ?7, ?7);",
			getExternalCatalog(),
			getInternalCatalog(),
			(schema != null) ? schema : "",
			getJdbcUrl(),
			getUser(),
			getPass(),
			"{\"router_authority\":{\"x\":250,\"y\":370,\"color\":\"#1494CC\"},\"router_catalog\":{\"x\":0,\"y\":0,\"color\":\"#2BBB88\"},\"router_command\":{\"x\":0,\"y\":370,\"color\":\"#0066CC\"},\"router_command_role\":{\"x\":0,\"y\":270,\"color\":\"#0066CC\"},\"router_config\":{\"x\":750,\"y\":240,\"color\":\"#FF0000\"},\"router_converter\":{\"x\":750,\"y\":400,\"color\":\"#FF0000\"},\"router_dashboard\":{\"x\":0,\"y\":640,\"color\":\"#CCCC33\"},\"router_entity\":{\"x\":250,\"y\":0,\"color\":\"#2BBB88\"},\"router_field\":{\"x\":500,\"y\":0,\"color\":\"#2BBB88\"},\"router_foreign_key\":{\"x\":750,\"y\":0,\"color\":\"#2BBB88\"},\"router_ipv4_blocks\":{\"x\":0,\"y\":890,\"color\":\"#CCAC81\"},\"router_ipv6_blocks\":{\"x\":500,\"y\":890,\"color\":\"#CCAA88\"},\"router_locations\":{\"x\":250,\"y\":905,\"color\":\"#CCAA88\"},\"router_markdown\":{\"x\":750,\"y\":640,\"color\":\"#CCCC33\"},\"router_monitoring\":{\"x\":750,\"y\":500,\"color\":\"#FF0000\"},\"router_role\":{\"x\":250,\"y\":270,\"color\":\"#0066CC\"},\"router_search_interface\":{\"x\":250,\"y\":640,\"color\":\"#CCCC33\"},\"router_short_url\":{\"x\":500,\"y\":640,\"color\":\"#CCCC33\"},\"router_user\":{\"x\":500,\"y\":370,\"color\":\"#0066CC\"},\"router_user_role\":{\"x\":500,\"y\":270,\"color\":\"#0066CC\"}}",
			admin_user
		);

		/*------------------------------------------------------------------------------------------------------------*/

		this.commit();

		/*------------------------------------------------------------------------------------------------------------*/
		/* CONVERTERS                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.root.info("setup converters...");

		executeSQLUpdate("router_converter", "INSERT INTO `router_converter` (`xslt`, `mime`) VALUES ('/xslt/AMIXmlToText.xsl', 'text/plain')");

		executeSQLUpdate("router_converter", "INSERT INTO `router_converter` (`xslt`, `mime`) VALUES ('/xslt/AMIXmlToCsv.xsl', 'text/csv')");

		executeSQLUpdate("router_converter", "INSERT INTO `router_converter` (`xslt`, `mime`) VALUES ('/xslt/AMIXmlToJson.xsl', 'application/json')");

		executeSQLUpdate("router_converter", "INSERT INTO `router_converter` (`xslt`, `mime`) VALUES ('/xslt/AMIXmlToXml.xsl', 'application/xml')");

		/*------------------------------------------------------------------------------------------------------------*/

		this.commit();

		/*------------------------------------------------------------------------------------------------------------*/
		/* ROLES                                                                                                      */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.root.info("setup roles...");

		executeSQLUpdate("router_role", "INSERT INTO `router_role` (`role`) VALUES ('AMI_ADMIN')");

		executeSQLUpdate("router_role", "INSERT INTO `router_role` (`role`) VALUES ('AMI_SUDOER')");

		executeSQLUpdate("router_role", "INSERT INTO `router_role` (`role`) VALUES ('AMI_CERT')");

		executeSQLUpdate("router_role", "INSERT INTO `router_role` (`role`) VALUES ('AMI_WRITER')");

		executeSQLUpdate("router_role", "INSERT INTO `router_role` (`role`) VALUES ('AMI_USER')");

		executeSQLUpdate("router_role", "INSERT INTO `router_role` (`role`) VALUES ('AMI_GUEST')");

		/*------------------------------------------------------------------------------------------------------------*/

		this.commit();

		/*------------------------------------------------------------------------------------------------------------*/
		/* USERS                                                                                                      */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.root.info("setup users...");

		/**/

		executeSQLUpdate("router_user", "INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?0, ?^1, ?0, ?0, ?2, 'N/A', '1');",
			admin_user,
			admin_pass,
			admin_email
		);

		executeSQLUpdate("router_user", "INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?0, ?^1, ?0, ?0, ?2, 'N/A', '1');",
			sudoer_user,
			sudoer_pass,
			admin_email
		);

		executeSQLUpdate("router_user", "INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?0, ?^1, ?0, ?0, ?2, 'N/A', '1');",
			guest_user,
			guest_pass,
			admin_email
		);

		/**/

		executeSQLUpdate("router_user", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?0), (SELECT `id` FROM `router_role` WHERE `role` = ?1));",
			admin_user,
			"AMI_ADMIN"
		);

		executeSQLUpdate("router_user", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?0), (SELECT `id` FROM `router_role` WHERE `role` = ?1));",
			sudoer_user,
			"AMI_USER"
		);

		executeSQLUpdate("router_user", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?0), (SELECT `id` FROM `router_role` WHERE `role` = ?1));",
			sudoer_user,
			"AMI_SUDOER"
		);

		executeSQLUpdate("router_user", "INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?0), (SELECT `id` FROM `router_role` WHERE `role` = ?1));",
			guest_user,
			"AMI_GUEST"
		);

		/*------------------------------------------------------------------------------------------------------------*/

		this.commit();

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMMANDS                                                                                                   */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.root.info("setup commands...");

		/*------------------------------------------------------------------------------------------------------------*/

		int i = 0;

		try(PreparedStatement statement1 = sqlPreparedStatement("router_command", "INSERT INTO `router_command` (`command`, `class`, `visible`) VALUES (?, ?, ?)", false, null, false))
		{
			try(PreparedStatement statement2 = sqlPreparedStatement("router_command_role", "INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", false, null, false))
			{
				String commandName;
				String commandRole;

				int commandVisible;

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
						/*--------------------------------------------------------------------------------------------*/

						commandName = clazz.getSimpleName();

						commandRole = commandMetadata.role();

						commandVisible = commandMetadata.visible() ? 1 : 0;

						/*--------------------------------------------------------------------------------------------*/

						statement1.setString(1, commandName);
						statement1.setString(2, commandClass);
						statement1.setInt(3, commandVisible);

						statement2.setString(1, commandName);
						statement2.setString(2, commandRole);

						statement1.addBatch();
						statement2.addBatch();

						/*--------------------------------------------------------------------------------------------*/
					}

					statement1.executeBatch();
					statement2.executeBatch();

					if(i++ % 50 == 0)
					{
						this.commit();
					}
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		this.commit();

		/*------------------------------------------------------------------------------------------------------------*/
		/* LOCALIZATION                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.root.info("setup localization...");

		LocalizationSingleton.importCSVToAMI(this);

		/*------------------------------------------------------------------------------------------------------------*/
		/* DONE                                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		LogSingleton.root.info("done");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void patchSchemaSingleton() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_config = SchemaSingleton.getEntityInfo("self", "router_config");

		router_config.columns.get("paramName").crypted = true;
		router_config.columns.get("paramValue").crypted = true;
		router_config.columns.get("created").created = true;
		router_config.columns.get("createdBy").createdBy = true;
		router_config.columns.get("modified").modified = true;
		router_config.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_monitoring = SchemaSingleton.getEntityInfo("self", "router_monitoring");

		router_monitoring.columns.get("modified").modified = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_catalog = SchemaSingleton.getEntityInfo("self", "router_catalog");

		router_catalog.columns.get("internalCatalog").webLinkScript = "import net.hep.ami.data.WebLink;\n\nwebLink = new WebLink();\n\nif(rowSet.isANameOrLabel(\"internalCatalog\"))\n{\n\twebLink.newLinkProperties().setLabel(\"Show/Edit catalog\").setHRef(\"./?subapp=schemaViewer&userdata=\" + row.getValue(\"internalCatalog\")).setTarget(\"_blank\");\n}\n\nreturn webLink;\n";
		router_catalog.columns.get("internalCatalog").hidden = true;
		router_catalog.columns.get("internalSchema").hidden = true;
		router_catalog.columns.get("jdbcUrl").adminOnly = true;
		router_catalog.columns.get("user").crypted = true;
		router_catalog.columns.get("pass").crypted = true;
		router_catalog.columns.get("json").json = true;
		router_catalog.columns.get("archived").groupable = true;
		router_catalog.columns.get("created").created = true;
		router_catalog.columns.get("createdBy").createdBy = true;
		router_catalog.columns.get("modified").modified = true;
		router_catalog.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_entity = SchemaSingleton.getEntityInfo("self", "router_entity");

		router_entity.columns.get("json").json = true;
		router_entity.columns.get("created").created = true;
		router_entity.columns.get("createdBy").createdBy = true;
		router_entity.columns.get("modified").modified = true;
		router_entity.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_field = SchemaSingleton.getEntityInfo("self", "router_field");

		router_field.columns.get("json").json = true;
		router_field.columns.get("created").created = true;
		router_field.columns.get("createdBy").createdBy = true;
		router_field.columns.get("modified").modified = true;
		router_field.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_foreign_key = SchemaSingleton.getEntityInfo("self", "router_foreign_key");

		router_foreign_key.columns.get("created").created = true;
		router_foreign_key.columns.get("createdBy").createdBy = true;
		router_foreign_key.columns.get("modified").modified = true;
		router_foreign_key.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_command_role = SchemaSingleton.getEntityInfo("self", "router_command_role");

		router_command_role.bridge = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_user_role = SchemaSingleton.getEntityInfo("self", "router_user_role");

		router_user_role.bridge = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_command = SchemaSingleton.getEntityInfo("self", "router_command");

		router_command.columns.get("visible").groupable = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_user = SchemaSingleton.getEntityInfo("self", "router_user");

		router_user.columns.get("ssoUser").adminOnly = true;
		router_user.columns.get("AMIPass").hashed = true;
		router_user.columns.get("clientDN").crypted = true;
		router_user.columns.get("issuerDN").crypted = true;
		router_user.columns.get("firstName").adminOnly = true;
		router_user.columns.get("lastName").adminOnly = true;
		router_user.columns.get("email").adminOnly = true;
		router_user.columns.get("country").adminOnly = true;
		router_user.columns.get("json").adminOnly = true;
		router_user.columns.get("json").json = true;
		router_user.columns.get("valid").groupable = true;
		router_user.columns.get("created").created = true;
		router_user.columns.get("modified").modified = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_dashboard = SchemaSingleton.getEntityInfo("self", "router_dashboard");

		router_dashboard.columns.get("params").json = true;
		router_dashboard.columns.get("settings").json = true;
		router_dashboard.columns.get("owner").createdBy = true;
		router_dashboard.columns.get("created").created = true;
		router_dashboard.columns.get("modified").modified = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_search_interface = SchemaSingleton.getEntityInfo("self", "router_search_interface");

		router_search_interface.columns.get("json").json = true;
		router_search_interface.columns.get("archived").groupable = true;
		router_search_interface.columns.get("created").created = true;
		router_search_interface.columns.get("createdBy").createdBy = true;
		router_search_interface.columns.get("modified").modified = true;
		router_search_interface.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_short_url = SchemaSingleton.getEntityInfo("self", "router_short_url");

		router_short_url.columns.get("json").json = true;
		router_short_url.columns.get("owner").createdBy = true;
		router_short_url.columns.get("created").created = true;
		router_short_url.columns.get("modified").modified = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_markdown = SchemaSingleton.getEntityInfo("self", "router_markdown");

		router_markdown.columns.get("name").webLinkScript = "import net.hep.ami.data.WebLink;\n\nwebLink = new WebLink();\n\nif(rowSet.isANameOrLabel(\"name\"))\n{\n\twebLink.newLinkProperties().setLabel(\"Show/Edit page\").setHRef(\"./?subapp=document&userdata=\" + row.getValue(\"name\")).setTarget(\"_blank\");\n}\n\nreturn webLink;\n";
		router_markdown.columns.get("archived").groupable = true;
		router_markdown.columns.get("created").created = true;
		router_markdown.columns.get("createdBy").createdBy = true;
		router_markdown.columns.get("modified").modified = true;
		router_markdown.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table router_authority = SchemaSingleton.getEntityInfo("self", "router_authority");

		router_authority.columns.get("vo").adminOnly = true;
		router_authority.columns.get("clientDN").crypted = true;
		router_authority.columns.get("issuerDN").crypted = true;
		router_authority.columns.get("notBefore").adminOnly = true;
		router_authority.columns.get("notAfter").adminOnly = true;
		router_authority.columns.get("email").adminOnly = true;
		router_authority.columns.get("reason").adminOnly = true;
		router_authority.columns.get("created").created = true;
		router_authority.columns.get("createdBy").createdBy = true;
		router_authority.columns.get("modified").modified = true;
		router_authority.columns.get("modifiedBy").modifiedBy = true;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload(boolean full)
	{
		ClassSingleton.reload(/**/);
		ConfigSingleton.reload(/**/);

		DriverSingleton.reload(/**/);
		CatalogSingleton.reload(full);

		CommandSingleton.reload(/**/);
		ConverterSingleton.reload(/**/);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
