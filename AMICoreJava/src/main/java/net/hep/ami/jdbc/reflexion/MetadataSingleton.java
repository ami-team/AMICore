package net.hep.ami.jdbc.reflexion;

import java.util.Map;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.FrgnKeys;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.JSON;

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

		Router router = new Router();

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet1 = router.executeSQLQuery("router_entity", "SELECT `catalog`, `entity`, `rank`, `json`, `description` FROM `router_entity`");

			/*-------------------------------------------------------------*/
			/* UPDATE ENTITIES                                             */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet1.iterate())
			{
				updateEntity(
					row.getValue(0),
					row.getValue(1),
					row.getValue(2, (Integer) null),
					row.getValue(3),
					row.getValue(4)
				);
			}

			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet2 = router.executeSQLQuery("router_field", "SELECT `catalog`, `entity`, `field`, `rank`, `json`, `description` FROM `router_field`");

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
					row.getValue(4),
					row.getValue(5)
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

	@SuppressWarnings("unchecked")
	public static void updateEntity(String catalog, String entity, Integer rank, String json, String description)
	{
		try
		{
			/*-------------------------------------------------------------*/

			SchemaSingleton.Table table = SchemaSingleton.getEntityInfo(catalog, entity);

			/*-------------------------------------------------------------*/

			Map<String, ?> map = (Map<String, ?>) JSON.parse(json != null && json.isEmpty() == false && "@NULL".equalsIgnoreCase(json) == false ? json : "{}", Map.class);

			/*-------------------------------------------------------------*/

			if(rank != null)
			{
				table.rank = rank;
			}

			table.description = (description != null) ? description.trim() : "N∕A";

			/*-------------------------------------------------------------*/

			table.bridge = map.containsKey("bridge") ? (Boolean) map.get("bridge") : false;

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	@SuppressWarnings("unchecked")
	public static void updateColumn(String catalog, String entity, String field, @Nullable Integer rank, String json, String description)
	{
		try
		{
			/*-------------------------------------------------------------*/

			SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(catalog, entity, field);

			/*-------------------------------------------------------------*/

			Map<String, ?> map = (Map<String, ?>) JSON.parse(json != null && json.isEmpty() == false && "@NULL".equalsIgnoreCase(json) == false ? json : "{}", Map.class);

			/*-------------------------------------------------------------*/

			if(rank != null)
			{
				column.rank = rank;
			}

			column.description = (description != null) ? description.trim() : "N∕A";

			/*-------------------------------------------------------------*/

			column.hidden = map.containsKey("hidden") ? (Boolean) map.get("hidden") : false;
			column.crypted = map.containsKey("crypted") ? (Boolean) map.get("crypted") : false;
			column.adminOnly = map.containsKey("adminOnly") ? (Boolean) map.get("adminOnly") : false;
			column.primary = map.containsKey("primary") ? (Boolean) map.get("primary") : false;
			column.readable = map.containsKey("readable") ? (Boolean) map.get("readable") : false;

			column.automatic = map.containsKey("automatic") ? (Boolean) map.get("automatic") : false;
			column.created = map.containsKey("created") ? (Boolean) map.get("created") : false;
			column.createdBy = map.containsKey("createdBy") ? (Boolean) map.get("createdBy") : false;
			column.modified = map.containsKey("modified") ? (Boolean) map.get("modified") : false;
			column.modifiedBy = map.containsKey("modifiedBy") ? (Boolean) map.get("modifiedBy") : false;

			column.statable = map.containsKey("statable") ? (Boolean) map.get("statable") : false;
			column.groupable = map.containsKey("groupable") ? (Boolean) map.get("groupable") : false;

			column.displayable = map.containsKey("displayable") ? (Boolean) map.get("displayable") : false;
			column.base64 = map.containsKey("base64") ? (Boolean) map.get("base64") : false;
			column.mime = map.containsKey("mime") ? ((String) map.get("ctrl")).trim() : "@NULL";
			column.ctrl = map.containsKey("ctrl") ? ((String) map.get("ctrl")).trim() : "@NULL";

			column.webLinkScript = map.containsKey("webLinkScript") ? ((String) map.get("webLinkScript")).trim() : "@NULL";

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

			Map<String, FrgnKeys> forwardFKs = SchemaSingleton.s_catalogs.get(fkColumn.externalCatalog)
			                                                  .tables.get(fkColumn.entity)
			                                                  .forwardFKs
			;

			Map<String, FrgnKeys> backwardFKs = SchemaSingleton.s_catalogs.get(pkColumn.externalCatalog)
			                                                   .tables.get(pkColumn.entity)
			                                                   .backwardFKs
			;

			/*-------------------------------------------------------------*/

			FrgnKeys a = forwardFKs.get(fkColumn.field);

			if(a == null) {
				forwardFKs.put(fkColumn.field, a = new FrgnKeys());
			}

			FrgnKeys b = backwardFKs.get(pkColumn.field);

			if(b == null) {
				backwardFKs.put(pkColumn.field, b = new FrgnKeys());
			}

			/*-------------------------------------------------------------*/

			a.add(frgnKey);
			b.add(frgnKey);

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/
}
