package net.hep.ami.jdbc.reflexion;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

public class SchemaSingleton
{
	/*---------------------------------------------------------------------*/

	public static class Column
	{
		public String internalCatalog;
		public String catalog;
		public String table;
		public String name;
		public String type;
		public int size;
		public int digits;

		public Column(String _internalCatalog, String _catalog, String _table, String _name, String _type, int _size, int _digits)
		{
			internalCatalog = _internalCatalog;
			catalog = _catalog;
			table = _table;
			name = _name;
			type = _type;
			size = _size;
			digits = _digits;
		}
	}

	/*---------------------------------------------------------------------*/

	public static class FrgnKey
	{
		public String catalog;
		public String name;
		public String fkTable;
		public String fkColumn;
		public String pkTable;
		public String pkColumn;

		public FrgnKey(String _catalog, String _name, String _fkTable, String _fkColumn, String _pkTable, String _pkColumn)
		{
			catalog = _catalog;
			name = _name;
			fkTable = _fkTable;
			fkColumn = _fkColumn;
			pkTable = _pkTable;
			pkColumn = _pkColumn;
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Set<String> s_catalogs = new java.util.concurrent.ConcurrentSkipListSet<String>();

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_internalCatalogToExternalCatalog = new java.util.concurrent.ConcurrentHashMap<String, String>();
	private static final Map<String, String> s_externalCatalogToInternalCatalog = new java.util.concurrent.ConcurrentHashMap<String, String>();

	/*---------------------------------------------------------------------*/

	private static final Map<String, Map<String, Map<String, Column>>> s_columns = new java.util.concurrent.ConcurrentHashMap<String, Map<String, Map<String, Column>>>();
	private static final Map<String, Map<String, Map<String, FrgnKey>>> s_frgnKeys = new java.util.concurrent.ConcurrentHashMap<String, Map<String, Map<String, FrgnKey>>>();

	/*---------------------------------------------------------------------*/

	private static long s_executionTime = 0;

	/*---------------------------------------------------------------------*/

	private SchemaSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		try
		{
			Class.forName("net.hep.ami.jdbc.CatalogSingleton");
		}
		catch(Exception e)
		{
			/* IGNORE */
		}
	}

	/*---------------------------------------------------------------------*/

	public static void clear()
	{
		s_catalogs.clear();

		s_internalCatalogToExternalCatalog.clear();
		s_externalCatalogToInternalCatalog.clear();

		s_columns.clear();
		s_frgnKeys.clear();

		s_executionTime = 0;
	}

	/*---------------------------------------------------------------------*/

