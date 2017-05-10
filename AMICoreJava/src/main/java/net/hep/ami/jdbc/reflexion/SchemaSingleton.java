package net.hep.ami.jdbc.reflexion;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

public class SchemaSingleton
{
	/*---------------------------------------------------------------------*/

	public static class CIHM<U> extends LinkedHashMap<String, U>
	{
		private static final long serialVersionUID = -6586122357660827472L;

		public CIHM()
		{
			super();
		}

		@Override
		public U put(String key, U value)
		{
			return super.put(key.toLowerCase(), value);
		}

		@Override
		public U get(Object key)
		{
			return super.get(key.toString().toLowerCase());
		}
	}

	/*---------------------------------------------------------------------*/

	public static class Column
	{
		public final String internalCatalog;
		public final String catalog;
		public final String table;
		public final String name;
		public final String type;
		public final int size;
		public final int digits;

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

		public String toString()
		{
			return "<" + type + "(" + size + "," + digits + ")>";
		}
	}

	/*---------------------------------------------------------------------*/

	public static class FrgnKey
	{
		public final String name;
		public final String fkInternalCatalog;
		public final String fkCatalog;
		public final String fkTable;
		public final String fkColumn;
		public final String pkInternalCatalog;
		public final String pkCatalog;
		public final String pkTable;
		public final String pkColumn;

		public FrgnKey(String _name, String _fkInternalCatalog, String _fkCatalog, String _fkTable, String _fkColumn, String _pkInternalCatalog, String _pkCatalog, String _pkTable, String _pkColumn)
		{
			name = _name;
			fkInternalCatalog = _fkInternalCatalog;
			fkCatalog = _fkCatalog;
			fkTable = _fkTable;
			fkColumn = _fkColumn;
			pkInternalCatalog = _pkInternalCatalog;
			pkCatalog = _pkCatalog;
			pkTable = _pkTable;
			pkColumn = _pkColumn;
		}

		public String toString()
		{
			return "<" + fkCatalog + "." + fkTable + "." + fkColumn + "->" + pkCatalog + "." + pkTable + "." + pkColumn + ">";
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_internalCatalogToExternalCatalog = new java.util.concurrent.ConcurrentHashMap<>();
	private static final Map<String, String> s_externalCatalogToInternalCatalog = new java.util.concurrent.ConcurrentHashMap<>();

	/*---------------------------------------------------------------------*/

	private static final Map<String, Map<String, Map<String, Column >>> s_columns  = new java.util.concurrent.ConcurrentHashMap<>();
	private static final Map<String, Map<String, Map<String, FrgnKey>>> s_frgnKeys = new java.util.concurrent.ConcurrentHashMap<>();

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
			s_internalCatalogToExternalCatalog.put(internalCatalog, externalCatalog);
			s_externalCatalogToInternalCatalog.put(externalCatalog, internalCatalog);

			s_columns.put(externalCatalog, new CIHM<>());
			s_frgnKeys.put(externalCatalog, new CIHM<>());
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

		AbstractDriver driver1 = CatalogSingleton.getConnection(externalCatalog);

		try
		{
			long t1 = System.currentTimeMillis();

			/**/	/*-----------------------------------------------------*/
			/**/
			/**/	@SuppressWarnings("deprecation") DatabaseMetaData metaData = driver1.getConnection().getMetaData();
			/**/
			/**/	ResultSet resultSet = metaData.getTables(internalCatalog, internalCatalog, "%", null);
			/**/
			/**/	Set<String> tables = new HashSet<>();
			/**/
			/**/	/*-----------------------------------------------------*/
			/**/
			/**/	while(resultSet.next())
			/**/	{
			/**/		String name = resultSet.getString("TABLE_NAME");
			/**/
			/**/		if(name != null)
			/**/		{
			/**/			name = name.toLowerCase();
			/**/
			/**/			if(name.startsWith("db_") == false
			/**/			   &&
			/**/			   name.startsWith("x_db_") == false
			/**/			 ) {
			/**/				s_columns.get(externalCatalog).put(name, new CIHM<>());
			/**/				s_frgnKeys.get(externalCatalog).put(name, new CIHM<>());
			/**/
			/**/				tables.add(name);
			/**/			}
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
			String fkInternalCatalog = resultSet.getString("FKTABLE_CAT");
			String fkTable = resultSet.getString("FKTABLE_NAME");
			String fkColumn = resultSet.getString("FKCOLUMN_NAME");
			String pkInternalCatalog = resultSet.getString("PKTABLE_CAT");
			String pkTable = resultSet.getString("PKTABLE_NAME");
			String pkColumn = resultSet.getString("PKCOLUMN_NAME");

			String fkCatalog;
			String pkCatalog;

			if(fkInternalCatalog == null)
			{
				fkInternalCatalog = internalCatalog;
				fkCatalog = externalCatalog;
			}
			else
			{
				fkCatalog = s_internalCatalogToExternalCatalog.get(fkInternalCatalog);

				if(fkCatalog == null)
				{
					fkCatalog = externalCatalog;
				}
			}

			if(pkInternalCatalog == null)
			{
				pkInternalCatalog = internalCatalog;
				pkCatalog = externalCatalog;
			}
			else
			{
				pkCatalog = s_internalCatalogToExternalCatalog.get(pkInternalCatalog);

				if(pkCatalog == null)
				{
					pkCatalog = externalCatalog;
				}
			}

			if(name != null && fkInternalCatalog != null && fkCatalog != null && fkTable != null && fkColumn != null && pkInternalCatalog != null && pkCatalog != null && pkTable != null && pkColumn != null)
			{
				name = name.toLowerCase();
				fkTable = fkTable.toLowerCase();
				fkColumn = fkColumn.toLowerCase();
				pkTable = pkTable.toLowerCase();
				pkColumn = pkColumn.toLowerCase();

				Map<String, FrgnKey> frgnKey = s_frgnKeys.get(externalCatalog).get(fkTable);

				if(frgnKey != null)
				{
					frgnKey.put(fkColumn, new FrgnKey(
						name,
						fkInternalCatalog,
						fkCatalog,
						fkTable,
						fkColumn,
						pkInternalCatalog,
						pkCatalog,
						pkTable,
						pkColumn
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
		return null;
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
			Map<String, Column> map2 = map1.get(table);

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
			Map<String, Column> map2 = map1.get(table);

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
			Map<String, FrgnKey> map2 = map1.get(table);

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
						"<field name=\"name\">" + frgnKey.name + "</field>"
						+
						"<field name=\"fkInternalCatalog\">" + frgnKey.fkInternalCatalog + "</field>"
						+
						"<field name=\"fkCatalog\">" + frgnKey.fkCatalog + "</field>"
						+
						"<field name=\"fkTable\">" + frgnKey.fkTable + "</field>"
						+
						"<field name=\"fkColumn\">" + frgnKey.fkColumn + "</field>"
						+
						"<field name=\"pkInternalCatalog\">" + frgnKey.pkInternalCatalog + "</field>"
						+
						"<field name=\"pkCatalog\">" + frgnKey.pkCatalog + "</field>"
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
