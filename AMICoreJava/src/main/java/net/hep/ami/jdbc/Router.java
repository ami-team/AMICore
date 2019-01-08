package net.hep.ami.jdbc;

import java.io.*;
import java.sql.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.driver.*;
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
			ConfigSingleton.getProperty("router_pass")
		);
	}

	/*---------------------------------------------------------------------*/

	public Router(String externalCatalog) throws Exception
	{
		this(
			externalCatalog,
			ConfigSingleton.getProperty("router_catalog"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);
	}

	/*---------------------------------------------------------------------*/

	public Router(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		m_driver = DriverSingleton.getConnection(
			externalCatalog,
			internalCatalog,
			jdbcUrl,
			user,
			pass
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
	public RowSet executeSQLQuery(String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLQuery(sql, args);
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
	public PreparedStatement prepareStatement(String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames) throws Exception
	{
		return m_driver.prepareStatement(sql, returnGeneratedKeys, columnNames);
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
	 * Create the AMI routeur tables.
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
		String sso_user = ConfigSingleton.getProperty("sso_user");
		String sso_pass = ConfigSingleton.getProperty("sso_pass");
		String guest_user = ConfigSingleton.getProperty("guest_user");
		String guest_pass = ConfigSingleton.getProperty("guest_pass");

		/*-----------------------------------------------------------------*/
		/* CATALOGS                                                        */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup catalogs...");

		executeSQLUpdate("INSERT INTO `router_catalog` (`externalCatalog`, `internalCatalog`, `internalSchema`, `jdbcUrl`, `user`, `pass`, `custom`, `archived`, `createdBy`, `modifiedBy`) VALUES (?, ?, ?, ?, ?, ?, NULL, '0', 'admin', 'admin');",
			getExternalCatalog(),
			getInternalCatalog(),
			(schema != null) ? schema : "",
			getJdbcUrl(),
			SecuritySingleton.encrypt(getUser()),
			SecuritySingleton.encrypt(getPass())
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

		/*-----------------------------------------------------------------*/
		/* ROLES                                                           */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup roles...");

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_GUEST')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_USER')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_CERT')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_SSO')" +
			";"
		);

		executeSQLUpdate(
			"INSERT INTO `router_role` (`role`) VALUES" +
			" ('AMI_ADMIN')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* USERS                                                           */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup users...");

		/**/

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, 'admin', 'admin', ?, 'N/A', '1');",
			admin_user,
			SecuritySingleton.encrypt(admin_pass),
			admin_email
		);

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, 'sso', 'sso', ?, 'N/A', '1');",
			sso_user,
			SecuritySingleton.encrypt(sso_pass),
			admin_email
		);

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, 'guest', 'guest', ?, 'N/A', '1');",
			guest_user,
			SecuritySingleton.encrypt(guest_pass),
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

		PreparedStatement statement1 = prepareStatement("INSERT INTO `router_command` (`command`, `class`, `visible`, `secured`) VALUES (?, ?, ?, ?)", false, null);

		PreparedStatement statement2 = prepareStatement("INSERT INTO `router_command_role` (`commandFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_command` WHERE `command` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?))", false, null);

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

		LocalizationSingleton.importCSVToAMI(this);

		/*-----------------------------------------------------------------*/
		/* DONE                                                            */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("done");

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
