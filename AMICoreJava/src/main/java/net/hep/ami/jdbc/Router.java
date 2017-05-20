package net.hep.ami.jdbc;

import java.io.*;
import java.sql.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

public class Router implements Querier
{
	/*---------------------------------------------------------------------*/

	private AbstractDriver m_driver;

	/*---------------------------------------------------------------------*/

	public Router() throws Exception
	{
		this("self");
	}

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
	public String mqlToSQL(String mql) throws Exception
	{
		return m_driver.mqlToSQL(mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String mqlToAST(String mql) throws Exception
	{
		return m_driver.mqlToAST(mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeQuery(String sql) throws Exception
	{
		return m_driver.executeQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql) throws Exception
	{
		return m_driver.executeMQLQuery(mql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeUpdate(String sql) throws Exception
	{
		return m_driver.executeUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql) throws Exception
	{
		return m_driver.sqlPrepareStatement(sql);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql, String[] columnNames) throws Exception
	{
		return m_driver.sqlPrepareStatement(sql, columnNames);
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

						executeUpdate(query);

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
		/*-----------------------------------------------------------------*/
		/* CATALOGS                                                        */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup catalogs...");

		executeUpdate(
			"INSERT INTO `router_catalog` (`externalCatalog`, `internalCatalog`, `jdbcUrl`, `user`, `pass`, `archived`, `jsonSerialization`) VALUES" +
			" ('self', '" + getInternalCatalog().replace("'", "''") + "', '" + getJdbcUrl().replace("'", "''") + "', '" + SecuritySingleton.encrypt(getUser()) + "', '" + SecuritySingleton.encrypt(getPass()) + "', 0, NULL)" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* CONVERTERS                                                      */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup converters...");

		executeUpdate(
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

		executeUpdate(
			"INSERT INTO `router_role` (`lft`, `rgt`, `role`) VALUES" +
			" (0, 3, 'AMI_guest_role')," +
			" (1, 2, 'AMI_admin_role')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* USERS                                                           */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup users...");

		String emptyDN = SecuritySingleton.encrypt("");

		executeUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES" +
			" ('" + ConfigSingleton.getProperty("admin_user") + "', '" + SecuritySingleton.encrypt(ConfigSingleton.getProperty("admin_pass")) + "', '" + emptyDN + "', '" + emptyDN + "', 'admin', 'admin', 'ami@lpsc.in2p3.fr', 'N/A', 1)," +
			" ('" + ConfigSingleton.getProperty("guest_user") + "', '" + SecuritySingleton.encrypt(ConfigSingleton.getProperty("guest_pass")) + "', '" + emptyDN + "', '" + emptyDN + "', 'guest', 'guest', 'ami@lpsc.in2p3.fr', 'N/A', 1)" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* COMMANDS                                                        */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup commands...");

		for(String className: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			try
			{
				CommandSingleton.registerCommand(this, null, className);
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}

		/*-----------------------------------------------------------------*/
		/* LOCALIZATION                                                    */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("setup localization...");

		LocalizationSingleton.fill(this);

		/*-----------------------------------------------------------------*/
		/* DONE                                                            */
		/*-----------------------------------------------------------------*/

		LogSingleton.root.info("done");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void reload() throws Exception
	{
		final String[] classNames = {
			"net.hep.ami.ConfigSingleton",
			"net.hep.ami.ConverterSingleton",
			"net.hep.ami.jdbc.DriverSingleton",
			"net.hep.ami.jdbc.CatalogSingleton",
			"net.hep.ami.RoleSingleton",
			"net.hep.ami.CommandSingleton",
		};

		for(String className: classNames)
		{
			Class.forName(className).getDeclaredMethod("reload").invoke(null);
		}
	}

	/*---------------------------------------------------------------------*/
}
