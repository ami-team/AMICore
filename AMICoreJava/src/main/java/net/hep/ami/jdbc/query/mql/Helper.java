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

	private static int s_cnt = 0;

	/*---------------------------------------------------------------------*/

	private Helper() {}

	/*---------------------------------------------------------------------*/

	public static StringBuilder isolate(String stdCatalog, String stdEntity, String stdPrimaryKey, Set<QId> globalFromSet, Set<String> globalJoinSet, List<Resolution> resolutionList, StringBuilder expression, boolean isolationNeeded, boolean isSelectPart, boolean isModifStm) throws Exception
	{
		/*-----------------------------------------------------------------*/

		QId mainPrimarykeyQId = new QId(stdCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		Set<String> localJoinList = new LinkedHashSet<>();

		/*-----------------------------------------------------------------*/

		Set<QId> localFromSet = new LinkedHashSet<>();
		for(Resolution resolution: resolutionList) 
		{
			String tmpCatalog = resolution.getQId().getCatalog();
			String tmpEntity = resolution.getQId().getEntity();

			if(tmpCatalog.equalsIgnoreCase(stdCatalog) == false
			   ||
			   tmpEntity.equalsIgnoreCase(stdEntity) == false
			 ) {
				/*---------------------------------------------------------*/

				QId tmpCatalogEntity = new QId(tmpCatalog, tmpEntity, null);

				localFromSet.add(tmpCatalogEntity);

				if(isSelectPart)
				{
					globalFromSet.add(tmpCatalogEntity);
				}

				/*---------------------------------------------------------*/

				Set<String> tmpJoinList = new LinkedHashSet<>();

				/*---------------------------------------------------------*/

				Set<QId> tmpFromList = new LinkedHashSet<>();
				for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths()) 
				{
					/*-----------------------------------------------------*/

					Set<SchemaSingleton.FrgnKey> tmpWhereList = new LinkedHashSet<>();
					for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
					{
						tmpFromList.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null));

						tmpFromList.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null));

						tmpWhereList.add(frgnKey);
					}

					/*-----------------------------------------------------*/

					if(tmpWhereList.isEmpty() == false)
					{
						SchemaSingleton.Column localTablePrimaryKey = SchemaSingleton.getPrimaryKey(
							SchemaSingleton.internalCatalogToExternalCatalog(tmpCatalog),
							tmpEntity
						);

						QId localPrimarykeyQId = new QId(localTablePrimaryKey.internalCatalog, localTablePrimaryKey.table, localTablePrimaryKey.name);

						SelectObj query2 = new SelectObj().addSelectPart(localPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
						                                  .addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
						                                  .addFromPart(tmpFromList.stream().map(x -> x.toString()).collect(Collectors.toList()))
						                                  .addWherePart(tmpWhereList.stream().map(x -> x.toString()).collect(Collectors.toList()))
						;

						tmpJoinList.add(
							new StringBuilder().append("(")
							                   .append(localPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
							                   .append(", ")
							                   .append(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
							                   .append(") IN (")
							                   .append(query2.toString())
							                   .append(")")
							                   .toString()
						);
					}

					/*-----------------------------------------------------*/
				}

				/*---------------------------------------------------------*/

				localJoinList.add("(" + String.join(" OR ", tmpJoinList) + ")");

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		StringBuilder result = expression;

		/*-----------------------------------------------------------------*/

		if(isSelectPart)
		{
			if(localJoinList.isEmpty() == false)
			{
				globalJoinSet.add(String.join(" AND ", localJoinList));
			}
		}
		else
		{
			if(localJoinList.isEmpty() == false)
			{
				result = new StringBuilder();

				if(isModifStm)
				{
					SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_ENTITY_FIELD))
					                                 .addFromPart(mainPrimarykeyQId.toString(QId.MASK_ENTITY))
					                                 .addFromPart(localFromSet)
					                                 .addWherePart(expression)
					                                 .addWherePart(localJoinList)
					;

					result.append(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
					      .append(" IN (SELECT * FROM (")
					      .append(query)
					      .append(") AS T").append(s_cnt++).append(")");
					;
				}
				else
				{
					SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
					                                 .addFromPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY))
					                                 .addFromPart(localFromSet)
					                                 .addWherePart(expression)
					                                 .addWherePart(localJoinList)
					;

					result.append(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
					      .append(" IN (")
					      .append(query)
					      .append(")")
					;
				}
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<List<StringBuilder>, List<StringBuilder>> resolve(String stdCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, List<StringBuilder> expressionList)
	{
		/* TODO */
		/* TODO */
		/* TODO */

		return new Tuple2<List<StringBuilder>, List<StringBuilder>>(
			resolutionList.stream().map(x -> x.getQId().toStringBuilder(QId.MASK_ENTITY)).collect(Collectors.toList()),
			expressionList
		);
	}

	/*---------------------------------------------------------------------*/
}
