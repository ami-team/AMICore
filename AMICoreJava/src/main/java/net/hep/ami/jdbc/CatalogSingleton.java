package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;
import net.hep.ami.jdbc.reflexion.*;

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

	private static final Map<String, Tuple> s_catalogs = new ConcurrentHashMap<>();

	/*---------------------------------------------------------------------*/

	private CatalogSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		SchemaSingleton.clear();

		s_catalogs.clear();

		try
		{
			addCatalogs();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not add catalogs", e);
		}
	}

	/*---------------------------------------------------------------------*/

	static void addCatalogs() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver = DriverSingleton.getConnection(
			"self",
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
						row.getValue(0),
						row.getValue(1),
						SecuritySingleton.decrypt(row.getValue(2)),
						SecuritySingleton.decrypt(row.getValue(3)),
						row.getValue(4)
					);
				}
				catch(Exception e)
				{
					LogSingleton.root.error(LogSingleton.FATAL, "for catalog `" + row.getValue(0) + "`", e);
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

	private static void addCatalog(String catalog, String jdbcUrl, String user, String pass, String archived) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* ADD CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		s_catalogs.put(
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
		/* READ SCHEMA                                                     */
		/*-----------------------------------------------------------------*/

		if(DriverSingleton.isType(jdbcUrl, Jdbc.Type.SQL))
		{
			Connection connection = DriverManager.getConnection(jdbcUrl, user, pass);

			try
			{
				SchemaSingleton.addSchema(connection.getCatalog(), catalog);
			}
			finally
			{
				connection.close();
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String catalog) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return DriverSingleton.getConnection(catalog, tuple.x, tuple.y, tuple.z);
	}

	/*---------------------------------------------------------------------*/

	public static boolean isType(String catalog, Jdbc.Type jdbcType) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return DriverSingleton.isType(tuple.x, jdbcType);
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

		for(Map.Entry<String, Tuple> entry: s_catalogs.entrySet())
		{
			catalog = entry.getKey();

			jdbcUrl = entry.getValue().x;
			user = entry.getValue().y;
			pass = entry.getValue().z;
			archived = entry.getValue().t;

			result.append("<row>")
			      .append("<field name=\"catalog\"><![CDATA[").append(catalog).append("]]></field>")
			      .append("<field name=\"jdbcUrl\"><![CDATA[").append(jdbcUrl).append("]]></field>")
			      .append("<field name=\"user\"><![CDATA[").append(user).append("]]></field>")
			      .append("<field name=\"pass\"><![CDATA[").append(pass).append("]]></field>")
			      .append("<field name=\"valid\"><![CDATA[").append(archived).append("]]></field>")
			      .append("</row>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
