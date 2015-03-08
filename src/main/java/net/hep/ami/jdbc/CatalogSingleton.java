package net.hep.ami.jdbc;

import java.util.*;
import java.util.Map.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;

public class CatalogSingleton {
	/*---------------------------------------------------------------------*/

	private static class CatalogTuple extends Tuple3<String, String, String> {

		public CatalogTuple(String _x, String _y, String _z) {
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, CatalogTuple> m_catalogs = new HashMap<String, CatalogTuple>();

	/*---------------------------------------------------------------------*/

	static {

		try {
			addCatalogs();

		} catch(Exception e) {
			LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	static void addCatalogs() throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		QueryResult queryResult;

		try {
			addCatalog(driver, "self");

			queryResult = driver.executeSQLQuery("SELECT `catalog`,`jdbcUrl`,`user`,`pass`,`name` FROM `router_catalogs`");

		} finally {
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF CATALOGS                                          */
		/*-----------------------------------------------------------------*/

		final int nr = queryResult.getNumberOfRows();

		/*-----------------------------------------------------------------*/
		/* ADD CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < nr; i++) {

			try {
				driver = DriverSingleton.getConnection(
					queryResult.getValue(i, "jdbcUrl"),
					queryResult.getValue(i, "user"),
					queryResult.getValue(i, "pass")
				);

				try {
					addCatalog(driver, queryResult.getValue(i, "catalog"));

				} finally {
					driver.rollbackAndRelease();
				}

			} catch(Exception e) {
				LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("deprecation")
	/*---------------------------------------------------------------------*/

	private static void addCatalog(DriverAbstractClass driver, String catalog) throws Exception {
		/*-----------------------------------------------------------------*/
		/* READ SCHEMA                                                     */
		/*-----------------------------------------------------------------*/

		SchemaSingleton.readSchema(driver.getConnection(), catalog);

		/*-----------------------------------------------------------------*/
		/* ADD CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		m_catalogs.put(
			catalog
			,
			new CatalogTuple(
				driver.getJdbcUrl()
				,
				driver.getUser()
				,
				driver.getPass()
			)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String catalog) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		CatalogTuple tuple = m_catalogs.get(catalog);

		if(tuple == null) {
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		/*-----------------------------------------------------------------*/
		/* CONNECTION                                                      */
		/*-----------------------------------------------------------------*/

		return DriverSingleton.getConnection(
			tuple.x,
			tuple.y,
			tuple.z
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listCatalogs() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Entry<String, CatalogTuple> entry: m_catalogs.entrySet()) {

			String catalog = entry.getKey();

			String jdbcUrl = entry.getValue().x;
			String user    = entry.getValue().y;
			String pass    = entry.getValue().z;

			user = Cryptography.encrypt(user);
			pass = Cryptography.encrypt(pass);

			result.append(
				"<row>"
				+
				"<field name=\"catalog\"><![CDATA[" + catalog + "]]></field>"
				+
				"<field name=\"jdbcUrl\"><![CDATA[" + jdbcUrl + "]]></field>"
				+
				"<field name=\"user\"><![CDATA[" + user + "]]></field>"
				+
				"<field name=\"pass\"><![CDATA[" + pass + "]]></field>"
				+
				"</row>"
			);
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
