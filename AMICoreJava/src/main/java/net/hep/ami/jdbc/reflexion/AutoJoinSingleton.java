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

	public static final class AMIJoins extends HashMap<String, List<String>>
	{
		private static final long serialVersionUID = 5606411046465630272L;

		public static final String WHERE = "@";

		public SQLJoins toSQL()
		{
			String joinKey;

			StringBuilder part1 = new StringBuilder();
			StringBuilder part2 = new StringBuilder();

			for(Map.Entry<String, List<String>> entry: entrySet())
			{
				joinKey = entry.getKey();

				if(WHERE.equals(joinKey) == false)
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
	}

	/*---------------------------------------------------------------------*/

	private static final int WITH_INNER_JOINS = 0;
	private static final int WITH_NESTED_SELECT = 1;

	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

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

	private static void _mergeInnerJoins(AMIJoins joins, Map<String, List<String>> temp, SchemaSingleton.FrgnKey frgnKey)
	{
		/*-----------------------------------------------------------------*/
		/* BUILD SQL JOIN                                                  */
		/*-----------------------------------------------------------------*/

		String joinKey = "INNER JOIN `" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`";

		String joinValue = "`" + frgnKey.fkInternalCatalog + "`.`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`"
		                 + "=" +
		                   "`" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "`"
		;

		/*-----------------------------------------------------------------*/
		/* MERGE                                                           */
		/*-----------------------------------------------------------------*/

		for(Map.Entry<String, List<String>> entry2: temp.entrySet())
		{
			_getList(joins, entry2.getKey()).addAll(
				entry2.getValue()
			);
		}

		_getList(joins, joinKey).add(
			joinValue
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void _mergeNestedSelect(AMIJoins joins, AMIJoins temp, SchemaSingleton.FrgnKey frgnKey)
	{
		/*-----------------------------------------------------------------*/
		/* BUILD SQL JOIN                                                  */
		/*-----------------------------------------------------------------*/

		SQLJoins sqlJoins = temp.toSQL();

		/*-----------------------------------------------------------------*/

		String joinKey = AMIJoins.WHERE;

		String joinValue = "`" + frgnKey.fkInternalCatalog + "`.`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`"
		                 + "="
		                 + "("
		                 + "SELECT `" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "` FROM `" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`" + sqlJoins.from + " WHERE " + sqlJoins.where
		                 + ")"
		;

		/*-----------------------------------------------------------------*/
		/* MERGE                                                           */
		/*-----------------------------------------------------------------*/

		_getList(joins, joinKey).add(
			joinValue
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static boolean _resolve(
		AMIJoins joins,
		Set<String> done,
		int method,
		String defaultCatalog,
		String defaultTable,
		String givenCatalog,
		String givenTable,
		String givenColumn,
		@Nullable
		String givenValue
	 ) throws Exception {
		/*-----------------------------------------------------------------*/
		/* BREAK CYCLES                                                    */
		/*-----------------------------------------------------------------*/

		String key = (defaultTable + '.' + givenColumn).toLowerCase();

		if(done.contains(key))
		{
			return false;
		}

		done.add(key);

		/*-----------------------------------------------------------------*/
		/* RESOLVE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column column = SchemaSingleton.getColumns(defaultCatalog, defaultTable).get(givenColumn);

		/*-----------------------------------------------------------------*/

		if(column == null)
		{
			AMIJoins temp;

			Collection<SchemaSingleton.FrgnKeys> lists;

			/*-------------------------------------------------------------*/
			/* FORWARD RESOLUTION                                          */
			/*-------------------------------------------------------------*/

			lists = SchemaSingleton.getForwardFKs(defaultCatalog, defaultTable).values();

			/*-------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: lists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					temp = new AMIJoins();

					if(_resolve(temp, done, WITH_NESTED_SELECT, frgnKey.pkExternalCatalog, frgnKey.pkTable, givenCatalog, givenTable, givenColumn, givenValue))
					{
						switch(method)
						{
							case WITH_INNER_JOINS:
								_mergeInnerJoins(joins, temp, frgnKey);
								break;

							case WITH_NESTED_SELECT:
								_mergeNestedSelect(joins, temp, frgnKey);
								break;
						}

						return true;
					}
				}
			}

			/*-------------------------------------------------------------*/
			/* BACKWARD RESOLUTION                                         */
			/*-------------------------------------------------------------*/

			lists = SchemaSingleton.getBackwardFKs(defaultCatalog, defaultTable).values();

			/*-------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: lists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					temp = new AMIJoins();

					if(_resolve(temp, done, WITH_NESTED_SELECT, frgnKey.fkExternalCatalog, frgnKey.fkTable, givenCatalog, givenTable, givenColumn, givenValue))
					{
						switch(method)
						{
							case WITH_INNER_JOINS:
								_mergeInnerJoins(joins, temp, frgnKey);
								break;

							case WITH_NESTED_SELECT:
								_mergeNestedSelect(joins, temp, frgnKey);
								break;
						}

						return true;
					}
				}
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			if(givenValue != null)
			{
				/*---------------------------------------------------------*/
				/* CONDITION ON VALUE                                      */
				/*---------------------------------------------------------*/

				_getList(joins, AMIJoins.WHERE).add(
					"`" + column.internalCatalog + "`.`" + column.table + "`.`" + column.name + "` = '" + givenValue.replace("'", "''") + "'"
				);

				/*---------------------------------------------------------*/
			}

			return true;
		}

		/*-----------------------------------------------------------------*/

		return false;
	}

	/*---------------------------------------------------------------------*/

	private static AMIJoins resolve(AMIJoins joins, int method, String defaultCatalog, String defaultTable, String qid, @Nullable String givenValue) throws Exception
	{
		/*-----------------------------------------------------------------*/

		String[] parts = qid.trim().split("\\.");

		final int nb = parts.length;

		String givenCatalog;
		String givenTable;
		String givenColumn;

		/*-----------------------------------------------------------------*/

		/**/ if(nb == 1)
		{
			givenCatalog = defaultCatalog;
			givenTable = defaultTable;
			givenColumn = parts[0].trim();
		}
		else if(nb == 2)
		{
			givenCatalog = defaultCatalog;
			givenTable = parts[0].trim();
			givenColumn = parts[1].trim();
		}
		else if(nb == 3)
		{
			givenCatalog = parts[0].trim();
			givenTable = parts[1].trim();
			givenColumn = parts[2].trim();
		}
		else
		{
			throw new Exception("could not parse column name `" + qid + "`");
		}

		/*-----------------------------------------------------------------*/

		if(_resolve(joins, new HashSet<>(), method, defaultCatalog, defaultTable, givenCatalog, givenTable, givenColumn, givenValue) == false)
		{
			throw new Exception("could not resolve column name `" + qid + "`");
		}

		/*-----------------------------------------------------------------*/

		return joins;
	}

	/*---------------------------------------------------------------------*/

	public static AMIJoins resolveWithInnerJoins(AMIJoins joins, String defaultCatalog, String defaultTable, String qid, @Nullable String givenValue) throws Exception
	{
		return resolve(joins, WITH_INNER_JOINS, defaultCatalog, defaultTable, qid, givenValue);
	}

	/*---------------------------------------------------------------------*/

	public static AMIJoins resolveWithNestedSelect(AMIJoins joins, String defaultCatalog, String defaultTable, String qid, @Nullable String givenValue) throws Exception
	{
		return resolve(joins, WITH_NESTED_SELECT, defaultCatalog, defaultTable, qid, givenValue);
	}

	/*---------------------------------------------------------------------*/
}
