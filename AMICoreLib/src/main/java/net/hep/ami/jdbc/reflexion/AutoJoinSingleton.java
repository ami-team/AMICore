package net.hep.ami.jdbc.reflexion;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.query.*;

import net.hep.ami.utility.Empty;
import org.jetbrains.annotations.*;

public class AutoJoinSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private AutoJoinSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void resolve(
		@NotNull Resolution resolution,
		@NotNull Stack<SchemaSingleton.FrgnKey> resolvedPath,
		@NotNull Set<String> done,
		int cnt,
		int max,
		@NotNull String defaultCatalog,
		@NotNull String defaultEntity,
		@NotNull QId givenQId
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

		SchemaSingleton.Column resolvedColumn = checkNow ? SchemaSingleton.getEntityInfo(defaultCatalog, defaultEntity).columns.get(givenColumn) : null;

		if(resolvedColumn == null)
		{
			String key;

			Collection<SchemaSingleton.FrgnKeys> forwardLists;
			Collection<SchemaSingleton.FrgnKeys> backwardLists;

			/*--------------------------------------------------------------------------------------------------------*/
			/* CHECK IF VIEW OF TABLE                                                                                 */
			/*--------------------------------------------------------------------------------------------------------*/

			SchemaSingleton.Table entityInfo = SchemaSingleton.getEntityInfo(defaultCatalog, defaultEntity);

			String newDefaultEntity = !Empty.is(entityInfo.viewOfTable, Empty.STRING_NULL_EMPTY_BLANK) ? entityInfo.viewOfTable : defaultEntity;

			/*--------------------------------------------------------------------------------------------------------*/
			/* FORWARD RESOLUTION                                                                                     */
			/*--------------------------------------------------------------------------------------------------------*/

			forwardLists = SchemaSingleton.getForwardFKs(defaultCatalog, newDefaultEntity).values();

			/*--------------------------------------------------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: forwardLists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					key = frgnKey.fkExternalCatalog + "$" + frgnKey.fkEntity;

					if(!done.contains(key))
					{
						done.add(key);
						resolvedPath.add(frgnKey);
						resolve(resolution, resolvedPath, done, cnt + 1, max, frgnKey.pkExternalCatalog, frgnKey.pkEntity, givenQId);
						resolvedPath.pop();
						done.remove(key);
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
			/* BACKWARD RESOLUTION                                                                                    */
			/*--------------------------------------------------------------------------------------------------------*/

			backwardLists = SchemaSingleton.getBackwardFKs(defaultCatalog, newDefaultEntity).values();

			/*--------------------------------------------------------------------------------------------------------*/

			for(SchemaSingleton.FrgnKeys list: backwardLists)
			{
				for(SchemaSingleton.FrgnKey frgnKey: list)
				{
					key = frgnKey.pkExternalCatalog + "$" + frgnKey.pkEntity;

					if(!done.contains(key))
					{
						done.add(key);
						resolvedPath.add(frgnKey);
						resolve(resolution, resolvedPath, done, cnt + 1, max, frgnKey.fkExternalCatalog, frgnKey.fkEntity, givenQId);
						resolvedPath.pop();
						done.remove(key);
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		else
		{
			/*--------------------------------------------------------------------------------------------------------*/

			Map<QId, Boolean> map = givenQId.getConstraints().stream().collect(Collectors.toMap(x -> x, QId::getExclusion));

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
				if(!found)
				{
					return;
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			resolution.addPath(givenQId, resolvedColumn, resolvedPath);

			/*--------------------------------------------------------------------------------------------------------*/
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Resolution resolve(@NotNull String defaultExternalCatalog, @NotNull String defaultEntity, @NotNull QId givenQId, int max) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, max, defaultExternalCatalog, defaultEntity, givenQId);

		return result.finalize(givenQId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Resolution resolve(@NotNull String defaultExternalCatalog, @NotNull String defaultEntity, @NotNull QId givenQId) throws Exception
	{
		Resolution result = new Resolution();

		resolve(result, new Stack<>(), new HashSet<>(), 0, 999, defaultExternalCatalog, defaultEntity, givenQId);

		return result.finalize(givenQId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Resolution resolve(@NotNull String defaultExternalCatalog, @NotNull String defaultEntity, @NotNull String givenQId, int max) throws Exception
	{
		return resolve(defaultExternalCatalog, defaultEntity, QId.parseQId(givenQId, QId.Type.FIELD), max);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Resolution resolve(@NotNull String defaultExternalCatalog, @NotNull String defaultEntity, @NotNull String givenQId) throws Exception
	{
		return resolve(defaultExternalCatalog, defaultEntity, QId.parseQId(givenQId, QId.Type.FIELD), 999);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
