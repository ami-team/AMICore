package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.jdbc.reflexion.structure.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	private static void pathToIslet(Islets islets, Stack<SchemaSingleton.FrgnKey> path, QId qId, @Nullable String givenValue)
	{
		Query query = new Query();

		if(path.isEmpty() == false)
		{
			/*-------------------------------------------------------------*/
			/* WITH JOIN                                                   */
			/*-------------------------------------------------------------*/

			SchemaSingleton.FrgnKey firstFrgnKey = path.firstElement();
			SchemaSingleton.FrgnKey lastFrgnKey = path.lastElement();

			islets.getQuery(
				new QId(firstFrgnKey.fkInternalCatalog, firstFrgnKey.fkTable, firstFrgnKey.fkColumn).toString()
				,
				new QId(lastFrgnKey.pkInternalCatalog, lastFrgnKey.pkTable, lastFrgnKey.pkColumn).toString()
			).add(query);

			/*-------------------------------------------------------------*/

			QId firstPk = new QId(firstFrgnKey.pkInternalCatalog, firstFrgnKey.pkTable, firstFrgnKey.pkColumn);

			query.addSelectPart(firstPk.toString(QId.Deepness.COLUMN))
			     .addFromPart(firstPk.toString(QId.Deepness.TABLE))
			;

			/*-------------------------------------------------------------*/

			QId fk;
			QId pk;

			for(SchemaSingleton.FrgnKey frgnKey: path.subList(1, path.size()))
			{
				fk = new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);
				pk = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

				query.addFromPart(fk.toString(QId.Deepness.TABLE))
				     .addFromPart(pk.toString(QId.Deepness.TABLE))
				     .addWherePart(fk.toString() + " = " + pk.toString())
				;
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/
			/* WITHOUT JOIN                                                */
			/*-------------------------------------------------------------*/

			islets.getQuery(
				Islets.DUMMY
				,
				Islets.DUMMY
			).add(query);

			/*-------------------------------------------------------------*/

			query.addFromPart(qId.toString(QId.Deepness.TABLE));

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		if(givenValue != null)
		{
			query.addWherePart(qId.toString() + " = '" + givenValue.replace("'", "''") + "'");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void _findPaths(
		QId[] result_qid,
		Islets result_islets,
		Stack<SchemaSingleton.FrgnKey> path,
		Set<String> done,
		String defaultCatalog,
		String defaultTable,
		QId givenQId,
		@Nullable String givenValue
	 ) throws Exception {

		String givenCatalog = givenQId.getCatalog();
		String givenTable = givenQId.getTable();
		String givenColumn = givenQId.getColumn();

		boolean checkNow = (givenCatalog == null || defaultCatalog.equals(givenCatalog))
		                   &&
		                   (givenTable == null || defaultTable.equals(givenTable))
		;

		SchemaSingleton.Column column = checkNow ? SchemaSingleton.getColumns(defaultCatalog, defaultTable).get(givenColumn) : null;

		if(column == null)
		{
			String key;

			Collection<SchemaSingleton.FrgnKeys> forwardLists;
			Collection<SchemaSingleton.FrgnKeys> backwardLists;

			/*-------------------------------------------------------------*/
			/* FORWARD RESOLUTION                                          */
			/*-------------------------------------------------------------*/

			forwardLists = SchemaSingleton.getForwardFKs(defaultCatalog, defaultTable).values();

			/*-------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: forwardLists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					key = frgnKey.fkExternalCatalog + "$" + frgnKey.fkTable;

					if(done.contains(key) == false)
					{
						done.add(key);
						path.add(frgnKey);
						_findPaths(result_qid, result_islets, path, done, frgnKey.pkExternalCatalog, frgnKey.pkTable, givenQId, givenValue);
						path.pop();
						done.remove(key);
					}
				}
			}

			/*-------------------------------------------------------------*/
			/* BACKWARD RESOLUTION                                         */
			/*-------------------------------------------------------------*/

			backwardLists = SchemaSingleton.getBackwardFKs(defaultCatalog, defaultTable).values();

			/*-------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: backwardLists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					key = frgnKey.pkExternalCatalog + "$" + frgnKey.pkTable;

					if(done.contains(key) == false)
					{
						done.add(key);
						path.add(frgnKey);
						_findPaths(result_qid, result_islets, path, done, frgnKey.fkExternalCatalog, frgnKey.fkTable, givenQId, givenValue);
						path.pop();
						done.remove(key);
					}
				}
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/

			QId qId = new QId(column.internalCatalog, column.table, column.name);

			/*-------------------------------------------------------------*/

			if(result_qid[0] == null)
			{
				result_qid[0] = qId;
			}
			else
			{
				if(result_qid[0].equals(qId) == false)
				{
					throw new Exception("could not resolve column name `" + givenQId + "`: ambiguous resolution");
				}
			}

			/*-------------------------------------------------------------*/

			pathToIslet(result_islets, path, qId, givenValue);

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	public static QId resolve(Islets islets, String defaultCatalog, String defaultTable, String givenQId, @Nullable String givenValue) throws Exception
	{
		QId[] result = new QId[] {null};

		/*-----------------------------------------------------------------*/

		_findPaths(result, islets, new Stack<>(), new HashSet<>(), defaultCatalog, defaultTable, new QId(givenQId, QId.Deepness.COLUMN), givenValue);

		/*-----------------------------------------------------------------*/

		if(result[0] == null)
		{
			throw new Exception("could not resolve column name `" + givenQId + "`: not found");
		}

		/*-----------------------------------------------------------------*/

		return result[0];
	}

	/*---------------------------------------------------------------------*/
}
