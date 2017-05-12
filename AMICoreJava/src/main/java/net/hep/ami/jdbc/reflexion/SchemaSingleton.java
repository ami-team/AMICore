package net.hep.ami.jdbc.reflexion;

import java.io.*;
import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

@SuppressWarnings({"unchecked", "deprecation"})
public class SchemaSingleton
{
	/*---------------------------------------------------------------------*/

	public static class Column implements Serializable
	{
		private static final long serialVersionUID = 9088165113864128126L;

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
	}

	/*---------------------------------------------------------------------*/

	public static class FrgnKey implements Serializable
	{
		private static final long serialVersionUID = 7467966033785286381L;

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
	}

	/*---------------------------------------------------------------------*/

	private static final String REBUILD_SCHEMA_CACHE_PARAM_NAME = "rebuild_schema_cache";

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_internalCatalogToExternalCatalog = new AMIHashMap<>();
	private static final Map<String, String> s_externalCatalogToInternalCatalog = new AMIHashMap<>();

	/*---------------------------------------------------------------------*/

	private static final Map<String, Map<String, Map<String, Column >>> s_columns  = new AMIHashMap<>();
	private static final Map<String, Map<String, Map<String, FrgnKey>>> s_frgnKeys = new AMIHashMap<>();

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
		/*-----------------------------------------------------------------*/

		s_internalCatalogToExternalCatalog.clear();
		s_externalCatalogToInternalCatalog.clear();

		/*-----------------------------------------------------------------*/

