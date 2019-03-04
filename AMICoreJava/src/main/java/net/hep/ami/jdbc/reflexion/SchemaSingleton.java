package net.hep.ami.jdbc.reflexion;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class SchemaSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class Column implements Serializable
	{
		private static final long serialVersionUID = 9088165113864128126L;

		/**/

		private static final Pattern s_numberPattern = Pattern.compile(".*(?:BIT|INT|FLOAT|DOUBLE|SERIAL|DECIMAL|NUMERIC).*", Pattern.CASE_INSENSITIVE);

		/**/

		public final String externalCatalog;
		public final String internalCatalog;
		public final String table;
		public final String name;
		public final String type;
		public final int size;
		public final int digits;
		public final String def;

		/**/

		public int rank = 999;
		public boolean hidden = false;
		public boolean adminOnly = false;
		public boolean crypted = false;
		public boolean primary = false;
		public boolean created = false;
		public boolean createdBy = false;
		public boolean modified = false;
		public boolean modifiedBy = false;
		public boolean statable = false;
		public boolean groupable = false;
		public String description = "N/A";
		public String webLinkScript = "@NULL";

		/**/

		public Column(String _externalCatalog, String _internalCatalog, String _table, String _name, String _type, int _size, int _digits, String _def)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			table = _table;
			name = _name;
			type = _type;
			size = _size;
			digits = _digits;
			def = _def;

			statable = s_numberPattern.matcher(type).matches();
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(internalCatalog, table, name);
		}

		@Override
		public String toString()
		{
			return "`" + internalCatalog + "`.`" + table + "`.`" + name + "`";
		}
	}

	/*---------------------------------------------------------------------*/

	public static final class FrgnKey implements Serializable
	{
		private static final long serialVersionUID = 7467966033785286381L;

		/**/

		public final String name;
		public final String fkExternalCatalog;
		public final String fkInternalCatalog;
		public final String fkTable;
		public final String fkColumn;
		public final String pkExternalCatalog;
		public final String pkInternalCatalog;
		public final String pkTable;
		public final String pkColumn;

		/**/

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

		@Override
		public int hashCode()
		{
			return Objects.hash(fkInternalCatalog, fkTable, fkColumn, pkInternalCatalog, pkTable, pkColumn);
		}

		@Override
		public String toString()
		{
			return "`" + fkInternalCatalog + "`.`" + fkTable + "`.`" + fkColumn + "` = `" + pkInternalCatalog + "`.`" + pkTable + "`.`" + pkColumn + "`";
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

		public FrgnKeys(Collection<FrgnKey> frgnKeys)
		{
			super(); addAll(frgnKeys);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_externalCatalogToInternalCatalog = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true);
	private static final Map<String, String> s_internalCatalogToExternalCatalog = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true);

	/*---------------------------------------------------------------------*/

	protected static final Map<String, Map<String, Map<String, Column>>> s_columns = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true);
	protected static final Map<String, Map<String, Map<String, FrgnKeys>>> s_forwardFKs = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true);
	protected static final Map<String, Map<String, Map<String, FrgnKeys>>> s_backwardFKs = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true);

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
			LogSingleton.root.error(LogSingleton.FATAL, "could not load `CatalogSingleton`", e);
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

		public static final org.slf4j.Logger m_logger = LogSingleton.getLogger(Extractor.class.getSimpleName(), "INFO");

		/*-----------------------------------------------------------------*/

		private final Map<String, String> m_externalCatalogToInternalCatalog;
		private final Map<String, String> m_internalCatalogToExternalCatalog;

		private final Map<String, Map<String, Map<String, Column>>> m_columns;
		private final Map<String, Map<String, Map<String, FrgnKeys>>> m_frgnKeys;

		/*-----------------------------------------------------------------*/

		private final String m_externalCatalog;
		private final String m_internalCatalog;

		CatalogSingleton.Tuple m_tuple;

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
			CatalogSingleton.Tuple tuple,
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

			m_tuple = tuple;

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
				m_logger.error(LogSingleton.FATAL, "could not extract catalog schemas", e);
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
			m_logger.info("saving to file schema of catalog '{}'", m_externalCatalog);

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
			m_logger.info("for catalog '{}', loading from schema from file...", m_externalCatalog);

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

			m_logger.info("for catalog '{}', loading from schema from database...", m_externalCatalog);

			/*-------------------------------------------------------------*/
			/* CREATE CONNECTION                                           */
			/*-------------------------------------------------------------*/

			Connection connection = DriverManager.getConnection(
				m_tuple.t, m_tuple.u, m_tuple.v
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

				try(ResultSet resultSet = metaData.getTables(m_internalCatalog, m_tuple.z, "%", new String[] {"TABLE", "VIEW"}))
				{
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
				}

				/*---------------------------------------------------------*/

				for(String table: tables)
				{
					loadColumnMetadata(metaData, table);

					loadFgnKeyMetadata(metaData, table);
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

			try(ResultSet resultSet = metaData.getColumns(m_internalCatalog, m_tuple.z, _table, "%"))
			{
				boolean isOracle = resultSet.getClass().getName().startsWith("oracle");

				while(resultSet.next())
				{
					String table = resultSet.getString("TABLE_NAME");
					String name = resultSet.getString("COLUMN_NAME");
					String type = resultSet.getString("TYPE_NAME");
					int size = resultSet.getInt("COLUMN_SIZE");
					int digits = resultSet.getInt("DECIMAL_DIGITS");
					String def = resultSet.getString("COLUMN_DEF");
					boolean nullable = resultSet.getBoolean("NULLABLE");

					if(table != null && name != null && type != null)
					{
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
								digits,
								def == null ? (nullable ? "@NULL" : "")
								            : (def.toUpperCase().contains("CURRENT_TIMESTAMP") ? "@CURRENT_TIMESTAMP"
								                                                               : (isOracle ? Utility.sqlValToText(def) : def))
							));
						}
					}
				}
			}

			/*-------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getPrimaryKeys(m_internalCatalog, m_tuple.z, _table))
			{
				while(resultSet.next())
				{
					m_tmp1.get(_table).get(resultSet.getString("COLUMN_NAME")).primary = true;
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

			try(ResultSet resultSet = metaData.getExportedKeys(m_internalCatalog, m_tuple.z, _table))
			{
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

					if(fkInternalCatalog != null)
					{
						fkExternalCatalog = m_internalCatalogToExternalCatalog.containsKey(fkInternalCatalog) ? m_internalCatalogToExternalCatalog.get(fkInternalCatalog)
						                                                                                      : m_externalCatalog
						;
					}
					else
					{
						fkExternalCatalog = m_externalCatalog; /* BERK BUT NO OTHER SOLUTION */
						fkInternalCatalog = m_internalCatalog;
					}

					if(pkInternalCatalog != null)
					{
						pkExternalCatalog = m_internalCatalogToExternalCatalog.containsKey(pkInternalCatalog) ? m_internalCatalogToExternalCatalog.get(pkInternalCatalog)
						                                                                                      : m_externalCatalog
						;
					}
					else
					{
						pkExternalCatalog = m_externalCatalog; /* BERK BUT NO OTHER SOLUTION */
						pkInternalCatalog = m_internalCatalog;
					}

					if(name != null && fkExternalCatalog != null && fkInternalCatalog != null && fkTable != null && fkColumn != null && pkExternalCatalog != null && pkInternalCatalog != null && pkTable != null && pkColumn != null)
					{
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
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static class Executor implements Runnable
	{
		/*-----------------------------------------------------------------*/

		public static final org.slf4j.Logger m_logger = LogSingleton.getLogger(Extractor.class.getSimpleName(), "INFO");

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
				catch(Exception e)
				{
					m_logger.error(LogSingleton.FATAL, "could not start thread", e);
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
				catch(Exception e)
				{
					m_logger.error(LogSingleton.FATAL, "could not join thread", e);
				}
			}

			/*-------------------------------------------------------------*/
			/* POST TREATMENT                                              */
			/*-------------------------------------------------------------*/

			for(Map.Entry<String, Map<String, Map<String, Column>>> entry1: this.m_columns.entrySet())
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

	public static void rebuildSchemas(boolean flush) throws Exception
	{
		/*-----------------------------------------------------------------*/

		boolean slow = ConfigSingleton.getProperty("rebuild_schema_cache_in_background", false);

		/*-----------------------------------------------------------------*/
		/* FORCE                                                      */
		/*-----------------------------------------------------------------*/

		if(flush)
		{
			try
			{
				File[] files = new File(ConfigSingleton.getConfigPathName() + File.separator + "cache").listFiles((dir, name) -> name.toLowerCase().endsWith(".ser"));

				if(files != null)
				{
					for(File file: files)
					{
						try
						{
							file.delete();
						}
						catch(SecurityException e)
						{
							LogSingleton.root.error(e.getMessage(), e);
						}
					}
				}
			}
			catch(SecurityException e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}

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
						CatalogSingleton.getTuple(entry.getKey()),
						true // fast
					), "Fast metadata extractor for '" + entry.getKey() + "'"
				));
			}

			new Executor(s_columns, s_forwardFKs, s_backwardFKs, threads).run();
		}

		/*-----------------------------------------------------------------*/
		/* SLOW METHOD                                                     */
		/*-----------------------------------------------------------------*/

		if(slow)
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
						CatalogSingleton.getTuple(entry.getKey()),
						false // slow
					), "Slow metadata extractor for '" + entry.getKey() + "'"
				));
			}

			new Thread(new Executor(s_columns, s_forwardFKs, s_backwardFKs, threads)).start();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String internalCatalogToExternalCatalog_noException(String internalCatalog, String value)
	{
		String result = s_internalCatalogToExternalCatalog.get(internalCatalog);

		return result != null ? result : value;
	}

	/*---------------------------------------------------------------------*/

	public static String externalCatalogToInternalCatalog_noException(String externalCatalog, String value)
	{
		String result =  s_externalCatalogToInternalCatalog.get(externalCatalog);

		return result != null ? result : value;
	}

	/*---------------------------------------------------------------------*/

	public static String internalCatalogToExternalCatalog(String internalCatalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		String result = s_internalCatalogToExternalCatalog.get(internalCatalog);

		if(result != null)
		{
			return result;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("internal catalog not found `" + internalCatalog + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String externalCatalogToInternalCatalog(String externalCatalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		String result = s_externalCatalogToInternalCatalog.get(externalCatalog);

		if(result != null)
		{
			return result;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("external catalog not found `" + externalCatalog + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, Map<String, Column>> getTables(String externalCatalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, Column>> map = s_columns.get(externalCatalog);

		if(map != null)
		{
			return map;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("catalog not found `" + externalCatalog + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, Column> getEntityInfo(String externalCatalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Column> map = getTables(externalCatalog).get(table);

		if(map != null)
		{
			return map;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("entity not found `" + externalCatalog + "`.`" + table + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Column getFieldInfo(String externalCatalog, String table, String field) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Column column = getEntityInfo(externalCatalog, table).get(field);

		if(column != null)
		{
			return column;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("field not found `" + externalCatalog + "`.`" + table + "`.`" + field + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, FrgnKeys> getForwardFKs(String externalCatalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, FrgnKeys>> map1 = s_forwardFKs.get(externalCatalog);

		if(map1 != null)
		{
			Map<String, FrgnKeys> map2 = map1.get(table);

			if(map2 != null)
			{
				return map2;
			}
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("table not found `" + externalCatalog + "`.`" + table + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, FrgnKeys> getBackwardFKs(String externalCatalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, FrgnKeys>> map1 = s_backwardFKs.get(externalCatalog);

		if(map1 != null)
		{
			Map<String, FrgnKeys> map2 = map1.get(table);

			if(map2 != null)
			{
				return map2;
			}
		}
		/*-----------------------------------------------------------------*/

		throw new Exception("table not found `" + externalCatalog + "`.`" + table + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getInternalCatalogNames()
	{
		return new ArrayList<>(
			s_internalCatalogToExternalCatalog.keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getExternalCatalogNames()
	{
		return new ArrayList<>(
			s_externalCatalogToInternalCatalog.keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getTableNames(String externalCatalog) throws Exception
	{
		return new ArrayList<>(
			getTables(externalCatalog).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getColumnNames(String externalCatalog, String table) throws Exception
	{
		return new ArrayList<>(
			getEntityInfo(externalCatalog, table).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getForwardFKNames(String externalCatalog, String table) throws Exception
	{
		return new LinkedHashSet<>(
			getForwardFKs(externalCatalog, table).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getBackwardFKNames(String externalCatalog, String table) throws Exception
	{
		return new LinkedHashSet<>(
			getBackwardFKs(externalCatalog, table).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static Column getPrimaryKey(String externalCatalog, String table) throws Exception
	{
		/*-----------------------------------------------------------------*/

		for(Column column: getEntityInfo(externalCatalog, table).values())
		{
			if(column.primary)
			{
				return column;
			}
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("primary key not found for `" + externalCatalog + "`.`" + table + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static List<QId> getSortedColumnQId(String externalCatalog, String table, @Nullable List<QId> constraints, boolean isAdmin) throws Exception
	{
		return getEntityInfo(externalCatalog, table).values().stream()
		                                                     .filter(x -> isAdmin || (x.adminOnly == false && x.crypted == false)).sorted((x, y) -> x.rank - y.rank)
		                                                     .map(x -> new QId(x, false, constraints))
		                                                     .collect(Collectors.toList())
		;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getDBSchemas()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		Column column;

		result.append("<rowset type=\"fields\">");

		for(Map.Entry<String, Map<String, Map<String, Column>>> entry1: s_columns.entrySet())
		for(Map.Entry<String, Map<String, Column>> entry2: entry1.getValue().entrySet())
		for(Map.Entry<String, Column> entry3: entry2.getValue().entrySet())
		{
			column = entry3.getValue();

			result.append("<row>")
			      .append("<field name=\"externalCatalog\"><![CDATA[").append(column.externalCatalog).append("]]></field>")
			      .append("<field name=\"internalCatalog\"><![CDATA[").append(column.internalCatalog).append("]]></field>")
			      .append("<field name=\"entity\"><![CDATA[").append(column.table).append("]]></field>")
			      .append("<field name=\"name\"><![CDATA[").append(column.name).append("]]></field>")
			      .append("<field name=\"type\"><![CDATA[").append(column.type).append("]]></field>")
			      .append("<field name=\"size\"><![CDATA[").append(column.size).append("]]></field>")
			      .append("<field name=\"digits\"><![CDATA[").append(column.digits).append("]]></field>")
			      .append("<field name=\"def\"><![CDATA[").append(column.def).append("]]></field>")
			      .append("<field name=\"rank\"><![CDATA[").append(column.rank).append("]]></field>")
			      .append("<field name=\"hidden\"><![CDATA[").append(column.hidden).append("]]></field>")
			      .append("<field name=\"adminOnly\"><![CDATA[").append(column.adminOnly).append("]]></field>")
			      .append("<field name=\"crypted\"><![CDATA[").append(column.crypted).append("]]></field>")
			      .append("<field name=\"primary\"><![CDATA[").append(column.primary).append("]]></field>")
			      .append("<field name=\"created\"><![CDATA[").append(column.created).append("]]></field>")
			      .append("<field name=\"createdBy\"><![CDATA[").append(column.createdBy).append("]]></field>")
			      .append("<field name=\"modified\"><![CDATA[").append(column.modified).append("]]></field>")
			      .append("<field name=\"modifiedBy\"><![CDATA[").append(column.modifiedBy).append("]]></field>")
			      .append("<field name=\"statable\"><![CDATA[").append(column.statable).append("]]></field>")
			      .append("<field name=\"groupable\"><![CDATA[").append(column.groupable).append("]]></field>")
			      .append("<field name=\"description\"><![CDATA[").append(column.description).append("]]></field>")
			      .append("<field name=\"webLinkScript\"><![CDATA[").append(column.webLinkScript).append("]]></field>")
			      .append("</row>")
			;
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

			result.append("<row>")
			      .append("<field name=\"name\"><![CDATA[").append(frgnKey.name).append("]]></field>")
			      .append("<field name=\"fkExternalCatalog\"><![CDATA[").append(frgnKey.fkExternalCatalog).append("]]></field>")
			      .append("<field name=\"fkInternalCatalog\"><![CDATA[").append(frgnKey.fkInternalCatalog).append("]]></field>")
			      .append("<field name=\"fkTable\"><![CDATA[").append(frgnKey.fkTable).append("]]></field>")
			      .append("<field name=\"fkColumn\"><![CDATA[").append(frgnKey.fkColumn).append("]]></field>")
			      .append("<field name=\"pkExternalCatalog\"><![CDATA[").append(frgnKey.pkExternalCatalog).append("]]></field>")
			      .append("<field name=\"pkInternalCatalog\"><![CDATA[").append(frgnKey.pkInternalCatalog).append("]]></field>")
			      .append("<field name=\"pkTable\"><![CDATA[").append(frgnKey.pkTable).append("]]></field>")
			      .append("<field name=\"pkColumn\"><![CDATA[").append(frgnKey.pkColumn).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
