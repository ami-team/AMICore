package net.hep.ami.jdbc;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.reflexion.*;

public class CatalogSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class Tuple extends Tuple8<String, String, String, String, String, String, String, Boolean>
	{
		private static final long serialVersionUID = -7534852988258983396L;

		public Tuple(String _x, String _y, String _z, String _t, String _u, String _v, String _w, boolean _a)
		{
			super(_x, _y, _z, _t, _u, _v, _w, _a);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_catalogs = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, true, true);

	/*---------------------------------------------------------------------*/

	private CatalogSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reload(false);
	}

	/*---------------------------------------------------------------------*/

	public static void reload(boolean full)
	{
		s_catalogs.clear();

		SchemaSingleton.clear();

		try
		{
			CatalogSingleton.addCatalogs();

			SchemaSingleton.rebuildSchemas(full);

			MetadataSingleton.patchSchemaSingleton();
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

		Router router = new Router(
			"self",
			ConfigSingleton.getProperty("router_catalog"),
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

			RowSet rowSet = router.executeSQLQuery("SELECT `externalCatalog`, `internalCatalog`, `internalSchema`, `jdbcUrl`, `user`, `pass`, `description`, `archived` FROM `router_catalog`");

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
						row.getValue(3),
						row.getValue(4),
						row.getValue(5),
						row.getValue(6),
						row.getValue(7, false)
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
			router.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void addCatalog(String externalCatalog, String internalCatalog, String internalSchema, String jdbcUrl, String user, String pass, String description, boolean archived) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* ADD CATALOG                                                     */
		/*-----------------------------------------------------------------*/

		if("@NULL".equalsIgnoreCase(internalSchema))
		{
			internalSchema = null;
		}

		/*-----------------------------------------------------------------*/

		s_catalogs.put(
			externalCatalog
			,
			new Tuple(
				externalCatalog,
				internalCatalog,
				internalSchema,
				jdbcUrl,
				user,
				pass,
				description,
				archived
			)
		);

		/*-----------------------------------------------------------------*/
		/* ADD SCHEMA                                                      */
		/*-----------------------------------------------------------------*/

		if(DriverSingleton.getType(jdbcUrl) == DriverMetadata.Type.SQL)
		{
			SchemaSingleton.addSchema(externalCatalog, internalCatalog);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple getTuple(String catalog) throws Exception
	{
		Tuple tuple = s_catalogs.get(catalog);

		if(tuple == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return tuple;
	}

	/*---------------------------------------------------------------------*/

	public static AbstractDriver getConnection(String catalog, String AMIUser, boolean isAdmin, boolean links) throws Exception
	{
		Tuple tuple = getTuple(catalog);

		return DriverSingleton.getConnection(tuple.x, tuple.y, tuple.t, tuple.u, tuple.v, AMIUser, isAdmin, links);
	}

	/*---------------------------------------------------------------------*/

	public static DriverMetadata.Type getType(String catalog) throws Exception
	{
		Tuple tuple = getTuple(catalog);

		return DriverSingleton.getType(tuple.t);
	}

	/*---------------------------------------------------------------------*/

	public static String getProto(String catalog) throws Exception
	{
		Tuple tuple = getTuple(catalog);

		return DriverSingleton.getProto(tuple.t);
	}

	/*---------------------------------------------------------------------*/

	public static String getKey(String catalog) throws Exception
	{
		Tuple tuple = getTuple(catalog);

		return DriverSingleton.getKey(tuple.y, tuple.t, tuple.u , tuple.v);
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listCatalogs()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"catalogs\">");

		for(Tuple tuple: s_catalogs.values())
		{
			result.append("<row>")
			      .append("<field name=\"externalCatalog\"><![CDATA[").append(tuple.x).append("]]></field>")
			      .append("<field name=\"internalCatalog\"><![CDATA[").append(tuple.y).append("]]></field>")
			      .append("<field name=\"internalSchema\"><![CDATA[").append(tuple.z).append("]]></field>")
			      .append("<field name=\"description\"><![CDATA[").append(tuple.w).append("]]></field>")
			      .append("<field name=\"archived\"><![CDATA[").append(tuple.a).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
