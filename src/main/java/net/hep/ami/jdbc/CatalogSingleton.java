package net.hep.ami.jdbc;

import java.util.*;
import java.util.Map.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;

public class CatalogSingleton {
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple4<String, String, String, String> {

		public Tuple(String _x, String _y, String _z, String _t) {
			super(_x, _y, _z, _t);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> m_catalogs = new HashMap<String, Tuple>();

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
			queryResult = driver.executeSQLQuery("SELECT `catalog`,`jdbcUrl`,`user`,`pass`,`archived` FROM `router_catalog`");

		} finally {
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF CATALOGS                                          */
		/*-----------------------------------------------------------------*/

		final int numberOfRows = queryResult.getNumberOfRows();

		/*-----------------------------------------------------------------*/
		/* ADD CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < numberOfRows; i++) {

			try {
				addCatalog(
					queryResult.getValue(i, "catalog"),
					queryResult.getValue(i, "jdbcUrl"),
					Cryptography.decrypt(queryResult.getValue(i, "user")),
					Cryptography.decrypt(queryResult.getValue(i, "pass")),
					queryResult.getValue(i, "archived")
				);

			} catch(Exception e) {
				LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("deprecation")
	/*---------------------------------------------------------------------*/

	private static void addCatalog(String catalog, String jdbcUrl, String user, String pass, String archived) throws Exception {
		/*-----------------------------------------------------------------*/
		/* READ SCHEMA                                                     */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			jdbcUrl,
			user,
			pass
		);

		try {
			SchemaSingleton.readSchema(driver.getConnection(), catalog);

		} finally {
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* ADD CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		m_catalogs.put(
			catalog
			,
			new Tuple(
				jdbcUrl,
				user,
				pass,
				archived
			)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String catalog) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		Tuple tuple = m_catalogs.get(catalog);

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

		for(Entry<String, Tuple> entry: m_catalogs.entrySet()) {

			String catalog = entry.getKey();

			String jdbcUrl  = entry.getValue().x;
			String user     = entry.getValue().y;
			String pass     = entry.getValue().z;
			String archived = entry.getValue().t;

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
				"<field name=\"valid\"><![CDATA[" + archived + "]]></field>"
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
