package net.hep.ami.jdbc;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.reflexion.*;

public class CatalogSingleton
{
	/*---------------------------------------------------------------------*/

	private static final class Tuple extends Tuple6<String, String, String, String, String, String>
	{
		public Tuple(String _x, String _y, String _z, String _t, String _u, String _v)
		{
			super(_x, _y, _z, _t, _u, _v);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_catalogs = new AMIMap<>();

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
		s_catalogs.clear();

		SchemaSingleton.clear();

		try
		{
			CatalogSingleton.addCatalogs();

			SchemaSingleton.rebuildSchemas();
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

			for(Row row: rowSet.iterate())
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
					LogSingleton.root.error(LogSingleton.FATAL, "for catalog `{}`", row.getValue(0), e);
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

	private static void addCatalog(String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String archived) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* ADD CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		s_catalogs.put(
			externalCatalog
			,
			new Tuple(
				externalCatalog,
				internalCatalog,
				jdbcUrl,
				user,
				pass,
				archived
			)
		);

		/*-----------------------------------------------------------------*/
		/* ADD SCHEMA                                                      */
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

		return DriverSingleton.getConnection(tuple.x, tuple.y, tuple.z, tuple.t, tuple.u);
	}

	/*---------------------------------------------------------------------*/

	public static boolean isTypeOf(String catalog, Jdbc.Type jdbcType) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return DriverSingleton.isTypeOf(tuple.z, jdbcType);
	}

	/*---------------------------------------------------------------------*/

	public static String getKey(String catalog) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return DriverSingleton.getKey(tuple.y, tuple.z, tuple.t, tuple.u);
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listCatalogs()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(Tuple tuple: s_catalogs.values())
		{
			result.append("<row>")
			      .append("<field name=\"externalCatalog\"><![CDATA[").append(tuple.x).append("]]></field>")
			      .append("<field name=\"internalCatalog\"><![CDATA[").append(tuple.y).append("]]></field>")
			      .append("<field name=\"jdbcUrl\"><![CDATA[").append(tuple.z).append("]]></field>")
			      .append("<field name=\"user\"><![CDATA[").append(tuple.t).append("]]></field>")
			      .append("<field name=\"pass\"><![CDATA[").append(tuple.u).append("]]></field>")
			      .append("<field name=\"archived\"><![CDATA[").append(tuple.v).append("]]></field>")
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
