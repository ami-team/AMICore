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

			RowSet rowSet1 = router.executeSQLQuery("SELECT `catalog`, `entity`, `field`, `rank`, `isHidden`, `isAdminOnly`, `isCrypted`, `isPrimary`, `isCreated`, `isCreatedBy`, `isModified`, `isModifiedBy`, `isStatable`, `isGroupable`, `isDisplayable`, `isBase64`, `mime`, `ctrl`, `description`, `webLinkScript` FROM `router_field`");

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
					Integer.parseInt(row.getValue(14)) != 0,
					Integer.parseInt(row.getValue(15)) != 0,
					row.getValue(16),
					row.getValue(17),
					row.getValue(18),
					row.getValue(19)
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

	public static void updateColumn(String catalog, String entity, String field, int rank, boolean hidden, boolean adminOnly, boolean crypted, boolean primary, boolean created, boolean createdBy, boolean modified, boolean modifiedBy, boolean statable, boolean groupable, boolean displayable, boolean base64, String mime, String ctrl, String description, String webLinkScript)
	{
		try
		{
			SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(catalog, entity, field);

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
			column.displayable = displayable;
			column.base64 = base64;
			column.mime = mime != null ? mime : "@NULL";
			column.ctrl = ctrl != null ? ctrl : "@NULL";
			column.description = description != null ? description.trim() : "N∕A";
			column.webLinkScript = webLinkScript != null ? webLinkScript.trim() : "@NULL";
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static void updateForeignKeys(String name, String fkCatalog, String fkEntity, String fkField, String pkCatalog, String pkEntity, String pkField)
	{
		Map<String, Map<String, Map<String, SchemaSingleton.FrgnKeys>>> a1 = SchemaSingleton.s_forwardFKs;
		Map<String, Map<String, Map<String, SchemaSingleton.FrgnKeys>>> a2 = SchemaSingleton.s_backwardFKs;

		try
		{
			/*-------------------------------------------------------------*/

			SchemaSingleton.Column column1 = SchemaSingleton.getFieldInfo(fkCatalog, fkEntity, fkField);
			SchemaSingleton.Column column2 = SchemaSingleton.getFieldInfo(pkCatalog, pkEntity, pkField);

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

			Map<String, SchemaSingleton.FrgnKeys> c1 = b1.get(column1.entity);
			Map<String, SchemaSingleton.FrgnKeys> c2 = b2.get(column2.entity);

			if(c1 == null) {
				b1.put(column1.entity, c1 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			if(c2 == null) {
				b2.put(column2.entity, c2 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKeys d1 = c1.get(column1.field);
			SchemaSingleton.FrgnKeys d2 = c2.get(column2.field);

			if(d1 == null) {
				c1.put(column1.field, d1 = new SchemaSingleton.FrgnKeys());
			}

			if(d2 == null) {
				c2.put(column2.field, d2 = new SchemaSingleton.FrgnKeys());
			}

			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKey frgnKey = new SchemaSingleton.FrgnKey(
				name,
				column1.externalCatalog,
				column1.internalCatalog,
				column1.entity,
				column1.field,
				column2.externalCatalog,
				column2.internalCatalog,
				column2.entity,
				column2.field
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

		Map<String, SchemaSingleton.Column> router_config = SchemaSingleton.getEntityInfo("self", "router_config");

		router_config.get("paramName").crypted = true;
		router_config.get("paramValue").crypted = true;
		router_config.get("created").created = true;
		router_config.get("createdBy").createdBy = true;
		router_config.get("modified").modified = true;
		router_config.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_catalog = SchemaSingleton.getEntityInfo("self", "router_catalog");

		router_catalog.get("internalCatalog").hidden = true;
		router_catalog.get("internalSchema").hidden = true;
		router_catalog.get("jdbcUrl").adminOnly = true;
		router_catalog.get("user").crypted = true;
		router_catalog.get("pass").crypted = true;
		router_catalog.get("archived").groupable = true;
		router_catalog.get("created").created = true;
		router_catalog.get("createdBy").createdBy = true;
		router_catalog.get("modified").modified = true;
		router_catalog.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_entity = SchemaSingleton.getEntityInfo("self", "router_entity");

		router_entity.get("isBridge").groupable = true;
		router_entity.get("created").created = true;
		router_entity.get("createdBy").createdBy = true;
		router_entity.get("modified").modified = true;
		router_entity.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_field = SchemaSingleton.getEntityInfo("self", "router_field");

		router_field.get("isHidden").groupable = true;
		router_field.get("isAdminOnly").groupable = true;
		router_field.get("isCrypted").groupable = true;
		router_field.get("isPrimary").groupable = true;
		router_field.get("isCreated").groupable = true;
		router_field.get("isCreatedBy").groupable = true;
		router_field.get("isModified").groupable = true;
		router_field.get("isModifiedBy").groupable = true;
		router_field.get("isStatable").groupable = true;
		router_field.get("isGroupable").groupable = true;
		router_field.get("isDisplayable").groupable = true;
		router_field.get("isBase64").groupable = true;
		router_field.get("created").created = true;
		router_field.get("createdBy").createdBy = true;
		router_field.get("modified").modified = true;
		router_field.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_foreign_key = SchemaSingleton.getEntityInfo("self", "router_foreign_key");

		router_foreign_key.get("created").created = true;
		router_foreign_key.get("createdBy").createdBy = true;
		router_foreign_key.get("modified").modified = true;
		router_foreign_key.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_command = SchemaSingleton.getEntityInfo("self", "router_command");

		router_command.get("visible").groupable = true;
		router_command.get("secured").groupable = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_user = SchemaSingleton.getEntityInfo("self", "router_user");

		router_user.get("AMIPass").crypted = true;
		router_user.get("clientDN").crypted = true;
		router_user.get("issuerDN").crypted = true;
		router_user.get("valid").groupable = true;
		router_user.get("created").created = true;
		router_user.get("modified").modified = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_short_url = SchemaSingleton.getEntityInfo("self", "router_short_url");

		router_short_url.get("created").created = true;
		router_short_url.get("modified").modified = true;

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> router_authority = SchemaSingleton.getEntityInfo("self", "router_authority");

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

		Map<String, SchemaSingleton.Column> router_search_interface = SchemaSingleton.getEntityInfo("self", "router_search_interface");

		router_search_interface.get("archived").groupable = true;
		router_search_interface.get("created").created = true;
		router_search_interface.get("createdBy").createdBy = true;
		router_search_interface.get("modified").modified = true;
		router_search_interface.get("modifiedBy").modifiedBy = true;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
