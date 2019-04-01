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

	public static final class Catalog implements Serializable
	{
		private static final long serialVersionUID = 3229166541876111056L;

		/**/

		public final Map<String, Table> tables = new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true);

		/**/

		public final String externalCatalog;
		public final String internalCatalog;
		public /*-*/ int rank;

		/**/

		public boolean archived = false;

		public String description = "N/A";

		/**/

		public Catalog(String _externalCatalog, String _internalCatalog, int _rank)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			rank = _rank;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(internalCatalog);
		}

		@Override
		public String toString()
		{
			return "`" + internalCatalog + "`";
		}
	}

	/*---------------------------------------------------------------------*/

	public static final class Table implements Serializable
	{
		private static final long serialVersionUID = 6564839225538503809L;

		/**/

		public final Map<String, Column> columns = new AMIMap<>(AMIMap.Type.LINKED_HASH_MAP, false, true);

		public final Map<String, FrgnKeys> forwardFKs = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);
		public final Map<String, FrgnKeys> backwardFKs = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);

		/**/

		public final String externalCatalog;
		public final String internalCatalog;
		public final String entity;
		public /*-*/ int rank;

		/**/

		public boolean bridge = false;

		public String description = "N/A";

		/**/

		public Table(String _externalCatalog, String _internalCatalog, String _entity, int _rank)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			entity = _entity;
			rank = _rank;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(internalCatalog, entity);
		}

		@Override
		public String toString()
		{
			return "`" + internalCatalog + "`.`" + entity + "`";
		}
	}

	/*---------------------------------------------------------------------*/

	public static final class Column implements Serializable
	{
		private static final long serialVersionUID = 9088165113864128126L;

		/**/

		private static final Pattern s_numberPattern = Pattern.compile(".*(?:BIT|INT|FLOAT|DOUBLE|SERIAL|DECIMAL|NUMBER).*", Pattern.CASE_INSENSITIVE);

		/**/

		public final String externalCatalog;
		public final String internalCatalog;
		public final String entity;
		public final String field;
		public final String type;
		public final int size;
		public final int digits;
		public final String def;
		public /*-*/ int rank;

		/**/

		public boolean hidden = false;
		public boolean adminOnly = false;
		public boolean crypted = false;
		public boolean primary = false;
		public boolean readable = false;

		public boolean automatic = false;
		public boolean created = false;
		public boolean createdBy = false;
		public boolean modified = false;
		public boolean modifiedBy = false;

		public boolean statable = false;
		public boolean groupable = false;

		public boolean displayable = false;
		public boolean base64 = false;
		public String mime = "@NULL";
		public String ctrl = "@NULL";

		public String description = "N/A";
		public String webLinkScript = "@NULL";

		/**/

		public Column(String _externalCatalog, String _internalCatalog, String _entity, String _field, String _type, int _size, int _digits, String _def, int _rank)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			entity = _entity;
			field = _field;
			type = _type;
			size = _size;
			digits = _digits;
			def = _def;
			rank = _rank;

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

	private static final Map<String, String> s_externalCatalogToInternalCatalog = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, true, true);
	private static final Map<String, String> s_internalCatalogToExternalCatalog = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, true, true);

	/*---------------------------------------------------------------------*/

	protected static final Map<String, Catalog> s_catalogs = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, true);

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

		s_catalogs.clear();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void addSchema(String externalCatalog, String internalCatalog)
	{
		/*-----------------------------------------------------------------*/

		s_externalCatalogToInternalCatalog.put(externalCatalog, internalCatalog);
		s_internalCatalogToExternalCatalog.put(internalCatalog, externalCatalog);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unused")
	/*---------------------------------------------------------------------*/

	private static class Extractor implements Runnable
	{
		/*-----------------------------------------------------------------*/

		public static final org.slf4j.Logger m_logger = LogSingleton.getLogger(Extractor.class.getSimpleName(), "INFO");

		/*-----------------------------------------------------------------*/

		private final Map<String, String> m_externalCatalogToInternalCatalog;
		private final Map<String, String> m_internalCatalogToExternalCatalog;

		private final Map<String, Catalog> m_catalogs;

		/*-----------------------------------------------------------------*/

		private final String m_externalCatalog;
		private final String m_internalCatalog;

		private final CatalogSingleton.Tuple m_tuple;

		private final int m_rank;

		private final boolean m_fast;

		/*-----------------------------------------------------------------*/

		private Catalog m_catalog = null;

		/*-----------------------------------------------------------------*/

		public Extractor(
			Map<String, String> externalCatalogToInternalCatalog,
			Map<String, String> internalCatalogToExternalCatalog,
			Map<String, Catalog> catalogs,
			/**/
			String externalCatalog,
			String internalCatalog,
			CatalogSingleton.Tuple tuple,
			/**/
			int rank,
			/**/
			boolean fast
		 ) {
			m_externalCatalogToInternalCatalog = externalCatalogToInternalCatalog;
			m_internalCatalogToExternalCatalog = internalCatalogToExternalCatalog;

			m_catalogs = catalogs;

			/**/

			m_externalCatalog = externalCatalog;
			m_internalCatalog = internalCatalog;

			m_tuple = tuple;

			/**/

			m_rank = rank;

			/**/

			m_fast = fast;
		}

		/*-----------------------------------------------------------------*/

		@Override
		public void run()
		{
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
			m_catalog.description = m_tuple.w;

			m_catalogs.put(m_externalCatalog, m_catalog);
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

			try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(basePath + File.separator + m_externalCatalog + ".ser")))
			{
				objectOutputStream.writeObject((Catalog) m_catalog);
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

			try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(basePath + File.separator + m_externalCatalog + ".ser")))
			{
				m_catalog = (Catalog) objectInputStream.readObject();
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		private void loadSchemaFromDatabase() throws Exception
		{
			m_logger.info("for catalog '{}', loading from schema from database...", m_externalCatalog);

			/*-------------------------------------------------------------*/
			/* CREATE CATALOG                                              */
			/*-------------------------------------------------------------*/

			m_catalog = new Catalog(m_externalCatalog, m_internalCatalog, m_rank);

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

				Set<String> entities = new HashSet<>();

				/*---------------------------------------------------------*/

				try(ResultSet resultSet = metaData.getTables(m_internalCatalog, m_tuple.z, "%", new String[] {"TABLE", "VIEW"}))
				{
					String entity;

					int rank = 1000;

					while(resultSet.next())
					{
						entity = resultSet.getString("TABLE_NAME");

						if(entity != null
						   &&
						   entity.toLowerCase().startsWith("db_") == false
						   &&
						   entity.toLowerCase().startsWith("x_db_") == false
						 ) {
							m_catalog.tables.put(entity, new Table(m_externalCatalog, m_internalCatalog, entity, rank++));

							entities.add(entity);
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

			boolean isOracle = metaData.getClass().getName().startsWith("oracle");

			/*-------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getColumns(m_internalCatalog, m_tuple.z, _entity, "%"))
			{
				Table table;

				int rank = 1000;

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
						table = m_catalog.tables.get(entity);

						if(table != null)
						{
							table.columns.put(field, new Column(
								m_externalCatalog,
								m_internalCatalog,
								entity,
								field,
								type,
								size,
								digits,
								def == null ? (nullable ? "@NULL" : "")
								            : (def.toUpperCase().contains("CURRENT_TIMESTAMP") ? "@CURRENT_TIMESTAMP"
								                                                               : (isOracle ? Utility.sqlValToText(def)
								                                                                           : /*----------------*/(def)
								                                                                 )
								              ),
								rank++
							));
						}
					}
				}
			}

			/*-------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getPrimaryKeys(m_internalCatalog, m_tuple.z, _entity))
			{
				while(resultSet.next())
				{
					String entity = resultSet.getString("TABLE_NAME");
					String field = resultSet.getString("COLUMN_NAME");

					Column column = m_catalog.tables.get(entity).columns.get(field);

					if(column.statable)
					{
						column.primary = true;

						break;
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
				Table table;

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
						table = m_catalog.tables.get(fkEntity);

						if(table != null)
						{
							table.forwardFKs.put(fkField, new FrgnKeys(new FrgnKey(
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

		private final Map<String, Catalog> m_catalogs;

		private final List<Thread> m_threads;

		/*-----------------------------------------------------------------*/

		public Executor(
			Map<String, Catalog> catalogs,
			List<Thread> threads
		 ) {
			m_catalogs = catalogs;

			m_threads = threads;
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

			FrgnKeys frgnKeys1;

			for(Catalog catalog1: m_catalogs.values())
			 for(Table table1: catalog1.tables.values())
			  for(Column column1: table1.columns.values())
			{
				frgnKeys1 = new FrgnKeys();

				m_catalogs.get(catalog1.externalCatalog)
				          .tables.get(table1.entity)
				          .backwardFKs.put(column1.field, frgnKeys1)
				;
			}

			/*-------------------------------------------------------------*/

			FrgnKey frgnKey2;

			for(Catalog catalog2: m_catalogs.values())
			 for(Table table2: catalog2.tables.values())
			  for(FrgnKeys frgnKeys2: table2.forwardFKs.values())
			{
				frgnKey2 = frgnKeys2.get(0);

				m_catalogs.get(frgnKey2.pkExternalCatalog)
				          .tables.get(frgnKey2.pkEntity)
				          .backwardFKs.get(frgnKey2.pkField).add(frgnKey2)
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
		/* FORCE                                                           */
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
			int rank = 1000;

			List<Thread> threads = new ArrayList<>();

			for(Map.Entry<String, String> entry: s_externalCatalogToInternalCatalog.entrySet())
			{
				threads.add(new Thread(new Extractor(
						s_externalCatalogToInternalCatalog,
						s_internalCatalogToExternalCatalog,
						s_catalogs,
						entry.getKey(),
						entry.getValue(),
						CatalogSingleton.getTuple(entry.getKey()),
						rank++,
						true // fast
					), "Fast metadata extractor for '" + entry.getKey() + "'"
				));
			}

			new Executor(s_catalogs, threads).run();
		}

		/*-----------------------------------------------------------------*/
		/* SLOW METHOD                                                     */
		/*-----------------------------------------------------------------*/

		if(slow)
		{
			int rank = 1000;

			List<Thread> threads = new ArrayList<>();

			for(Map.Entry<String, String> entry: s_externalCatalogToInternalCatalog.entrySet())
			{
				threads.add(new Thread(new Extractor(
						s_externalCatalogToInternalCatalog,
						s_internalCatalogToExternalCatalog,
						s_catalogs,
						entry.getKey(),
						entry.getValue(),
						CatalogSingleton.getTuple(entry.getKey()),
						rank++,
						false // slow
					), "Slow metadata extractor for '" + entry.getKey() + "'"
				));
			}

			new Thread(new Executor(s_catalogs, threads)).start();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String internalCatalogToExternalCatalog_noException(@Nullable String internalCatalog, String value)
	{
		String result = s_internalCatalogToExternalCatalog.get(internalCatalog);

		return result != null ? result : value;
	}

	/*---------------------------------------------------------------------*/

	public static String externalCatalogToInternalCatalog_noException(@Nullable String externalCatalog, String value)
	{
		String result =  s_externalCatalogToInternalCatalog.get(externalCatalog);

		return result != null ? result : value;
	}

	/*---------------------------------------------------------------------*/

	public static String internalCatalogToExternalCatalog(@Nullable String internalCatalog) throws Exception
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

	public static String externalCatalogToInternalCatalog(@Nullable String externalCatalog) throws Exception
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

	public static Catalog getCatalogInfo(String externalCatalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Catalog catalog = s_catalogs.get(externalCatalog);

		if(catalog != null)
		{
			return catalog;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("catalog not found `" + externalCatalog + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Table getEntityInfo(String externalCatalog, String _entity) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Table table = getCatalogInfo(externalCatalog).tables.get(_entity);

		if(table != null)
		{
			return table;
		}

		/*-----------------------------------------------------------------*/

		throw new Exception("entity not found `" + externalCatalog + "`.`" + _entity + "`");

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Column getFieldInfo(String externalCatalog, String entity, String field) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Column column = getEntityInfo(externalCatalog, entity).columns.get(field);

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

		Catalog catalog = s_catalogs.get(externalCatalog);

		if(catalog != null)
		{
			Map<String, FrgnKeys> map2 = catalog.tables.get(entity).forwardFKs;

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

		Catalog catalog = s_catalogs.get(externalCatalog);

		if(catalog != null)
		{
			Map<String, FrgnKeys> map2 = catalog.tables.get(entity).backwardFKs;

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
			getCatalogInfo(externalCatalog).tables.keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static List<String> getFieldNames(String externalCatalog, String entity) throws Exception
	{
		return new ArrayList<>(
			getEntityInfo(externalCatalog, entity).columns.keySet()
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

		for(Column column: getEntityInfo(externalCatalog, entity).columns.values())
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

	public static List<QId> getSortedQIds(String externalCatalog, String entity, @Nullable List<QId> constraints) throws Exception
	{
		return getEntityInfo(externalCatalog, entity).columns.values().stream()
		                                                              .sorted((x, y) -> x.rank - y.rank)
		                                                              .map(x -> new QId(x, false, constraints))
		                                                              .collect(Collectors.toList())
		;
	}

	/*---------------------------------------------------------------------*/

	public static List<QId> getReadableQIds(String externalCatalog, String entity, @Nullable List<QId> constraints) throws Exception
	{
		return getEntityInfo(externalCatalog, entity).columns.values().stream()
		                                                              .filter(x -> x.readable)
		                                                              .map(x -> new QId(x, false, constraints))
		                                                              .collect(Collectors.toList())
		;
	}

	/*---------------------------------------------------------------------*/

	public static void appendCatalogToStringBuilder(StringBuilder stringBuilder, Catalog catalog)
	{
		stringBuilder.append("<row>")
		             .append("<field name=\"externalCatalog\"><![CDATA[").append(catalog.externalCatalog).append("]]></field>")
		             .append("<field name=\"internalCatalog\"><![CDATA[").append(catalog.internalCatalog).append("]]></field>")
		             .append("<field name=\"rank\"><![CDATA[").append(catalog.rank).append("]]></field>")
		             .append("<field name=\"description\"><![CDATA[").append(catalog.description).append("]]></field>")
		             .append("</row>")
		;
	}

	/*---------------------------------------------------------------------*/

	public static void appendTableToStringBuilder(StringBuilder stringBuilder, Table table)
	{
		stringBuilder.append("<row>")
		             .append("<field name=\"externalCatalog\"><![CDATA[").append(table.externalCatalog).append("]]></field>")
		             .append("<field name=\"internalCatalog\"><![CDATA[").append(table.internalCatalog).append("]]></field>")
		             .append("<field name=\"entity\"><![CDATA[").append(table.entity).append("]]></field>")
		             .append("<field name=\"rank\"><![CDATA[").append(table.rank).append("]]></field>")
		             .append("<field name=\"bridge\"><![CDATA[").append(table.bridge).append("]]></field>")
		             .append("<field name=\"description\"><![CDATA[").append(table.description).append("]]></field>")
		             .append("</row>")
		;
	}

	/*---------------------------------------------------------------------*/

	public static void appendColumnToStringBuilder(StringBuilder stringBuilder, Column column)
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
		             .append("<field name=\"automatic\"><![CDATA[").append(column.automatic).append("]]></field>")
		             .append("<field name=\"created\"><![CDATA[").append(column.created).append("]]></field>")
		             .append("<field name=\"createdBy\"><![CDATA[").append(column.createdBy).append("]]></field>")
		             .append("<field name=\"modified\"><![CDATA[").append(column.modified).append("]]></field>")
		             .append("<field name=\"modifiedBy\"><![CDATA[").append(column.modifiedBy).append("]]></field>")
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

	public static void appendFrgnKeyToStringBuilder(StringBuilder stringBuilder, FrgnKey frgnKey)
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

		result.append("<rowset type=\"catalogs\">");

		for(Catalog catalog: s_catalogs.values())
		{
			appendCatalogToStringBuilder(result, catalog);
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"entities\">");

		for(Catalog catalog: s_catalogs.values())
		 for(Table table: catalog.tables.values())
		{
			appendTableToStringBuilder(result, table);
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"fields\">");

		for(Catalog catalog: s_catalogs.values())
		 for(Table table: catalog.tables.values())
		  for(Column column: table.columns.values())
		{
			appendColumnToStringBuilder(result, column);
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(Catalog catalog: s_catalogs.values())
		 for(Table table: catalog.tables.values())
		  for(FrgnKeys frgnKeys: table.forwardFKs.values())
		{
			appendFrgnKeyToStringBuilder(result, frgnKeys.get(0));
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
