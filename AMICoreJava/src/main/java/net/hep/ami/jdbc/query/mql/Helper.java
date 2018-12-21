package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

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

	public static Tuple2<List<StringBuilder>, List<StringBuilder>> resolve(String stdCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, List<StringBuilder> expressionList, String AMIUser) throws Exception
	{
		final int nb1 = resolutionList.size();
		final int nb2 = expressionList.size();

		if(nb1 != nb2)
		{
			throw new Exception("internal error");
		}

		Resolution resolution;
		StringBuilder expression;

		SchemaSingleton.Column column;

		List<StringBuilder> X = new ArrayList<>();
		List<StringBuilder> Y = new ArrayList<>();

		for(int i = 0; i < nb1; i++)
		{
			resolution = resolutionList.get(i);
			expression = expressionList.get(i);

			column = resolution.getColumn();

			/*-------------------------------------------------------------*/

			X.add(resolution.getQId().toStringBuilder(QId.MASK_FIELD));

			/*-------------------------------------------------------------*/

			/**/ if(column.crypted
			        ||
			        "self".equals(column.externalCatalog) && (
						"paramName".equals(column.name)
						||
						"paramValue".equals(column.name)
						||
						"user".equals(column.name)
						||
						"pass".equals(column.name)
						||
						"AMIUser".equals(column.name)
						||
						"AMIPass".equals(column.name)
						||
						"clientDN".equals(column.name)
						||
						"issuerDN".equals(column.name)
			        )
			 ) {
				Y.add(new StringBuilder(Utility.textToSqlVal(SecuritySingleton.encrypt(Utility.sqlValToText(expression.toString())))));
			}

			/**/

			else if(column.created || "self".equals(column.externalCatalog) && "created".equals(column.name))
			{
				Y.add(new StringBuilder("CURRENT_TIMESTAMP"));
			}
			else if(column.createdBy || "self".equals(column.externalCatalog) && "createdBy".equals(column.name))
			{
				Y.add(new StringBuilder(Utility.textToSqlVal(AMIUser)));
			}

			/**/

			else if(column.modified || "self".equals(column.externalCatalog) && "modified".equals(column.name))
			{
				Y.add(new StringBuilder("CURRENT_TIMESTAMP"));
			}
			else if(column.modifiedBy || "self".equals(column.externalCatalog) && "modifiedBy".equals(column.name))
			{
				Y.add(new StringBuilder(Utility.textToSqlVal(AMIUser)));
			}

			/**/

			else
			{
				Y.add(expression);
			}

			/*-------------------------------------------------------------*/
		}

		return new Tuple2<List<StringBuilder>, List<StringBuilder>>(X, Y);
	}

	/*---------------------------------------------------------------------*/
}
