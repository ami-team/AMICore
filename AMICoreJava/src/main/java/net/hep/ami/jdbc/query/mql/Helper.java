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

	public static StringBuilder isolate(String stdCatalog, String stdEntity, String stdPrimaryKey, Set<QId> globalFromSet, Set<String> globalJoinSet, List<Resolution> resolutionList, @Nullable StringBuilder expression, boolean isSelectPart, boolean isModifStm) throws Exception
	{
		/*-----------------------------------------------------------------*/

		QId mainPrimarykeyQId = new QId(stdCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		Set<String> localJoinList = new LinkedHashSet<>();

		/*-----------------------------------------------------------------*/

		Set<QId> localFromSet = new LinkedHashSet<>();
		for(Resolution resolution: resolutionList) 
		{
			String tmpCatalog = resolution.getExternalQId().getCatalog();
			String tmpEntity = resolution.getExternalQId().getEntity();

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

				if(tmpJoinList.isEmpty() == false)
				{
					localJoinList.add("(" + String.join(" OR ", tmpJoinList) + ")");
				}

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		StringBuilder result = expression;

		/*-----------------------------------------------------------------*/

		if(localJoinList.isEmpty() == false)
		{
			if(isSelectPart)
			{
				globalJoinSet.add(String.join(" AND ", localJoinList));
			}
			else
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

	public static Tuple2<List<StringBuilder>, List<StringBuilder>> resolve(String stdCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, List<StringBuilder> expressionList, String AMIUser, boolean insert) throws Exception
	{
		final int nb1 = resolutionList.size();
		final int nb2 = expressionList.size();

		if(nb1 != nb2)
		{
			throw new Exception("internal error");
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

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
				expression = new StringBuilder(
					/* NOT FOR SQL EXPRESSION */
					Utility.textToSqlVal(SecuritySingleton.encrypt(Utility.sqlValToText(expression.toString())))
					/* NOT FOR SQL EXPRESSION */
				);
			}

			/**/

			else if(column.created
			        ||
			        column.createdBy
			        ||
			        column.modified
			        ||
			        column.modifiedBy
			        ||
			        "self".equals(column.externalCatalog) && (
			        	"created".equals(column.name)
			        	||
			        	"createdBy".equals(column.name)
			        	||
			        	"modified".equals(column.name)
			        	||
			        	"modifiedBy".equals(column.name)
			        )
			 ) {
				continue;
			}

			/*-------------------------------------------------------------*/

			if(resolution.getMaxPathLen() > 0)
			{
				/*---------------------------------------------------------*/

				SchemaSingleton.FrgnKey frgnKey = resolution.getPaths().get(0).get(0);

				QId foreignKey = new QId(frgnKey.fkExternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);
				QId primaryKey = new QId(frgnKey.pkExternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

				StringBuilder comparison = new StringBuilder().append(resolution.getExternalQId().toStringBuilder(QId.MASK_CATALOG_ENTITY_FIELD))
				                                              .append(" = ")
				                                              .append(expression)
				;

				SelectObj query = new SelectObj().addSelectPart(primaryKey.toStringBuilder(QId.MASK_CATALOG_ENTITY_FIELD))
				                                 .addWherePart(comparison)
				;

				/*---------------------------------------------------------*/

				expression = new StringBuilder().append("(").append(MQLToSQL.parse(frgnKey.pkExternalCatalog, frgnKey.pkInternalCatalog, frgnKey.pkTable, query.toString())).append(")");

				/*---------------------------------------------------------*/

				X.add(foreignKey.toStringBuilder(QId.MASK_FIELD));

				Y.add(expression);
			}
			else
			{
				X.add(resolution.getExternalQId().toStringBuilder(QId.MASK_FIELD));

				Y.add(expression);
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		String createdName = null;
		String createdByName = null;
		String modifiedName = null;
		String modifiedByName = null;

		for(SchemaSingleton.Column tmp: SchemaSingleton.getColumns(stdCatalog, stdEntity).values())
		{
			if(tmp.created) {
				createdName = tmp.name;
			}

			if(tmp.createdBy) {
				createdByName = tmp.name;
			}

			if(tmp.modified) {
				modifiedName = tmp.name;
			}

			if(tmp.modifiedBy) {
				modifiedByName = tmp.name;
			}
		}

		/*-----------------------------------------------------------------*/

		if(insert)
		{
			if(createdName != null) {
				X.add(new StringBuilder(createdName)); Y.add(new StringBuilder("CURRENT_TIMESTAMP"));
			}

			if(createdByName != null) {
				X.add(new StringBuilder(createdByName)); Y.add(new StringBuilder(/*-*/ AMIUser /*-*/));
			}
		}

		if(true)
		{
			if(modifiedName != null) {
				X.add(new StringBuilder(modifiedName)); Y.add(new StringBuilder("CURRENT_TIMESTAMP"));
			}

			if(modifiedByName != null) {
				X.add(new StringBuilder(modifiedByName)); Y.add(new StringBuilder(/*-*/ AMIUser /*-*/));
			}
		}

		/*-----------------------------------------------------------------*/

		return new Tuple2<List<StringBuilder>, List<StringBuilder>>(X, Y);
	}

	/*---------------------------------------------------------------------*/
}
