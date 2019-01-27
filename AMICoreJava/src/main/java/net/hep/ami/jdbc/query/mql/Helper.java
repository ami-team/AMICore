package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class Helper
{
	/*---------------------------------------------------------------------*/

	private Helper() {}

	/*---------------------------------------------------------------------*/

	/* globalJoinSet MUST be null for insert or update parts */

	public static String isolate(String stdExternalCatalog, String stdInternalCatalog, String stdEntity, String stdPrimaryKey, Set<QId> globalFromSet, @Nullable Set<String> globalJoinSet, List<Resolution> resolutionList, CharSequence expression, boolean isSelectPart, boolean isModifStm) throws Exception
	{
		/*-----------------------------------------------------------------*/

		boolean isOracle = "jdbc:oracle".equals(CatalogSingleton.getProto(
			stdExternalCatalog
		));

		/*-----------------------------------------------------------------*/
		/* BUILD JOINS                                                     */
		/*-----------------------------------------------------------------*/

		QId mainPrimarykeyQId = new QId(stdInternalCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		Set<QId> localFromSet = new LinkedHashSet<>();

		Set<String> localJoinList = new LinkedHashSet<>();

		localFromSet.add(mainPrimarykeyQId.as(QId.MASK_CATALOG_ENTITY));

		for(Resolution resolution: resolutionList) 
		{
			String tmpExternalCatalog = resolution.getExternalQId().getCatalog();
			String tmpInternalCatalog = resolution.getInternalQId().getCatalog();
			String tmpEntity          = resolution.getExternalQId().getEntity ();

			if(tmpInternalCatalog.equalsIgnoreCase(stdInternalCatalog) == false
			   ||
			   tmpEntity.equalsIgnoreCase(stdEntity) == false
			 ) {
				/*---------------------------------------------------------*/

				QId tmpCatalogEntity = new QId(tmpInternalCatalog, tmpEntity, null);

				localFromSet.add(tmpCatalogEntity);

				if(isSelectPart)
				{
					globalFromSet.add(tmpCatalogEntity);
				}

				/*---------------------------------------------------------*/

				Set<QId> tmpFromSet = new LinkedHashSet<>();

				Set<String> tmpJoinSet = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
				{
					/*-----------------------------------------------------*/

					int cnt = 0;

					Set<SchemaSingleton.FrgnKey> tmpWhereList = new LinkedHashSet<>();

					for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
					{
						if(globalJoinSet == null && cnt++ == 0)
						{
							continue;
						}

						tmpFromSet.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null));

						tmpFromSet.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null));

						tmpWhereList.add(frgnKey);
					}

					/*-----------------------------------------------------*/

					if(tmpWhereList.isEmpty() == false)
					{
						/*-------------------------------------------------*/

						if(isOracle == false)
						{
							tmpFromSet.removeAll(globalFromSet);
						}

						QId localPrimarykeyQId = new QId(SchemaSingleton.getPrimaryKey(
							tmpExternalCatalog,
							tmpEntity
						), true);

						SelectObj query = new SelectObj().addSelectPart(localPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
						                                 .addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
						                                 .addFromPart(tmpFromSet.stream().map(x -> x.toString()).collect(Collectors.toList()))
						                                 .addWherePart(isSelectPart == false ? expression : null)
						                                 .addWherePart(tmpWhereList.stream().map(x -> x.toString()).collect(Collectors.toList()))

						;

						/*-------------------------------------------------*/

						tmpJoinSet.add(
							new StringBuilder().append("(")
							                   .append(localPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
							                   .append(", ")
							                   .append(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
							                   .append(") IN (")
							                   .append(query)
							                   .append(")")
							                   .toString()
						);

						/*-------------------------------------------------*/
					}

					/*-----------------------------------------------------*/
				}

				/*---------------------------------------------------------*/

				if(tmpJoinSet.isEmpty() == false)
				{
					localJoinList.add("(" + String.join(" OR ", tmpJoinSet) + ")");
				}

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/
		/* ISOLATE EXPRESSION                                              */
		/*-----------------------------------------------------------------*/

		if(localJoinList.isEmpty() == false)
		{
			if(isSelectPart)
			{
				globalJoinSet.add(String.join(" AND ", localJoinList));
			}
			else
			{
				/*---------------------------------------------------------*/

				if(isOracle == false)
				{
					localFromSet.removeAll(globalFromSet);
				}

				SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
				                                 .addFromPart(localFromSet)
				                                 .addWherePart(localJoinList)
				;

				/*---------------------------------------------------------*/

				expression = new StringBuilder().append(mainPrimarykeyQId.toString(isModifStm == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD))
				                                .append(" IN (")
				                                .append(query)
				                                .append(")")
				;

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		return expression.toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<List<String>, List<String>> resolve(String stdExternalCatalog, String stdEntity, String stdPrimaryKey, Set<QId> globalFromSet, List<Resolution> resolutionList, List<? extends CharSequence> expressionList, String AMIUser, boolean isAdmin, boolean insert) throws Exception
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

		QId primaryKey;

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
				expression = new StringBuilder().append(resolution.getInternalQId().toStringBuilder(QId.MASK_CATALOG_ENTITY_FIELD))
				                                .append(" = ")
				                                .append(expression)
				;

				for(SchemaSingleton.FrgnKeys path: resolution.getPaths())
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
				/*---------------------------------------------------------*/

				SchemaSingleton.FrgnKey frgnKey = tuple.y.get(0).getPaths().get(0).get(0);

				primaryKey = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

				/*---------------------------------------------------------*/

				String where = Helper.isolate(
					frgnKey.pkExternalCatalog, frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn,
					globalFromSet,
					null,
					tuple.y,
					expression,
					false, false
				);

				SelectObj query = new SelectObj().addSelectPart(primaryKey.toString(QId.MASK_CATALOG_ENTITY_FIELD))
				                                 .addFromPart(primaryKey.toString(QId.MASK_CATALOG_ENTITY))
				                                 .addWherePart(where)
				;

				expression = new StringBuilder().append("(")
				                                .append(query)
				                                .append(")")
				;

				/*---------------------------------------------------------*/

				X.add(Utility.textToSqlId(field));

				Y.add(expression.toString());
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

		/*-----------------------------------------------------------------*/

		return new Tuple2<List<String>, List<String>>(X, Y);
	}

	/*---------------------------------------------------------------------*/
}
