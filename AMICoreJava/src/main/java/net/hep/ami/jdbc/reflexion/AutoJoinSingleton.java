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
		String defaultEntity,
		QId givenQId
	 ) throws Exception {

		if(cnt >= max)
		{
			return;
		}

		String givenCatalog = givenQId.getCatalog();
		String givenEntity = givenQId.getEntity();
		String givenColumn = givenQId.getField();

		boolean checkNow = (givenCatalog == null || defaultCatalog.equalsIgnoreCase(givenCatalog))
		                   &&
		                   (givenEntity == null || defaultEntity.equalsIgnoreCase(givenEntity))
		;

		SchemaSingleton.Column resolvedColumn = checkNow ? SchemaSingleton.getEntityInfo(defaultCatalog, defaultEntity).get(givenColumn) : null;

		if(resolvedColumn == null)
		{
			String key;

			Collection<SchemaSingleton.FrgnKeys> forwardLists;
			Collection<SchemaSingleton.FrgnKeys> backwardLists;

			/*-------------------------------------------------------------*/
			/* FORWARD RESOLUTION                                          */
			/*-------------------------------------------------------------*/

			forwardLists = SchemaSingleton.getForwardFKs(defaultCatalog, defaultEntity).values();

			/*-------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: forwardLists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					key = frgnKey.fkExternalCatalog + "$" + frgnKey.fkEntity;

					if(done.contains(key) == false)
					{
						done.add(key);
						resolvedPath.add(frgnKey);
						resolve(resolution, resolvedPath, done, cnt + 1, max, frgnKey.pkExternalCatalog, frgnKey.pkEntity, givenQId);
						resolvedPath.pop();
						done.remove(key);
					}
				}
			}

			/*-------------------------------------------------------------*/
			/* BACKWARD RESOLUTION                                         */
			/*-------------------------------------------------------------*/

			backwardLists = SchemaSingleton.getBackwardFKs(defaultCatalog, defaultEntity).values();

			/*-------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: backwardLists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					key = frgnKey.pkExternalCatalog + "$" + frgnKey.pkEntity;

					if(done.contains(key) == false)
					{
						done.add(key);
						resolvedPath.add(frgnKey);
						resolve(resolution, resolvedPath, done, cnt + 1, max, frgnKey.fkExternalCatalog, frgnKey.fkEntity, givenQId);
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
					if(qId.matches(new QId(frgnKey.pkExternalCatalog, frgnKey.pkEntity, frgnKey.pkField))
					   ||
					   qId.matches(new QId(frgnKey.fkExternalCatalog, frgnKey.fkEntity, frgnKey.fkField))
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

	public static Resolution resolve(String defaultExternalCatalog, String defaultEntity, QId givenQId, int max) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, max, defaultExternalCatalog, defaultEntity, givenQId);

		return result.finalize(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultExternalCatalog, String defaultEntity, QId givenQId) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, 999, defaultExternalCatalog, defaultEntity, givenQId);

		return result.finalize(givenQId);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultExternalCatalog, String defaultEntity, String givenQId, int max) throws Exception
	{
		return resolve(defaultExternalCatalog, defaultEntity, QId.parseQId(givenQId, QId.Type.FIELD), max);
	}

	/*---------------------------------------------------------------------*/

	public static Resolution resolve(String defaultExternalCatalog, String defaultEntity, String givenQId) throws Exception
	{
		return resolve(defaultExternalCatalog, defaultEntity, QId.parseQId(givenQId, QId.Type.FIELD), 999);
	}

	/*---------------------------------------------------------------------*/
}
