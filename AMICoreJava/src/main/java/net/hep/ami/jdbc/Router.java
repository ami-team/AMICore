package net.hep.ami.jdbc;

import java.io.*;
import java.sql.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.driver.*;

public class Router implements Querier
{
	/*---------------------------------------------------------------------*/

	private final AbstractDriver m_driver;

	/*---------------------------------------------------------------------*/

	public Router() throws Exception
	{
		this(
			"self",
			ConfigSingleton.getProperty("router"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);	}

	/*---------------------------------------------------------------------*/

	public Router(String externalCatalog) throws Exception
	{
		this(
			externalCatalog,
			ConfigSingleton.getProperty("router"),
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
	public int executeSQLUpdate(String sql, Object... args) throws Exception
	{
		return m_driver.executeSQLUpdate(sql, args);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement prepareStatement(String sql) throws Exception
	{
		return m_driver.prepareStatement(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws Exception
	{
		return m_driver.prepareStatement(sql, columnNames);
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
	public Jdbc.Type getJdbcType()
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
		else if(jdbcUrl.contains("jdbc:oracle")) {
			path = "/sql/oracle.sql";
		}
		else if(jdbcUrl.contains("jdbc:postgresql")) {
			path = "/sql/postgresql.sql";
		}
		else {
			throw new Exception("only `mysql`, `oracle` and `postgresql` are supported");
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
				   &&
				   line.startsWith("#") == false
				 ) {
					query += line + " ";

					if(line.endsWith(";"))
					{
						LogSingleton.root.info(query);

						executeSQLUpdate(query);

						query = "";
					}
				}
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public void fill() throws Exception
	{
		String admin_user = ConfigSingleton.getProperty("admin_user");
		String admin_pass = ConfigSingleton.getProperty("admin_pass");
		String admin_email = ConfigSingleton.getProperty("admin_email");

		/*-----------------------------------------------------------------*/
		/* CATALOGS                                                        */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup catalogs...");

		executeSQLUpdate("INSERT INTO `router_catalog` (`externalCatalog`, `internalCatalog`, `jdbcUrl`, `user`, `pass`, `custom`, `archived`) VALUES (?, ?, ?, ?, ?, NULL, 0);",
			getExternalCatalog(),
			getInternalCatalog(),
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
			" ('/xslt/AMIXmlToText.xsl', 'text/plain')," +
			" ('/xslt/AMIXmlToCsv.xsl', 'text/csv')," +
			" ('/xslt/AMIXmlToJson.xsl', 'application/json')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* ROLES                                                           */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup roles...");

		executeSQLUpdate(
			"INSERT INTO `router_role` (`lft`, `rgt`, `role`) VALUES" +
			" (0, 5, 'AMI_GUEST')," +
			" (1, 4, 'AMI_CERT')," +
			" (2, 3, 'AMI_ADMIN')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* USERS                                                           */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup users...");

		executeSQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES (?, ?, 'admin', 'admin', ?, 'N/A', 1);",
			admin_user,
			SecuritySingleton.encrypt(admin_pass),
			admin_email
		);

		executeSQLUpdate(
			"INSERT INTO `router_user_role` (`userFK`, `roleFK`) VALUES ((SELECT `id` FROM `router_user` WHERE `AMIUser` = ?), (SELECT `id` FROM `router_role` WHERE `role` = ?));",
			admin_user,
			"AMI_ADMIN"
		);

		/*-----------------------------------------------------------------*/
		/* COMMANDS                                                        */
		/*-----------------------------------------------------------------*/

		String commandName;

		LogSingleton.root.info("setup commands...");

		for(String className: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			commandName = className.substring(className.lastIndexOf('.') + 1);

			if("AbstractCommand".equals(commandName) == false)
			{
				executeSQLUpdate("INSERT INTO `router_command` (`command`, `class`) VALUES (?, ?)",
					commandName,
					className
				);
			}
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

	public static void reload() throws Exception
	{
		ClassSingleton.reload();
		ConfigSingleton.reload();

		DriverSingleton.reload();
		CatalogSingleton.reload();

		CommandSingleton.reload();
		ConverterSingleton.reload();
	}

	/*---------------------------------------------------------------------*/
}