		s_columns.clear();
		s_frgnKeys.clear();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void addSchema(String internalCatalog, String externalCatalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		s_internalCatalogToExternalCatalog.put(internalCatalog, externalCatalog);
		s_externalCatalogToInternalCatalog.put(externalCatalog, internalCatalog);

		/*-----------------------------------------------------------------*/

		s_columns.put(externalCatalog, new AMIHashMap<>(AMIHashMap.Type.LINKED_HASH_MAP, false, true));
		s_frgnKeys.put(externalCatalog, new AMIHashMap<>(AMIHashMap.Type.LINKED_HASH_MAP, false, true));

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static class Extractor implements Runnable
	{
		private String m_internalCatalog;
		private String m_externalCatalog;

		public Extractor(String internalCatalog, String externalCatalog)
		{
			m_internalCatalog = internalCatalog;
			m_externalCatalog = externalCatalog;
		}

		public void run()
		{
			try
			{
				/*---------------------------------------------------------*/

				Map<String, Map<String, Column >> tmp1 = new AMIHashMap<>(AMIHashMap.Type.LINKED_HASH_MAP, false, true);
				Map<String, Map<String, FrgnKey>> tmp2 = new AMIHashMap<>(AMIHashMap.Type.LINKED_HASH_MAP, false, true);

				/*--------------------------------------------------------*/

				if(Thread.currentThread().getId() == 0L)
				{
					try
					{
						loadSchemaFromFiles(tmp1, tmp2, m_externalCatalog);

						s_columns.put(m_externalCatalog, tmp1);
						s_frgnKeys.put(m_externalCatalog, tmp2);
					}
					catch(Exception e)
					{
						if(ConfigSingleton.getProperty(REBUILD_SCHEMA_CACHE_PARAM_NAME, false) == false)
						{
							loadSchemaFromDatabase(tmp1, tmp2, m_internalCatalog, m_externalCatalog);

							s_columns.put(m_externalCatalog, tmp1);
							s_frgnKeys.put(m_externalCatalog, tmp2);

							saveSchemaToFiles(tmp1, tmp2, m_externalCatalog);
						}
					}
				}
				else
				{
					loadSchemaFromDatabase(tmp1, tmp2, m_internalCatalog, m_externalCatalog);

					s_columns.put(m_externalCatalog, tmp1);
					s_frgnKeys.put(m_externalCatalog, tmp2);

					saveSchemaToFiles(tmp1, tmp2, m_externalCatalog);
				}

				/*---------------------------------------------------------*/
			}
			catch(Exception e)
			{
				LogSingleton.root.error(LogSingleton.FATAL, e.getMessage(), e);
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static void rebuildSchemaCache()
	{
		/*-----------------------------------------------------------------*/

		boolean isOk = ConfigSingleton.getProperty(REBUILD_SCHEMA_CACHE_PARAM_NAME, false);

		/*-----------------------------------------------------------------*/

		if(true) for(Map.Entry<String, String> entry: s_internalCatalogToExternalCatalog.entrySet())
		{
			/*-----*/ (
				new Extractor(entry.getKey(), entry.getValue())
			). run ();
		}

		if(isOk) for(Map.Entry<String, String> entry: s_internalCatalogToExternalCatalog.entrySet())
		{
			new Thread(
				new Extractor(entry.getKey(), entry.getValue())
			).start();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void saveSchemaToFiles(
		Map<String, Map<String, Column >> tmp1,
		Map<String, Map<String, FrgnKey>> tmp2,
		String externalCatalog
	 ) throws Exception {

		ObjectOutputStream objectOutputStream;

		LogSingleton.root.info("saving schema of catalog '" + externalCatalog + "'");

		/*-----------------------------------------------------------------*/

		String basePath = ConfigSingleton.getConfigPathName() + File.separator + "cache";

		/*-----------------------------------------------------------------*/

		File file = new File(basePath);

		if(file.exists() == false)
		{
			file.mkdirs();
		}

		/*-----------------------------------------------------------------*/

		objectOutputStream = new ObjectOutputStream(new FileOutputStream(basePath + File.separator + externalCatalog + "_column.ser"));

		try
		{
			objectOutputStream.writeObject(tmp1);
		}
		finally
		{
			objectOutputStream.close();
		}

		/*-----------------------------------------------------------------*/

		objectOutputStream = new ObjectOutputStream(new FileOutputStream(basePath + File.separator + externalCatalog + "_frgnkey.ser"));

		try
		{
			objectOutputStream.writeObject(tmp2);
		}
		finally
		{
			objectOutputStream.close();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void loadSchemaFromFiles(
		Map<String, Map<String, Column >> tmp1,
		Map<String, Map<String, FrgnKey>> tmp2,
		String externalCatalog
	 ) throws Exception {

		ObjectInputStream objectInputStream;

		LogSingleton.root.info("loading schema of catalog '" + externalCatalog + "'");

		/*-----------------------------------------------------------------*/

		String basePath = ConfigSingleton.getConfigPathName() + File.separator + "cache";

		/*-----------------------------------------------------------------*/

		File file = new File(basePath);

		if(file.exists() == false)
		{
			file.mkdirs();
		}

		/*-----------------------------------------------------------------*/

		objectInputStream = new ObjectInputStream(new FileInputStream(basePath + File.separator + externalCatalog + "_column.ser"));

		try
		{
			tmp1.putAll((Map<String, Map<String, Column>>) objectInputStream.readObject());
		}
		finally
		{
			objectInputStream.close();
		}

		/*-----------------------------------------------------------------*/

		objectInputStream = new ObjectInputStream(new FileInputStream(basePath + File.separator + externalCatalog + "_frgnkey.ser"));

		try
		{
			tmp2.putAll((Map<String, Map<String, FrgnKey>>) objectInputStream.readObject());
		}
		finally
		{
			objectInputStream.close();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void loadSchemaFromDatabase(
		Map<String, Map<String, Column >> tmp1,
		Map<String, Map<String, FrgnKey>> tmp2,
		String internalCatalog,
		String externalCatalog
	 ) throws Exception {

		Set<String> tables = new HashSet<>();

		/*-----------------------------------------------------------------*/
		/* CREATE DRIVER                                                   */
		/*-----------------------------------------------------------------*/

		AbstractDriver driver = CatalogSingleton.getConnection(externalCatalog);

		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/
			/* GET METADATA OBJECT                                         */
			/*-------------------------------------------------------------*/

			DatabaseMetaData metaData = driver.getConnection().getMetaData();

			/*-------------------------------------------------------------*/
			/* LOAD METADATA FROM DATABASE                                 */
			/*-------------------------------------------------------------*/

			ResultSet resultSet = metaData.getTables(internalCatalog, internalCatalog, "%", null);

			/*-------------------------------------------------------------*/

			while(resultSet.next())
			{
				String name = resultSet.getString("TABLE_NAME");

				if(name != null)
				{
					name = name.toLowerCase();

					if(name.startsWith("db_") == false
					   &&
					   name.startsWith("x_db_") == false
					 ) {
						tmp1.put(name, new AMIHashMap<>(AMIHashMap.Type.LINKED_HASH_MAP, false, true));
						tmp2.put(name, new AMIHashMap<>(AMIHashMap.Type.LINKED_HASH_MAP, false, true));

						tables.add(name);
					}
				}
			}

			/*-------------------------------------------------------------*/

			loadColumnMetadata(tmp1, metaData, internalCatalog, externalCatalog, "%");

			for(String name: tables)
			{
				loadFgnKeyMetadata(tmp2, metaData, internalCatalog, externalCatalog, name);
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

	private static void loadColumnMetadata(
		Map<String, Map<String, Column>> tmp1,
		DatabaseMetaData metaData,
		String internalCatalog,
		String externalCatalog,
		String _table
	 ) throws SQLException {
		/*-----------------------------------------------------------------*/

		ResultSet resultSet = metaData.getColumns(internalCatalog, internalCatalog, _table, "%");

		/*-----------------------------------------------------------------*/

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

				Map<String, Column> column = tmp1.get(table);

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

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void loadFgnKeyMetadata(
		Map<String, Map<String, FrgnKey>> tmp2,
		DatabaseMetaData metaData,
		String internalCatalog,
		String externalCatalog,
		String _table
	 ) throws SQLException {
		/*-----------------------------------------------------------------*/

		ResultSet resultSet = metaData.getExportedKeys(internalCatalog, internalCatalog, _table);

		/*-----------------------------------------------------------------*/

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
				fkCatalog = s_internalCatalogToExternalCatalog.containsKey(fkInternalCatalog) ? s_internalCatalogToExternalCatalog.get(fkInternalCatalog)
				                                                                              : externalCatalog
				;
			}

			if(pkInternalCatalog == null)
			{
				pkInternalCatalog = internalCatalog;
				pkCatalog = externalCatalog;
			}
			else
			{
				pkCatalog = s_internalCatalogToExternalCatalog.containsKey(pkInternalCatalog) ? s_internalCatalogToExternalCatalog.get(pkInternalCatalog)
				                                                                              : externalCatalog
				;
			}

			if(name != null && fkInternalCatalog != null && fkCatalog != null && fkTable != null && fkColumn != null && pkInternalCatalog != null && pkCatalog != null && pkTable != null && pkColumn != null)
			{
				name = name.toLowerCase();
				fkTable = fkTable.toLowerCase();
				fkColumn = fkColumn.toLowerCase();
				pkTable = pkTable.toLowerCase();
				pkColumn = pkColumn.toLowerCase();

				Map<String, FrgnKey> frgnKey = tmp2.get(fkTable);

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

		/*-----------------------------------------------------------------*/
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
		return s_columns.keySet();
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getTableNames(String catalog) throws Exception
	{
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

	public static Map<String, Column> getColumns(String catalog, String table) throws Exception
	{
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

	public static Map<String, FrgnKey> getFrgnKeys(String catalog, String table) throws Exception
	{
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

	public static Set<String> getColumnNames(String catalog, String table) throws Exception
	{
		return getColumns(catalog, table).keySet();
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getFrgnKeyNames(String catalog, String table) throws Exception
	{
		return getFrgnKeys(catalog, table).keySet();
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
						"<field name=\"catalog\">" + column.catalog + "</field>"
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

		return result;
	}

	/*---------------------------------------------------------------------*/
}
