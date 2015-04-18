package net.hep.ami.jdbc.introspection;

import java.util.*;
import java.util.Map.*;

import net.hep.ami.*;

public class AutoJoinSingleton {
	/*---------------------------------------------------------------------*/

	private static final int m_maxLevel = ConfigSingleton.getProperty("auto_join_max_level", 10);

	/*---------------------------------------------------------------------*/

	public static class SQLJoins {

		public String from;
		public String where;

		public SQLJoins(String _from, String _where) {
			from = _from;
			where = _where;
		}
	}

	/*---------------------------------------------------------------------*/

	public static class SQLFieldValue {

		public String field;
		public String value;

		public SQLFieldValue(String _field, String _value) {
			field = _field;
			value = _value;
		}
	}

	/*---------------------------------------------------------------------*/

	public static SQLJoins joinsToSQL(Map<String, List<String>> joins) {

		/**/ String  joinKey;
		List<String> joinValue;

		StringBuilder part1 = new StringBuilder();
		StringBuilder part2 = new StringBuilder();

		for(Entry<String, List<String>> entry: joins.entrySet()) {

			joinKey = entry.getKey();
			joinValue = entry.getValue();

			if(joinKey.equals("@") == false) {

				part1.append(joinKey + " ON (" + String.join(" AND ", joinValue) + ")");
			} else {
				part2.append(/***********/ "(" + String.join(" AND ", joinValue) + ")");
			}
		}

		return new SQLJoins(part1.toString(), part2.toString());
	}

	/*---------------------------------------------------------------------*/

	private static List<String> _getList(Map<String, List<String>> map, String key) {

		List<String> result = map.get(key);

		if(result == null) {
			result = new ArrayList<String>();

			map.put(key, result);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static boolean _resolveWithInnerJoins(Map<String, List<String>> joins, String catalog, String table, String column, String value, int level, int maxLevel) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CHECK LEVEL                                                     */
		/*-----------------------------------------------------------------*/

		if(level > maxLevel) {
			return false;
		}

		/*-----------------------------------------------------------------*/
		/* GET COLUMNS AND FOREIGN KEYS                                    */
		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> columns = SchemaSingleton.getColumns(catalog, table);
		Map<String, SchemaSingleton.FrgnKey> fgnKeys = SchemaSingleton.getFgnKeys(catalog, table);

		/*-----------------------------------------------------------------*/
		/* RESOLVE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column _column = columns.get(column);

		if(_column == null) {

			String joinKey;
			String joinValue;

			Map<String, List<String>> temp;

			for(SchemaSingleton.FrgnKey frgnKey: fgnKeys.values()) {

				temp = new HashMap<String, List<String>>();

				if(_resolveWithInnerJoins(temp, catalog, frgnKey.pkTable, column, value, level + 1, maxLevel)) {
					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					joinKey = " INNER JOIN `" + frgnKey.pkTable + "`";

					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					joinValue = "`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`"
					            + "=" +
					            "`" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "`"
					;

					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					_getList(joins, joinKey).add(
						joinValue
					);

					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					for(Entry<String, List<String>> entry2: temp.entrySet()) {

						_getList(joins, entry2.getKey()).addAll(
							entry2.getValue()
						);
					}

					/*-----------------------------------------------------*/

					return true;
				}
			}

		} else {
			/*-------------------------------------------------------------*/
			/* TRIVIAL CASE                                                */
			/*-------------------------------------------------------------*/

			_getList(joins, "@").add(
				"`" + _column.table + "`.`" + column + "`='" + value.replace("'", "''") + "'"
			);

			/*-------------------------------------------------------------*/

			return true;
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	private static boolean _resolveWithNestedSelect(Map<String, List<String>> joins, String catalog, String table, String column, String value, int level, int maxLevel) throws Exception {

		/*-----------------------------------------------------------------*/
		/* CHECK LEVEL                                                     */
		/*-----------------------------------------------------------------*/

		if(level > maxLevel) {
			return false;
		}

		/*-----------------------------------------------------------------*/
		/* GET COLUMNS AND FOREIGN KEYS                                    */
		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> columns = SchemaSingleton.getColumns(catalog, table);
		Map<String, SchemaSingleton.FrgnKey> fgnKeys = SchemaSingleton.getFgnKeys(catalog, table);

		/*-----------------------------------------------------------------*/
		/* RESOLVE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column _column = columns.get(column);

		if(_column == null) {

			SQLJoins sqlParts;

			Map<String, List<String>> temp;

			for(SchemaSingleton.FrgnKey frgnKey: fgnKeys.values()) {

				temp = new HashMap<String, List<String>>();

				if(_resolveWithInnerJoins(temp, catalog, frgnKey.pkTable, column, value, level + 1, maxLevel)) {
					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					sqlParts = joinsToSQL(temp);

					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					_getList(joins, "@").add(
						"`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`"
						+ "="
						+ "("
						+ "SELECT `" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "` FROM `" + frgnKey.pkTable + "`" + sqlParts.from + " WHERE " + sqlParts.where
						+ ")"
					);

					/*-----------------------------------------------------*/

					return true;
				}
			}

		} else {
			/*-------------------------------------------------------------*/
			/* TRIVIAL CASE                                                */
			/*-------------------------------------------------------------*/

			_getList(joins, "@").add(
				"`" + _column.table + "`.`" + column + "`='" + value.replace("'", "''") + "'"
			);

			/*-------------------------------------------------------------*/

			return true;
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static void resolveWithInnerJoins(Map<String, List<String>> joins, String catalog, String table, String column, String value, int maxLevel) throws Exception {

		if(column.isEmpty() == false) {

			if(_resolveWithInnerJoins(joins, catalog, table, column, value, 0, maxLevel) == false) {

				throw new Exception("could not resolve foreign key");
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static void resolveWithNestedSelect(Map<String, List<String>> joins, String catalog, String table, String column, String value, int maxLevel) throws Exception {

		if(column.isEmpty() == false) {

			if(_resolveWithNestedSelect(joins, catalog, table, column, value, 0, maxLevel) == false) {

				throw new Exception("could not resolve foreign key");
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static SQLFieldValue resolveFieldValue(String catalog, String table, String column, String value, int maxLevel) throws Exception {
		/*-----------------------------------------------------------------*/
		/* RESOLVE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		Map<String, List<String>> joins = new HashMap<String, List<String>>();

		resolveWithNestedSelect(joins, catalog, table, column, value, maxLevel);

		/*-----------------------------------------------------------------*/
		/* EXTRACT FIELD AND VALUE                                         */
		/*-----------------------------------------------------------------*/

 		String[] colVal = joins.get("@").get(0).split("=", 2);

		return new SQLFieldValue(colVal[0], colVal[1]);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void resolveWithInnerJoins(Map<String, List<String>> joins, String catalog, String table, String column, String value) throws Exception {

		resolveWithInnerJoins(joins, catalog, table, column, value, m_maxLevel);
	}

	/*---------------------------------------------------------------------*/

	public static void resolveWithNestedSelect(Map<String, List<String>> joins, String catalog, String table, String column, String value) throws Exception {

		resolveWithNestedSelect(joins, catalog, table, column, value, m_maxLevel);
	}

	/*---------------------------------------------------------------------*/

	public static SQLFieldValue resolveFieldValue(String catalog, String table, String column, String value) throws Exception {

		return resolveFieldValue(catalog, table, column, value, m_maxLevel);
	}

	/*---------------------------------------------------------------------*/
}
