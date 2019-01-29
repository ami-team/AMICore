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

			idSet.add(stdPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD));

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
						fkSet.add(qId.toString(QId.MASK_CATALOG_ENTITY_FIELD));
					}
					else {
						tmpFromSet.add(tmp);
					}

					/*-----------------------------------------------------*/

					qId = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

					tmp = qId.toString(QId.MASK_CATALOG_ENTITY);

					if(globalFromSet.contains(tmp)) {
						idSet.add(qId.toString(QId.MASK_CATALOG_ENTITY_FIELD));
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

		QId mainPrimarykeyQId = new QId(stdInternalCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		Set<String> localJoinList = new LinkedHashSet<>();

		for(Resolution resolution: resolutionList) 
		{
			if(resolution.getMaxPathLen() == 0)
			{
				continue;
			}

			/*-------------------------------------------------------------*/

			Set<QId> tmpFromSet = new LinkedHashSet<>();

			Set<String> tmpJoinSet = new LinkedHashSet<>();

			for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
			{
				/*---------------------------------------------------------*/

				Set<SchemaSingleton.FrgnKey> tmpWhereList = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
				{
					tmpFromSet.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null));

					tmpFromSet.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null));

					tmpWhereList.add(frgnKey);
				}

				/*---------------------------------------------------------*/

				if(tmpWhereList.isEmpty() == false)
				{
					/*-----------------------------------------------------*/

					SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
					                                 .addFromPart(tmpFromSet.stream().map(x -> x.toString()).collect(Collectors.toList()))
					                                 .addWherePart(expression)
					                                 .addWherePart(tmpWhereList.stream().map(x -> x.toString()).collect(Collectors.toList()))

					;

					/*-----------------------------------------------------*/

					tmpJoinSet.add(
						new StringBuilder().append(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
						                   .append(" IN (")
						                   .append(query)
						                   .append(")")
						                   .toString()
					);

					/*-----------------------------------------------------*/
				}

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

			SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
			                                 .addFromPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY))
			                                 .addWherePart(localJoinList)
			;

			/*-------------------------------------------------------------*/

			expression = new StringBuilder().append(mainPrimarykeyQId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD))
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
		return new Tuple2<List<String>, List<String>>(null, null);
	}

	/*---------------------------------------------------------------------*/
}
