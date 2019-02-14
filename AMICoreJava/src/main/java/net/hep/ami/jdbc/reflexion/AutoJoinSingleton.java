package net.hep.ami.jdbc.reflexion;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.query.*;

public class AutoJoinSingleton
{
	/*---------------------------------------------------------------------*/

	private AutoJoinSingleton() {}

	/*---------------------------------------------------------------------*/

	private static void resolve(
		Resolution resolution,
		Stack<SchemaSingleton.FrgnKey> resolvedPath,
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

		boolean checkNow = (givenCatalog == null || defaultCatalog.equalsIgnoreCase(givenCatalog))
		                   &&
		                   (givenTable == null || defaultTable.equalsIgnoreCase(givenTable))
		;

		SchemaSingleton.Column resolvedColumn = checkNow ? SchemaSingleton.getEntityInfo(defaultCatalog, defaultTable).get(givenColumn) : null;

		if(resolvedColumn == null)
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
						resolvedPath.add(frgnKey);
						resolve(resolution, resolvedPath, done, cnt + 1, max, frgnKey.pkExternalCatalog, frgnKey.pkTable, givenQId);
						resolvedPath.pop();
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
						resolvedPath.add(frgnKey);
						resolve(resolution, resolvedPath, done, cnt + 1, max, frgnKey.fkExternalCatalog, frgnKey.fkTable, givenQId);
						resolvedPath.pop();
						done.remove(key);
					}
				}
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/

			Map<QId, Boolean> map = givenQId.getConstraints().stream().collect(Collectors.toMap(x -> x, x -> x.getExclusion()));

			/**/

			for(QId qId: map.keySet())
			{
				for(SchemaSingleton.FrgnKey frgnKey: resolvedPath)
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

			resolution.addPath(givenQId, resolvedColumn, resolvedPath);

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultExternalCatalog, String defaultTable, QId givenQId, int max) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, max, defaultExternalCatalog, defaultTable, givenQId);

		return result.finalize(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultExternalCatalog, String defaultTable, QId givenQId) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, 999, defaultExternalCatalog, defaultTable, givenQId);

		return result.finalize(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultExternalCatalog, String defaultTable, String givenQId, int max) throws Exception
	{
		return resolve(defaultExternalCatalog, defaultTable, QId.parseQId(givenQId, QId.Type.FIELD), max);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultExternalCatalog, String defaultTable, String givenQId) throws Exception
	{
		return resolve(defaultExternalCatalog, defaultTable, QId.parseQId(givenQId, QId.Type.FIELD), 999);
	}

	/*---------------------------------------------------------------------*/
}
