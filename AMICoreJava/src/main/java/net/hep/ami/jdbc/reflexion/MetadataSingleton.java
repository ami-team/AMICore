package net.hep.ami.jdbc.reflexion;

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

			RowSet rowSet1 = router.executeSQLQuery("router_entity", "SELECT `catalog`, `entity`, `rank`, `isBridge`, `description` FROM `router_entity`");

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

			RowSet rowSet2 = router.executeSQLQuery("router_field", "SELECT `catalog`, `entity`, `field`, `rank`, `isHidden`, `isAdminOnly`, `isCrypted`, `isPrimary`, `isReadable`, `isAutomatic`, `isCreated`, `isCreatedBy`, `isModified`, `isModifiedBy`, `isStatable`, `isGroupable`, `isDisplayable`, `isBase64`, `mime`, `ctrl`, `description`, `webLinkScript` FROM `router_field`");

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

			RowSet rowSet3 = router.executeSQLQuery("router_foreign_key", "SELECT `name`, `fkCatalog`, `fkTable`, `fkColumn`, `pkCatalog`, `pkTable`, `pkColumn` FROM `router_foreign_key`");

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
		try
		{
			/*-------------------------------------------------------------*/

			SchemaSingleton.Table table = SchemaSingleton.getEntityInfo(catalog, entity);

			/*-------------------------------------------------------------*/

			if(rank != null)
			{
				table.rank = rank;
			}

			table.bridge = bridge;

			table.description = (description != null) ? description.trim() : "N∕A";

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static void updateColumn(String catalog, String entity, String field, @Nullable Integer rank, boolean hidden, boolean adminOnly, boolean crypted, boolean primary, boolean readable, boolean automatic, boolean created, boolean createdBy, boolean modified, boolean modifiedBy, boolean statable, boolean groupable, boolean displayable, boolean base64, String mime, String ctrl, String description, String webLinkScript)
	{
		try
		{
			/*-------------------------------------------------------------*/

			SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(catalog, entity, field);

			/*-------------------------------------------------------------*/

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

			/*-------------------------------------------------------------*/
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

			SchemaSingleton.Column fkColumn = SchemaSingleton.getFieldInfo(fkCatalog, fkEntity, fkField);
			SchemaSingleton.Column pkColumn = SchemaSingleton.getFieldInfo(pkCatalog, pkEntity, pkField);

			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKey frgnKey = new SchemaSingleton.FrgnKey(
				name,
				fkColumn.externalCatalog,
				fkColumn.internalCatalog,
				fkColumn.entity,
				fkColumn.field,
				pkColumn.externalCatalog,
				pkColumn.internalCatalog,
				pkColumn.entity,
				pkColumn.field
			);

			/*-------------------------------------------------------------*/

			SchemaSingleton.s_catalogs.get(fkColumn.externalCatalog)
			                          .tables.get(fkColumn.entity)
			                          .forwardFKs.get(fkColumn.field)
			                          .add(frgnKey)
			;

			SchemaSingleton.s_catalogs.get(pkColumn.externalCatalog)
			                          .tables.get(pkColumn.entity)
			                          .backwardFKs.get(pkColumn.field)
			                          .add(frgnKey)
			;

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/
}
