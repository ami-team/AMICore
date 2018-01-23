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
		String defaultCatalog,
		String defaultTable,
		QId givenQId
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
						resolve(pathList, path, done, frgnKey.pkExternalCatalog, frgnKey.pkTable, givenQId);
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
						resolve(pathList, path, done, frgnKey.fkExternalCatalog, frgnKey.fkTable, givenQId);
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

			pathList.addPath(givenQId, resolvedQId, path);

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	public static PathList resolve(String defaultCatalog, String defaultTable, QId givenQId) throws Exception
	{
		PathList result = new PathList();

		resolve(result, new Stack<>(), new HashSet<>(), defaultCatalog, defaultTable, givenQId);

		return result.check(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static PathList resolve(String defaultCatalog, String defaultTable, String givenQId) throws Exception
	{
		return resolve(defaultCatalog, defaultTable, new QId(givenQId));
	}

	/*---------------------------------------------------------------------*/
}
