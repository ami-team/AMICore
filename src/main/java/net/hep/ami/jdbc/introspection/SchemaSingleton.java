package net.hep.ami.jdbc.introspection;

import java.sql.*;
import java.util.*;
import java.util.Map.*;

public class SchemaSingleton {
	/*---------------------------------------------------------------------*/

	public static class Column {
		/*-----------------------------------------------------------------*/

		public String m_internalName;
		public String m_catalog;
		public String m_table;
		public String m_name;
		public String m_type;
		public int m_size;

		/*-----------------------------------------------------------------*/

		public Column(String internalName, String catalog, String table, String name, String type, int size) {

			m_internalName = internalName;
			m_catalog = catalog;
			m_table = table;
			m_name = name;
			m_type = type;
			m_size = size;
		}

		/*-----------------------------------------------------------------*/

		public String getinternalName() {

			return m_internalName;
		}

		/*-----------------------------------------------------------------*/

		public String getCatalog() {

			return m_catalog;
		}

		/*-----------------------------------------------------------------*/

		public String getTable() {

			return m_table;
		}

		/*-----------------------------------------------------------------*/

		public String getName() {

			return m_name;
		}

		/*-----------------------------------------------------------------*/

		public String getType() {

			return m_type;
		}

		/*-----------------------------------------------------------------*/

