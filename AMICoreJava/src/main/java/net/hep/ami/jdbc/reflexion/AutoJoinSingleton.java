package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.jdbc.reflexion.structure.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	private static void resolve(
		PathList pathList,
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

			QId resolvedQId = new QId(column.internalCatalog, column.table, column.name);

			/*-------------------------------------------------------------*/

			for(QId pathQId: givenQId.getPath())
			{
				for(FrgnKey frgnKey: path)
				{
					if(pathQId.check(new QId(frgnKey.pkExternalCatalog, frgnKey.pkTable, frgnKey.pkColumn)) == false)
					{
						return;
					}
				}
			}

			/*-------------------------------------------------------------*/

			pathList.addPath(givenQId, resolvedQId, path);

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	public static PathList resolve(String defaultCatalog, String defaultTable, QId givenQId, int max) throws Exception
	{
		PathList result = new PathList();

		resolve(result, new Stack<>(), new HashSet<>(), 0, max, defaultCatalog, defaultTable, givenQId);

		return result.check(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static PathList resolve(String defaultCatalog, String defaultTable, QId givenQId) throws Exception
	{
		PathList result = new PathList();

		resolve(result, new Stack<>(), new HashSet<>(), 0, 999, defaultCatalog, defaultTable, givenQId);

		return result.check(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static PathList resolve(String defaultCatalog, String defaultTable, String givenQId, int max) throws Exception
	{
		return resolve(defaultCatalog, defaultTable, new QId(givenQId), max);
	}

	/*---------------------------------------------------------------------*/

	public static PathList resolve(String defaultCatalog, String defaultTable, String givenQId) throws Exception
	{
		return resolve(defaultCatalog, defaultTable, new QId(givenQId), 999);
	}

	/*---------------------------------------------------------------------*/
}
