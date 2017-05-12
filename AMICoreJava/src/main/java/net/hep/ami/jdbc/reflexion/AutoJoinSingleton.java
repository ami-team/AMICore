package net.hep.ami.jdbc.reflexion;

import java.util.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class SQLJoins
	{
		public final String from;
		public final String where;

		public SQLJoins(String _from, String _where)
		{
			from = _from;
			where = _where;
		}

		public String toString()
		{
			StringBuilder stringBuilder = new StringBuilder();

			if(from.isEmpty() == false)
			{
				stringBuilder.append(" FROM ").append(from);
			}

			if(where.isEmpty() == false)
			{
				stringBuilder.append(" WHERE ").append(where);
			}

			return stringBuilder.toString();
		}
	}

	/*---------------------------------------------------------------------*/

	public static final class SQLFieldValue
	{
		public final String field;
		public final String value;

		public SQLFieldValue(String _field, String _value)
		{
			field = _field;
			value = _value;
		}

		public String toString()
		{
			return "`" + field + "`.`" + value + "`";
		}
	}

	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	public static SQLJoins joinsToSQL(Map<String, List<String>> joins)
	{
		String joinKey;

		StringBuilder part1 = new StringBuilder();
		StringBuilder part2 = new StringBuilder();

		for(Map.Entry<String, List<String>> entry: joins.entrySet())
		{
			joinKey = entry.getKey();

			if("@".equals(joinKey) == false)
			{
				part1.append(joinKey + " ON (" + String.join(" AND ", entry.getValue()) + ")");
			}
			else
			{
				part2.append("(" + String.join(" AND ", entry.getValue()) + ")");
			}
		}

		return new SQLJoins(part1.toString(), part2.toString());
	}

	/*---------------------------------------------------------------------*/

	private static List<String> _getList(Map<String, List<String>> map, String key)
	{
		List<String> result = map.get(key);

		if(result == null)
		{
			result = new ArrayList<>();

			map.put(key, result);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static boolean _resolveWithInnerJoins(Map<String, List<String>> joins, Set<String> done, String defaultCatalog, String defaultTable, @Nullable String givenCatalog, @Nullable String givenTable, String givenColumn, @Nullable String givenValue) throws Exception
	{
		if(givenCatalog == null)
		{
			givenCatalog = defaultCatalog;
		}

		if(givenTable == null)
		{
			givenTable = defaultTable;
		}

		/*-----------------------------------------------------------------*/
		/* CHECK CYCLES                                                    */
		/*-----------------------------------------------------------------*/

		String key = (defaultTable + '.' + givenColumn).toLowerCase();

		if(done.contains(key))
		{
			return false;
		}

		done.add(key);

		/*-----------------------------------------------------------------*/
		/* GET COLUMNS AND FOREIGN KEYS                                    */
		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> columns = SchemaSingleton.getColumns(givenCatalog, givenTable);
		Map<String, SchemaSingleton.FrgnKey> fgnKeys = SchemaSingleton.getFrgnKeys(givenCatalog, givenTable);

		/*-----------------------------------------------------------------*/
		/* RESOLVE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column _column = columns.get(givenColumn);

		if(_column == null)
		{
			String joinKey;
			String joinValue;

			Map<String, List<String>> temp;

			for(SchemaSingleton.FrgnKey frgnKey: fgnKeys.values())
			{
				temp = new HashMap<>();

				if(_resolveWithInnerJoins(temp, done, defaultCatalog, frgnKey.pkTable, givenCatalog, givenColumn, givenTable, givenValue))
				{
					/*-----------------------------------------------------*/
					/* BUILD SQL JOIN                                      */
					/*-----------------------------------------------------*/

					joinKey = " INNER JOIN `" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`";

					/*-----------------------------------------------------*/

					joinValue = "`" + frgnKey.fkInternalCatalog + "`.`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`"
					            + "=" +
					            "`" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "`"
					;

					/*-----------------------------------------------------*/
					/* ADD SQL JOIN                                        */
					/*-----------------------------------------------------*/

					_getList(joins, joinKey).add(
						joinValue
					);

					/*-----------------------------------------------------*/
					/* MERGE                                               */
					/*-----------------------------------------------------*/

					for(Map.Entry<String, List<String>> entry2: temp.entrySet())
					{
						_getList(joins, entry2.getKey()).addAll(
							entry2.getValue()
						);
					}

					/*-----------------------------------------------------*/

					return true;
				}
			}
		}
		else
		{
			if(givenValue != null)
			{
				/*---------------------------------------------------------*/
				/* CONDITION ON VALUE                                      */
				/*---------------------------------------------------------*/

				_getList(joins, "@").add(
					"`" + _column.internalCatalog + "`.`" + _column.table + "`.`" + _column.name + "`='" + givenValue.replace("'", "''") + "'"
				);

				/*---------------------------------------------------------*/
			}

			return true;
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	private static boolean _resolveWithNestedSelect(Map<String, List<String>> joins, Set<String> done, String defaultCatalog, String defaultTable, @Nullable String givenCatalog, @Nullable String givenTable, String givenColumn, @Nullable String givenValue) throws Exception
	{
		if(givenCatalog == null)
		{
			givenCatalog = defaultCatalog;
		}

		if(givenTable == null)
		{
			givenTable = defaultTable;
		}

		/*-----------------------------------------------------------------*/
		/* CHECK CYCLES                                                    */
		/*-----------------------------------------------------------------*/

		String key = (defaultTable + '.' + givenColumn).toLowerCase();

		if(done.contains(key))
		{
			return false;
		}

		done.add(key);

		/*-----------------------------------------------------------------*/
		/* GET COLUMNS AND FOREIGN KEYS                                    */
		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> columns = SchemaSingleton.getColumns(defaultCatalog, givenTable);
		Map<String, SchemaSingleton.FrgnKey> fgnKeys = SchemaSingleton.getFrgnKeys(defaultCatalog, givenTable);

		/*-----------------------------------------------------------------*/
		/* RESOLVE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column _column = columns.get(givenColumn);

		if(_column == null)
		{
			SQLJoins sqlParts;

			Map<String, List<String>> temp;

			for(SchemaSingleton.FrgnKey frgnKey: fgnKeys.values())
			{
				temp = new HashMap<>();

				if(_resolveWithInnerJoins(temp, done, defaultCatalog, frgnKey.pkTable, givenCatalog, givenTable, givenColumn, givenValue))
				{
					/*-----------------------------------------------------*/
					/* GET SQL JOINS                                       */
					/*-----------------------------------------------------*/

					sqlParts = joinsToSQL(temp);

					/*-----------------------------------------------------*/
					/* ADD NESTED SELECT                                   */
					/*-----------------------------------------------------*/

					_getList(joins, "@").add(
						"`" + frgnKey.fkInternalCatalog + "`.`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`"
						+ "="
						+ "("
						+ "SELECT `" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "` FROM `" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`" + sqlParts.from + " WHERE " + sqlParts.where
						+ ")"
					);

					/*-----------------------------------------------------*/

					return true;
				}
			}
		}
		else
		{
			if(givenValue != null)
			{
				/*---------------------------------------------------------*/
				/* CONDITION ON VALUE                                      */
				/*---------------------------------------------------------*/

				_getList(joins, "@").add(
					"`" + _column.internalCatalog + "`.`" + _column.table + "`.`" + _column.name + "`='" + givenValue.replace("'", "''") + "'"
				);

				/*---------------------------------------------------------*/

				return true;
			}
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static void resolveWithInnerJoins(Map<String, List<String>> joins, String defaultCatalog, String defaultTable, String qid, @Nullable String givenValue) throws Exception
	{
		if(qid.isEmpty() == false)
		{
			Set<String> done = new HashSet<>();

			String[] parts = qid.split("\\.");

			final int nb = parts.length;

			/**/ if(nb == 1)
			{
				if(_resolveWithInnerJoins(joins, done, defaultCatalog, defaultTable, (((((null))))), (((((null))))), parts[0].trim(), givenValue) == false)
				{
					throw new Exception("could not resolve foreign key `" + qid + "`");
				}
			}
			else if(nb == 2)
			{
				if(_resolveWithInnerJoins(joins, done, defaultCatalog, defaultTable, (((((null))))), parts[0].trim(), parts[1].trim(), givenValue) == false)
				{
					throw new Exception("could not resolve foreign key `" + qid + "`");
				}
			}
			else if(nb == 3)
			{
				if(_resolveWithInnerJoins(joins, done, defaultCatalog, defaultTable, parts[0].trim(), parts[1].trim(), parts[2].trim(), givenValue) == false)
				{
					throw new Exception("could not resolve foreign key `" + qid + "`");
				}
			}
			else
			{
				throw new Exception("could not parse column name `" + qid + "`");
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static void resolveWithNestedSelect(Map<String, List<String>> joins, String defaultCatalog, String defaultTable, String qid, @Nullable String givenValue) throws Exception
	{
		if(qid.isEmpty() == false)
		{
			Set<String> done = new HashSet<>();

			String[] parts = qid.split("\\.");

			final int nb = parts.length;

			/**/ if(nb == 1)
			{
				if(_resolveWithNestedSelect(joins, done, defaultCatalog, defaultTable, (((((null))))), (((((null))))), parts[0].trim(), givenValue) == false)
				{
					throw new Exception("could not resolve foreign key `" + qid + "`");
				}
			}
			else if(nb == 2)
			{
				if(_resolveWithNestedSelect(joins, done, defaultCatalog, defaultTable, (((((null))))), parts[0].trim(), parts[1].trim(), givenValue) == false)
				{
					throw new Exception("could not resolve foreign key `" + qid + "`");
				}
			}
			else if(nb == 3)
			{
				if(_resolveWithNestedSelect(joins, done, defaultCatalog, defaultTable, parts[0].trim(), parts[1].trim(), parts[2].trim(), givenValue) == false)
				{
					throw new Exception("could not resolve foreign key `" + qid + "`");
				}
			}
			else
			{
				throw new Exception("could not parse column name `" + qid + "`");
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static SQLFieldValue resolveFieldValue(String defaultCatalog, String defaultTable, String qid, @Nullable String givenValue) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* RESOLVE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		Map<String, List<String>> joins = new HashMap<>();

		resolveWithNestedSelect(joins, defaultCatalog, defaultTable, qid, givenValue);

		/*-----------------------------------------------------------------*/
		/* EXTRACT FIELD AND VALUE                                         */
		/*-----------------------------------------------------------------*/

		String[] colVal = joins.get("@").get(0).split("=", 2);

		return new SQLFieldValue(colVal[0], colVal[1]);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
