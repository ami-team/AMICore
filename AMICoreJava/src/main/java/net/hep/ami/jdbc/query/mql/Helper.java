package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;

public class Helper
{
	/*---------------------------------------------------------------------*/

	private Helper() {}

	/*---------------------------------------------------------------------*/

	public static Set<String> isolatePath(String stdInternalCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, boolean isFieldNameOnly) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* BUILD GLOBAL FROM SET                                           */
		/*-----------------------------------------------------------------*/

		Set<String> globalFromSet = resolutionList.stream().map(x -> x.getInternalQId().toString(QId.MASK_CATALOG_ENTITY)).collect(Collectors.toSet());

		/*-----------------------------------------------------------------*/
		/* BUILD JOINS                                                     */
		/*-----------------------------------------------------------------*/

		QId stdPrimarykeyQId = new QId(stdInternalCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		QId qId;

		String tmp;

		Set<String> result = new LinkedHashSet<>();

		for(Resolution resolution: resolutionList) 
		{
			if(resolution.getMaxPathLen() == 0)
			{
				continue;
			}

			/*-------------------------------------------------------------*/

			Set<String> fkSet = new LinkedHashSet<>();
			Set<String> idSet = new LinkedHashSet<>();

			Set<String> joinSet = new LinkedHashSet<>();

			idSet.add(stdPrimarykeyQId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD));

			for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
			{
				/*---------------------------------------------------------*/

				Set<String> tmpFromSet = new LinkedHashSet<>();

				Set<String> tmpWhereSet = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
				{
					/*-----------------------------------------------------*/

					qId = new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);

					tmp = qId.toString(QId.MASK_CATALOG_ENTITY);

					if(globalFromSet.contains(tmp)) {
						fkSet.add(qId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD));
					}
					else {
						tmpFromSet.add(tmp);
					}

					/*-----------------------------------------------------*/

					qId = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

					tmp = qId.toString(QId.MASK_CATALOG_ENTITY);

					if(globalFromSet.contains(tmp)) {
						idSet.add(qId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD));
					}
					else {
						tmpFromSet.add(tmp);
					}

					/*-----------------------------------------------------*/

					tmpWhereSet.add(frgnKey.toString());

					/*-----------------------------------------------------*/
				}

				/*---------------------------------------------------------*/

				joinSet.add(new SelectObj().addSelectPart(idSet)
				                           .addFromPart(tmpFromSet)
				                           .addWherePart(tmpWhereSet)
				                           .toString()
				);

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/

			result.add(new StringBuilder().append("(")
			                              .append(String.join(", ", idSet))
			                              .append(") IN (")
			                              .append(String.join(" UNION ", joinSet))
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

	public static String isolateExpression(String stdInternalCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, CharSequence expression, boolean isFieldNameOnly) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* BUILD JOINS                                                     */
		/*-----------------------------------------------------------------*/

		QId stdPrimarykeyQId = new QId(stdInternalCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		Set<String> localJoinList = new LinkedHashSet<>();

		for(Resolution resolution: resolutionList) 
		{
			if(resolution.getMaxPathLen() == 0)
			{
				continue;
			}

			/*-------------------------------------------------------------*/

			Set<String> tmpFromSet = new LinkedHashSet<>();

			Set<String> tmpJoinSet = new LinkedHashSet<>();

			for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
			{
				/*---------------------------------------------------------*/

				Set<String> tmpWhereList = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
				{
					tmpFromSet.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null).toString(QId.MASK_CATALOG_ENTITY_FIELD));

					tmpFromSet.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null).toString(QId.MASK_CATALOG_ENTITY_FIELD));

					tmpWhereList.add(frgnKey.toString());
				}

				/*---------------------------------------------------------*/

				SelectObj query = new SelectObj().addSelectPart(stdPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
				                                 .addFromPart(tmpFromSet)
				                                 .addWherePart(expression)
				                                 .addWherePart(tmpWhereList)

				;

				/*---------------------------------------------------------*/

				tmpJoinSet.add(
					new StringBuilder().append(stdPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
					                   .append(" IN (")
					                   .append(query)
					                   .append(")")
					                   .toString()
				);

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/

			if(tmpJoinSet.isEmpty() == false)
			{
				localJoinList.add("(" + String.join(" OR ", tmpJoinSet) + ")");
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/* ISOLATE EXPRESSION                                              */
		/*-----------------------------------------------------------------*/

		if(localJoinList.isEmpty() == false)
		{
			/*-------------------------------------------------------------*/

			SelectObj query = new SelectObj().addSelectPart(stdPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
			                                 .addFromPart(stdPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY))
			                                 .addWherePart(localJoinList)
			;

			/*-------------------------------------------------------------*/

			expression = new StringBuilder().append(stdPrimarykeyQId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD))
			                                .append(" IN (")
			                                .append(query)
			                                .append(")")
			;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return expression.toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<List<String>, List<String>> resolve(String stdExternalCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, List<? extends CharSequence> expressionList, String AMIUser, boolean isAdmin, boolean insert) throws Exception
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

		return new Tuple2<List<String>, List<String>>(X, Y);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
