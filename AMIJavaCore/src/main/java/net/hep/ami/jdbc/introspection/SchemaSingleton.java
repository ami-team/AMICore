package net.hep.ami.jdbc.introspection;

import java.sql.*;
import java.util.*;
import java.util.Map.*;

public class SchemaSingleton {
	/*---------------------------------------------------------------------*/

	public static class Column {

		public String internalCatalog;
		public String catalog;
		public String table;
		public String name;
		public String type;
		public int size;

		public Column(String _internalCatalog, String _catalog, String _table, String _name, String _type, int _size) {

			internalCatalog = _internalCatalog;
			catalog = _catalog;
			table = _table;
			name = _name;
			type = _type;
			size = _size;
		}
	}

	/*---------------------------------------------------------------------*/

	public static class FrgnKey {

		public String catalog;
		public String name;
		public String fkTable;
		public String fkColumn;
		public String pkTable;
		public String pkColumn;

		public FrgnKey(String _catalog, String _name, String _fkTable, String _fkColumn, String _pkTable, String _pkColumn) {

			catalog = _catalog;
			name = _name;
			fkTable = _fkTable;
			fkColumn = _fkColumn;
			pkTable = _pkTable;
			pkColumn = _pkColumn;
		}
	}

	/*---------------------------------------------------------------------*/

	public static class Index {

		public String catalog;
		public String table;
		public String name;
		public String type;
		public String column;
		public int position;