		public int getSize() {

			return m_size;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static class FrgnKey {
		/*-----------------------------------------------------------------*/

		public String m_catalog;
		public String m_name;
		public String m_fkTable;
		public String m_fkColumn;
		public String m_pkTable;
		public String m_pkColumn;

		/*-----------------------------------------------------------------*/

		public FrgnKey(String catalog, String name, String fkTable, String fkColumn, String pkTable, String pkColumn) {

			m_catalog = catalog;
			m_name = name;
			m_fkTable = fkTable;
			m_fkColumn = fkColumn;
			m_pkTable = pkTable;
			m_pkColumn = pkColumn;
		}

		/*-----------------------------------------------------------------*/

		public String getCatalog() {

			return m_catalog;
		}

		/*-----------------------------------------------------------------*/

		public String getName() {

			return m_name;
		}

		/*-----------------------------------------------------------------*/

		public String getmFKTable() {

			return m_fkTable;
		}

		/*-----------------------------------------------------------------*/

		public String getFKColumn() {

			return m_fkColumn;
		}

		/*-----------------------------------------------------------------*/

		public String getPKTable() {

			return m_pkTable;
		}

		/*-----------------------------------------------------------------*/

		public String getPKColumn() {

			return m_pkColumn;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static class Index {
		/*-----------------------------------------------------------------*/

		public String m_catalog;
		public String m_table;
		public String m_name;
		public String m_type;
		public String m_column;
		public int m_position;

		/*-----------------------------------------------------------------*/

		public Index(String catalog, String table, String name, String type, String column, int position) {

			m_catalog = catalog;
			m_table = table;
			m_name = name;
			m_type = type;
			m_column = column;
			m_position = position;
		}

		/*-----------------------------------------------------------------*/

		public String getCatalog() {

			return m_catalog;
		}

		/*-----------------------------------------------------------------*/

		public String getTable() {

			return m_table;
		}

		/*-----------------------------------------------------------------*/

		public String getName() {

			return m_name;
		}

		/*-----------------------------------------------------------------*/

		public String getType() {

			return m_type;
		}

		/*-----------------------------------------------------------------*/

		public String getColumn() {

			return m_column;
		}

		/*-----------------------------------------------------------------*/

		public int getPosition() {

			return m_position;
		}

		/*-----------------------------------------------------------------*/
	}

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

	private static void readMetaData(DatabaseMetaData metaData, String internalCatalogName, String catalog) throws SQLException {
		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		ResultSet resultSet = metaData.getTables(internalCatalogName, null, "%", null);

		List<String> names = new ArrayList<String>();

		while(resultSet.next()) {

			String name = resultSet.getString("TABLE_NAME");

			m_columns.get(catalog).put(name, new LinkedHashMap<String, Column>());
			m_frgnKeys.get(catalog).put(name, new LinkedHashMap<String, FrgnKey>());
			m_indices.get(catalog).put(name, new ArrayList<Index>());

			names.add(name);
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		for(String name: names) {
			readColumnMetaData(metaData, internalCatalogName, catalog, name);
			readFgnKeyMetaData(metaData, internalCatalogName, catalog, name);
			readIndexMetaData(metaData, internalCatalogName, catalog, name);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void readColumnMetaData(DatabaseMetaData metaData, String internalCatalogName, String catalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getColumns(internalCatalogName, null, table, null);

		while(resultSet.next()) {

			String name = resultSet.getString("COLUMN_NAME");
			String type = resultSet.getString("TYPE_NAME");
			int size = resultSet.getInt("COLUMN_SIZE");

			m_columns.get(catalog).get(table).put(name, new Column(
				internalCatalogName,
				catalog,
				table,
				name,
				type,
				size
			));
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readFgnKeyMetaData(DatabaseMetaData metaData, String internalCatalogName, String catalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getExportedKeys(internalCatalogName, null, table);

		while(resultSet.next()) {

			String name = resultSet.getString("FK_NAME");
			String fktable = resultSet.getString("FKTABLE_NAME");
			String fkcolumn = resultSet.getString("FKCOLUMN_NAME");
			String pktable = resultSet.getString("PKTABLE_NAME");
			String pkcolumn = resultSet.getString("PKCOLUMN_NAME");

			m_frgnKeys.get(catalog).get(fktable).put(fkcolumn, new FrgnKey(
				catalog,
				name,
				fktable,
				fkcolumn,
				pktable,
				pkcolumn
			));
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readIndexMetaData(DatabaseMetaData metaData, String internalCatalogName, String catalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getIndexInfo(internalCatalogName, null, table, false, false);

		while(resultSet.next()) {

			String name = resultSet.getString("INDEX_NAME");
			String column = resultSet.getString("COLUMN_NAME");
			int position = resultSet.getInt("ORDINAL_POSITION");
			boolean unique = !resultSet.getBoolean("NON_UNIQUE");

			String type = unique ? name.equals("PRIMARY") ? "PRIMARY"
			                                              : "UNIQUE"
			                                              : "INDEX"
			;

			m_indices.get(catalog).get(table).add(new Index(
				catalog,
				table,
				name,
				type,
				column,
				position
			));
		}
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
						"<field name=\"internalName\">" + entry3.getValue().m_internalName + "</field>"
						+
						"<field name=\"catalog\">" + entry3.getValue().m_catalog + "</field>"
						+
						"<field name=\"table\">" + entry3.getValue().m_table + "</field>"
						+
						"<field name=\"name\">" + entry3.getValue().m_name + "</field>"
						+
						"<field name=\"type\">" + entry3.getValue().m_type + "</field>"
						+
						"<field name=\"size\">" + entry3.getValue().m_size + "</field>"
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
						"<field name=\"catalog\">" + entry3.getValue().m_catalog + "</field>"
						+
						"<field name=\"fkTable\">" + entry3.getValue().m_fkTable + "</field>"
						+
						"<field name=\"fkColumn\">" + entry3.getValue().m_fkColumn + "</field>"
						+
						"<field name=\"pkTable\">" + entry3.getValue().m_pkTable + "</field>"
						+
						"<field name=\"pkColumn\">" + entry3.getValue().m_pkColumn + "</field>"
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
						"<field name=\"catalog\">" + entry3.m_catalog + "</field>"
						+
						"<field name=\"table\">" + entry3.m_table + "</field>"
						+
						"<field name=\"name\">" + entry3.m_name + "</field>"
						+
						"<field name=\"type\">" + entry3.m_type + "</field>"
						+
						"<field name=\"column\">" + entry3.m_column + "</field>"
						+
						"<field name=\"position\">" + entry3.m_position + "</field>"
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
