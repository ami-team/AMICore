package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

public class SchemaExtraInfoSingleton
{
	/*---------------------------------------------------------------------*/

	private SchemaExtraInfoSingleton() {}

	/*---------------------------------------------------------------------*/

	public static void updateSchemas() throws Exception
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

			RowSet rowSet1 = router.executeSQLQuery("SELECT `catalog`, `entity`, `field`, `rank`, `isHidden`, `isAdminOnly`, `isCrypted`, `isPrimary`, `isCreated`, `isCreatedBy`, `isModified`, `isModifiedBy`, `isStatable`, `isGroupable`, `description`, `webLinkScript` FROM `router_catalog_extra`");

			/*-------------------------------------------------------------*/
			/* UPDATE COLUMN                                               */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet1.iterate())
			{
				updateColumn(
					row.getValue(0),
					row.getValue(1),
					row.getValue(2),
					Integer.parseInt(row.getValue(3)),
					Integer.parseInt(row.getValue(4)) != 0,
					Integer.parseInt(row.getValue(5)) != 0,
					Integer.parseInt(row.getValue(6)) != 0,
					Integer.parseInt(row.getValue(7)) != 0,
					Integer.parseInt(row.getValue(8)) != 0,
					Integer.parseInt(row.getValue(9)) != 0,
					Integer.parseInt(row.getValue(10)) != 0,
					Integer.parseInt(row.getValue(11)) != 0,
					Integer.parseInt(row.getValue(12)) != 0,
					Integer.parseInt(row.getValue(13)) != 0,
					row.getValue(14),
					row.getValue(15)
				);
			}

			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet2 = router.executeSQLQuery("SELECT `name`, `fkCatalog`, `fkTable`, `fkColumn`, `pkCatalog`, `pkTable`, `pkColumn` FROM `router_foreign_key`");

			/*-------------------------------------------------------------*/
			/* UPDATE FOREIGN KEYS                                         */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet2.iterate())
			{
				updateForeignKeys(
					row.getValue(0),
					row.getValue(1),
					row.getValue(2),
					row.getValue(3),
					row.getValue(4),
					row.getValue(5),
					row.getValue(6)
				);
			}

			/*-------------------------------------------------------------*/
		}
		finally
		{
			router.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* DEFAULT INFO                                                    */
		/*-----------------------------------------------------------------*/

		try
		{
			defaultInfo();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "the AMI database is not properly setup", e);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void updateColumn(String catalog, String entity, String field, int rank, boolean hidden, boolean adminOnly, boolean crypted, boolean primary, boolean created, boolean createdBy, boolean modified, boolean modifiedBy, boolean statable, boolean groupable, String description, String webLinkScript)
	{
		try
		{
			SchemaSingleton.Column column = SchemaSingleton.getColumn(catalog, entity, field);

			column.rank = rank;
			column.hidden = hidden;
			column.crypted = crypted;
			column.adminOnly = adminOnly;
			column.primary = primary;
			column.created = created;
			column.createdBy = createdBy;
			column.modified = modified;
			column.modifiedBy = modifiedBy;
			column.statable = statable;
			column.groupable = groupable;
			column.description = description != null ? description.trim() : "N∕A";
			column.webLinkScript = webLinkScript != null ? webLinkScript.trim() : "@NULL";
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static void updateForeignKeys(String name, String fkCatalog, String fkTable, String fkColumn, String pkCatalog, String pkTable, String pkColumn)
	{
		Map<String, Map<String, Map<String, SchemaSingleton.FrgnKeys>>> a1 = SchemaSingleton.s_forwardFKs;
		Map<String, Map<String, Map<String, SchemaSingleton.FrgnKeys>>> a2 = SchemaSingleton.s_backwardFKs;

		try
		{
			/*-------------------------------------------------------------*/

			SchemaSingleton.Column column1 = SchemaSingleton.getColumn(fkCatalog, fkTable, fkColumn);
			SchemaSingleton.Column column2 = SchemaSingleton.getColumn(pkCatalog, pkTable, pkColumn);

			/*-------------------------------------------------------------*/

			Map<String, Map<String, SchemaSingleton.FrgnKeys>> b1 = a1.get(column1.externalCatalog);
			Map<String, Map<String, SchemaSingleton.FrgnKeys>> b2 = a2.get(column2.externalCatalog);

			if(b1 == null) {
				a1.put(column1.externalCatalog, b1 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			if(b2 == null) {
				a2.put(column2.externalCatalog, b2 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			/*-------------------------------------------------------------*/

			Map<String, SchemaSingleton.FrgnKeys> c1 = b1.get(column1.table);
			Map<String, SchemaSingleton.FrgnKeys> c2 = b2.get(column2.table);

			if(c1 == null) {
				b1.put(column1.table, c1 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			if(c2 == null) {
				b2.put(column2.table, c2 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKeys d1 = c1.get(column1.name);
			SchemaSingleton.FrgnKeys d2 = c2.get(column2.name);

			if(d1 == null) {
				c1.put(column1.name, d1 = new SchemaSingleton.FrgnKeys());
			}

			if(d2 == null) {
				c2.put(column2.name, d2 = new SchemaSingleton.FrgnKeys());
			}

			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKey frgnKey = new SchemaSingleton.FrgnKey(
				name,
				column1.externalCatalog,
				column1.internalCatalog,
				column1.table,
				column1.name,
				column2.externalCatalog,
				column2.internalCatalog,
				column2.table,
				column2.name
			);

			d1.add(frgnKey);
			d2.add(frgnKey);

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static void defaultInfo() throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_config = SchemaSingleton.getColumns("self", "router_config");

		router_config.get("paramName").crypted = true;
		router_config.get("paramValue").crypted = true;
		router_config.get("created").created = true;
		router_config.get("createdBy").createdBy = true;
		router_config.get("modified").modified = true;
		router_config.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_catalog = SchemaSingleton.getColumns("self", "router_catalog");

		router_catalog.get("internalCatalog").hidden = true;
		router_catalog.get("internalSchema").hidden = true;
		router_catalog.get("jdbcUrl").adminOnly = true;
		router_catalog.get("user").crypted = true;
		router_catalog.get("pass").crypted = true;
		router_catalog.get("created").created = true;
		router_catalog.get("createdBy").createdBy = true;
		router_catalog.get("modified").modified = true;
		router_catalog.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_catalog_extra = SchemaSingleton.getColumns("self", "router_catalog_extra");

		router_catalog_extra.get("created").created = true;
		router_catalog_extra.get("createdBy").createdBy = true;
		router_catalog_extra.get("modified").modified = true;
		router_catalog_extra.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_foreign_key = SchemaSingleton.getColumns("self", "router_foreign_key");

		router_foreign_key.get("created").created = true;
		router_foreign_key.get("createdBy").createdBy = true;
		router_foreign_key.get("modified").modified = true;
		router_foreign_key.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_user = SchemaSingleton.getColumns("self", "router_user");

		router_user.get("AMIPass").crypted = true;
		router_user.get("clientDN").crypted = true;
		router_user.get("issuerDN").crypted = true;

		router_user.get("created").created = true;
		router_user.get("modified").modified = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_short_url = SchemaSingleton.getColumns("self", "router_short_url");

		router_short_url.get("created").created = true;
		router_short_url.get("modified").modified = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_authority = SchemaSingleton.getColumns("self", "router_authority");

		router_authority.get("vo").adminOnly = true;
		router_authority.get("clientDN").adminOnly = true;
		router_authority.get("issuerDN").adminOnly = true;
		router_authority.get("notBefore").adminOnly = true;
		router_authority.get("notAfter").adminOnly = true;
		router_authority.get("email").adminOnly = true;
		router_authority.get("reason").adminOnly = true;
		router_authority.get("created").created = true;
		router_authority.get("createdBy").createdBy = true;
		router_authority.get("modified").modified = true;
		router_authority.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_search_interface = SchemaSingleton.getColumns("self", "router_search_interface");

		router_search_interface.get("created").created = true;
		router_search_interface.get("createdBy").createdBy = true;
		router_search_interface.get("modified").modified = true;
		router_search_interface.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
