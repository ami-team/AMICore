package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;
import java.util.Map.*;

import net.hep.ami.*;

import org.apache.tomcat.jdbc.pool.*;

public class SchemaSingleton {
	/*---------------------------------------------------------------------*/

	public static class Column {
		/*-----------------------------------------------------------------*/

		private String m_catalog;
		private String m_table;
		private String m_name;
		private String m_type;
		private int m_size;

		/*-----------------------------------------------------------------*/

		public Column(String catalog, String table, String name, String type, int size) {

			m_catalog = catalog;
			m_table = table;
			m_name = name;
			m_type = type;
			m_size = size;
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

	public static class ForeignKey {
		/*-----------------------------------------------------------------*/

		private String m_catalog;
		private String m_fkTable;
		private String m_fkColumn;
		private String m_pkTable;
		private String m_pkColumn;

		/*-----------------------------------------------------------------*/

		public ForeignKey(String catalog, String fkTable, String fkColumn, String pkTable, String pkColumn) {

			m_catalog = catalog;
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

		private String m_catalog;
		private String m_table;
		private String m_name;
		private String m_type;
		private String m_column;
		private int m_position;

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

	private static Map<String, Map<String, Map<String, Column>>> m_columns = new HashMap<String, Map<String, Map<String, Column>>>();
	private static Map<String, Map<String, List<ForeignKey>>> m_foreignKeys = new HashMap<String, Map<String, List<ForeignKey>>>();
	private static Map<String, Map<String, List<Index>>> m_indices = new HashMap<String, Map<String, List<Index>>>();

	/*---------------------------------------------------------------------*/

	private static long m_executionTime = 0;

	/*---------------------------------------------------------------------*/

	public static void readSchema(DataSource dataSource) {

		Connection connection = null;

		try {
			/*-------------------------------------------------------------*/
			/* GET CONNECTION                                              */
			/*-------------------------------------------------------------*/

			connection = dataSource.getConnection();

			/*-------------------------------------------------------------*/
			/* READ DB METADATA                                            */
			/*-------------------------------------------------------------*/

			long t1 = System.currentTimeMillis();
			readDBMetaData(connection.getMetaData());
			long t2 = System.currentTimeMillis();

			/*-------------------------------------------------------------*/
			/* UPDATE EXECUTION TIME                                       */
			/*-------------------------------------------------------------*/

			m_executionTime += t2 - t1;

			/*-------------------------------------------------------------*/

		} catch(SQLException e) {
			LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());

		} finally {

			if(connection != null) {

				try {
					connection.close();

				} catch(SQLException e) {
					LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());
				}
			}
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readDBMetaData(DatabaseMetaData metaData) throws SQLException {

		ResultSet resultSet = metaData.getCatalogs();

		while(resultSet.next()) {

			String name = resultSet.getString("TABLE_CAT");

			if(name.equals(/****/"mysql"/*****/) == false
			   &&
			   name.equals("information_schema") == false
			   &&
			   name.equals("performance_schema") == false
			 ) {
				  m_columns.put(name, new HashMap<String, Map<String, Column>>());
				  m_foreignKeys.put(name, new HashMap<String, List<ForeignKey>>());
				  m_indices.put(name, new HashMap<String, List<Index>>());

				readTableMetaData(metaData, name);
			}
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readTableMetaData(DatabaseMetaData metaData, String catalog) throws SQLException {

		ResultSet resultSet = metaData.getTables(catalog, null, "%", null);

		while(resultSet.next()) {

			String name = resultSet.getString("TABLE_NAME");

			m_columns.get(catalog).put(name, new LinkedHashMap<String, Column>());
			m_foreignKeys.get(catalog).put(name, new ArrayList<ForeignKey>());
			m_indices.get(catalog).put(name, new ArrayList<Index>());

			readColumnMetaData(metaData, catalog, name);
			readForeignKeyMetaData(metaData, catalog, name);
			readIndexMetaData(metaData, catalog, name);
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readColumnMetaData(DatabaseMetaData metaData, String catalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getColumns(catalog, null, table, null);

		while(resultSet.next()) {

			String name = resultSet.getString("COLUMN_NAME");
			String type = resultSet.getString("TYPE_NAME");
			int size = resultSet.getInt("COLUMN_SIZE");

			m_columns.get(catalog).get(table).put(name, new Column(
				catalog,
				table,
				name,
				type,
				size
			));
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readForeignKeyMetaData(DatabaseMetaData metaData, String catalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getExportedKeys(catalog, null, table);

		while(resultSet.next()) {

			String fktable = resultSet.getString("FKTABLE_NAME");
			String fkcolumn = resultSet.getString("FKCOLUMN_NAME");
			String pktable = resultSet.getString("PKTABLE_NAME");
			String pkcolumn = resultSet.getString("PKCOLUMN_NAME");

			m_foreignKeys.get(catalog).get(table).add(new ForeignKey(
				catalog,
				fktable,
				fkcolumn,
				pktable,
				pkcolumn
			));
		}
	}

	/*---------------------------------------------------------------------*/

	private static void readIndexMetaData(DatabaseMetaData metaData, String catalog, String table) throws SQLException {

		ResultSet resultSet = metaData.getIndexInfo(catalog, null, table, false, false);

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

	public Set<String> getCatalogList() {

		return m_columns.keySet();
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getTableList(String catalog) throws Exception {

		if(m_columns.containsKey(catalog)) {

			return m_columns.get(catalog).keySet();
		}

		throw new Exception("catalog not found `" + catalog + "`");
	}

	/*---------------------------------------------------------------------*/

	public static Map<String, Column> getColumns(String catalog, String table) throws Exception {

		if(m_columns.containsKey(catalog)) {

			Map<String, Map<String, Column>> catalogMap = m_columns.get(catalog);

			if(catalogMap.containsKey(table)) {

				return catalogMap.get(table);
			}
		}

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");
	}

	/*---------------------------------------------------------------------*/

	public static List<ForeignKey> getForeignKeys(String catalog, String table) throws Exception {

		if(m_foreignKeys.containsKey(catalog)) {

			Map<String, List<ForeignKey>> catalogMap = m_foreignKeys.get(catalog);

			if(catalogMap.containsKey(table)) {

				return catalogMap.get(table);
			}
		}

		throw new Exception("table not found `" + catalog + "`.`" + table + "`");
	}

	/*---------------------------------------------------------------------*/

	public static List<Index> getIndices(String catalog, String table) throws Exception {

		if(m_indices.containsKey(catalog)) {

			Map<String, List<Index>> catalogMap = m_indices.get(catalog);

			if(catalogMap.containsKey(table)) {

				return catalogMap.get(table);
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

		for(Entry<String, Map<String, List<ForeignKey>>> entry1: m_foreignKeys.entrySet()) {

			for(Entry<String, List<ForeignKey>> entry2: entry1.getValue().entrySet()) {

				for(ForeignKey entry3: entry2.getValue()) {

					result.append(
						"<row>"
						+
						"<field name=\"catalog\">" + entry3.m_catalog + "</field>"
						+
						"<field name=\"fkTable\">" + entry3.m_fkTable + "</field>"
						+
						"<field name=\"fkColumn\">" + entry3.m_fkColumn + "</field>"
						+
						"<field name=\"pkTable\">" + entry3.m_pkTable + "</field>"
						+
						"<field name=\"pkColumn\">" + entry3.m_pkColumn + "</field>"
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

		result.append("<info>" + String.format(Locale.US, "%.3f", 0.001f * m_executionTime) + " s</info>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
