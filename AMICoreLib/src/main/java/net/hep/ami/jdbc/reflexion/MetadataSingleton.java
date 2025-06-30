package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.QId;
import net.hep.ami.utility.Empty;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class MetadataSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(MetadataSingleton.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private MetadataSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void patchSchemaSingleton() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE QUERIER                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		RouterQuerier querier = new RouterQuerier();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet1 = querier.executeSQLQuery("router_entity", "SELECT `catalog`, `entity`, `rank`, `json`, `description` FROM `router_entity`");

			/*--------------------------------------------------------------------------------------------------------*/
			/* UPDATE ENTITIES                                                                                        */
			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet2 = querier.executeSQLQuery("router_field", "SELECT `catalog`, `entity`, `field`, `rank`, `json`, `description` FROM `router_field`");

			/*--------------------------------------------------------------------------------------------------------*/
			/* UPDATE COLUMNS                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet3 = querier.executeSQLQuery("router_foreign_key", "SELECT `name`, `fkCatalog`, `fkTable`, `fkColumn`, `pkCatalog`, `pkTable`, `pkColumn` FROM `router_foreign_key`");

			/*--------------------------------------------------------------------------------------------------------*/
			/* UPDATE FOREIGN KEYS                                                                                    */
			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
		}
		finally
		{
			querier.rollbackAndRelease();
		}

		/*--------------------------------------------------------------------------------------------------------*/
		/* POST TREATMENT - AUTO SCOPING                                                                          */
		/*--------------------------------------------------------------------------------------------------------*/

		Map<String, Map<String, String>> scopeMap = new HashMap<>();

		for(SchemaSingleton.Catalog catalog3: SchemaSingleton.s_catalogs.values())
			for(SchemaSingleton.Table table3: catalog3.tables.values())
				for(SchemaSingleton.Column column3: table3.columns.values())
				{
					if(column3.scope && !Empty.is(column3.scopeLabel, Empty.STRING_NULL_EMPTY_BLANK))
					{
						String entity = new QId(column3.externalCatalog, column3.entity, null).toString();

						Map<String, String> map;

						if(scopeMap.containsKey(entity)) {
							map = scopeMap.get(entity);
						}
						else {
							scopeMap.put(entity, map = new HashMap<>());
						}

						map.put(column3.scopeLabel, column3.field);
					}
				}

		/*--------------------------------------------------------------------------------------------------------*/

		for(SchemaSingleton.Catalog catalog3: SchemaSingleton.s_catalogs.values())
			for(SchemaSingleton.Table table3: catalog3.tables.values())
				for(SchemaSingleton.FrgnKeys frgnKeys3: table3.forwardFKs.values())
					for(SchemaSingleton.FrgnKey frgnKey3: frgnKeys3)
					{
						String fkEntity = new QId(frgnKey3.fkExternalCatalog, frgnKey3.fkEntity, null).toString();
						String pkEntity = new QId(frgnKey3.pkExternalCatalog, frgnKey3.pkEntity, null).toString();

						Map<String, String> fkScopes = scopeMap.get(fkEntity);
						Map<String, String> pkScopes = scopeMap.get(pkEntity);

						if(fkScopes != null && pkScopes != null)
						{
							for(Map.Entry<String, String> fkScope: fkScopes.entrySet())
							{
								for(Map.Entry<String, String> pkScope: pkScopes.entrySet())
								{
									if(fkScope.getKey().equals(pkScope.getKey()))
									{
										frgnKey3.fkScope = fkScope.getValue();
										frgnKey3.pkScope = pkScope.getValue();

										break;
									}
								}
							}
						}
					}

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			RouterQuerier.patchSchemaSingleton();
		}
		catch(Exception e)
		{
			LOG.error(LogSingleton.FATAL, "the AMI database is not properly setup", e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(value = "null, _ -> param2", pure = true)
	private static int _safeInteger(Integer i, int def)
	{
		return i != null ? i : def;
	}

	@Contract(value = "null, _ -> param2", pure = true)
	private static boolean _safeBoolean(Boolean b, boolean def)
	{
		return b != null ? b : def;
	}

	@Contract(value = "null, _ -> param2", pure = true)
	private static String _safeString(String s, String def)
	{
		return ConfigSingleton.checkString(s, def);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@SuppressWarnings("unchecked")
	public static void updateEntity(String catalog, String entity, Integer rank, String json, String description)
	{
		try
		{
			/*--------------------------------------------------------------------------------------------------------*/

			SchemaSingleton.Table table = SchemaSingleton.getEntityInfo(catalog, entity);

			/*--------------------------------------------------------------------------------------------------------*/

			Map<String, ?> map = (Map<String, ?>) JSON.parse(_safeString(json, "{}"), Map.class, false);

			if(map == null)
			{
				return;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			table.rank = _safeInteger(rank, table.rank);

			table.description = _safeString(description, "N∕A");

			/*--------------------------------------------------------------------------------------------------------*/

			table.bridge = _safeBoolean((Boolean) map.get("bridge"), false);
			table.ignoreForwardEntities = _safeBoolean((Boolean) map.get("ignoreForwardEntities"), false);
			table.ignoreBackwardEntities = _safeBoolean((Boolean) map.get("ignoreBackwardEntities"), false);

			table.hidden = _safeBoolean((Boolean) map.get("hidden"), false);
			table.adminOnly = _safeBoolean((Boolean) map.get("adminOnly"), false);

			table.viewOf = _safeBoolean((Boolean) map.get("viewOf"), false);
			table.viewOfTable = _safeString((String) map.get("viewOfTable"), "");

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@SuppressWarnings("unchecked")
	public static void updateColumn(String catalog, String entity, String field, @Nullable Integer rank, String json, String description)
	{
		try
		{
			/*--------------------------------------------------------------------------------------------------------*/

			SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(catalog, entity, field);

			/*--------------------------------------------------------------------------------------------------------*/

			Map<String, ?> map = (Map<String, ?>) JSON.parse(_safeString(json, "{}"), Map.class, false);

			if(map == null)
			{
				return;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			column.rank = _safeInteger(rank, column.rank);

			column.description = _safeString(description, "N∕A");

			/*--------------------------------------------------------------------------------------------------------*/

			column.hidden = _safeBoolean((Boolean) map.get("hidden"), false);
			column.adminOnly = _safeBoolean((Boolean) map.get("adminOnly"), false);
			column.hashed = _safeBoolean((Boolean) map.get("hashed"), false);
			column.crypted = _safeBoolean((Boolean) map.get("crypted"), false);
			column.primary = _safeBoolean((Boolean) map.get("primary"), false);
			column.scope = _safeBoolean((Boolean) map.get("scope"), false);
			column.scopeLabel = _safeString((String) map.get("scopeLabel"), "");
			column.json = _safeBoolean((Boolean) map.get("json"), false);

			column.automatic = _safeBoolean((Boolean) map.get("automatic"), false);
			column.created = _safeBoolean((Boolean) map.get("created"), false);
			column.createdBy = _safeBoolean((Boolean) map.get("createdBy"), false);
			column.modified = _safeBoolean((Boolean) map.get("modified"), false);
			column.modifiedBy = _safeBoolean((Boolean) map.get("modifiedBy"), false);

			column.statable = _safeBoolean((Boolean) map.get("statable"), false);
			column.groupable = _safeBoolean((Boolean) map.get("groupable"), false);

			column.displayQuery = _safeString((String) map.get("displayQuery"), "@NULL");

			column.webLinkScript = _safeString((String) map.get("webLinkScript"), "@NULL");

			column.media =_safeBoolean((Boolean) map.get("media"), false);
			column.base64 = _safeBoolean((Boolean) map.get("base64"), false);
			column.mime = _safeString((String) map.get("mime"), "@NULL");
			column.ctrl = _safeString((String) map.get("ctrl"), "@NULL");

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void updateForeignKeys(String name, String fkCatalog, String fkEntity, String fkField, String pkCatalog, String pkEntity, String pkField)
	{
		try
		{
			/*--------------------------------------------------------------------------------------------------------*/

			SchemaSingleton.Column fkColumn = SchemaSingleton.getFieldInfo(fkCatalog, fkEntity, fkField);
			SchemaSingleton.Column pkColumn = SchemaSingleton.getFieldInfo(pkCatalog, pkEntity, pkField);

			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/

			Map<String, SchemaSingleton.FrgnKeys> forwardFKs = SchemaSingleton.s_catalogs.get(fkColumn.externalCatalog)
			                                                                  .tables.get(fkColumn.entity)
			                                                                  .forwardFKs
			;

			Map<String, SchemaSingleton.FrgnKeys> backwardFKs = SchemaSingleton.s_catalogs.get(pkColumn.externalCatalog)
			                                                                   .tables.get(pkColumn.entity)
			                                                                   .backwardFKs
			;

			/*--------------------------------------------------------------------------------------------------------*/

			SchemaSingleton.FrgnKeys a = forwardFKs.get(fkColumn.field);

			if(a == null) {
				forwardFKs.put(fkColumn.field, a = new SchemaSingleton.FrgnKeys());
			}

			SchemaSingleton.FrgnKeys b = backwardFKs.get(pkColumn.field);

			if(b == null) {
				backwardFKs.put(pkColumn.field, b = new SchemaSingleton.FrgnKeys());
			}

			/*--------------------------------------------------------------------------------------------------------*/

			a.add(frgnKey);
			b.add(frgnKey);

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
