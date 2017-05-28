package net.hep.ami.jdbc.reflexion;

import java.util.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class SQLJoins
	{
		/*-----------------------------------------------------------------*/

		public final String from;
		public final String where;

		/*-----------------------------------------------------------------*/

		public SQLJoins(String _from, String _where)
		{
			from = _from;
			where = _where;
		}

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static final class AMIJoins extends HashMap<String, List<String>>
	{
		/*-----------------------------------------------------------------*/

		private static final long serialVersionUID = 5606411046465630272L;

		/*-----------------------------------------------------------------*/

		public static final String WHERE = "@";

		/*-----------------------------------------------------------------*/

		public List<String> getOrAdd(String joinKey)
		{
			List<String> result = get(joinKey);

			if(result == null)
			{
				result = new ArrayList<>();

				put(joinKey, result);
			}

			return result;
		}

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static final int WITH_INNER_JOINS = 0;
	private static final int WITH_NESTED_SELECT = 1;

	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	private static void _mergeInnerJoins(AMIJoins joins, AMIJoins temp, SchemaSingleton.FrgnKey frgnKey)
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
			joins.getOrAdd(entry2.getKey()).addAll(
				entry2.getValue()
			);
		}

		joins.getOrAdd(joinKey).add(
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

		joins.getOrAdd(joinKey).add(
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

		SchemaSingleton.Column column = defaultCatalog.equals(givenCatalog) && defaultTable.equals(givenTable) ? SchemaSingleton.getColumns(givenCatalog, givenTable).get(givenColumn)
		                                                                                                       : null
		;

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
								return true;

							case WITH_NESTED_SELECT:
								_mergeNestedSelect(joins, temp, frgnKey);
								return true;
						}

						return false;
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
								return true;

							case WITH_NESTED_SELECT:
								_mergeNestedSelect(joins, temp, frgnKey);
								return true;
						}

						return false;
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

				joins.getOrAdd(AMIJoins.WHERE).add(
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

	private static String unquote(String s)
	{
		final int l = s.length() - 1;

		if(s.charAt(0) == '`'
		   &&
		   s.charAt(l) == '`'
		 ) {
			return s.substring(1, l).replace("``", "`").trim();
		}

		return s;
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
			givenColumn = unquote(parts[0]);
		}
		else if(nb == 2)
		{
			givenCatalog = defaultCatalog;
			givenTable = unquote(parts[0]);
			givenColumn = unquote(parts[1]);
		}
		else if(nb == 3)
		{
			givenCatalog = unquote(parts[0]);
			givenTable = unquote(parts[1]);
			givenColumn = unquote(parts[2]);
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
