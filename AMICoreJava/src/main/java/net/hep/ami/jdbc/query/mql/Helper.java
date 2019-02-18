package net.hep.ami.jdbc.query.mql;

import java.util.*;

import net.hep.ami.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.FrgnKeys;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class Helper
{
	/*---------------------------------------------------------------------*/

	private Helper() {}

	/*---------------------------------------------------------------------*/

	public static Set<String> getFromSetFromResolutionList(QId mainPrimaryField, List<Resolution> resolutionList)
	{
		Set<String> result = new LinkedHashSet<>();

		/*-----------------------------------------------------------------*/

		result.add(mainPrimaryField.toString(QId.MASK_CATALOG_ENTITY));

		for(Resolution resolution: resolutionList)
		{
			result.add(resolution.getInternalQId().toString(QId.MASK_CATALOG_ENTITY));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static boolean isTrivialQuery(List<Resolution> resolutionList)
	{
		if(resolutionList.isEmpty() == false)
		{
			int oldPathHashCode = 0;
			int newPathHashCode = 0;

			for(Resolution resolution: resolutionList)
			{
				/*---------------------------------------------------------*/

				if(resolution.getPaths().size() > 1)
				{
					return false;
				}

				/*---------------------------------------------------------*/

				newPathHashCode = resolution.getPathHashCode();

				/*---------------------------------------------------------*/

				if(newPathHashCode != 1)
				{
					if(oldPathHashCode == 0)
					{
						oldPathHashCode = newPathHashCode;
					}
					else
					{
						if(oldPathHashCode != newPathHashCode)
						{
							return false;
						}
					}
				}

				/*---------------------------------------------------------*/
			}
		}

		return true;
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<Set<String>, Set<String>> getIsolatedPath(QId mainPrimaryField, List<Resolution> resolutionList, boolean isFieldNameOnly) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* BUILD GLOBAL FROM SET                                           */
		/*-----------------------------------------------------------------*/

		Set<String> globalFromSet = getFromSetFromResolutionList(mainPrimaryField, resolutionList);

		/*-----------------------------------------------------------------*/
		/* ISOLATE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		Set<String> globalWherSet = new LinkedHashSet<>();

		/*-----------------------------------------------------------------*/

		if(isTrivialQuery(resolutionList))
		{
			for(Resolution resolution: resolutionList)
			{
				globalFromSet.add(resolution.getInternalQId().toString(QId.MASK_CATALOG_ENTITY));

				for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
				{
					for(SchemaSingleton.FrgnKey frgnKey: /*----*/ frgnKeys /*----*/)
					{
						globalFromSet.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null).toString(QId.MASK_CATALOG_ENTITY));
						globalFromSet.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null).toString(QId.MASK_CATALOG_ENTITY));

						globalWherSet.add(frgnKey.toString());
					}
				}
			}

			return new Tuple2<>(globalFromSet, globalWherSet);
		}

		/*-----------------------------------------------------------------*/

		QId qId;

		String tmp;

		String query;

		for(Resolution resolution: resolutionList) 
		{
			if(resolution.getMaxPathLen() == 0)
			{
				continue;
			}

			/*-------------------------------------------------------------*/

			int cnt = 0;

			boolean trivialCase = true;

			Set<String> idSet = new TreeSet<>();

			Set<String> whereSet1 = new LinkedHashSet<>();
			Set<String> whereSet2 = new LinkedHashSet<>();

			idSet.add(mainPrimaryField.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD));

			for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
			{
				/*---------------------------------------------------------*/

				Set<String> tmpIdSet = new TreeSet<>();

				Set<String> tmpFromSet = new TreeSet<>();

				Set<String> tmpWhereSet = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKey frgnKey: frgnKeys)
				{
					/*-----------------------------------------------------*/

					qId = new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);

					tmp = qId.toString(QId.MASK_CATALOG_ENTITY);

					if(globalFromSet.contains(tmp) == false)
					{
						tmpFromSet.add(tmp);
					}
					else
					{
						tmp = new QId(SchemaSingleton.getPrimaryKey(frgnKey.fkExternalCatalog, frgnKey.fkTable), true).toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD);

						tmpIdSet.add(tmp);

						if(idSet.add(tmp) && cnt > 0)
						{
							trivialCase = false;
						}
					}

					/*-----------------------------------------------------*/

					qId = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

					tmp = qId.toString(QId.MASK_CATALOG_ENTITY);

					if(globalFromSet.contains(tmp) == false)
					{
						tmpFromSet.add(tmp);
					}
					else
					{
						tmp = new QId(SchemaSingleton.getPrimaryKey(frgnKey.pkExternalCatalog, frgnKey.pkTable), true).toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD);

						tmpIdSet.add(tmp);

						if(idSet.add(tmp) && cnt > 0)
						{
							trivialCase = false;
						}
					}

					/*-----------------------------------------------------*/

					tmpWhereSet.add(frgnKey.toString());

					/*-----------------------------------------------------*/
				}

				/*---------------------------------------------------------*/

				if(tmpFromSet.isEmpty())
				{
					tmpFromSet.add("DUAL");
				}

				/*---------------------------------------------------------*/

				query = new SelectObj().addSelectPart(tmpIdSet)
				                       .addFromPart(tmpFromSet)
				                       .addWherePart(tmpWhereSet)
				                       .toString()
				;

				whereSet1.add(query);

				/*---------------------------------------------------------*/

				query = new StringBuilder().append("(")
				                           .append(String.join(", ", tmpIdSet))
				                           .append(") IN (")
				                           .append(query)
				                           .append(")")
				                           .toString()
				;

				whereSet2.add(query);

				/*---------------------------------------------------------*/

				cnt++;

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/

			if(trivialCase)
			{
				query = String.join(" UNION ", whereSet1);
			}
			else
			{
				query = String.join(((" OR ")), whereSet2);

				query = new SelectObj().addSelectPart(idSet)
				                       .addFromPart("DUAL")
				                       .addWherePart(query)
				                       .toString()
				;
			}

			/*-------------------------------------------------------------*/

			globalWherSet.add(new StringBuilder().append("(")
			                                     .append(String.join(", ", idSet))
			                                     .append(") IN (")
			                                     .append(query)
			                                     .append(")")
			                                     .toString()
			);

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return new Tuple2<>(globalFromSet, globalWherSet);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String getIsolatedExpression(QId mainPrimaryField, List<Resolution> resolutionList, CharSequence expression, int skip, boolean isFieldNameOnly) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* ISOLATE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		Tuple2<Set<String>, Set<String>> tuple = getIsolatedPath(
			mainPrimaryField,
			resolutionList,
			false
		);

		/*-----------------------------------------------------------------*/
		/* ISOLATE EXPRESSION                                              */
		/*-----------------------------------------------------------------*/

		SelectObj query = new SelectObj().addSelectPart(mainPrimaryField.toString(QId.MASK_CATALOG_ENTITY_FIELD))
		                                 .addFromPart(tuple.x)
		                                 .addWherePart(expression)
		                                 .addWherePart(tuple.y)
		;

		/*-----------------------------------------------------------------*/

		expression = new StringBuilder().append(mainPrimaryField.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD))
		                                .append(" IN (")
		                                .append(query)
		                                .append(")")
		;

		/*-----------------------------------------------------------------*/

		return expression.toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<List<String>, List<String>> resolve(QId mainPrimaryField, List<Resolution> resolutionList, List<? extends CharSequence> expressionList, String AMIUser, boolean isAdmin, boolean insert) throws Exception
	{
		final int nb1 = resolutionList.size();
		final int nb2 = expressionList.size();

		if(nb1 != nb2)
		{
			throw new Exception("internal error");
		}

		/*-----------------------------------------------------------------*/
		/* GROUP FIELDS                                                    */
		/*-----------------------------------------------------------------*/

		String field;

		Resolution resolution;
		CharSequence expression;

		SchemaSingleton.Column column;

		Tuple2<List<CharSequence>, List<Resolution>> tuple;

		Map<String, Tuple2<List<CharSequence>, List<Resolution>>> entries = new LinkedHashMap<>();

		for(int i = 0; i < nb1; i++)
		{
			resolution = resolutionList.get(i);
			expression = expressionList.get(i);

			column = resolution.getColumn();

			/*-------------------------------------------------------------*/

			/**/ if(column.adminOnly)
			{
				if(isAdmin == false)
				{
					throw new Exception("user `" + AMIUser + "` not allow to modify admin-only field " + new QId(column, false).toString());
				}
			}
			else if(column.crypted)
			{
				if(isAdmin == false)
				{
					throw new Exception("user `" + AMIUser + "` not allow to modify crypted field " + new QId(column, false).toString());
				}

				expression = /* NOT FOR SQL EXPRESSION */
				             Utility.textToSqlVal(SecuritySingleton.encrypt(Utility.sqlValToText(expression.toString())))
				             /* NOT FOR SQL EXPRESSION */
				;
			}

			/**/

			else if(column.created
			        ||
			        column.createdBy
			        ||
			        column.modified
			        ||
			        column.modifiedBy
			 ) {
				continue;
			}

			/*-------------------------------------------------------------*/

			if(resolution.getMaxPathLen() > 0)
			{
				for(FrgnKeys path: resolution.getPaths())
				{
					/*-----------------------------------------------------*/

					field = path.get(0).fkColumn;

					/*-----------------------------------------------------*/

					tuple = entries.get(field);

					if(tuple == null)
					{
						entries.put(field, tuple = new Tuple2<>(
							new ArrayList<>(),
							new ArrayList<>()
						));
					}

					tuple.x.add(expression);
					tuple.y.add(resolution);

					/*-----------------------------------------------------*/
				}
			}
			else
			{
				/*---------------------------------------------------------*/

				field = resolution.getColumn().name;

				/*---------------------------------------------------------*/

				tuple = entries.get(field);

				if(tuple == null)
				{
					entries.put(field, tuple = new Tuple2<>(
						new ArrayList<>(),
						new ArrayList<>()
					));
				}

				tuple.x.add(expression);
				tuple.y.add(resolution);

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/
		}

		System.out.println(entries);

		/*-----------------------------------------------------------------*/
		/* ISOLATE EXPRESSIONS                                             */
		/*-----------------------------------------------------------------*/

		List<String> X = new ArrayList<>();
		List<String> Y = new ArrayList<>();

		for(Map.Entry<String, Tuple2<List<CharSequence>, List<Resolution>>> entry: entries.entrySet())
		{
			field = entry.getKey();
			tuple = entry.getValue();

			expression = String.join(" AND ", tuple.x);

			if(tuple.y.get(0).getMaxPathLen() > 0)
			{
				/* TODO */
				/* TODO */
				/* TODO */
			}
			else
			{
				X.add(Utility.textToSqlId(field));

				Y.add(expression.toString());
			}
		}

		/*-----------------------------------------------------------------*/
		/* FILL RESERVED FIELDS                                            */
		/*-----------------------------------------------------------------*/

		for(SchemaSingleton.Column tmp: SchemaSingleton.getEntityInfo(SchemaSingleton.internalCatalogToExternalCatalog_noException(mainPrimaryField.getCatalog(), null), mainPrimaryField.getEntity()).values())
		{
			if(tmp.created && insert) {
				X.add(Utility.textToSqlId(tmp.name)); Y.add("CURRENT_TIMESTAMP");
			}

			if(tmp.createdBy && insert) {
				X.add(Utility.textToSqlId(tmp.name)); Y.add(Utility.textToSqlVal(AMIUser));
			}

			if(tmp.modified) {
				X.add(Utility.textToSqlId(tmp.name)); Y.add("CURRENT_TIMESTAMP");
			}

			if(tmp.modifiedBy) {
				X.add(Utility.textToSqlId(tmp.name)); Y.add(Utility.textToSqlVal(AMIUser));
			}
		}

		/*-----------------------------------------------------------------*/

		return new Tuple2<List<String>, List<String>>(X, Y);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
