package net.hep.ami.jdbc;

import lombok.*;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.reflexion.*;

import org.jetbrains.annotations.*;

public class CatalogSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static final class CatalogDescr
	{
		@NotNull private final String externalCatalog;
		@NotNull private final String internalCatalog;
		@Nullable private final String internalSchema;
		@NotNull private final String jdbcUrl;
		@Nullable private final String username;
		@Nullable private final String password;
		@Nullable private final String description;
		/*----*/ private final boolean archived;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, CatalogDescr> s_catalogs = new AMIMap<>(AMIMap.Type.CONCURRENT_HASH_MAP, true, true);

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private CatalogSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		reload(false);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	static void addCatalogs() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE QUERIER                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		Router router = new Router();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet = router.executeSQLQuery("router_catalog", "SELECT `externalCatalog`, `internalCatalog`, `internalSchema`, `jdbcUrl`, `user`, `pass`, `description`, `archived` FROM `router_catalog`");

			/*--------------------------------------------------------------------------------------------------------*/
			/* ADD CATALOGS                                                                                           */
			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
		}
		finally
		{
			router.rollbackAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addCatalog(@NotNull String externalCatalog, @NotNull String internalCatalog, @NotNull String internalSchema, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, @NotNull String description, boolean archived) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD CATALOG                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		if("@NULL".equalsIgnoreCase(internalSchema))
		{
			internalSchema = null;
		}

		if("@NULL".equalsIgnoreCase(user))
		{
			user = null;
		}

		if("@NULL".equalsIgnoreCase(pass))
		{
			pass = null;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		s_catalogs.put(
			externalCatalog
			,
			new CatalogDescr(
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

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD SCHEMA                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		if(DriverSingleton.getType(jdbcUrl) == DriverMetadata.Type.SQL)
		{
			SchemaSingleton.addSchema(externalCatalog, internalCatalog);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static CatalogSingleton.CatalogDescr getCatalogDescr(@NotNull String catalog) throws Exception
	{
		CatalogDescr catalogDescr = s_catalogs.get(catalog);

		if(catalogDescr == null)
		{
			throw new Exception("unknown catalog `" + catalog + "`");
		}

		return catalogDescr;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static AbstractDriver getConnection(@NotNull String catalog, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		CatalogDescr catalogDescr = getCatalogDescr(catalog);

		return DriverSingleton.getConnection(catalogDescr.getExternalCatalog(), catalogDescr.getInternalCatalog(), catalogDescr.getJdbcUrl(), catalogDescr.getUsername(), catalogDescr.getPassword(), AMIUser, timeZone, flags);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static DriverMetadata.Type getType(@NotNull String catalog) throws Exception
	{
		CatalogDescr catalogDescr = getCatalogDescr(catalog);

		return DriverSingleton.getType(catalogDescr.getJdbcUrl());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getProto(@NotNull String catalog) throws Exception
	{
		CatalogDescr catalogDescr = getCatalogDescr(catalog);

		return DriverSingleton.getProto(catalogDescr.getJdbcUrl());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getClass(@NotNull String catalog) throws Exception
	{
		CatalogDescr catalogDescr = getCatalogDescr(catalog);

		return DriverSingleton.getClass(catalogDescr.getJdbcUrl());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	////////
	public static int getFlags(@NotNull String catalog) throws Exception
	{
		CatalogDescr catalogDescr = getCatalogDescr(catalog);

		return DriverSingleton.getFlags(catalogDescr.getJdbcUrl());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getKey(@NotNull String catalog) throws Exception
	{
		CatalogDescr catalogDescr = getCatalogDescr(catalog);

		return DriverSingleton.getKey(catalogDescr.getInternalCatalog(), catalogDescr.getJdbcUrl(), catalogDescr.getUsername() , catalogDescr.getPassword());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<String> resolve(@NotNull String catalogPattern)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Pattern pattern = Pattern.compile(catalogPattern, Pattern.CASE_INSENSITIVE);

		/*------------------------------------------------------------------------------------------------------------*/

		List<String> result = new ArrayList<>();

		for(String catalog: s_catalogs.keySet())
		{
			if(pattern.matcher(catalog).matches())
			{
				result.add(catalog);
			}
		}

		return result;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder listCatalogs()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"catalogs\">");

		for(CatalogDescr catalogDescr: s_catalogs.values())
		{
			result.append("<row>")
			      .append("<field name=\"externalCatalog\"><![CDATA[").append(catalogDescr.getExternalCatalog()).append("]]></field>")
			      .append("<field name=\"internalCatalog\"><![CDATA[").append(catalogDescr.getInternalCatalog()).append("]]></field>")
			      .append("<field name=\"internalSchema\"><![CDATA[").append(catalogDescr.getInternalSchema()).append("]]></field>")
			      .append("<field name=\"description\"><![CDATA[").append(catalogDescr.getDescription()).append("]]></field>")
			      .append("<field name=\"archived\"><![CDATA[").append(catalogDescr.isArchived()).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
