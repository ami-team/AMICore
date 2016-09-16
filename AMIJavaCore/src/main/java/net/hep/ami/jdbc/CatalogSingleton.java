package net.hep.ami.jdbc;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.introspection.*;

public class CatalogSingleton
{
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple4<String, String, String, String>
	{
		public Tuple(String _x, String _y, String _z, String _t)
		{
			super(_x, _y, _z, _t);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> m_catalogs = new java.util.concurrent.ConcurrentHashMap<String, Tuple>();

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		SchemaSingleton.clear();

		m_catalogs.clear();

		try
		{
			addCatalogs();
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.fatal(e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	static void addCatalogs() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = driver.executeQuery("SELECT `catalog`, `jdbcUrl`, `user`, `pass`, `archived` FROM `router_catalog`");

			/*-------------------------------------------------------------*/
			/* ADD CATALOGS                                                */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet.iter())
			{
				try
				{
					addCatalog(
						row.getValue("catalog"),
						row.getValue("jdbcUrl"),
						Cryptography.decrypt(row.getValue("user")),
						Cryptography.decrypt(row.getValue("pass")),
						row.getValue("archived")
					);
				}
				catch(Exception e)
				{
					LogSingleton.defaultLogger.error(e.getMessage());
				}
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("deprecation")
	/*---------------------------------------------------------------------*/

	private static void addCatalog(String catalog, String jdbcUrl, String user, String pass, String archived) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* READ SCHEMA                                                     */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			jdbcUrl,
			user,
			pass
		);

		try
		{
			SchemaSingleton.readSchema(driver.getConnection(), catalog);
		}
		finally
		{
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

	public static DriverAbstractClass getConnection(String catalog) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		Tuple tuple = m_catalogs.get(catalog);

		if(tuple == null)
		{
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

	public static StringBuilder listCatalogs()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		String catalog;

		String jdbcUrl;
		String user;
		String pass;
		String archived;

		for(Map.Entry<String, Tuple> entry: m_catalogs.entrySet())
		{
			catalog = entry.getKey();

			jdbcUrl = entry.getValue().x;
			user = entry.getValue().y;
			pass = entry.getValue().z;
			archived = entry.getValue().t;

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

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
