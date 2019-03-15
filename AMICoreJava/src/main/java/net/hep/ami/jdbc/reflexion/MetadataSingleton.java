package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

public class MetadataSingleton
{
	/*---------------------------------------------------------------------*/

	private MetadataSingleton() {}

	/*---------------------------------------------------------------------*/

	public static void patchSchemaSingleton() throws Exception
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

			RowSet rowSet1 = router.executeSQLQuery("SELECT `catalog`, `entity`, `rank`, `isBridge`, `description` FROM `router_entity`");

			/*-------------------------------------------------------------*/
			/* UPDATE ENTITIES                                             */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet1.iterate())
			{
				updateEntity(
					row.getValue(0),
					row.getValue(1),
					row.getValue(2, (Integer) null),
					row.getValue(3, false),
					row.getValue(4)
				);
			}

			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet2 = router.executeSQLQuery("SELECT `catalog`, `entity`, `field`, `rank`, `isHidden`, `isAdminOnly`, `isCrypted`, `isPrimary`, `isReadable`, `isAutomatic`, `isCreated`, `isCreatedBy`, `isModified`, `isModifiedBy`, `isStatable`, `isGroupable`, `isDisplayable`, `isBase64`, `mime`, `ctrl`, `description`, `webLinkScript` FROM `router_field`");

			/*-------------------------------------------------------------*/
			/* UPDATE COLUMNS                                              */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet2.iterate())
			{
				updateColumn(
					row.getValue(0),
					row.getValue(1),
					row.getValue(2),
					row.getValue(3, (Integer) null),
					row.getValue(4, false),
					row.getValue(5, false),
					row.getValue(6, false),
					row.getValue(7, false),
					row.getValue(8, false),
					row.getValue(9, false),
					row.getValue(10, false),
					row.getValue(11, false),
					row.getValue(12, false),
					row.getValue(13, false),
					row.getValue(14, false),
					row.getValue(15, false),
					row.getValue(16, false),
					row.getValue(17, false),
					row.getValue(18),
					row.getValue(19),
					row.getValue(20),
					row.getValue(21)
				);
			}

			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet3 = router.executeSQLQuery("SELECT `name`, `fkCatalog`, `fkTable`, `fkColumn`, `pkCatalog`, `pkTable`, `pkColumn` FROM `router_foreign_key`");

			/*-------------------------------------------------------------*/
			/* UPDATE FOREIGN KEYS                                         */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet3.iterate())
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

		try
		{
			Router.patchSchemaSingleton();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "the AMI database is not properly setup", e);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void updateEntity(String catalog, String entity, Integer rank, boolean bridge, String description)
	{
		if(rank != null)
		{
			/* TODO */
		}

		/* TODO */
	}

	/*---------------------------------------------------------------------*/

	public static void updateColumn(String catalog, String entity, String field, @Nullable Integer rank, boolean hidden, boolean adminOnly, boolean crypted, boolean primary, boolean readable, boolean automatic, boolean created, boolean createdBy, boolean modified, boolean modifiedBy, boolean statable, boolean groupable, boolean displayable, boolean base64, String mime, String ctrl, String description, String webLinkScript)
	{
		try
		{
			SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(catalog, entity, field);

			if(rank != null)
			{
				column.rank = rank;
			}

			column.hidden = hidden;
			column.crypted = crypted;
			column.adminOnly = adminOnly;
			column.primary = primary;
			column.readable = readable;

			column.automatic = automatic;
			column.created = created;
			column.createdBy = createdBy;
			column.modified = modified;
			column.modifiedBy = modifiedBy;

			column.statable = statable;
			column.groupable = groupable;

			column.displayable = displayable;
			column.base64 = base64;
			column.mime = (mime != null) ? mime.trim() : "@NULL";
			column.ctrl = (ctrl != null) ? ctrl.trim() : "@NULL";

			column.description = (description != null) ? description.trim() : "N∕A";
			column.webLinkScript = (webLinkScript != null) ? webLinkScript.trim() : "@NULL";
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static void updateForeignKeys(String name, String fkCatalog, String fkEntity, String fkField, String pkCatalog, String pkEntity, String pkField)
	{
		try
		{
			/*-------------------------------------------------------------*/

			SchemaSingleton.Column column1 = SchemaSingleton.getFieldInfo(fkCatalog, fkEntity, fkField);
			SchemaSingleton.Column column2 = SchemaSingleton.getFieldInfo(pkCatalog, pkEntity, pkField);

			/*-------------------------------------------------------------*/
			/* STEP 1                                                      */
			/*-------------------------------------------------------------*/

			Map<String, Map<String, SchemaSingleton.FrgnKeys>> b1 = SchemaSingleton.s_forwardFKs.get(column1.externalCatalog);
			Map<String, Map<String, SchemaSingleton.FrgnKeys>> b2 = SchemaSingleton.s_backwardFKs.get(column2.externalCatalog);

			if(b1 == null) {
				SchemaSingleton.s_forwardFKs.put(column1.externalCatalog, b1 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			if(b2 == null) {
				SchemaSingleton.s_backwardFKs.put(column2.externalCatalog, b2 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			/*-------------------------------------------------------------*/
			/* STEP 2                                                      */
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
			/* STEP 3                                                      */
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
			/* STEP 4                                                      */
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
}