		public Index(String _catalog, String _table, String _name, String _type, String _column, int _position) {

			catalog = _catalog;
			table = _table;
			name = _name;
			type = _type;
			column = _column;
			position = _position;
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> m_internalCatalogToExternalCatalog = new HashMap<String, String>();
	private static final Map<String, String> m_externalCatalogToInternalCatalog = new HashMap<String, String>();

	/*---------------------------------------------------------------------*/

	private static final Map<String, Map<String, Map<String, Column>>> m_columns = new HashMap<String, Map<String, Map<String, Column>>>();
	private static final Map<String, Map<String, Map<String, FrgnKey>>> m_frgnKeys = new HashMap<String, Map<String, Map<String, FrgnKey>>>();
	private static final Map<String, Map<String, List<Index>>> m_indices = new HashMap<String, Map<String, List<Index>>>();

	/*---------------------------------------------------------------------*/

	private static long m_executionTime = 0;

	/*---------------------------------------------------------------------*/

	public static void readSchema(Connection connection, String catalog) throws SQLException {
			/*-------------------------------------------------------------*/
			/* READ DB METADATA                                            */
			/*-------------------------------------------------------------*/

			long t1 = System.currentTimeMillis();

			m_columns.put(catalog, new HashMap<String, Map<String, Column>>());
			m_frgnKeys.put(catalog, new HashMap<String, Map<String, FrgnKey>>());
			m_indices.put(catalog, new HashMap<String, List<Index>>());

			readMetaData(
				connection.getMetaData(),
				connection.getCatalog(),
				catalog
			);

			long t2 = System.currentTimeMillis();

			/*-------------------------------------------------------------*/
			/* UPDATE EXECUTION TIME                                       */
			/*-------------------------------------------------------------*/

			m_executionTime += t2 - t1;

			/*-------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readMetaData(DatabaseMetaData metaData, String internalCatalog, String externalCatalog) throws SQLException {
		/*-----------------------------------------------------------------*/
		/* INTERNAL/EXTERNAL CATALOG BIJECTION                             */
		/*-----------------------------------------------------------------*/

		m_internalCatalogToExternalCatalog.put(internalCatalog, externalCatalog);

		m_externalCatalogToInternalCatalog.put(externalCatalog, internalCatalog);

		/*-----------------------------------------------------------------*/
		/* INITIALIZE STRUCTURES                                           */
		/*-----------------------------------------------------------------*/

		ResultSet resultSet = metaData.getTables(internalCatalog, null, "%", null);

		List<String> names = new ArrayList<String>();

		while(resultSet.next()) {

			String name = resultSet.getString("TABLE_NAME");

			m_columns.get(externalCatalog).put(name, new LinkedHashMap<String, Column>());
			m_frgnKeys.get(externalCatalog).put(name, new LinkedHashMap<String, FrgnKey>());
			m_indices.get(externalCatalog).put(name, new ArrayList<Index>());

			names.add(name);
		}

		/*-----------------------------------------------------------------*/
		/* READ METADATA                                                   */
		/*-----------------------------------------------------------------*/

		for(String name: names) {
			readColumnMetaData(metaData, internalCatalog, externalCatalog, name);
			readFgnKeyMetaData(metaData, internalCatalog, externalCatalog, name);
			readIndexMetaData(metaData, internalCatalog, externalCatalog, name);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readColumnMetaData(DatabaseMetaData metaData, String internalCatalog, String externalCatalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getColumns(internalCatalog, null, table, null);

		while(resultSet.next()) {

			String name = resultSet.getString("COLUMN_NAME");
			String type = resultSet.getString("TYPE_NAME");
			int size = resultSet.getInt("COLUMN_SIZE");

			m_columns.get(externalCatalog).get(table).put(name, new Column(
				internalCatalog,
				externalCatalog,
				table,
				name,
				type,
				size
			));
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readFgnKeyMetaData(DatabaseMetaData metaData, String internalCatalog, String externalCatalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getExportedKeys(internalCatalog, null, table);

		while(resultSet.next()) {

			String name = resultSet.getString("FK_NAME");
			String fktable = resultSet.getString("FKTABLE_NAME");
			String fkcolumn = resultSet.getString("FKCOLUMN_NAME");
			String pktable = resultSet.getString("PKTABLE_NAME");
			String pkcolumn = resultSet.getString("PKCOLUMN_NAME");

			m_frgnKeys.get(externalCatalog).get(fktable).put(fkcolumn, new FrgnKey(
				externalCatalog,
				name,
				fktable,
				fkcolumn,
				pktable,
				pkcolumn
			));
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readIndexMetaData(DatabaseMetaData metaData, String internalCatalog, String externalCatalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getIndexInfo(internalCatalog, null, table, false, false);

		while(resultSet.next()) {

			String name = resultSet.getString("INDEX_NAME");
			String column = resultSet.getString("COLUMN_NAME");
			int ordinalPosition = resultSet.getInt("ORDINAL_POSITION");
			boolean nonUnique = resultSet.getBoolean("NON_UNIQUE");

			String type = (nonUnique == false) ? name.equals("PRIMARY") ? "PRIMARY"
			                                                            : "UNIQUE"
			                                                            : "INDEX"
			;

			m_indices.get(externalCatalog).get(table).add(new Index(
				externalCatalog,
				table,
				name,
				type,
				column,
				ordinalPosition
			));
		}
	}

	/*---------------------------------------------------------------------*/

	public static String internalCatalogToExternalCatalog(String catalog) throws Exception {

		String result = m_internalCatalogToExternalCatalog.get(catalog);

		if(result != null) {
			return result;
		}

		throw new Exception("internal catalog not found `" + catalog + "`");
	}

	/*---------------------------------------------------------------------*/

	public static String externalCatalogToInternalCatalog(String catalog) throws Exception {

		String result = m_externalCatalogToInternalCatalog.get(catalog);

		if(result != null) {
			return result;
		}

		throw new Exception("external catalog not found `" + catalog + "`");
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getCatalogNames() {

		return m_columns.keySet();
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getTableNames(String catalog) throws Exception {

		Map<String, Map<String, Column>> map = m_columns.get(catalog);

		if(map != null) {
			return map.keySet();
		}

		throw new Exception("catalog not found `" + catalog + "`");
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getColumnNames(String catalog, String table) throws Exception {

		Map<String, Map<String, Column>> map1 = m_columns.get(catalog);

		if(map1 != null) {

			Map<String, Column> map2 = map1.get(table);

			if(map2 != null) {
				return map2.keySet();
			}
		}

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, Column> getColumns(String catalog, String table) throws Exception {

		Map<String, Map<String, Column>> map1 = m_columns.get(catalog);

		if(map1 != null) {

			Map<String, Column> map2 = map1.get(table);

			if(map2 != null) {
				return map2;
			}
		}

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, FrgnKey> getFgnKeys(String catalog, String table) throws Exception {

		Map<String, Map<String, FrgnKey>> map1 = m_frgnKeys.get(catalog);

		if(map1 != null) {

			Map<String, FrgnKey> map2 = map1.get(table);

			if(map2 != null) {
				return map2;
			}
		}

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");
	}

	/*---------------------------------------------------------------------*/

	public static List<Index> getIndices(String catalog, String table) throws Exception {

		Map<String, List<Index>> map1 = m_indices.get(catalog);

		if(map1 != null) {

			List<Index> map2 = map1.get(table);

			if(map2 != null) {
				return map2;
			}
		}

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getDBSchemas() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"columns\">");

		for(Entry<String, Map<String, Map<String, Column>>> entry1: m_columns.entrySet()) {

			for(Entry<String, Map<String, Column>> entry2: entry1.getValue().entrySet()) {

				for(Entry<String, Column> entry3: entry2.getValue().entrySet()) {

					result.append(
						"<row>"
						+
						"<field name=\"internalCatalog\">" + entry3.getValue().internalCatalog + "</field>"
						+
						"<field name=\"externalCatalog\">" + entry3.getValue().catalog + "</field>"
						+
						"<field name=\"table\">" + entry3.getValue().table + "</field>"
						+
						"<field name=\"name\">" + entry3.getValue().name + "</field>"
						+
						"<field name=\"type\">" + entry3.getValue().type + "</field>"
						+
						"<field name=\"size\">" + entry3.getValue().size + "</field>"
						+
						"</row>"
					);
				}
			}
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(Entry<String, Map<String, Map<String, FrgnKey>>> entry1: m_frgnKeys.entrySet()) {

			for(Entry<String, Map<String, FrgnKey>> entry2: entry1.getValue().entrySet()) {

				for(Entry<String, FrgnKey> entry3: entry2.getValue().entrySet()) {

					result.append(
						"<row>"
						+
						"<field name=\"catalog\">" + entry3.getValue().catalog + "</field>"
						+
						"<field name=\"fkTable\">" + entry3.getValue().fkTable + "</field>"
						+
						"<field name=\"fkColumn\">" + entry3.getValue().fkColumn + "</field>"
						+
						"<field name=\"pkTable\">" + entry3.getValue().pkTable + "</field>"
						+
						"<field name=\"pkColumn\">" + entry3.getValue().pkColumn + "</field>"
						+
						"</row>"
					);
				}
			}
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"indices\">");

		for(Entry<String, Map<String, List<Index>>> entry1: m_indices.entrySet()) {

			for(Entry<String, List<Index>> entry2: entry1.getValue().entrySet()) {

				for(Index entry3: entry2.getValue()) {

					result.append(
						"<row>"
						+
						"<field name=\"catalog\">" + entry3.catalog + "</field>"
						+
						"<field name=\"table\">" + entry3.table + "</field>"
						+
						"<field name=\"name\">" + entry3.name + "</field>"
						+
						"<field name=\"type\">" + entry3.type + "</field>"
						+
						"<field name=\"column\">" + entry3.column + "</field>"
						+
						"<field name=\"position\">" + entry3.position + "</field>"
						+
						"</row>"
					);
				}
			}
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("</Result>");

		/*-----------------------------------------------------------------*/

		result.append("<info>" + String.format(Locale.US, "%.3f", 0.001f * m_executionTime) + " s at startup</info>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
