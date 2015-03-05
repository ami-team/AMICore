package net.hep.ami.jdbc;

import java.util.*;
import java.util.Map.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;

public class CatalogSingleton {
	/*---------------------------------------------------------------------*/

	private static class CatalogTuple extends Tuple4<String, String, String, String> {

		public CatalogTuple(String _x, String _y, String _z, String _t) {
			super(_x, _y, _z, _t);
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

		BasicLoader basicLoader = null;
		QueryResult queryResult = null;

		try {
			basicLoader = new BasicLoader(
				ConfigSingleton.getProperty("jdbc_url"),
				ConfigSingleton.getProperty("router_user"),
				ConfigSingleton.getProperty("router_pass"),
				ConfigSingleton.getProperty("router_name")
			);

			queryResult = basicLoader.executeQuery("SELECT `catalog`, `jdbcUrl`, `user`, `pass`, `name` FROM router_catalogs");

		} finally {

			if(basicLoader != null) {
				basicLoader.rollbackAndRelease();
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

			m_catalogs.put(
				queryResult.getValue(i, "catalog").trim()
				,
				new CatalogTuple(
					queryResult.getValue(i, "jdbcUrl").trim()
					,
					queryResult.getValue(i, "user").trim()
					,
					queryResult.getValue(i, "pass").trim()
					,
					queryResult.getValue(i, "name").trim()
				)
			);
		}

		/*-----------------------------------------------------------------*/
		/* ADD CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		m_catalogs.put(
			"self"
			,
			new CatalogTuple(
				basicLoader.getJdbcUrl()
				,
				basicLoader.getUser()
				,
				basicLoader.getPass()
				,
				basicLoader.getDB()
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

		return ConnectionSingleton.getConnection(tuple.x, tuple.y, tuple.z, tuple.t);

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
			String name    = entry.getValue().t;

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
				"<field name=\"name\"><![CDATA[" + name + "]]></field>"
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
