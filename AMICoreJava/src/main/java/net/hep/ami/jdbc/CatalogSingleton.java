package net.hep.ami.jdbc;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;
import net.hep.ami.jdbc.reflexion.*;

public class CatalogSingleton
{
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple5<String, String, String, String, String>
	{
		public Tuple(String _x, String _y, String _z, String _t, String _u)
		{
			super(_x, _y, _z, _t, _u);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_catalogs = new AMIHashMap<>();

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
			LogSingleton.root.error(LogSingleton.FATAL, "could not add catalogs: " + e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	static void addCatalogs() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("router"),
			ConfigSingleton.getProperty("router_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = driver.executeQuery("SELECT `externalCatalog`, `internalCatalog`, `jdbcUrl`, `user`, `pass`, `archived` FROM `router_catalog`");

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
						row.getValue(2),
						SecuritySingleton.decrypt(row.getValue(3)),
						SecuritySingleton.decrypt(row.getValue(4)),
						row.getValue(5)
					);
				}
				catch(Exception e)
				{
					LogSingleton.root.error(LogSingleton.FATAL, "for catalog `" + row.getValue(0) + "`: " + e.getMessage(), e);
				}
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* REBUILD SCHEMA CACHE                                            */
		/*-----------------------------------------------------------------*/

		SchemaSingleton.rebuildSchemaCache();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void addCatalog(String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String archived) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* ADD CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		s_catalogs.put(
			externalCatalog
			,
			new Tuple(
				internalCatalog,
				jdbcUrl,
				user,
				pass,
				archived
			)
		);

		/*-----------------------------------------------------------------*/
		/* READ SCHEMA                                                     */
		/*-----------------------------------------------------------------*/

		if(DriverSingleton.isTypeOf(jdbcUrl, Jdbc.Type.SQL))
		{
			SchemaSingleton.addSchema(externalCatalog, internalCatalog);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static AbstractDriver getConnection(String catalog) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return DriverSingleton.getConnection(catalog, tuple.x, tuple.y, tuple.z, tuple.t);
	}

	/*---------------------------------------------------------------------*/

	public static boolean isTypeOf(String catalog, Jdbc.Type jdbcType) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return DriverSingleton.isTypeOf(tuple.y, jdbcType);
	}

	/*---------------------------------------------------------------------*/

	public static String getKey(String catalog) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return DriverSingleton.getKey(tuple.x, tuple.y, tuple.z);
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listCatalogs()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		String externalCatalog;

		String internalCatalog;
		String jdbcUrl;
		String user;
		String pass;
		String archived;

		for(Map.Entry<String, Tuple> entry: s_catalogs.entrySet())
		{
			externalCatalog = entry.getKey();

			internalCatalog = entry.getValue().x;
			jdbcUrl = entry.getValue().y;
			user = entry.getValue().z;
			pass = entry.getValue().t;
			archived = entry.getValue().u;

			result.append("<row>")
			      .append("<field name=\"externalCatalog\"><![CDATA[").append(externalCatalog).append("]]></field>")
			      .append("<field name=\"internalCatalog\"><![CDATA[").append(internalCatalog).append("]]></field>")
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
