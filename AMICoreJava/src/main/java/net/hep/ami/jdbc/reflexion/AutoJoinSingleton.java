package net.hep.ami.jdbc.reflexion;

import java.util.*;
import java.util.Map.Entry;

import net.hep.ami.jdbc.reflexion.structure.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	private static final int TRIVIAL = 0;
	private static final int WITH_INNER_JOINS = 1;
	private static final int WITH_NESTED_SELECT = 2;

	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	private static void _mergeInnerJoins(Islets islets, Islets temp, SchemaSingleton.FrgnKey frgnKey)
	{
		/*-----------------------------------------------------------------*/
		/* BUILD SQL JOIN                                                  */
		/*-----------------------------------------------------------------*/

		QId fk = new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);
		QId pk = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

		String where = fk + " = " + pk;

		/*-----------------------------------------------------------------*/
		/* MERGE                                                           */
		/*-----------------------------------------------------------------*/

		for(Entry<String, Map<String, Joins>> entry1: ((((((temp)))))).entrySet()) for(Entry<String, Joins> entry2: entry1.getValue().entrySet())
		{
			for(Entry<String, Map<String, Query>> entry3: entry2.getValue().entrySet()) for(Entry<String, Query> entry4: entry3.getValue().entrySet())
			{
				islets.getJoins(entry1.getKey(), entry2.getKey())
				      .getQuery(entry3.getKey(), entry4.getKey())
				      .addWholeQuery(entry4.getValue())
				;
			}
		}

		/*-----------------------------------------------------------------*/

		islets.getJoins(Islets.DUMMY, Islets.DUMMY)
		      .getQuery(fk.toString(QId.Deepness.TABLE), pk.toString(QId.Deepness.TABLE))
		      .addWherePart(where)
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void _mergeNestedSelect(Islets islets, Islets temp, SchemaSingleton.FrgnKey frgnKey)
	{
		/*-----------------------------------------------------------------*/
		/* BUILD SQL JOIN                                                  */
		/*-----------------------------------------------------------------*/

		QId fk = new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);
		QId pk = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

		String from = "`" + frgnKey.pkInternalCatalog + "`.`" + frgnKey.pkTable + "`";

		/*-----------------------------------------------------------------*/
		/* MERGE                                                           */
		/*-----------------------------------------------------------------*/

		islets.getJoins(fk.toString(), pk.toString())
		      .getQuery(Joins.DUMMY, Joins.DUMMY)
		      .addFromPart(from)
		      .addWholeQuery(temp.toQuery())
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static QId _resolveJoins(
		Islets islets,
		Set<String> done,
		int method,
		String defaultCatalog,
		String defaultTable,
		QId givenQId,
		@Nullable String givenValue,
		boolean goBackward
	 ) throws Exception {

		QId result;

		String givenCatalog = givenQId.getCatalog();
		String givenTable = givenQId.getTable();
		String givenColumn = givenQId.getColumn();

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
				Islets temp;

				Collection<SchemaSingleton.FrgnKeys> fowardLists;
				Collection<SchemaSingleton.FrgnKeys> backwardLists;

				/*---------------------------------------------------------*/
				/* FORWARD RESOLUTION                                      */
				/*---------------------------------------------------------*/

				fowardLists = SchemaSingleton.getForwardFKs(defaultCatalog, defaultTable).values();

				/*---------------------------------------------------------*/

				for(SchemaSingleton.FrgnKeys list: fowardLists)
				{
					for(SchemaSingleton.FrgnKey frgnKey: list)
					{
						temp = new Islets();

						result = _resolveJoins(temp, done, WITH_INNER_JOINS, frgnKey.pkExternalCatalog, frgnKey.pkTable, givenQId, givenValue, false);

						if(result != null)
						{
							switch(method)
							{
								case WITH_INNER_JOINS:
									_mergeInnerJoins(islets, temp, frgnKey);
									break;

								case WITH_NESTED_SELECT:
									_mergeNestedSelect(islets, temp, frgnKey);
									break;
							}

							return result;
						}
					}
				}

				/*---------------------------------------------------------*/
				/* BACKWARD RESOLUTION                                     */
				/*---------------------------------------------------------*/

				backwardLists = SchemaSingleton.getBackwardFKs(defaultCatalog, defaultTable).values();

				/*---------------------------------------------------------*/

				for(SchemaSingleton.FrgnKeys list: backwardLists)
				{
					for(SchemaSingleton.FrgnKey frgnKey: list)
					{
						temp = new Islets();

						boolean testIfNextNotGoFoward = SchemaSingleton.getForwardFKs(frgnKey.fkExternalCatalog, frgnKey.fkTable).size() <= 1;
						if(goBackward || testIfNextNotGoFoward)
						{
							result = _resolveJoins(temp, done, WITH_INNER_JOINS, frgnKey.fkExternalCatalog, frgnKey.fkTable, givenQId, givenValue, goBackward);

							if(result != null)
							{
								switch(method)
								{
									case WITH_INNER_JOINS:
										_mergeInnerJoins(islets, temp, frgnKey);
										break;

									case WITH_NESTED_SELECT:
										_mergeNestedSelect(islets, temp, frgnKey);
										break;
								}

								return result;
							}
						}
					}
				}

				/*---------------------------------------------------------*/
			}
			else
			{
				result = new QId(column.internalCatalog, column.table, column.name);

				if(givenValue != null)
				{
					/*-----------------------------------------------------*/
					/* CONDITION ON VALUE                                  */
					/*-----------------------------------------------------*/

					islets.getJoins(Islets.DUMMY, Islets.DUMMY)
					      .getQuery(Joins.DUMMY, Joins.DUMMY)
					      .addWherePart(result + "='" + givenValue.replace("'", "''") + "'")
					;

					/*-----------------------------------------------------*/
				}

				return result;
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
				result = new QId(column.internalCatalog, column.table, column.name);

				if(givenValue != null)
				{
					/*-----------------------------------------------------*/
					/* CONDITION ON VALUE                                  */
					/*-----------------------------------------------------*/

					islets.getJoins(Islets.DUMMY, Islets.DUMMY)
					      .getQuery(Joins.DUMMY, Joins.DUMMY)
					      .addWherePart(result + "='" + givenValue.replace("'", "''") + "'")
					;

					/*-----------------------------------------------------*/
				}

				return result;
			}

			/*-------------------------------------------------------------*/
		}

		return null;
	}

	/*---------------------------------------------------------------------*/

	private static QId resolve(Islets islets, int method, String defaultCatalog, String defaultTable, QId givenQId, @Nullable String givenValue) throws Exception
	{
		QId result;

		/*-----------------------------------------------------------------*/

		result = _resolveJoins(islets, new HashSet<>(), method, defaultCatalog, defaultTable, givenQId, givenValue,true);

		if(result == null)
		{
			result = _resolveJoins(islets, new HashSet<>(), TRIVIAL, defaultCatalog, defaultTable, givenQId, givenValue,true);

			if(result == null)
			{
				throw new Exception("could not resolve column name `" + givenQId + "`");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static QId resolveWithInnerJoins(Islets islets, String defaultCatalog, String defaultTable, String givenQId, @Nullable String givenValue) throws Exception
	{
		return resolve(islets, WITH_INNER_JOINS, defaultCatalog, defaultTable, new QId(givenQId, QId.Deepness.COLUMN), givenValue);
	}

	/*---------------------------------------------------------------------*/

	public static QId resolveWithNestedSelect(Islets islets, String defaultCatalog, String defaultTable, String givenQId, @Nullable String givenValue) throws Exception
	{
		return resolve(islets, WITH_NESTED_SELECT, defaultCatalog, defaultTable, new QId(givenQId, QId.Deepness.COLUMN), givenValue);
	}

	/*---------------------------------------------------------------------*/
}
