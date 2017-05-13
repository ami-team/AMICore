package net.hep.ami.jdbc;

import java.io.*;

import net.hep.ami.*;

public class Router extends SimpleQuerier
{
	/*---------------------------------------------------------------------*/

	public Router() throws Exception
	{
		super("self");
	}

	/*---------------------------------------------------------------------*/

	public Router(String catalog) throws Exception
	{
		super(catalog);
	}

	/*---------------------------------------------------------------------*/

	public Router(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
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

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try
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

					if(line.endsWith(";"))
					{
						LogSingleton.root.info(query);

						executeUpdate(query);

						query = "";
					}
				}
			}
		}
		finally
		{
			bufferedReader.close();
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
}
