package net.hep.ami.jdbc.introspection;

import java.util.*;
import java.util.Map.*;

public class AutoJoinSingleton {
	/*---------------------------------------------------------------------*/

	private static final String m_nojoin = "@";

	/*---------------------------------------------------------------------*/

	public static class SQLParts {

		public String from;
		public String where;

		public SQLParts(String _from, String _where) {
			from = _from;
			where = _where;
		}
	}

	/*---------------------------------------------------------------------*/

	public static SQLParts joinsToSQL(Map<String, List<String>> joins) {

		/**/ String  joinKey;
		List<String> joinValue;

		StringBuilder part1 = new StringBuilder();
		StringBuilder part2 = new StringBuilder();

		for(Entry<String, List<String>> entry: joins.entrySet()) {

			joinKey = entry.getKey();
			joinValue = entry.getValue();

			if(joinKey.equals(m_nojoin) == false) {

				part1.append(joinKey + " ON (" + String.join(" AND ", joinValue) + ")");
			} else {
				part2.append(/***********/ "(" + String.join(" AND ", joinValue) + ")");
			}
		}

		return new SQLParts(part1.toString(), part2.toString());
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
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		if(level > maxLevel) {

			return false;
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> columns = SchemaSingleton.getColumns(catalog, table);
		Map<String, SchemaSingleton.FrgnKey> fgnKeys = SchemaSingleton.getFgnKeys(catalog, table);

		SchemaSingleton.Column _column = columns.get(column);
		SchemaSingleton.FrgnKey _fgnKeys = fgnKeys.get(column);

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		if(_column == null
		   ||
		   _fgnKeys != null
		 ) {
			String joinKey;
			String joinValue;

			SchemaSingleton.FrgnKey frgnKey;

			Map<String, List<String>> tempJoins;

			for(Entry<String, SchemaSingleton.FrgnKey> entry: fgnKeys.entrySet()) {

				frgnKey = entry.getValue();

				tempJoins = new HashMap<String, List<String>>();

				if(_resolveWithInnerJoins(tempJoins, catalog, frgnKey.m_pkTable, column, value, level + 1, maxLevel)) {
					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					joinKey = " INNER JOIN `" + frgnKey.m_pkTable + "`";

					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					joinValue = "`" + frgnKey.m_fkTable + "`.`" + frgnKey.m_fkColumn + "`"
					            + "=" +
					            "`" + frgnKey.m_pkTable + "`.`" + frgnKey.m_pkColumn + "`"
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

					for(Entry<String, List<String>> entry2: tempJoins.entrySet()) {

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
			/*                                                             */
			/*-------------------------------------------------------------*/

			_getList(joins, m_nojoin).add(
				"`" + _column.getTable() + "`.`" + column + "`='" + value + "'"
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
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		if(level > maxLevel) {

			return false;
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> columns = SchemaSingleton.getColumns(catalog, table);
		Map<String, SchemaSingleton.FrgnKey> fgnKeys = SchemaSingleton.getFgnKeys(catalog, table);

		SchemaSingleton.Column _column = columns.get(column);
		SchemaSingleton.FrgnKey _fgnKeys = fgnKeys.get(column);

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		if(_column == null
		   ||
		   _fgnKeys != null
		 ) {
			SQLParts sqlParts;

			SchemaSingleton.FrgnKey frgnKey;

			Map<String, List<String>> tempJoins;

			for(Entry<String, SchemaSingleton.FrgnKey> entry: fgnKeys.entrySet()) {

				frgnKey = entry.getValue();

				tempJoins = new HashMap<String, List<String>>();

				if(_resolveWithInnerJoins(tempJoins, catalog, frgnKey.m_pkTable, column, value, level + 1, maxLevel)) {
					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					sqlParts = joinsToSQL(tempJoins);

					/*-----------------------------------------------------*/
					/*                                                     */
					/*-----------------------------------------------------*/

					_getList(joins, m_nojoin).add(
							"`" + frgnKey.m_fkTable + "`.`" + frgnKey.m_fkColumn + "`"
							+ "="
							+ "("
							+ "SELECT `" + frgnKey.m_pkTable + "`.`" + frgnKey.m_pkColumn + "` FROM `" + frgnKey.m_pkTable + "`" + sqlParts.from + " WHERE " + sqlParts.where
							+ ")"
					);

					/*-----------------------------------------------------*/

					return true;
				}
			}

		} else {
			/*-------------------------------------------------------------*/
			/*                                                             */
			/*-------------------------------------------------------------*/

			_getList(joins, m_nojoin).add(
				"`" + _column.getTable() + "`.`" + column + "`='" + value + "'"
			);

			/*-------------------------------------------------------------*/

			return true;
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static void resolveWithInnerJoins(Map<String, List<String>> joins, String catalog, String table, String column, String value) throws Exception {

		if(column.isEmpty() == false) {

			if(_resolveWithInnerJoins(joins, catalog, table, column, value, 0, 10) == false) {

				throw new Exception("could not resolve foreign key");
			}
		}
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

	public static void resolveWithNestedSelect(Map<String, List<String>> joins, String catalog, String table, String column, String value) throws Exception {

		if(column.isEmpty() == false) {

			if(_resolveWithNestedSelect(joins, catalog, table, column, value, 0, 10) == false) {

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
}