	public static void addSchema(String internalCatalog, String externalCatalog) throws Exception
	{
		if(internalCatalog != null
		   &&
		   externalCatalog != null
		 ) {
			s_catalogs.add(externalCatalog);

			s_internalCatalogToExternalCatalog.put(internalCatalog, externalCatalog);
			s_externalCatalogToInternalCatalog.put(externalCatalog, internalCatalog);

			s_columns.put(externalCatalog, new HashMap<String, Map<String, Column>>());
			s_frgnKeys.put(externalCatalog, new HashMap<String, Map<String, FrgnKey>>());
		}
		else
		{
			throw new Exception("no metadata information");
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readMetaData(String externalCatalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		String internalCatalog = s_externalCatalogToInternalCatalog.get(externalCatalog);

		if(internalCatalog == null)
		{
				return;
		}

		/*-----------------------------------------------------------------*/

		if(s_columns.get(externalCatalog).isEmpty() == false
		   ||
		   s_frgnKeys.get(externalCatalog).isEmpty() == false
		 ) {
			return;
		}

		/*-----------------------------------------------------------------*/
		/* INITIALIZE STRUCTURES                                           */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass driver1 = CatalogSingleton.getConnection(externalCatalog);

		try
		{
			long t1 = System.currentTimeMillis();

			/**/	/*-----------------------------------------------------*/
			/**/
			/**/	@SuppressWarnings("deprecation") DatabaseMetaData metaData = driver1.getConnection().getMetaData();
			/**/
			/**/	ResultSet resultSet = metaData.getTables(internalCatalog, internalCatalog, "%", null);
			/**/
			/**/	Set<String> tables = new HashSet<String>();
			/**/
			/**/	/*-----------------------------------------------------*/
			/**/
			/**/	while(resultSet.next())
			/**/	{
			/**/		String name = resultSet.getString("TABLE_NAME");
			/**/
			/**/		if(name != null && name.startsWith("db_") == false && name.startsWith("x_db_") == false)
			/**/		{
			/**/			name = name.toLowerCase();
			/**/
			/**/			s_columns.get(externalCatalog).put(name, new LinkedHashMap<String, Column>());
			/**/			s_frgnKeys.get(externalCatalog).put(name, new LinkedHashMap<String, FrgnKey>());
			/**/
			/**/			tables.add(name);
			/**/		}
			/**/	}
			/**/
			/**/	/*-----------------------------------------------------*/
			/**/
			/**/	readColumnMetaData(metaData, internalCatalog, externalCatalog, "%");
			/**/
			/**/	for(String name: tables)
			/**/	{
			/**/		readFgnKeyMetaData(metaData, internalCatalog, externalCatalog, name);
			/**/	}
			/**/
			/**/	/*-----------------------------------------------------*/

			long t2 = System.currentTimeMillis();

			s_executionTime += t2 - t1;
		}
		finally
		{
			driver1.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* READ METADATA DICTIONNARY                                       */
		/*-----------------------------------------------------------------*/

		/* TODO */

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readColumnMetaData(DatabaseMetaData metaData, String internalCatalog, String externalCatalog, String _table) throws SQLException
	{
		ResultSet resultSet = metaData.getColumns(internalCatalog, internalCatalog, _table, "%");

		while(resultSet.next())
		{
			String table = resultSet.getString("TABLE_NAME");
			String name = resultSet.getString("COLUMN_NAME");
			String type = resultSet.getString("TYPE_NAME");
			int size = resultSet.getInt("COLUMN_SIZE");
			int digits = resultSet.getInt("DECIMAL_DIGITS");

			if(table != null && name != null && type != null)
			{
				table = table.toLowerCase();
				name = name.toLowerCase();
				type = type.toUpperCase();

				Map<String, Column> column = s_columns.get(externalCatalog).get(table);

				if(column != null)
				{
					column.put(name, new Column(
						internalCatalog,
						externalCatalog,
						table,
						name,
						type,
						size,
						digits
					));
				}
			}
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readFgnKeyMetaData(DatabaseMetaData metaData, String internalCatalog, String externalCatalog, String _table) throws SQLException
	{
		ResultSet resultSet = metaData.getExportedKeys(internalCatalog, internalCatalog, _table);

		while(resultSet.next())
		{
			String name = resultSet.getString("FK_NAME");
			String fktable = resultSet.getString("FKTABLE_NAME");
			String fkcolumn = resultSet.getString("FKCOLUMN_NAME");
			String pktable = resultSet.getString("PKTABLE_NAME");
			String pkcolumn = resultSet.getString("PKCOLUMN_NAME");

			if(name != null && fktable != null && fkcolumn != null && pktable != null && pkcolumn != null)
			{
				name = name.toLowerCase();
				fktable = fktable.toLowerCase();
				fkcolumn = fkcolumn.toLowerCase();
				pktable = pktable.toLowerCase();
				pkcolumn = pkcolumn.toLowerCase();

				Map<String, FrgnKey> frgnKey = s_frgnKeys.get(externalCatalog).get(fktable);

				if(frgnKey != null)
				{
					frgnKey.put(fkcolumn, new FrgnKey(
						externalCatalog,
						name,
						fktable,
						fkcolumn,
						pktable,
						pkcolumn
					));
				}
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static String internalCatalogToExternalCatalog(String catalog) throws Exception
	{
		String result = s_internalCatalogToExternalCatalog.get(catalog);

		if(result != null)
		{
			return result;
		}

		throw new Exception("internal catalog not found `" + catalog + "`");
	}

	/*---------------------------------------------------------------------*/

	public static String externalCatalogToInternalCatalog(String catalog) throws Exception
	{
		String result = s_externalCatalogToInternalCatalog.get(catalog);

		if(result != null)
		{
			return result;
		}

		throw new Exception("external catalog not found `" + catalog + "`");
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getCatalogNames()
	{
		return s_catalogs;
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getTableNames(String catalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		readMetaData(catalog);

		/*-----------------------------------------------------------------*/

		Map<String, Map<String, Column>> map = s_columns.get(catalog);

		if(map != null)
		{
			return map.keySet();
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("catalog not found `" + catalog + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getColumnNames(String catalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		readMetaData(catalog);

		/*-----------------------------------------------------------------*/

		Map<String, Map<String, Column>> map1 = s_columns.get(catalog);

		if(map1 != null)
		{
			Map<String, Column> map2 = map1.get(table.toLowerCase());

			if(map2 != null)
			{
				return map2.keySet();
			}
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, Column> getColumns(String catalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		readMetaData(catalog);

		/*-----------------------------------------------------------------*/

		Map<String, Map<String, Column>> map1 = s_columns.get(catalog);

		if(map1 != null)
		{
			Map<String, Column> map2 = map1.get(table.toLowerCase());

			if(map2 != null)
			{
				return map2;
			}
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, FrgnKey> getFgnKeys(String catalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		readMetaData(catalog);

		/*-----------------------------------------------------------------*/

		Map<String, Map<String, FrgnKey>> map1 = s_frgnKeys.get(catalog);

		if(map1 != null)
		{
			Map<String, FrgnKey> map2 = map1.get(table.toLowerCase());

			if(map2 != null)
			{
				return map2;
			}
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getDBSchemes()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		for(String catalog: s_catalogs)
		{
			try
			{
				readMetaData(catalog);
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*-----------------------------------------------------------------*/

		Column column;

		result.append("<rowset type=\"columns\">");

		for(Map.Entry<String, Map<String, Map<String, Column>>> entry1: s_columns.entrySet())
		{
			for(Map.Entry<String, Map<String, Column>> entry2: entry1.getValue().entrySet())
			{
				for(Map.Entry<String, Column> entry3: entry2.getValue().entrySet())
				{
					column = entry3.getValue();

					result.append(
						"<row>"
						+
						"<field name=\"internalCatalog\">" + column.internalCatalog + "</field>"
						+
						"<field name=\"externalCatalog\">" + column.catalog + "</field>"
						+
						"<field name=\"table\">" + column.table + "</field>"
						+
						"<field name=\"name\">" + column.name + "</field>"
						+
						"<field name=\"type\">" + column.type + "</field>"
						+
						"<field name=\"size\">" + column.size + "</field>"
						+
						"<field name=\"digits\">" + column.digits + "</field>"
						+
						"</row>"
					);
				}
			}
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		FrgnKey frgnKey;

		result.append("<rowset type=\"foreignKeys\">");

		for(Map.Entry<String, Map<String, Map<String, FrgnKey>>> entry1: s_frgnKeys.entrySet())
		{
			for(Map.Entry<String, Map<String, FrgnKey>> entry2: entry1.getValue().entrySet())
			{
				for(Map.Entry<String, FrgnKey> entry3: entry2.getValue().entrySet())
				{
					frgnKey = entry3.getValue();

					result.append(
						"<row>"
						+
						"<field name=\"catalog\">" + frgnKey.catalog + "</field>"
						+
						"<field name=\"name\">" + frgnKey.name + "</field>"
						+
						"<field name=\"fkTable\">" + frgnKey.fkTable + "</field>"
						+
						"<field name=\"fkColumn\">" + frgnKey.fkColumn + "</field>"
						+
						"<field name=\"pkTable\">" + frgnKey.pkTable + "</field>"
						+
						"<field name=\"pkColumn\">" + frgnKey.pkColumn + "</field>"
						+
						"</row>"
					);
				}
			}
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<info>" + String.format(Locale.US, "%.3f", 0.001f * s_executionTime) + " s</info>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
