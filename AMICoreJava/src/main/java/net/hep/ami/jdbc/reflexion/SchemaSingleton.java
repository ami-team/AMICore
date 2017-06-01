package net.hep.ami.jdbc.reflexion;

import java.io.*;
import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public class SchemaSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class Column implements Serializable
	{
		private static final long serialVersionUID = 9088165113864128126L;

		public final String externalCatalog;
		public final String internalCatalog;
		public final String table;
		public final String name;
		public final String type;
		public final int size;
		public final int digits;

		public Column(String _externalCatalog, String _internalCatalog, String _table, String _name, String _type, int _size, int _digits)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			table = _table;
			name = _name;
			type = _type;
			size = _size;
			digits = _digits;
		}

		public String toString()
		{
			return "`" + internalCatalog + "`.`" + table + "`.`" + name + "`";
		}
	}

	/*---------------------------------------------------------------------*/

	public static final class FrgnKey implements Serializable
	{
		private static final long serialVersionUID = 7467966033785286381L;

		public final String name;
		public final String fkExternalCatalog;
		public final String fkInternalCatalog;
		public final String fkTable;
		public final String fkColumn;
		public final String pkExternalCatalog;
		public final String pkInternalCatalog;
		public final String pkTable;
		public final String pkColumn;

		public FrgnKey(String _name, String _fkExternalCatalog, String _fkInternalCatalog, String _fkTable, String _fkColumn, String _pkExternalCatalog, String _pkInternalCatalog, String _pkTable, String _pkColumn)
		{
			name = _name;
			fkExternalCatalog = _fkExternalCatalog;
			fkInternalCatalog = _fkInternalCatalog;
			fkTable = _fkTable;
			fkColumn = _fkColumn;
			pkExternalCatalog = _pkExternalCatalog;
			pkInternalCatalog = _pkInternalCatalog;
			pkTable = _pkTable;
			pkColumn = _pkColumn;
		}

		public String toString()
		{
			return "`" + fkInternalCatalog + "`.`" + fkTable + "`.`" + fkColumn + "` -> `" + pkInternalCatalog + "`.`" + pkTable + "`.`" + pkColumn + "`";
		}
	}

	/*---------------------------------------------------------------------*/

	public static final class FrgnKeys extends ArrayList<FrgnKey>
	{
		private static final long serialVersionUID = 646216521092672318L;

		public FrgnKeys()
		{
			super();
		}

		public FrgnKeys(FrgnKey frgnKey)
		{
			super(); add(frgnKey);
		}

		public FrgnKeys(FrgnKeys frgnKeys)
		{
			super(); addAll(frgnKeys);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_externalCatalogToInternalCatalog = new AMIMap<>();
	private static final Map<String, String> s_internalCatalogToExternalCatalog = new AMIMap<>();

	/*---------------------------------------------------------------------*/

	private static final Map<String, Map<String, Map<String, Column>>> s_columns = new AMIMap<>();
	private static final Map<String, Map<String, Map<String, FrgnKeys>>> s_forwardFKs = new AMIMap<>();
	private static final Map<String, Map<String, Map<String, FrgnKeys>>> s_backwardFKs = new AMIMap<>();

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
			LogSingleton.root.error(LogSingleton.FATAL, e.getMessage(), e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static void clear()
	{
		/*-----------------------------------------------------------------*/

		s_externalCatalogToInternalCatalog.clear();
		s_internalCatalogToExternalCatalog.clear();

		/*-----------------------------------------------------------------*/

		s_columns.clear();
		s_forwardFKs.clear();
		s_backwardFKs.clear();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void addSchema(String externalCatalog, String internalCatalog)
	{
		/*-----------------------------------------------------------------*/

		s_externalCatalogToInternalCatalog.put(externalCatalog, internalCatalog);
		s_internalCatalogToExternalCatalog.put(internalCatalog, externalCatalog);

		/*-----------------------------------------------------------------*/

		s_columns.put(externalCatalog,
			new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true)
		);

		s_forwardFKs.put(externalCatalog,
			new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings({"unused", "unchecked"})
	/*---------------------------------------------------------------------*/

	private static class Extractor implements Runnable
	{
		/*-----------------------------------------------------------------*/

		private final Map<String, String> m_externalCatalogToInternalCatalog;
		private final Map<String, String> m_internalCatalogToExternalCatalog;

		private final Map<String, Map<String, Map<String, Column>>> m_columns;
		private final Map<String, Map<String, Map<String, FrgnKeys>>> m_frgnKeys;

		/*-----------------------------------------------------------------*/

		private final String m_externalCatalog;
		private final String m_internalCatalog;

		private final boolean m_fast;

		/*-----------------------------------------------------------------*/

		private Map<String, Map<String, Column>> m_tmp1;
		private Map<String, Map<String, FrgnKeys>> m_tmp2;

		/*-----------------------------------------------------------------*/

		public Extractor(
			Map<String, String> externalCatalogToInternalCatalog,
			Map<String, String> internalCatalogToExternalCatalog,
			Map<String, Map<String, Map<String, Column>>> columns,
			Map<String, Map<String, Map<String, FrgnKeys>>> frgnKeys,
			String externalCatalog,
			String internalCatalog,
			boolean fast
		 ) {
			/*-------------------------------------------------------------*/

			m_externalCatalogToInternalCatalog = externalCatalogToInternalCatalog;
			m_internalCatalogToExternalCatalog = internalCatalogToExternalCatalog;

			m_columns = columns;
			m_frgnKeys = frgnKeys;

			/*-------------------------------------------------------------*/

			m_externalCatalog = externalCatalog;
			m_internalCatalog = internalCatalog;

			m_fast = fast;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		@Override
		public void run()
		{
			m_tmp1 = new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true);
			m_tmp2 = new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true);

			try
			{
				if(m_fast)
				{
					try
					{
						loadSchemaFromFiles();
						apply();
						/* Yeahhhhhh!!! */
					}
					catch(Exception e)
					{
						loadSchemaFromDatabase();
						apply();
						saveSchemaToFiles();
					}
				}
				else
				{
					loadSchemaFromDatabase();
					apply();
					saveSchemaToFiles();
				}
			}
			catch(Exception e)
			{
				LogSingleton.root.error(LogSingleton.FATAL, e.getMessage(), e);
			}
		}

		/*-----------------------------------------------------------------*/

		private void apply()
		{
			m_columns.put(m_externalCatalog, m_tmp1);
			m_frgnKeys.put(m_externalCatalog, m_tmp2);
		}

		/*-----------------------------------------------------------------*/

		private void saveSchemaToFiles() throws Exception
		{
			LogSingleton.root.info("saving to file schema of catalog '" + m_externalCatalog + "'");

			/*-------------------------------------------------------------*/

			String basePath = ConfigSingleton.getConfigPathName() + File.separator + "cache";

			/*-------------------------------------------------------------*/

			File file = new File(basePath);

			if(file.exists() == false)
			{
				file.mkdirs();
			}

			/*-------------------------------------------------------------*/

			try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(basePath + File.separator + m_externalCatalog + "_columns.ser")))
			{
				objectOutputStream.writeObject(m_tmp1);
			}

			/*-------------------------------------------------------------*/

			try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(basePath + File.separator + m_externalCatalog + "_frgnkeys.ser")))
			{
				objectOutputStream.writeObject(m_tmp2);
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		private void loadSchemaFromFiles() throws Exception
		{
			LogSingleton.root.info("loading from file schema of catalog '" + m_externalCatalog + "'");

			/*-------------------------------------------------------------*/

			String basePath = ConfigSingleton.getConfigPathName() + File.separator + "cache";

			/*-------------------------------------------------------------*/

			File file = new File(basePath);

			if(file.exists() == false)
			{
				file.mkdirs();
			}

			/*-------------------------------------------------------------*/

			try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(basePath + File.separator + m_externalCatalog + "_columns.ser")))
			{
				m_tmp1.putAll((Map<String, Map<String, Column>>) objectInputStream.readObject());
			}

			/*-------------------------------------------------------------*/

			try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(basePath + File.separator + m_externalCatalog + "_frgnkeys.ser")))
			{
				m_tmp2.putAll((Map<String, Map<String, FrgnKeys>>) objectInputStream.readObject());
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		private void loadSchemaFromDatabase() throws Exception
		{
			Set<String> tables = new HashSet<>();

			LogSingleton.root.info("loading from database schema of catalog '" + m_externalCatalog + "'");

			/*-------------------------------------------------------------*/
			/* CREATE CONNECTION                                           */
			/*-------------------------------------------------------------*/

			Connection connection = DriverManager.getConnection(
				ConfigSingleton.getProperty("router_url"),
				ConfigSingleton.getProperty("router_user"),
				ConfigSingleton.getProperty("router_pass")
			);

			/*-------------------------------------------------------------*/

			try
			{
				/*---------------------------------------------------------*/
				/* GET METADATA OBJECT                                     */
				/*---------------------------------------------------------*/

				DatabaseMetaData metaData = connection.getMetaData();

				/*---------------------------------------------------------*/
				/* LOAD METADATA FROM DATABASE                             */
				/*---------------------------------------------------------*/

				ResultSet resultSet = metaData.getTables(m_internalCatalog, m_internalCatalog, "%", null);

				/*---------------------------------------------------------*/

				String temp;

				while(resultSet.next())
				{
					temp = resultSet.getString("TABLE_NAME");

					if(temp != null
					   &&
					   temp.toLowerCase().startsWith("db_") == false
					   &&
					   temp.toLowerCase().startsWith("x_db_") == false
					 ) {
						m_tmp1.put(temp, new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true));
						m_tmp2.put(temp, new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true));

						tables.add(temp);
					}
				}

				/*---------------------------------------------------------*/

				for(String name: tables)
				{
					loadColumnMetadata(metaData, name);

					loadFgnKeyMetadata(metaData, name);
				}

				/*---------------------------------------------------------*/
			}
			finally
			{
				connection.close();
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		private void loadColumnMetadata(
			DatabaseMetaData metaData,
			String _table
		 ) throws SQLException {
			/*-------------------------------------------------------------*/

			ResultSet resultSet = metaData.getColumns(m_internalCatalog, m_internalCatalog, _table, "%");

			/*-------------------------------------------------------------*/

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

					Map<String, Column> column = m_tmp1.get(table);

					if(column != null)
					{
						column.put(name, new Column(
							m_externalCatalog,
							m_internalCatalog,
							table,
							name,
							type,
							size,
							digits
						));
					}
				}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		private void loadFgnKeyMetadata(
			DatabaseMetaData metaData,
			String _table
		 ) throws SQLException {
			/*-------------------------------------------------------------*/

			ResultSet resultSet = metaData.getExportedKeys(m_internalCatalog, m_internalCatalog, _table);

			/*-------------------------------------------------------------*/

			while(resultSet.next())
			{
				String name = resultSet.getString("FK_NAME");
				String fkInternalCatalog = resultSet.getString("FKTABLE_CAT");
				String fkTable = resultSet.getString("FKTABLE_NAME");
				String fkColumn = resultSet.getString("FKCOLUMN_NAME");
				String pkInternalCatalog = resultSet.getString("PKTABLE_CAT");
				String pkTable = resultSet.getString("PKTABLE_NAME");
				String pkColumn = resultSet.getString("PKCOLUMN_NAME");

				String fkExternalCatalog;
				String pkExternalCatalog;

				if(fkInternalCatalog == null)
				{
					fkExternalCatalog = m_externalCatalog;
					fkInternalCatalog = m_internalCatalog;
				}
				else
				{
					fkExternalCatalog = m_internalCatalogToExternalCatalog.containsKey(fkInternalCatalog) ? m_internalCatalogToExternalCatalog.get(fkInternalCatalog)
					                                                                                      : m_externalCatalog
					;
				}

				if(pkInternalCatalog == null)
				{
					pkExternalCatalog = m_externalCatalog;
					pkInternalCatalog = m_internalCatalog;
				}
				else
				{
					pkExternalCatalog = m_internalCatalogToExternalCatalog.containsKey(pkInternalCatalog) ? m_internalCatalogToExternalCatalog.get(pkInternalCatalog)
					                                                                                      : m_externalCatalog
					;
				}

				if(name != null && fkExternalCatalog != null && fkInternalCatalog != null && fkTable != null && fkColumn != null && pkExternalCatalog != null && pkInternalCatalog != null && pkTable != null && pkColumn != null)
				{
					name = name.toLowerCase();
					fkTable = fkTable.toLowerCase();
					fkColumn = fkColumn.toLowerCase();
					pkTable = pkTable.toLowerCase();
					pkColumn = pkColumn.toLowerCase();

					Map<String, FrgnKeys> frgnKey = m_tmp2.get(fkTable);

					if(frgnKey != null)
					{
						frgnKey.put(fkColumn, new FrgnKeys(new FrgnKey(
							name,
							fkExternalCatalog,
							fkInternalCatalog,
							fkTable,
							fkColumn,
							pkExternalCatalog,
							pkInternalCatalog,
							pkTable,
							pkColumn
						)));
					}
				}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static class Executor implements Runnable
	{
		/*-----------------------------------------------------------------*/

		private final Map<String, Map<String, Map<String, Column>>> m_columns;
		private final Map<String, Map<String, Map<String, FrgnKeys>>> m_forwardFKs;
		private final Map<String, Map<String, Map<String, FrgnKeys>>> m_backwardFKs;

		/*-----------------------------------------------------------------*/

		private final List<Thread> m_threads;

		/*-----------------------------------------------------------------*/

		public Executor(
			Map<String, Map<String, Map<String, Column>>> columns,
			Map<String, Map<String, Map<String, FrgnKeys>>> forwardFKs,
			Map<String, Map<String, Map<String, FrgnKeys>>> backwardFKs,
			List<Thread> threads
		 ) {
			/*-------------------------------------------------------------*/

			m_columns = columns;
			m_forwardFKs = forwardFKs;
			m_backwardFKs = backwardFKs;

			/*-------------------------------------------------------------*/

			m_threads = threads;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		@Override
		public void run()
		{
			/*-------------------------------------------------------------*/
			/* START THREADS                                               */
			/*-------------------------------------------------------------*/

			for(Thread thread: m_threads)
			{
				try
				{
					thread.start();
				}
				catch(IllegalThreadStateException e)
				{
					LogSingleton.root.error(LogSingleton.FATAL, e.getMessage(), e);
				}
			}

			/*-------------------------------------------------------------*/
			/* WAIT FOR THREADS                                            */
			/*-------------------------------------------------------------*/

			for(Thread thread: m_threads)
			{
				try
				{
					thread.join();
				}
				catch(InterruptedException e)
				{
					LogSingleton.root.error(LogSingleton.FATAL, e.getMessage(), e);
				}
			}

			/*-------------------------------------------------------------*/
			/* POST TREATMENT                                              */
			/*-------------------------------------------------------------*/

			for(Map.Entry<String, Map<String, Map<String, Column>>> entry1: /**/(m_columns).entrySet())
			{
				m_backwardFKs.put(entry1.getKey(), new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true))
				;

				for(Map.Entry<String, Map<String, Column>> entry2: entry1.getValue().entrySet())
				{
					m_backwardFKs.get(entry1.getKey())
					             .put(entry2.getKey(), new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true))
					;

					for(Map.Entry<String, Column> entry3: entry2.getValue().entrySet())
					{
						m_backwardFKs.get(entry1.getKey())
						             .get(entry2.getKey())
						             .put(entry3.getKey(), new FrgnKeys())
						;
					}
				}
			}

			/*-------------------------------------------------------------*/

			FrgnKey frgnKey;

			for(Map<String, Map<String, FrgnKeys>> value1: m_forwardFKs.values())
			for(Map<String, FrgnKeys> value2: value1.values())
			for(FrgnKeys frgnKeys: value2.values())
			{
				frgnKey = frgnKeys.get(0);

				m_backwardFKs.get(frgnKey.pkExternalCatalog)
				             .get(frgnKey.pkTable)
				             .get(frgnKey.pkColumn)
				             .add(frgnKey)
				;
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void rebuildSchemas()
	{
		/*-----------------------------------------------------------------*/

		boolean isOk = ConfigSingleton.getProperty("rebuild_schema_cache_in_background", false);

		/*-----------------------------------------------------------------*/
		/* FAST METHOD                                                     */
		/*-----------------------------------------------------------------*/

		if(true)
		{
			List<Thread> threads = new ArrayList<>();

			for(Map.Entry<String, String> entry: s_externalCatalogToInternalCatalog.entrySet())
			{
				threads.add(new Thread(new Extractor(
						s_externalCatalogToInternalCatalog,
						s_internalCatalogToExternalCatalog,
						s_columns,
						s_forwardFKs,
						entry.getKey(),
						entry.getValue(),
						true // fast
					), "Fast metadata extractor for '" + entry.getKey() + "'"
				));
			}

			new Executor(s_columns, s_forwardFKs, s_backwardFKs, threads).run();
		}

		/*-----------------------------------------------------------------*/
		/* SLOW METHOD                                                     */
		/*-----------------------------------------------------------------*/

		if(isOk)
		{
			List<Thread> threads = new ArrayList<>();

			for(Map.Entry<String, String> entry: s_externalCatalogToInternalCatalog.entrySet())
			{
				threads.add(new Thread(new Extractor(
						s_externalCatalogToInternalCatalog,
						s_internalCatalogToExternalCatalog,
						s_columns,
						s_forwardFKs,
						entry.getKey(),
						entry.getValue(),
						false // slow
					), "Slow metadata extractor for '" + entry.getKey() + "'"
				));
			}

			new Thread(new Executor(s_columns, s_forwardFKs, s_backwardFKs, threads)).start();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String internalCatalogToExternalCatalog_noException(String catalog) throws Exception
	{
		return s_internalCatalogToExternalCatalog.get(catalog);
	}

	/*---------------------------------------------------------------------*/

	public static String externalCatalogToInternalCatalog_noException(String catalog) throws Exception
	{
		return s_externalCatalogToInternalCatalog.get(catalog);
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

	public static List<String> getCatalogNames()
	{
		return new ArrayList<>(
			s_columns.keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getTableNames(String catalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, Column>> map = s_columns.get(catalog);

		if(map != null)
		{
			return new ArrayList<>(
				map.keySet()
			);
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

	public static Map<String, FrgnKeys> getForwardFKs(String catalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, FrgnKeys>> map1 = s_forwardFKs.get(catalog);

		if(map1 != null)
		{
			Map<String, FrgnKeys> map2 = map1.get(table);

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

	public static Map<String, FrgnKeys> getBackwardFKs(String catalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, FrgnKeys>> map1 = s_backwardFKs.get(catalog);

		if(map1 != null)
		{
			Map<String, FrgnKeys> map2 = map1.get(table);

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

	public static List<String> getColumnNames(String catalog, String table) throws Exception
	{
		return new ArrayList<>(
			getColumns(catalog, table).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getForwardFKNames(String catalog, String table) throws Exception
	{
		return new ArrayList<>(
			getForwardFKs(catalog, table).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getBackwardFKNames(String catalog, String table) throws Exception
	{
		return new ArrayList<>(
			getBackwardFKs(catalog, table).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getDBSchemes()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		Column column;

		result.append("<rowset type=\"columns\">");

		for(Map.Entry<String, Map<String, Map<String, Column>>> entry1: s_columns.entrySet())
		for(Map.Entry<String, Map<String, Column>> entry2: entry1.getValue().entrySet())
		for(Map.Entry<String, Column> entry3: entry2.getValue().entrySet())
		{
			column = entry3.getValue();

			result.append(
				"<row>"
				+
				"<field name=\"externalCatalog\"><![CDATA[" + column.externalCatalog + "]]></field>"
				+
				"<field name=\"internalCatalog\"><![CDATA[" + column.internalCatalog + "]]></field>"
				+
				"<field name=\"table\"><![CDATA[" + column.table + "]]></field>"
				+
				"<field name=\"name\"><![CDATA[" + column.name + "]]></field>"
				+
				"<field name=\"type\"><![CDATA[" + column.type + "]]></field>"
				+
				"<field name=\"size\"><![CDATA[" + column.size + "]]></field>"
				+
				"<field name=\"digits\"><![CDATA[" + column.digits + "]]></field>"
				+
				"</row>"
			);
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		FrgnKey frgnKey;

		result.append("<rowset type=\"foreignKeys\">");

		for(Map.Entry<String, Map<String, Map<String, FrgnKeys>>> entry1: s_forwardFKs.entrySet())
		for(Map.Entry<String, Map<String, FrgnKeys>> entry2: entry1.getValue().entrySet())
		for(Map.Entry<String, FrgnKeys> entry3: entry2.getValue().entrySet())
		{
			frgnKey = entry3.getValue().get(0);

			result.append(
				"<row>"
				+
				"<field name=\"name\"><![CDATA[" + frgnKey.name + "]]></field>"
				+
				"<field name=\"fkExternalCatalog\"><![CDATA[" + frgnKey.fkExternalCatalog + "]]></field>"
				+
				"<field name=\"fkInternalCatalog\"><![CDATA[" + frgnKey.fkInternalCatalog + "]]></field>"
				+
				"<field name=\"fkTable\"><![CDATA[" + frgnKey.fkTable + "]]></field>"
				+
				"<field name=\"fkColumn\"><![CDATA[" + frgnKey.fkColumn + "]]></field>"
				+
				"<field name=\"pkExternalCatalog\"><![CDATA[" + frgnKey.pkExternalCatalog + "]]></field>"
				+
				"<field name=\"pkInternalCatalog\"><![CDATA[" + frgnKey.pkInternalCatalog + "]]></field>"
				+
				"<field name=\"pkTable\"><![CDATA[" + frgnKey.pkTable + "]]></field>"
				+
				"<field name=\"pkColumn\"><![CDATA[" + frgnKey.pkColumn + "]]></field>"
				+
				"</row>"
			);
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
