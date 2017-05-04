package net.hep.ami.jdbc;

import java.io.*;

import net.hep.ami.*;

public class RouterBuilder extends SimpleQuerier
{
	/*---------------------------------------------------------------------*/

	public RouterBuilder(String catalog) throws Exception
	{
		super(catalog);
	}

	/*---------------------------------------------------------------------*/

	public RouterBuilder(@Nullable String catalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(catalog, jdbcUrl, user, pass);
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

		InputStream inputStream = RouterBuilder.class.getResourceAsStream(path);

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
				query += line.trim();

				if(query.endsWith(";"))
				{
					executeUpdate(query);

					query = "";
				}
				else
				{
					query += "\n";
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
		/* SELF                                                            */
		/*-----------------------------------------------------------------*/

		executeUpdate(
			"INSERT INTO `router_catalog` (`catalog`, `jdbcUrl`, `user`, `pass`, `archived`, `jsonSerialization`) VALUES" +
			" ('self', '" + getJdbcUrl().replace("'", "''") + "', '" + SecuritySingleton.encrypt(getUser()) + "', '" + SecuritySingleton.encrypt(getPass()) + "', 0, NULL)" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* ROLES                                                           */
		/*-----------------------------------------------------------------*/

		executeUpdate(
			"INSERT INTO `router_role` (`lft`, `rgt`, `role`) VALUES" +
			" (0, 3, 'AMI_guest_role')," +
			" (1, 2, 'AMI_admin_role')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* USERS                                                           */
		/*-----------------------------------------------------------------*/

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

		for(String className: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			CommandSingleton.registerCommand(this, className);
		}

		/*-----------------------------------------------------------------*/
		/* CONVERTERS                                                      */
		/*-----------------------------------------------------------------*/

		executeUpdate(
			"INSERT INTO `router_converter` (`xslt`, `mime`) VALUES" +
			" ('/xslt/AMIXmlToText.xsl', 'text/plain')," +
			" ('/xslt/AMIXmlToCsv.xsl', 'text/csv')," +
			" ('/xslt/AMIXmlToJson.xsl', 'application/json')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* LOCALIZATION                                                    */
		/*-----------------------------------------------------------------*/

		LocalizationSingleton.fill(this);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
