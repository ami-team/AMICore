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
		public final String entity;
		public final String field;
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
		public boolean readable = false;
		public boolean created = false;
		public boolean createdBy = false;
		public boolean modified = false;
		public boolean modifiedBy = false;
		public boolean automatic = false;
		public boolean statable = false;
		public boolean groupable = false;
		public boolean displayable = false;
		public boolean base64 = false;
		public String mime = "@NULL";
		public String ctrl = "@NULL";
		public String description = "N/A";
		public String webLinkScript = "@NULL";

		/**/

		public Column(String _externalCatalog, String _internalCatalog, String _entity, String _field, String _type, int _size, int _digits, String _def)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			entity = _entity;
			field = _field;
			type = _type;
			size = _size;
			digits = _digits;
			def = _def;

			statable = s_numberPattern.matcher(type).matches();
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(internalCatalog, entity, field);
		}

		@Override
		public String toString()
		{
			return "`" + internalCatalog + "`.`" + entity + "`.`" + field + "`";
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
		public final String fkEntity;
		public final String fkField;
		public final String pkExternalCatalog;
		public final String pkInternalCatalog;
		public final String pkEntity;
		public final String pkField;

		/**/

		public FrgnKey(String _name, String _fkExternalCatalog, String _fkInternalCatalog, String _fkEntity, String _fkField, String _pkExternalCatalog, String _pkInternalCatalog, String _pkEntity, String _pkField)
		{
			name = _name;
			fkExternalCatalog = _fkExternalCatalog;
			fkInternalCatalog = _fkInternalCatalog;
			fkEntity = _fkEntity;
			fkField = _fkField;
			pkExternalCatalog = _pkExternalCatalog;
			pkInternalCatalog = _pkInternalCatalog;
			pkEntity = _pkEntity;
			pkField = _pkField;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(fkInternalCatalog, fkEntity, fkField, pkInternalCatalog, pkEntity, pkField);
		}

		@Override
		public String toString()
		{
			return "`" + fkInternalCatalog + "`.`" + fkEntity + "`.`" + fkField + "` = `" + pkInternalCatalog + "`.`" + pkEntity + "`.`" + pkField + "`";
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
			Set<String> entities = new HashSet<>();

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

							entities.add(temp);
						}
					}
				}

				/*---------------------------------------------------------*/

				for(String entity: entities)
				{
					loadColumnMetadata(metaData, entity);

					loadFgnKeyMetadata(metaData, entity);
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
			String _entity
		 ) throws SQLException {
			/*-------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getColumns(m_internalCatalog, m_tuple.z, _entity, "%"))
			{
				boolean isOracle = resultSet.getClass().getName().startsWith("oracle");

				Map<String, Column> columnEntry;

				while(resultSet.next())
				{
					String entity = resultSet.getString("TABLE_NAME");
					String field = resultSet.getString("COLUMN_NAME");
					String type = resultSet.getString("TYPE_NAME");
					int size = resultSet.getInt("COLUMN_SIZE");
					int digits = resultSet.getInt("DECIMAL_DIGITS");
					String def = resultSet.getString("COLUMN_DEF");
					boolean nullable = resultSet.getBoolean("NULLABLE");

					if(entity != null && field != null && type != null)
					{
						columnEntry = m_tmp1.get(entity);

						if(columnEntry != null)
						{
							columnEntry.put(field, new Column(
								m_externalCatalog,
								m_internalCatalog,
								entity,
								field,
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

			try(ResultSet resultSet = metaData.getPrimaryKeys(m_internalCatalog, m_tuple.z, _entity))
			{
				Column column;

				while(resultSet.next())
				{
					column = m_tmp1.get(_entity).get(resultSet.getString("COLUMN_NAME"));

					if(column.statable)
					{
						column.primary = true;
					}
				}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		private void loadFgnKeyMetadata(
			DatabaseMetaData metaData,
			String _entity
		 ) throws SQLException {
			/*-------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getExportedKeys(m_internalCatalog, m_tuple.z, _entity))
			{
				Map<String, FrgnKeys> frgnKeyEntry;

				while(resultSet.next())
				{
					String name = resultSet.getString("FK_NAME");
					String fkInternalCatalog = resultSet.getString("FKTABLE_CAT");
					String fkEntity = resultSet.getString("FKTABLE_NAME");
					String fkField = resultSet.getString("FKCOLUMN_NAME");
					String pkInternalCatalog = resultSet.getString("PKTABLE_CAT");
					String pkEntity = resultSet.getString("PKTABLE_NAME");
					String pkField = resultSet.getString("PKCOLUMN_NAME");

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

					if(name != null && fkExternalCatalog != null && fkInternalCatalog != null && fkEntity != null && fkField != null && pkExternalCatalog != null && pkInternalCatalog != null && pkEntity != null && pkField != null)
					{
						frgnKeyEntry = m_tmp2.get(fkEntity);

						if(frgnKeyEntry != null)
						{
							frgnKeyEntry.put(fkField, new FrgnKeys(new FrgnKey(
								name,
								fkExternalCatalog,
								fkInternalCatalog,
								fkEntity,
								fkField,
								pkExternalCatalog,
								pkInternalCatalog,
								pkEntity,
								pkField
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
				             .get(frgnKey.pkEntity)
				             .get(frgnKey.pkField)
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

	public static Map<String, Map<String, Column>> getEntities(String externalCatalog) throws Exception
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

	public static Map<String, Column> getEntityInfo(String externalCatalog, String entity) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Column> map = getEntities(externalCatalog).get(entity);

		if(map != null)
		{
			return map;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("entity not found `" + externalCatalog + "`.`" + entity + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Column getFieldInfo(String externalCatalog, String entity, String field) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Column column = getEntityInfo(externalCatalog, entity).get(field);

		if(column != null)
		{
			return column;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("field not found `" + externalCatalog + "`.`" + entity + "`.`" + field + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, FrgnKeys> getForwardFKs(String externalCatalog, String entity) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, FrgnKeys>> map1 = s_forwardFKs.get(externalCatalog);

		if(map1 != null)
		{
			Map<String, FrgnKeys> map2 = map1.get(entity);

			if(map2 != null)
			{
				return map2;
			}
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("entity not found `" + externalCatalog + "`.`" + entity + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, FrgnKeys> getBackwardFKs(String externalCatalog, String entity) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Map<String, Map<String, FrgnKeys>> map1 = s_backwardFKs.get(externalCatalog);

		if(map1 != null)
		{
			Map<String, FrgnKeys> map2 = map1.get(entity);

			if(map2 != null)
			{
				return map2;
			}
		}
		/*-----------------------------------------------------------------*/

		throw new Exception("entity not found `" + externalCatalog + "`.`" + entity + "`");

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

	public static List<String> getEntityNames(String externalCatalog) throws Exception
	{
		return new ArrayList<>(
			getEntities(externalCatalog).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getFieldNames(String externalCatalog, String entity) throws Exception
	{
		return new ArrayList<>(
			getEntityInfo(externalCatalog, entity).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getForwardFKNames(String externalCatalog, String entity) throws Exception
	{
		return new LinkedHashSet<>(
			getForwardFKs(externalCatalog, entity).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getBackwardFKNames(String externalCatalog, String entity) throws Exception
	{
		return new LinkedHashSet<>(
			getBackwardFKs(externalCatalog, entity).keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static Column getPrimaryKey(String externalCatalog, String entity) throws Exception
	{
		/*-----------------------------------------------------------------*/

		for(Column column: getEntityInfo(externalCatalog, entity).values())
		{
			if(column.primary)
			{
				return column;
			}
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("primary key not found for `" + externalCatalog + "`.`" + entity + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static List<QId> getSortedColumnQId(String externalCatalog, String entity, @Nullable List<QId> constraints, boolean isAdmin) throws Exception
	{
		return getEntityInfo(externalCatalog, entity).values().stream()
		                                                      .filter(x -> isAdmin || (x.adminOnly == false && x.crypted == false)).sorted((x, y) -> x.rank - y.rank)
		                                                      .map(x -> new QId(x, false, constraints))
		                                                      .collect(Collectors.toList())
		;
	}

	/*---------------------------------------------------------------------*/

	public static void columnToStringBuilder(StringBuilder stringBuilder, Column column)
	{
		stringBuilder.append("<row>")
		             .append("<field name=\"externalCatalog\"><![CDATA[").append(column.externalCatalog).append("]]></field>")
		             .append("<field name=\"internalCatalog\"><![CDATA[").append(column.internalCatalog).append("]]></field>")
		             .append("<field name=\"entity\"><![CDATA[").append(column.entity).append("]]></field>")
		             .append("<field name=\"field\"><![CDATA[").append(column.field).append("]]></field>")
		             .append("<field name=\"type\"><![CDATA[").append(column.type).append("]]></field>")
		             .append("<field name=\"size\"><![CDATA[").append(column.size).append("]]></field>")
		             .append("<field name=\"digits\"><![CDATA[").append(column.digits).append("]]></field>")
		             .append("<field name=\"def\"><![CDATA[").append(column.def).append("]]></field>")
		             .append("<field name=\"rank\"><![CDATA[").append(column.rank).append("]]></field>")
		             .append("<field name=\"hidden\"><![CDATA[").append(column.hidden).append("]]></field>")
		             .append("<field name=\"adminOnly\"><![CDATA[").append(column.adminOnly).append("]]></field>")
		             .append("<field name=\"crypted\"><![CDATA[").append(column.crypted).append("]]></field>")
		             .append("<field name=\"primary\"><![CDATA[").append(column.primary).append("]]></field>")
		             .append("<field name=\"readable\"><![CDATA[").append(column.readable).append("]]></field>")
		             .append("<field name=\"created\"><![CDATA[").append(column.created).append("]]></field>")
		             .append("<field name=\"createdBy\"><![CDATA[").append(column.createdBy).append("]]></field>")
		             .append("<field name=\"modified\"><![CDATA[").append(column.modified).append("]]></field>")
		             .append("<field name=\"modifiedBy\"><![CDATA[").append(column.modifiedBy).append("]]></field>")
		             .append("<field name=\"automatic\"><![CDATA[").append(column.automatic).append("]]></field>")
		             .append("<field name=\"statable\"><![CDATA[").append(column.statable).append("]]></field>")
		             .append("<field name=\"groupable\"><![CDATA[").append(column.groupable).append("]]></field>")
		             .append("<field name=\"displayable\"><![CDATA[").append(column.displayable).append("]]></field>")
		             .append("<field name=\"base64\"><![CDATA[").append(column.base64).append("]]></field>")
		             .append("<field name=\"mime\"><![CDATA[").append(column.mime).append("]]></field>")
		             .append("<field name=\"ctrl\"><![CDATA[").append(column.ctrl).append("]]></field>")
		             .append("<field name=\"description\"><![CDATA[").append(column.description).append("]]></field>")
		             .append("<field name=\"webLinkScript\"><![CDATA[").append(column.webLinkScript).append("]]></field>")
		             .append("</row>")
		;
	}

	/*---------------------------------------------------------------------*/

	public static void frgnKeyToStringBuilder(StringBuilder stringBuilder, FrgnKey frgnKey)
	{
		stringBuilder.append("<row>")
		             .append("<field name=\"name\"><![CDATA[").append(frgnKey.name).append("]]></field>")
		             .append("<field name=\"fkExternalCatalog\"><![CDATA[").append(frgnKey.fkExternalCatalog).append("]]></field>")
		             .append("<field name=\"fkInternalCatalog\"><![CDATA[").append(frgnKey.fkInternalCatalog).append("]]></field>")
		             .append("<field name=\"fkEntity\"><![CDATA[").append(frgnKey.fkEntity).append("]]></field>")
		             .append("<field name=\"fkColumn\"><![CDATA[").append(frgnKey.fkField).append("]]></field>")
		             .append("<field name=\"pkExternalCatalog\"><![CDATA[").append(frgnKey.pkExternalCatalog).append("]]></field>")
		             .append("<field name=\"pkInternalCatalog\"><![CDATA[").append(frgnKey.pkInternalCatalog).append("]]></field>")
		             .append("<field name=\"pkEntity\"><![CDATA[").append(frgnKey.pkEntity).append("]]></field>")
		             .append("<field name=\"pkColumn\"><![CDATA[").append(frgnKey.pkField).append("]]></field>")
		             .append("</row>")
		;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getDBSchemas()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"fields\">");

		for(Map.Entry<String, Map<String, Map<String, Column>>> entry1: s_columns.entrySet())
		for(Map.Entry<String, Map<String, Column>> entry2: entry1.getValue().entrySet())
		for(Map.Entry<String, Column> entry3: entry2.getValue().entrySet())
		{
			columnToStringBuilder(result, entry3.getValue());
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(Map.Entry<String, Map<String, Map<String, FrgnKeys>>> entry1: s_forwardFKs.entrySet())
		for(Map.Entry<String, Map<String, FrgnKeys>> entry2: entry1.getValue().entrySet())
		for(Map.Entry<String, FrgnKeys> entry3: entry2.getValue().entrySet())
		{
			frgnKeyToStringBuilder(result, entry3.getValue().get(0));
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
