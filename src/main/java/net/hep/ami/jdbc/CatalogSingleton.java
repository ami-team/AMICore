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

	private static Map<String, CatalogTuple> m_catalogs = new HashMap<String, CatalogTuple>();

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

		BasicQuerier basicQuerier = null;
		QueryResult queryResult = null;

		try {
			basicQuerier = new BasicQuerier(
				ConfigSingleton.getProperty("jdbc_url"),
				ConfigSingleton.getProperty("router_user"),
				ConfigSingleton.getProperty("router_pass")
			);

			queryResult = basicQuerier.executeQuery("SELECT `catalog`, `jdbcUrl`, `user`, `pass`, `name` FROM router_catalogs");

		} finally {

			if(basicQuerier != null) {
				basicQuerier.rollbackAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF PROPERTIES                                        */
		/*-----------------------------------------------------------------*/

		final int nr = queryResult.getNumberOfRows();

		/*-----------------------------------------------------------------*/
		/* ADD CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < nr; i++) {

			try {
				String catalog = queryResult.getValue(i, "catalog").trim();
				String jdbcUrl = queryResult.getValue(i, "jdbcUrl").trim();
				String user    = queryResult.getValue(i, "user").trim();
				String pass    = queryResult.getValue(i, "pass").trim();

				/*---------------------------------------------------------*/
				/* CHECK CATALOG                                           */
				/*---------------------------------------------------------*/

				new BasicQuerier(
					jdbcUrl,
					user,
					pass

				).rollbackAndRelease();

				/*---------------------------------------------------------*/
				/* ADD CATALOG                                             */
				/*---------------------------------------------------------*/

				m_catalogs.put(
					catalog
					,
					new CatalogTuple(
						jdbcUrl,
						user,
						pass
					)
				);

				/*---------------------------------------------------------*/
			} catch(Exception e) {
				LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());
			}
		}

		/*-----------------------------------------------------------------*/
		/* ADD CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		m_catalogs.put(
			"self"
			,
			new CatalogTuple(
				basicQuerier.getJdbcUrl()
				,
				basicQuerier.getUser()
				,
				basicQuerier.getPass()
			)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String catalog) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CHECK CATALOG                                                  */
		/*-----------------------------------------------------------------*/

		if(m_catalogs.containsKey(catalog) == false) {
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		/*-----------------------------------------------------------------*/
		/* GET CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		CatalogTuple tuple = m_catalogs.get(catalog);

		/*-----------------------------------------------------------------*/
		/* CONNECTION                                                      */
		/*-----------------------------------------------------------------*/

		return DriverSingleton.getConnection(tuple.x, tuple.y, tuple.z);

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
