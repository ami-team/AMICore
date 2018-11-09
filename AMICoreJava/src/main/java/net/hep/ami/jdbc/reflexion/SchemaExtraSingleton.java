package net.hep.ami.jdbc.reflexion;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

public class SchemaExtraSingleton
{
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

			RowSet rowSet = driver.executeSQLQuery("SELECT `catalog`, `entity`, `field`, `rank`, `isCrypted`, `isGroupable`, `isCreatedBy`, `isModifiedBy`, `description` FROM `router_catalog_extra`");

			/*-------------------------------------------------------------*/
			/* UPDATE COLUMN                                               */
			/*-------------------------------------------------------------*/

			for(Row row: rowSet.iterate())
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
					row.getValue(8)
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

	public static void updateColumn(String catalog, String entity, String field, int rank, boolean crypted, boolean groupable, boolean createdBy, boolean modifiedBy, String description)
	{
		try
		{
			SchemaSingleton.Column column = SchemaSingleton.getColumn(catalog, entity, field);

			column.rank = rank;
			column.crypted = crypted;
			column.groupable = groupable;
			column.createdBy = createdBy;
			column.modifiedBy = modifiedBy;
			column.description = description;
		}
		catch(Exception e)
		{
			LogSingleton.root.warn(e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/
}
