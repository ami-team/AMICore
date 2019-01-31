package net.hep.ami.jdbc.query.mql;

import java.util.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;

public class Helper
{
	/*---------------------------------------------------------------------*/

	private Helper() {}

	/*---------------------------------------------------------------------*/

	public static Set<String> getFromSetFromResolutionList(QId mainEntityQId, List<Resolution> resolutionList)
	{
		Set<String> result = new LinkedHashSet<>();

		/*-----------------------------------------------------------------*/

		result.add(mainEntityQId.toString(QId.MASK_CATALOG_ENTITY));

		for(Resolution resolution: resolutionList)
		{
			result.add(resolution.getInternalQId().toString(QId.MASK_CATALOG_ENTITY));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> getIsolatedPath(QId stdPrimaryKeyQId, List<Resolution> resolutionList, boolean isFieldNameOnly) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* BUILD GLOBAL FROM SET                                           */
		/*-----------------------------------------------------------------*/

		Set<String> globalFromSet = getFromSetFromResolutionList(stdPrimaryKeyQId, resolutionList);

		/*-----------------------------------------------------------------*/
		/* ISOLATE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		QId qId;

		String tmp;

		SelectObj query;

		Set<String> result = new LinkedHashSet<>();

		for(Resolution resolution: resolutionList) 
		{
			if(resolution.getMaxPathLen() == 0)
			{
				continue;
			}

			/*-------------------------------------------------------------*/

			Set<String> idSet = new LinkedHashSet<>();

			Set<String> whereSet = new LinkedHashSet<>();

			idSet.add(stdPrimaryKeyQId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD));

			for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
			{
				/*---------------------------------------------------------*/

				Set<String> tmpIdSet = new LinkedHashSet<>();

				Set<String> tmpFromSet = new LinkedHashSet<>();

				Set<String> tmpWhereSet = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
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
						idSet.add(tmp);
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
						idSet.add(tmp);
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
				;

				whereSet.add(new StringBuilder().append("(")
				                                .append(String.join(", ", query.getSelectPart()))
				                                .append(") IN (")
				                                .append(query)
				                                .append(")")
				                                .toString()
				);

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/

			query = new SelectObj().addSelectPart(idSet)
			                       .addFromPart("DUAL")
			                       .addWherePart(String.join(" OR ", whereSet))
			;

			result.add(new StringBuilder().append("(")
			                              .append(String.join(", ", idSet))
			                              .append(") IN (")
			                              .append(query)
			                              .append(")")
			                              .toString()
			);

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return result;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String getIsolatedExpression(QId mainPrimarykeyQId, List<Resolution> resolutionList, CharSequence expression, int skip, boolean isFieldNameOnly) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* BUILD GLOBAL FROM SET                                           */
		/*-----------------------------------------------------------------*/

		Set<String> fromSet = getFromSetFromResolutionList(mainPrimarykeyQId, resolutionList);

		/*-----------------------------------------------------------------*/
		/* ISOLATE JOINS                                                   */
		/*-----------------------------------------------------------------*/

		Set<String> whereSet = getIsolatedPath(
			mainPrimarykeyQId,
			resolutionList,
			false
		);

		/*-----------------------------------------------------------------*/
		/* ISOLATE EXPRESSION                                              */
		/*-----------------------------------------------------------------*/

		SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
		                                 .addFromPart(fromSet)
		                                 .addWherePart(expression)
		                                 .addWherePart(whereSet)
		;

		/*-----------------------------------------------------------------*/

		expression = new StringBuilder().append(mainPrimarykeyQId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD))
		                                .append(" IN (")
		                                .append(query)
		                                .append(")")
		;

		/*-----------------------------------------------------------------*/

		return expression.toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<List<String>, List<String>> resolve(QId stdPrimaryKeyQId, List<Resolution> resolutionList, List<? extends CharSequence> expressionList, String AMIUser, boolean isAdmin, boolean insert) throws Exception
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



		/*-----------------------------------------------------------------*/
		/* ISOLATE EXPRESSIONS                                             */
		/*-----------------------------------------------------------------*/

		List<String> X = new ArrayList<>();
		List<String> Y = new ArrayList<>();



		/*-----------------------------------------------------------------*/
		/* FILL RESERVED FIELDS                                            */
		/*-----------------------------------------------------------------*/
/*
		for(SchemaSingleton.Column tmp: SchemaSingleton.getColumns(stdExternalCatalog, stdEntity).values())
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
*/
		/*-----------------------------------------------------------------*/

		return new Tuple2<List<String>, List<String>>(X, Y);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
