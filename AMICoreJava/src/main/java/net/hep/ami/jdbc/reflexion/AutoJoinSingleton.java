package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.reflexion.Structure.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class SQLQId
	{
		/*-----------------------------------------------------------------*/

		public final String catalog;
		public final String table;
		public final String column;

		/*-----------------------------------------------------------------*/

		public SQLQId(String _catalog, String _table, String _column)
		{
			catalog = _catalog;
			table = _table;
			column = _column;
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			return "`" + catalog + "`.`" + table + "`.`" + column + "`";
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static final int TRIVIAL = 0;
	private static final int WITH_INNER_JOINS = 1;
	private static final int WITH_NESTED_SELECT = 2;

	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	private static void _mergeInnerJoins(Joins joins, Joins temp, SchemaSingleton.FrgnKey frgnKey)
	{
		/*-----------------------------------------------------------------*/
		/* BUILD SQL JOIN                                                  */
		/*-----------------------------------------------------------------*/

		String fkTable = "`" + frgnKey.fkInternalCatalog + "`.`" + frgnKey.fkTable + "`";
		String pkTable = "`" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`";

		String where = "`" + frgnKey.fkInternalCatalog + "`.`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`"
		               + " = " +
		               "`" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "`"
		;

		/*-----------------------------------------------------------------*/
		/* MERGE                                                           */
		/*-----------------------------------------------------------------*/

		for(Map.Entry<String, Islets> entry1: /*--*/temp/*--*/.entrySet())
		{
			for(Map.Entry<String, Select> entry2: entry1.getValue().entrySet())
			{
				joins.getJoin(entry1.getKey(), entry1.getValue().getPKTable())
				     .getIslet(entry2.getKey(), entry2.getValue().getFromPart())
				     .addAll(entry2.getValue())
				;
			}
		}

		/*-----------------------------------------------------------------*/

		Select select = joins.getJoin(fkTable, pkTable)
		     .getIslet(Structure.DUMMY, Structure.DUMMY)
		;

		select.addWhere(where);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void _mergeNestedSelect(Joins joins, Joins temp, SchemaSingleton.FrgnKey frgnKey)
	{
		/*-----------------------------------------------------------------*/
		/* BUILD SQL JOIN                                                  */
		/*-----------------------------------------------------------------*/

		String fkColumn = "`" + frgnKey.fkInternalCatalog + "`.`" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "`";
		String pkColumn = "`" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`.`" + frgnKey.pkColumn + "`";

		String from = "`" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`";

		/*-----------------------------------------------------------------*/
		/* MERGE                                                           */
		/*-----------------------------------------------------------------*/

		Select select = joins.getJoin(Structure.DUMMY, Structure.DUMMY)
		                     .getIslet(fkColumn, pkColumn)
		;

		select.addFrom(from);

		SQL sqlJoins = temp.toSQL();

		if(sqlJoins.from.isEmpty() == false) {
			select.addFrom(sqlJoins.from);
		}

		if(sqlJoins.where.isEmpty() == false) {
			select.addWhere(sqlJoins.where);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static SQLQId _resolveJoins(
		Joins joins,
		Set<String> done,
		int method,
		/*-----*/ String defaultCatalog,
		/*-----*/ String defaultTable,
		@Nullable String givenCatalog,
		@Nullable String givenTable,
		/*-----*/ String givenColumn,
		@Nullable String givenValue
	 ) throws Exception {

		if(method != TRIVIAL)
		{
			/*-------------------------------------------------------------*/
			/* BREAK CYCLES                                                */
			/*-------------------------------------------------------------*/

			String key = (defaultCatalog + '.' + defaultTable + '.' + givenColumn).toLowerCase();

			if(done.contains(key))
			{
				return null;
			}

			done.add(key);

			/*-------------------------------------------------------------*/
			/* RESOLVE JOINS                                               */
			/*-------------------------------------------------------------*/

			boolean checkNow = (givenCatalog == null || defaultCatalog.equals(givenCatalog))
				               &&
				               (givenTable == null || defaultTable.equals(givenTable))
			;

			SchemaSingleton.Column column = checkNow ? SchemaSingleton.getColumns(defaultCatalog, defaultTable).get(givenColumn) : null;

			/*-------------------------------------------------------------*/

			if(column == null)
			{
				SQLQId qId;

				Joins temp;

				Collection<SchemaSingleton.FrgnKeys> lists;

				/*---------------------------------------------------------*/
				/* FORWARD RESOLUTION                                      */
				/*---------------------------------------------------------*/

				lists = SchemaSingleton.getForwardFKs(defaultCatalog, defaultTable).values();

				/*---------------------------------------------------------*/

				for(SchemaSingleton.FrgnKeys list: lists)
				{
					for(SchemaSingleton.FrgnKey frgnKey: list)
					{
						temp = new Joins(frgnKey.pkExternalCatalog);

						qId = _resolveJoins(temp, done, WITH_NESTED_SELECT, frgnKey.pkExternalCatalog, frgnKey.pkTable, givenCatalog, givenTable, givenColumn, givenValue);

						if(qId != null)
						{
							switch(method)
							{
								case WITH_INNER_JOINS:
									_mergeInnerJoins(joins, temp, frgnKey);
									break;

								case WITH_NESTED_SELECT:
									_mergeNestedSelect(joins, temp, frgnKey);
									break;

								default:
									LogSingleton.root.error("internal error");
							}

							return qId;
						}
					}
				}

				/*---------------------------------------------------------*/
				/* BACKWARD RESOLUTION                                     */
				/*---------------------------------------------------------*/

				lists = SchemaSingleton.getBackwardFKs(defaultCatalog, defaultTable).values();

				/*---------------------------------------------------------*/

				for(SchemaSingleton.FrgnKeys list: lists)
				{
					for(SchemaSingleton.FrgnKey frgnKey: list)
					{
						temp = new Joins(frgnKey.fkExternalCatalog);

						qId = _resolveJoins(temp, done, WITH_NESTED_SELECT, frgnKey.fkExternalCatalog, frgnKey.fkTable, givenCatalog, givenTable, givenColumn, givenValue);

						if(qId != null)
						{
							switch(method)
							{
								case WITH_INNER_JOINS:
									_mergeInnerJoins(joins, temp, frgnKey);
									break;

								case WITH_NESTED_SELECT:
									_mergeNestedSelect(joins, temp, frgnKey);
									break;

								default:
									LogSingleton.root.error("internal error");
							}

							return qId;
						}
					}
				}

				/*---------------------------------------------------------*/
			}
			else
			{
				joins.getJoin("`" + column.internalCatalog + "`.`" + column.table + "`", Structure.DUMMY);

				SQLQId qId = new SQLQId(column.internalCatalog, column.table, column.name);

				if(givenValue != null)
				{
					/*-----------------------------------------------------*/
					/* CONDITION ON VALUE                                  */
					/*-----------------------------------------------------*/

					joins.getJoin(Structure.DUMMY, Structure.DUMMY)
					     .getIslet(Structure.DUMMY, Structure.DUMMY)
					     .addWhere(qId.toString() + "='" + givenValue.replace("'", "''") + "'")
					;

					/*-----------------------------------------------------*/
				}

				return qId;
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/

			Column column = SchemaSingleton.getColumns(
				givenCatalog != null ? givenCatalog : defaultCatalog,
				givenTable != null ? givenTable : defaultTable
			).get(givenColumn);

			/*-------------------------------------------------------------*/

			if(column != null)
			{
				joins.getJoin("`" + column.internalCatalog + "`.`" + column.table + "`", Structure.DUMMY);

				SQLQId qId = new SQLQId(column.internalCatalog, column.table, column.name);

				if(givenValue != null)
				{
					/*-----------------------------------------------------*/
					/* CONDITION ON VALUE                                  */
					/*-----------------------------------------------------*/

					joins.getJoin(Structure.DUMMY, Structure.DUMMY)
					     .getIslet(Structure.DUMMY, Structure.DUMMY)
					     .addWhere(qId.toString() + "='" + givenValue.replace("'", "''") + "'")
					;

					/*-----------------------------------------------------*/
				}

				return qId;
			}

			/*-------------------------------------------------------------*/
		}

		return null;
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

	private static SQLQId resolve(Joins joins, int method, String defaultCatalog, String defaultTable, String givenQId, @Nullable String givenValue) throws Exception
	{
		SQLQId result;

		/*-----------------------------------------------------------------*/

		String[] parts = givenQId.trim().split("\\.");

		final int nb = parts.length;

		String givenCatalog;
		String givenTable;
		String givenColumn;

		/**/ if(nb == 1)
		{
			givenCatalog = null;
			givenTable = null;
			givenColumn = unquote(parts[0]);
		}
		else if(nb == 2)
		{
			givenCatalog = null;
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
			throw new Exception("could not parse column name `" + givenQId + "`");
		}

		/*-----------------------------------------------------------------*/

		result = _resolveJoins(joins, new HashSet<>(), method, defaultCatalog, defaultTable, givenCatalog, givenTable, givenColumn, givenValue);

		if(result == null)
		{
			result = _resolveJoins(joins, new HashSet<>(), TRIVIAL, defaultCatalog, defaultTable, givenCatalog, givenTable, givenColumn, givenValue);

			if(result == null)
			{
				throw new Exception("could not resolve column name `" + givenQId + "`");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static SQLQId resolveWithInnerJoins(Joins joins, String defaultCatalog, String defaultTable, String givenQId, @Nullable String givenValue) throws Exception
	{
		return resolve(joins, WITH_INNER_JOINS, defaultCatalog, defaultTable, givenQId, givenValue);
	}

	/*---------------------------------------------------------------------*/

	public static SQLQId resolveWithNestedSelect(Joins joins, String defaultCatalog, String defaultTable, String givenQId, @Nullable String givenValue) throws Exception
	{
		return resolve(joins, WITH_NESTED_SELECT, defaultCatalog, defaultTable, givenQId, givenValue);
	}

	/*---------------------------------------------------------------------*/
}
