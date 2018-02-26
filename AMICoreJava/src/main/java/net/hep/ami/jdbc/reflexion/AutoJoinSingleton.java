package net.hep.ami.jdbc.reflexion;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.reflexion.structure.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	private static void resolve(
		Resolution pathList,
		Stack<FrgnKey> path,
		Set<String> done,
		int cnt,
		int max,
		String defaultCatalog,
		String defaultTable,
		QId givenQId
	 ) throws Exception {

		if(cnt >= max)
		{
			return;
		}

		String givenCatalog = givenQId.getCatalog();
		String givenTable = givenQId.getEntity();
		String givenColumn = givenQId.getField();

		boolean checkNow = (givenCatalog == null || defaultCatalog.equals(givenCatalog))
		                   &&
		                   (givenTable == null || defaultTable.equals(givenTable))
		;

		SchemaSingleton.Column column = checkNow ? SchemaSingleton.getColumns(defaultCatalog, defaultTable).get(givenColumn) : null;

		if(column == null)
		{
			String key;

			Collection<FrgnKeys> forwardLists;
			Collection<FrgnKeys> backwardLists;

			/*-------------------------------------------------------------*/
			/* FORWARD RESOLUTION                                          */
			/*-------------------------------------------------------------*/

			forwardLists = SchemaSingleton.getForwardFKs(defaultCatalog, defaultTable).values();

			/*-------------------------------------------------------------*/

			for(FrgnKeys list: forwardLists)
			{
				for(FrgnKey frgnKey: list)
				{
					key = frgnKey.fkExternalCatalog + "$" + frgnKey.fkTable;

					if(done.contains(key) == false)
					{
						done.add(key);
						path.add(frgnKey);
						resolve(pathList, path, done, cnt + 1, max, frgnKey.pkExternalCatalog, frgnKey.pkTable, givenQId);
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

			for(FrgnKeys list: backwardLists)
			{
				for(FrgnKey frgnKey: list)
				{
					key = frgnKey.pkExternalCatalog + "$" + frgnKey.pkTable;

					if(done.contains(key) == false)
					{
						done.add(key);
						path.add(frgnKey);
						resolve(pathList, path, done, cnt + 1, max, frgnKey.fkExternalCatalog, frgnKey.fkTable, givenQId);
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

			QId resolvedQId = new QId(column.internalCatalog, column.table, column.name, givenQId.getConstraints());

			/*-------------------------------------------------------------*/

			Map<QId, Boolean> map = givenQId.getConstraints().stream().collect(Collectors.toMap(qId -> qId, qId -> qId.getExclusion()));

			/**/

			for(QId qId: map.keySet())
			{
				for(FrgnKey frgnKey: path)
				{
					if(qId.matches(new QId(frgnKey.pkExternalCatalog, frgnKey.pkTable, frgnKey.pkColumn))
					   ||
					   qId.matches(new QId(frgnKey.fkExternalCatalog, frgnKey.fkTable, frgnKey.fkColumn))
					 ) {
						map.put(qId, !qId.getExclusion());
					}
				}
			}

			/**/

			for(boolean found: map.values())
			{
				if(found == false)
				{
					return;
				}
			}

			/*-------------------------------------------------------------*/

			pathList.addPath(givenQId, resolvedQId, path);

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultCatalog, String defaultTable, QId givenQId, int max) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, max, defaultCatalog, defaultTable, givenQId);

		return result.check(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultCatalog, String defaultTable, QId givenQId) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, 999, defaultCatalog, defaultTable, givenQId);

		return result.check(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultCatalog, String defaultTable, String givenQId, int max) throws Exception
	{
		return resolve(defaultCatalog, defaultTable, new QId(givenQId), max);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultCatalog, String defaultTable, String givenQId) throws Exception
	{
		return resolve(defaultCatalog, defaultTable, new QId(givenQId), 999);
	}

	/*---------------------------------------------------------------------*/
}
