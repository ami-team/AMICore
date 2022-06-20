package net.hep.ami.jdbc.reflexion;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.driver.*;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class SchemaSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

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

		@NotNull
		@Override
		@Contract(pure = true)
		public String toString()
		{
			return "`" + internalCatalog + "`";
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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
		public final String type;
		public /*-*/ int rank;

		/**/

		public boolean bridge = false;

		public boolean hidden = false;
		public boolean adminOnly = false;

		public String description = "N/A";

		/**/

		public Table(@NotNull String _externalCatalog, @NotNull String _internalCatalog, @NotNull String _entity, @NotNull String _type, int _rank)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			entity = _entity;
			type = _type;
			rank = _rank;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(internalCatalog, entity);
		}

		@NotNull
		@Override
		@Contract(pure = true)
		public String toString()
		{
			return "`" + internalCatalog + "`.`" + entity + "`";
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@SuppressWarnings("UnusedAssignment")
	public static final class Column implements Serializable
	{
		private static final long serialVersionUID = 9088165113864128126L;

		/**/

		public final String externalCatalog;
		public final String internalCatalog;
		public final String entity;
		public final String field;
		public final String type;
		public final String nativeType;
		public final int jdbcType;
		public final int size;
		public final int digits;
		public final boolean nullable;
		public final String def;
		public /*-*/ int rank;

		/**/

		public boolean hidden = false;
		public boolean adminOnly = false;
		public boolean hashed = false;
		public boolean crypted = false;
		public boolean primary = false;
		public boolean json = false;

		public boolean automatic = false;
		public boolean created = false;
		public boolean createdBy = false;
		public boolean modified = false;
		public boolean modifiedBy = false;

		public boolean statable = false;
		public boolean groupable = false;

		public String displayQuery = "@NULL";

		public String webLinkScript = "@NULL";

		public boolean media = false;
		public boolean base64 = false;
		public String mime = "@NULL";
		public String ctrl = "@NULL";

		public String description = "N/A";

		/**/

		@NotNull
		@Contract(pure = true)
		public static String jdbcTypesToAMITypes(@Nullable String nativeType, int jdbcType, int digits)
		{
			/**/ if("SET".equalsIgnoreCase(nativeType))
			{
				return "SET";
			}
			else if("ENUM".equalsIgnoreCase(nativeType))
			{
				return "ENUM";
			}
			else switch(jdbcType) {
				/*----------------------------------------------------------------------------------------------------*/

				case Types.ROWID:
					return "ROWID";

				/*----------------------------------------------------------------------------------------------------*/

				case Types.BIT:
				case Types.BOOLEAN:
					return "BOOL";

				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.INTEGER:
				case Types.BIGINT:
					return "INT";

				case Types.REAL:
				case Types.FLOAT:
				case Types.DOUBLE:
					return "REAL";

				case Types.NUMERIC:
				case Types.DECIMAL:
					return digits <= 0 ? "INT" : "REAL";

				/*----------------------------------------------------------------------------------------------------*/

				case Types.CHAR:
				case Types.NCHAR:
				case Types.VARCHAR:
				case Types.NVARCHAR:
					return "TEXT";

				case Types.CLOB:
				case Types.NCLOB:
				case Types.LONGVARCHAR:
				case Types.LONGNVARCHAR:
					return "LONGTEXT";

				/*----------------------------------------------------------------------------------------------------*/

				case Types.BLOB:
				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
					return "BIN";

				/*----------------------------------------------------------------------------------------------------*/

				case Types.TIMESTAMP:
				case Types.TIMESTAMP_WITH_TIMEZONE:
				case -101 /* Oracle timestamp with time zone */:
					return "TIMESTAMP";

				case Types.DATE:
					return "DATE";

				case Types.TIME:
				case Types.TIME_WITH_TIMEZONE:
					return "TIME";

				/*----------------------------------------------------------------------------------------------------*/

				default:
					return "OTHER";

				/*----------------------------------------------------------------------------------------------------*/
			}
		}

		/**/

		public Column(@NotNull String _externalCatalog, @NotNull String _internalCatalog, @NotNull String _entity, @NotNull String _field, @NotNull String _nativeType, int _jdbcType, int _size, int _digits, boolean _nullable, String _def, int _rank)
		{
			externalCatalog = _externalCatalog;
			internalCatalog = _internalCatalog;
			entity = _entity;
			field = _field;

			type = jdbcTypesToAMITypes(_nativeType, _jdbcType, _digits);
			nativeType = _nativeType;
			jdbcType = _jdbcType;
			size = _size;
			digits = _digits;
			nullable = _nullable;
			def = _def;
			rank = _rank;

			statable = "INT".equals(type)
			           ||
			           "REAL".equals(type)
			;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(internalCatalog, entity, field);
		}

		@NotNull
		@Override
		@Contract(pure = true)
		public String toString()
		{
			return "`" + internalCatalog + "`.`" + entity + "`.`" + field + "`";
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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

		@Contract(pure = true)
		public FrgnKey(@NotNull String _name, @NotNull String _fkExternalCatalog, @NotNull String _fkInternalCatalog, @NotNull String _fkEntity, @NotNull String _fkField, @NotNull String _pkExternalCatalog, @NotNull String _pkInternalCatalog, @NotNull String _pkEntity, @NotNull String _pkField)
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

		@NotNull
		@Override
		@Contract(pure = true)
		public String toString()
		{
			return "`" + fkInternalCatalog + "`.`" + fkEntity + "`.`" + fkField + "` = `" + pkInternalCatalog + "`.`" + pkEntity + "`.`" + pkField + "`";
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final class FrgnKeys extends ArrayList<FrgnKey>
	{
		private static final long serialVersionUID = 646216521092672318L;

		public FrgnKeys()
		{
			super();
		}

		public FrgnKeys(@NotNull FrgnKey frgnKey)
		{
			super(); add(frgnKey);
		}

		public FrgnKeys(@NotNull Collection<FrgnKey> frgnKeys)
		{
			super(); addAll(frgnKeys);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, String> s_externalCatalogToInternalCatalog = new AMIMap<>(AMIMap.Type.CONCURRENT_HASH_MAP, true, true);

	/*----------------------------------------------------------------------------------------------------------------*/

	protected static final Map<String, Catalog> s_catalogs = new AMIMap<>(AMIMap.Type.CONCURRENT_HASH_MAP, false, true);

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private SchemaSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void clear()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		s_externalCatalogToInternalCatalog.clear();

		/*------------------------------------------------------------------------------------------------------------*/

		s_catalogs.clear();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void addSchema(@NotNull String externalCatalog, @NotNull String internalCatalog)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		s_externalCatalogToInternalCatalog.put(externalCatalog, internalCatalog);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	@SuppressWarnings("FieldCanBeLocal")
	/*----------------------------------------------------------------------------------------------------------------*/

	private static class Extractor implements Runnable
	{
		/*------------------------------------------------------------------------------------------------------------*/

		public static final org.slf4j.Logger m_logger = LogSingleton.getLogger(Extractor.class.getSimpleName(), "INFO");

		/*------------------------------------------------------------------------------------------------------------*/

		private final Map<String, String> m_externalCatalogToInternalCatalog;

		private final Map<String, Catalog> m_catalogs;

		/*------------------------------------------------------------------------------------------------------------*/

		private final String m_externalCatalog;
		private final String m_internalCatalog;

		private final CatalogSingleton.CatalogDescr m_catalogTuple;
		private final DriverSingleton.DriverDescr m_driverDescr;

		private final int m_rank;

		private final boolean m_fast;

		/*------------------------------------------------------------------------------------------------------------*/

		private Catalog m_catalog = null;

		/*------------------------------------------------------------------------------------------------------------*/

		@Contract(pure = true)
		public Extractor(
			@NotNull Map<String, String> externalCatalogToInternalCatalog,
			@NotNull Map<String, Catalog> catalogs,
			/**/
			@NotNull String externalCatalog,
			@NotNull String internalCatalog,
			@NotNull CatalogSingleton.CatalogDescr catalogTuple,
			@NotNull DriverSingleton.DriverDescr driverDescr,
			/**/
			int rank,
			/**/
			boolean fast
		 ) {
			m_externalCatalogToInternalCatalog = externalCatalogToInternalCatalog;

			m_catalogs = catalogs;

			/**/

			m_externalCatalog = externalCatalog;
			m_internalCatalog = internalCatalog;

			m_catalogTuple = catalogTuple;
			m_driverDescr = driverDescr;

			/**/

			m_rank = rank;

			/**/

			m_fast = fast;
		}

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		private void apply()
		{
			m_catalog.description = m_catalogTuple.getDescription();

			m_catalogs.put(m_externalCatalog, m_catalog);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		private void saveSchemaToFiles() throws Exception
		{
			m_logger.info("saving to file schema of catalog '{}'", m_externalCatalog);

			/*--------------------------------------------------------------------------------------------------------*/

			String basePath = ConfigSingleton.getConfigPathName() + File.separator + "cache";

			/*--------------------------------------------------------------------------------------------------------*/

			File file = new File(basePath);

			if(!file.exists())
			{
				file.mkdirs();
			}

			/*--------------------------------------------------------------------------------------------------------*/

			try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(basePath + File.separator + m_externalCatalog + ".ser")))
			{
				objectOutputStream.writeObject(m_catalog);
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		private void loadSchemaFromFiles() throws Exception
		{
			m_logger.info("for catalog '{}', loading schema from file...", m_externalCatalog);

			/*--------------------------------------------------------------------------------------------------------*/

			String basePath = ConfigSingleton.getConfigPathName() + File.separator + "cache";

			/*--------------------------------------------------------------------------------------------------------*/

			File file = new File(basePath);

			if(!file.exists())
			{
				file.mkdirs();
			}

			/*--------------------------------------------------------------------------------------------------------*/

			try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(basePath + File.separator + m_externalCatalog + ".ser")))
			{
				m_catalog = (Catalog) objectInputStream.readObject();
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@Nullable
		@Contract(pure = true)
		private String _getCatalogName(@Nullable String type)
		{
			if((m_driverDescr.getFlags() & DriverMetadata.FLAG_HAS_CATALOG) != 0)
			{
				return "SYNONYM".equals(type) && m_internalCatalog.endsWith("_W") ? m_internalCatalog.substring(0, m_internalCatalog.length() - 2) : m_internalCatalog; /* BERK !!! */
			}
			else
			{
				return null;
			}
		}

		@Nullable
		@Contract(pure = true)
		private String _getSchemaName(@Nullable String type)
		{
			if((m_driverDescr.getFlags() & DriverMetadata.FLAG_HAS_SCHEMA) != 0)
			{
				return "SYNONYM".equals(type) && m_catalogTuple.getInternalSchema().endsWith("_W") ? m_catalogTuple.getInternalSchema().substring(0, m_catalogTuple.getInternalSchema().length() - 2) : m_catalogTuple.getInternalSchema(); /* BERK !!! */
			}
			else
			{
				return null;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		private void loadSchemaFromDatabase() throws Exception
		{
			m_logger.info("for catalog '{}', loading schema from database...", m_externalCatalog);

			/*--------------------------------------------------------------------------------------------------------*/
			/* CREATE CATALOG                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			m_catalog = new Catalog(m_externalCatalog, m_internalCatalog, m_rank);

			/*-------------------------------------------------------------*/
			/* CREATE CONNECTION                                           */
			/*-------------------------------------------------------------*/

			try(Connection connection = DriverManager.getConnection(m_catalogTuple.getJdbcUrl(), m_catalogTuple.getUsername(), m_catalogTuple.getPassword()))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* GET METADATA OBJECT                                                                                */
				/*----------------------------------------------------------------------------------------------------*/

				DatabaseMetaData metaData = connection.getMetaData();

				/*----------------------------------------------------------------------------------------------------*/
				/* LOAD METADATA FROM DATABASE                                                                        */
				/*----------------------------------------------------------------------------------------------------*/

				Map<String, String> entities = new HashMap<>();

				/*----------------------------------------------------------------------------------------------------*/

				try(ResultSet resultSet = metaData.getTables(_getCatalogName(null), _getSchemaName(null), "%", new String[] {"TABLE", "VIEW", "SYNONYM"}))
				{
					String type;
					String entity;

					int rank = 1000;

					while(resultSet.next())
					{
						type = resultSet.getString("TABLE_TYPE");
						entity = resultSet.getString("TABLE_NAME");

						if(entity != null
						   &&
						   !entity.toLowerCase().startsWith("db_")
						   &&
						   !entity.toLowerCase().startsWith("x_db_")
						 ) {
							m_catalog.tables.put(entity, new Table(m_externalCatalog, m_internalCatalog, entity, type, rank++));

							entities.put(entity, type);
						}
					}
				}

				/*----------------------------------------------------------------------------------------------------*/

				for(Map.Entry<String, String> entry: entities.entrySet())
				{
					loadColumnMetadata(metaData, entry.getKey(), entry.getValue());

					loadFgnKeyMetadata(metaData, entry.getKey(), entry.getValue());
				}

				/*----------------------------------------------------------------------------------------------------*/
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		private void loadColumnMetadata(
			@NotNull DatabaseMetaData metaData,
			@NotNull String _entity,
			@NotNull String _type
		 ) throws SQLException {
			/*--------------------------------------------------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getColumns(_getCatalogName(_type), _getSchemaName(_type), _entity, "%"))
			{
				Table table;

				int rank = 1000;

				while(resultSet.next())
				{
					String entity = resultSet.getString("TABLE_NAME");
					String field = resultSet.getString("COLUMN_NAME");
					String nativeType = resultSet.getString("TYPE_NAME");
					int jdbcType = resultSet.getInt("DATA_TYPE");
					int size = resultSet.getInt("COLUMN_SIZE");
					int digits = resultSet.getInt("DECIMAL_DIGITS");
					String def = resultSet.getString("COLUMN_DEF");

					boolean nullable = true;

					// resultSet.getShort("NULLABLE") == DatabaseMetaData.columnNullable
					// ||
					// "YES".equalsIgnoreCase(resultSet.getString("is_nullable"))

					if(_entity.equalsIgnoreCase(entity) && field != null)
					{
						table = m_catalog.tables.get(entity);

						if(table != null)
						{
							table.columns.put(field, new Column(
								m_externalCatalog,
								m_internalCatalog,
								_entity,
								field,
								nativeType,
								jdbcType,
								size,
								digits,
								nullable,
								def == null ? (nullable ? "@NULL" : "")
								            : (def.toUpperCase().contains("CURRENT_TIMESTAMP") ? "@CURRENT_TIMESTAMP"
								                                                               : Utility.sqlValToText(def, false)),
								rank++
							));
						}
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getPrimaryKeys(_getCatalogName(_type), _getSchemaName(_type), _entity))
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

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		private void loadFgnKeyMetadata(
			@NotNull DatabaseMetaData metaData,
			@NotNull String _entity,
			@NotNull String _type
		 ) throws SQLException {
			/*--------------------------------------------------------------------------------------------------------*/

			try(ResultSet resultSet = metaData.getExportedKeys(_getCatalogName(_type), _getSchemaName(_type), _entity))
			{
				Table table;

				while(resultSet.next())
				{
					String name = resultSet.getString("FK_NAME");
					String fkEntity = resultSet.getString("FKTABLE_NAME");
					String fkField = resultSet.getString("FKCOLUMN_NAME");
					String pkEntity = resultSet.getString("PKTABLE_NAME");
					String pkField = resultSet.getString("PKCOLUMN_NAME");

					if(name != null && fkEntity != null && fkField != null && pkEntity != null && pkField != null)
					{
						CatalogTuple catalogTuple = resolvePKExternalCatalog(m_internalCatalog, fkEntity, fkField, resultSet.getString("PKTABLE_CAT"), pkEntity, pkField);

						if(catalogTuple.getExternalCatalog() != null && catalogTuple.getInternalCatalog() != null)
						{
							table = m_catalog.tables.get(fkEntity);

							if(table != null)
							{
								table.forwardFKs.put(fkField, new FrgnKeys(new FrgnKey(
									name,
									m_externalCatalog,
									m_internalCatalog,
									fkEntity,
									fkField,
									catalogTuple.getExternalCatalog(),
									catalogTuple.getInternalCatalog(),
									pkEntity,
									pkField
								)));
							}
						}
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@Getter
		@Setter
		@AllArgsConstructor
		private static final class CatalogTuple
		{
			@Nullable private final String externalCatalog;
			@Nullable private final String internalCatalog;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@NotNull
		@Contract("_, _, _, _, _, _ -> new")
		private CatalogTuple resolvePKExternalCatalog(@Nullable String fkInternalCatalog, @NotNull String fkEntity, @NotNull String fkField, @Nullable String pkInternalCatalog, @NotNull String pkEntity, @NotNull String pkField)
		{
			String result_pkExternalCatalog;
			String result_pkInternalCatalog;

			/*--------------------------------------------------------------------------------------------------------*/

			if(pkInternalCatalog == null)
			{
				if(!m_catalog.tables.containsKey(pkEntity) || !m_catalog.tables.get(pkEntity).columns.containsKey(pkField))
				{
					result_pkExternalCatalog = null;
					result_pkInternalCatalog = null;
				}
				else
				{
					result_pkExternalCatalog = m_externalCatalog;
					result_pkInternalCatalog = m_internalCatalog;
				}
			}
			else
			{
				if(pkInternalCatalog.equalsIgnoreCase(fkInternalCatalog))
				{
					if(!m_catalog.tables.containsKey(pkEntity) || !m_catalog.tables.get(pkEntity).columns.containsKey(pkField))
					{
						result_pkExternalCatalog = null;
						result_pkInternalCatalog = null;
					}
					else
					{
						result_pkExternalCatalog = m_externalCatalog;
						result_pkInternalCatalog = m_internalCatalog;
					}
				}
				else
				{
					int cnt = 0;

					result_pkExternalCatalog = null;
					result_pkInternalCatalog = null;

					for(Map.Entry<String, String> entry: m_externalCatalogToInternalCatalog.entrySet())
					{
						if(pkInternalCatalog.equalsIgnoreCase(entry.getValue()))
						{
							if(cnt++ > 0)
							{
								result_pkExternalCatalog = null;
								result_pkInternalCatalog = null;
							}
							else
							{
								result_pkExternalCatalog = entry. getKey ();
								result_pkInternalCatalog = entry.getValue();
							}
						}
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			return new CatalogTuple(
				result_pkExternalCatalog,
				result_pkInternalCatalog
			);

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static class Executor implements Runnable
	{
		/*------------------------------------------------------------------------------------------------------------*/

		public static final org.slf4j.Logger m_logger = LogSingleton.getLogger(Extractor.class.getSimpleName(), "INFO");

		/*------------------------------------------------------------------------------------------------------------*/

		private final Map<String, Catalog> m_catalogs;

		private final List<Thread> m_threads;

		/*------------------------------------------------------------------------------------------------------------*/

		@Contract(pure = true)
		public Executor(
			Map<String, Catalog> catalogs,
			List<Thread> threads
		 ) {
			m_catalogs = catalogs;

			m_threads = threads;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		@Override
		public void run()
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* START THREADS                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
			/* WAIT FOR THREADS                                                                                       */
			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
			/* POST TREATMENT                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/

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

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void rebuildSchemas(boolean flush) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		boolean slow = ConfigSingleton.getProperty("rebuild_schema_cache_in_background", false);

		/*------------------------------------------------------------------------------------------------------------*/
		/* FORCE                                                                                                      */
		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* FAST METHOD                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		/*----*/
		{
			int rank = 1000;

			CatalogSingleton.CatalogDescr catalogTuple;
			DriverSingleton.DriverDescr driverDescr;

			List<Thread> threads = new ArrayList<>();

			for(Map.Entry<String, String> entry: s_externalCatalogToInternalCatalog.entrySet())
			{
				catalogTuple = CatalogSingleton.getCatalogDescr(entry.getKey());
				driverDescr = DriverSingleton.getDriverDescr(catalogTuple.getJdbcUrl());

				threads.add(
					new Thread(
						new Extractor(
							s_externalCatalogToInternalCatalog,
							s_catalogs,
							entry.getKey(),
							entry.getValue(),
							catalogTuple,
								driverDescr,
							rank++,
							true // fast
						), "Fast metadata extractor for '" + entry.getKey() + "'"
					)
				);
			}

			new Executor(s_catalogs, threads).run();
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* SLOW METHOD                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		if(slow)
		{
			int rank = 1000;

			CatalogSingleton.CatalogDescr catalogTuple;
			DriverSingleton.DriverDescr driverDescr;

			List<Thread> threads = new ArrayList<>();

			for(Map.Entry<String, String> entry: s_externalCatalogToInternalCatalog.entrySet())
			{
				catalogTuple = CatalogSingleton.getCatalogDescr(entry.getKey());
				driverDescr = DriverSingleton.getDriverDescr(catalogTuple.getJdbcUrl());

				threads.add(
					new Thread(
						new Extractor(
							s_externalCatalogToInternalCatalog,
							s_catalogs,
							entry.getKey(),
							entry.getValue(),
							catalogTuple,
								driverDescr,
							rank++,
							false // slow
						), "Slow metadata extractor for '" + entry.getKey() + "'"
					)
				);
			}

			new Thread(new Executor(s_catalogs, threads)).start();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static String externalCatalogToInternalCatalog_noException(@Nullable String externalCatalog, @Nullable String value)
	{
		return s_externalCatalogToInternalCatalog.getOrDefault(externalCatalog, value);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String externalCatalogToInternalCatalog(@Nullable String externalCatalog) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String result = s_externalCatalogToInternalCatalog.get(externalCatalog);

		if(result != null)
		{
			return result;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		throw new Exception("external catalog not found `" + externalCatalog + "`");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Catalog getCatalogInfo(@Nullable String externalCatalog) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Catalog catalog = s_catalogs.get(externalCatalog);

		if(catalog != null)
		{
			return catalog;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		throw new Exception("catalog not found `" + externalCatalog + "`");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Table getEntityInfo(@Nullable String externalCatalog, @Nullable String entity) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Table table = getCatalogInfo(externalCatalog).tables.get(entity);

		if(table != null)
		{
			return table;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		throw new Exception("entity not found `" + externalCatalog + "`.`" + entity + "`");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Column getFieldInfo(@Nullable String externalCatalog, @Nullable String entity, @Nullable String field) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Column column = getEntityInfo(externalCatalog, entity).columns.get(field);

		if(column != null)
		{
			return column;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		throw new Exception("field not found `" + externalCatalog + "`.`" + entity + "`.`" + field + "`");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Map<String, FrgnKeys> getForwardFKs(@Nullable String externalCatalog, @Nullable String entity) throws Exception
	{
		return getEntityInfo(externalCatalog, entity).forwardFKs;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Map<String, FrgnKeys> getBackwardFKs(@Nullable String externalCatalog, @Nullable String entity) throws Exception
	{
		return getEntityInfo(externalCatalog, entity).backwardFKs;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(" -> new")
	public static List<String> getInternalCatalogNames()
	{
		return new ArrayList<>(
			s_externalCatalogToInternalCatalog.values()
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(" -> new")
	public static List<String> getExternalCatalogNames()
	{
		return new ArrayList<>(
			s_externalCatalogToInternalCatalog.keySet()
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public static List<String> getEntityNames(@Nullable String externalCatalog) throws Exception
	{
		return new ArrayList<>(
			getCatalogInfo(externalCatalog).tables.keySet()
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_, _ -> new")
	public static List<String> getFieldNames(@Nullable String externalCatalog, @Nullable String entity) throws Exception
	{
		return new ArrayList<>(
			getEntityInfo(externalCatalog, entity).columns.keySet()
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_, _ -> new")
	public static Set<String> getForwardFKNames(@Nullable String externalCatalog, @Nullable String entity) throws Exception
	{
		return new LinkedHashSet<>(
			getForwardFKs(externalCatalog, entity).keySet()
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_, _ -> new")
	public static Set<String> getBackwardFKNames(@Nullable String externalCatalog, @Nullable String entity) throws Exception
	{
		return new LinkedHashSet<>(
			getBackwardFKs(externalCatalog, entity).keySet()
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Column getPrimaryKey(@Nullable String externalCatalog, @Nullable String entity) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		for(Column column: getEntityInfo(externalCatalog, entity).columns.values())
		{
			if(column.primary)
			{
				return column;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		throw new Exception("primary key not found for `" + externalCatalog + "`.`" + entity + "`");

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<QId> getSortedQIds(@Nullable String externalCatalog, @Nullable String entity, @Nullable List<QId> constraints) throws Exception
	{
		return getEntityInfo(externalCatalog, entity).columns.values().stream()
		                                                              .sorted(Comparator.comparingInt(x -> x.rank))
		                                                              .map(x -> new QId(x, false, constraints))
		                                                              .collect(Collectors.toList())
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<QId> getReadableQIds(@Nullable String externalCatalog, @Nullable String entity, @Nullable List<QId> constraints) throws Exception
	{
		return getEntityInfo(externalCatalog, entity).columns.values().stream()
		                                                              .filter(x -> Empty.is(x.displayQuery, Empty.STRING_JAVA_NULL | Empty.STRING_AMI_NULL | Empty.STRING_EMPTY | Empty.STRING_BLANK))
		                                                              .map(x -> new QId(x, false, constraints))
		                                                              .collect(Collectors.toList())
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void appendCatalogToStringBuilder(@NotNull StringBuilder stringBuilder, @NotNull Catalog catalog)
	{
		stringBuilder.append("<row>")
		             .append("<field name=\"externalCatalog\"><![CDATA[").append(catalog.externalCatalog).append("]]></field>")
		             .append("<field name=\"internalCatalog\"><![CDATA[").append(catalog.internalCatalog).append("]]></field>")
		             .append("<field name=\"rank\"><![CDATA[").append(catalog.rank).append("]]></field>")
		             .append("<field name=\"description\"><![CDATA[").append(catalog.description).append("]]></field>")
		             .append("</row>")
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void appendTableToStringBuilder(@NotNull StringBuilder stringBuilder, @NotNull Table table)
	{
		stringBuilder.append("<row>")
		             .append("<field name=\"externalCatalog\"><![CDATA[").append(table.externalCatalog).append("]]></field>")
		             .append("<field name=\"internalCatalog\"><![CDATA[").append(table.internalCatalog).append("]]></field>")
		             .append("<field name=\"entity\"><![CDATA[").append(table.entity).append("]]></field>")
		             .append("<field name=\"type\"><![CDATA[").append(table.type).append("]]></field>")
		             .append("<field name=\"rank\"><![CDATA[").append(table.rank).append("]]></field>")
		             /**/
		             .append("<field name=\"bridge\"><![CDATA[").append(table.bridge).append("]]></field>")
		             .append("<field name=\"hidden\"><![CDATA[").append(table.hidden).append("]]></field>")
		             .append("<field name=\"adminOnly\"><![CDATA[").append(table.adminOnly).append("]]></field>")
		             /**/
		             .append("<field name=\"description\"><![CDATA[").append(table.description).append("]]></field>")
		             .append("</row>")
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void appendColumnToStringBuilder(@NotNull StringBuilder stringBuilder, @NotNull Column column)
	{
		stringBuilder.append("<row>")
		             .append("<field name=\"externalCatalog\"><![CDATA[").append(column.externalCatalog).append("]]></field>")
		             .append("<field name=\"internalCatalog\"><![CDATA[").append(column.internalCatalog).append("]]></field>")
		             .append("<field name=\"entity\"><![CDATA[").append(column.entity).append("]]></field>")
		             .append("<field name=\"field\"><![CDATA[").append(column.field).append("]]></field>")
		             .append("<field name=\"type\"><![CDATA[").append(column.type).append("]]></field>")
		             .append("<field name=\"nativeType\"><![CDATA[").append(column.nativeType).append("]]></field>")
		             .append("<field name=\"jdbcType\"><![CDATA[").append(column.jdbcType).append("]]></field>")
		             .append("<field name=\"size\"><![CDATA[").append(column.size).append("]]></field>")
		             .append("<field name=\"digits\"><![CDATA[").append(column.digits).append("]]></field>")
		             .append("<field name=\"nullable\"><![CDATA[").append(column.nullable).append("]]></field>")
		             .append("<field name=\"def\"><![CDATA[").append(column.def).append("]]></field>")
		             .append("<field name=\"rank\"><![CDATA[").append(column.rank).append("]]></field>")
		             /**/
		             .append("<field name=\"hidden\"><![CDATA[").append(column.hidden).append("]]></field>")
		             .append("<field name=\"adminOnly\"><![CDATA[").append(column.adminOnly).append("]]></field>")
		             .append("<field name=\"hashed\"><![CDATA[").append(column.hashed).append("]]></field>")
		             .append("<field name=\"crypted\"><![CDATA[").append(column.crypted).append("]]></field>")
		             .append("<field name=\"primary\"><![CDATA[").append(column.primary).append("]]></field>")
		             .append("<field name=\"json\"><![CDATA[").append(column.json).append("]]></field>")
		             .append("<field name=\"automatic\"><![CDATA[").append(column.automatic).append("]]></field>")
		             .append("<field name=\"created\"><![CDATA[").append(column.created).append("]]></field>")
		             .append("<field name=\"createdBy\"><![CDATA[").append(column.createdBy).append("]]></field>")
		             .append("<field name=\"modified\"><![CDATA[").append(column.modified).append("]]></field>")
		             .append("<field name=\"modifiedBy\"><![CDATA[").append(column.modifiedBy).append("]]></field>")
		             /**/
		             .append("<field name=\"statable\"><![CDATA[").append(column.statable).append("]]></field>")
		             .append("<field name=\"groupable\"><![CDATA[").append(column.groupable).append("]]></field>")
		             /**/
		             .append("<field name=\"displayQuery\"><![CDATA[").append(column.displayQuery).append("]]></field>")
		             /**/
		             .append("<field name=\"webLinkScript\"><![CDATA[").append(column.webLinkScript).append("]]></field>")
		             /**/
		             .append("<field name=\"media\"><![CDATA[").append(column.media).append("]]></field>")
		             .append("<field name=\"base64\"><![CDATA[").append(column.base64).append("]]></field>")
		             .append("<field name=\"mime\"><![CDATA[").append(column.mime).append("]]></field>")
		             .append("<field name=\"ctrl\"><![CDATA[").append(column.ctrl).append("]]></field>")
		             /**/
		             .append("<field name=\"description\"><![CDATA[").append(column.description).append("]]></field>")
		             .append("</row>")
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void appendFrgnKeyToStringBuilder(@NotNull StringBuilder stringBuilder, @NotNull FrgnKey frgnKey)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder getDBSchemas()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"catalogs\">");

		for(Catalog catalog: s_catalogs.values())
		{
			appendCatalogToStringBuilder(result, catalog);
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"entities\">");

		for(Catalog catalog: s_catalogs.values())
		 for(Table table: catalog.tables.values())
		{
			appendTableToStringBuilder(result, table);
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"fields\">");

		for(Catalog catalog: s_catalogs.values())
		 for(Table table: catalog.tables.values())
		  for(Column column: table.columns.values())
		{
			appendColumnToStringBuilder(result, column);
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(Catalog catalog: s_catalogs.values())
		 for(Table table: catalog.tables.values())
		  for(FrgnKeys frgnKeys: table.forwardFKs.values())
		{
			appendFrgnKeyToStringBuilder(result, frgnKeys.get(0));
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
