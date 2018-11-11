package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

public class SchemaExtraSingleton
{
	/*---------------------------------------------------------------------*/

	private SchemaExtraSingleton() {}

	/*---------------------------------------------------------------------*/

	public static void updateSchemas() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = DriverSingleton.getConnection(
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

			RowSet rowSet1 = driver.executeSQLQuery("SELECT `catalog`, `entity`, `field`, `rank`, `isCrypted`, `isGroupable`, `isCreated`, `isCreatedBy`, `isModified`, `isModifiedBy`, `description` FROM `router_catalog_extra`");

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
					row.getValue(10)
				);
			}

			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet2 = driver.executeSQLQuery("SELECT `name`, `fkCatalog`, `fkTable`, `fkColumn`, `pkCatalog`, `pkTable`, `pkColumn` FROM `router_foreign_key`");

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
			driver.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void updateColumn(String catalog, String entity, String field, int rank, boolean crypted, boolean groupable, boolean created, boolean createdBy, boolean modified, boolean modifiedBy, String description)
	{
		try
		{
			SchemaSingleton.Column column = SchemaSingleton.getColumn(catalog, entity, field);

			column.rank = rank;
			column.crypted = crypted;
			column.groupable = groupable;
			column.created = created;
			column.createdBy = createdBy;
			column.modified = modified;
			column.modifiedBy = modifiedBy;
			column.description = description;
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

			Map<String, Map<String, SchemaSingleton.FrgnKeys>> b1 = a1.get(fkCatalog);
			Map<String, Map<String, SchemaSingleton.FrgnKeys>> b2 = a2.get(pkCatalog);

			if(b1 == null) {
				a1.put(fkCatalog, b1 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			if(b2 == null) {
				a2.put(pkCatalog, b2 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			/*-------------------------------------------------------------*/

			Map<String, SchemaSingleton.FrgnKeys> c1 = b1.get(fkTable);
			Map<String, SchemaSingleton.FrgnKeys> c2 = b2.get(pkTable);

			if(c1 == null) {
				b1.put(fkCatalog, c1 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			if(c2 == null) {
				b2.put(pkCatalog, c2 = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true));
			}

			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKeys d1 = c1.get(fkColumn);
			SchemaSingleton.FrgnKeys d2 = c2.get(pkColumn);

			if(d1 == null) {
				c1.put(fkCatalog, d1 = new SchemaSingleton.FrgnKeys());
			}

			if(d2 == null) {
				c2.put(pkCatalog, d2 = new SchemaSingleton.FrgnKeys());
			}

			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKey frgnKey = new SchemaSingleton.FrgnKey(
				name,
				fkCatalog,
				SchemaSingleton.externalCatalogToInternalCatalog(fkCatalog),
				fkTable,
				fkColumn,
				pkCatalog,
				SchemaSingleton.externalCatalogToInternalCatalog(pkCatalog),
				pkTable,
				pkColumn
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
