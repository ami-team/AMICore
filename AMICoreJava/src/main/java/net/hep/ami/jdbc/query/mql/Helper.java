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

	private static int s_cnt = 0;

	/*---------------------------------------------------------------------*/

	private Helper() {}

	/*---------------------------------------------------------------------*/

	public static StringBuilder isolate(String stdInternalCatalog, String stdEntity, String stdPrimaryKey, Set<QId> globalFromSet, Set<String> globalJoinSet, List<Resolution> resolutionList, int skip, @Nullable StringBuilder expression, boolean isSelectPart, boolean isModifStm) throws Exception
	{
		/*-----------------------------------------------------------------*/

		QId mainPrimarykeyQId = new QId(stdInternalCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		Set<String> localJoinList = new LinkedHashSet<>();

		/*-----------------------------------------------------------------*/

		Set<QId> localFromSet = new LinkedHashSet<>();
		for(Resolution resolution: resolutionList) 
		{
			String tmpExternalCatalog = resolution.getExternalQId().getCatalog();
			String tmpInternalCatalog = resolution.getInternalQId().getCatalog();
			String tmpEntity = resolution.getExternalQId().getEntity();

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

				Set<String> tmpJoinList = new LinkedHashSet<>();

				/*---------------------------------------------------------*/

				Set<QId> tmpFromList = new LinkedHashSet<>();
				for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
				{
					int cnt = 0;

					/*-----------------------------------------------------*/

					Set<SchemaSingleton.FrgnKey> tmpWhereList = new LinkedHashSet<>();
					for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
					{
						if(cnt++ < skip)
						{
							continue;
						}

						tmpFromList.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null));

						tmpFromList.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null));

						tmpWhereList.add(frgnKey);
					}

					/*-----------------------------------------------------*/

					if(tmpWhereList.isEmpty() == false)
					{
						SchemaSingleton.Column localTablePrimaryKey = SchemaSingleton.getPrimaryKey(
							tmpExternalCatalog,
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

		if(isSelectPart)
		{
			if(false || localJoinList.isEmpty() == false)
			{
				globalJoinSet.add(String.join(" AND ", localJoinList));
			}
		}
		else
		{
			if(isModifStm || localJoinList.isEmpty() == false)
			{
				result = new StringBuilder();

				SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
				                                 .addFromPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY))
				                                 .addFromPart(localFromSet)
				                                 .addWherePart(expression)
				                                 .addWherePart(localJoinList)
				;

				if(isModifStm)
				{
/*					String url = CatalogSingleton.getTuple(SchemaSingleton.internalCatalogToExternalCatalog(stdInternalCatalog)).t;

					if(url.toLowerCase().startsWith( "jdbc:mysql" )
					   ||
					   url.toLowerCase().startsWith("jdbc:mariadb")
					 ) {
						result.append(mainPrimarykeyQId.toString(QId.MASK_FIELD))
						      .append(" IN (SELECT ").append(mainPrimarykeyQId.toString(QId.MASK_FIELD)).append(" FROM (")
						      .append(query)
						      .append(") AS T").append(s_cnt++).append(")");
						;
					}
					else
*/					{
						result.append(mainPrimarykeyQId.toString(QId.MASK_FIELD))
						      .append(" IN (")
						      .append(query)
						      .append(")")
						;
					}
				}
				else
				{
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

	public static Tuple2<List<StringBuilder>, List<StringBuilder>> resolve(String stdExternalCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, List<StringBuilder> expressionList, String AMIUser, boolean insert) throws Exception
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

		String field;

		QId foreignKey;
		QId primaryKey;

		Resolution resolution;
		StringBuilder expression;

		SchemaSingleton.Column column;

		Tuple2<StringBuilder, List<Resolution>> tuple;

		Map<String, Tuple2<StringBuilder, List<Resolution>>> entries = new LinkedHashMap<>();

		for(int i = 0; i < nb1; i++)
		{
			resolution = resolutionList.get(i);
			expression = expressionList.get(i);

			column = resolution.getColumn();

			/*-------------------------------------------------------------*/

			/**/ if(column.crypted)
			{
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
			 ) {
				continue;
			}

			/*-------------------------------------------------------------*/

			if(resolution.getMaxPathLen() == 0)
			{
				field = resolution.getExternalQId().toString(QId.MASK_FIELD);
			}
			else
			{
				SchemaSingleton.FrgnKey frgnKey = resolution.getPaths().get(0).get(0);

				foreignKey = new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);
				primaryKey = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

				expression = new StringBuilder().append(resolution.getInternalQId().toStringBuilder(QId.MASK_CATALOG_ENTITY_FIELD))
				                                .append(" = ")
				                                .append(expression)
				;

				field = foreignKey.toString(QId.MASK_FIELD);
			}

			/*-------------------------------------------------------------*/

			tuple = entries.get(field);

			if(entries.containsKey(field) == false)
			{
				entries.put(field, tuple = new Tuple2<>(new StringBuilder(), new ArrayList<>()));

				tuple.x.append(((("")))).append(expression);
			}
			else
			{
				tuple.x.append(" AND ").append(expression);
			}

			tuple.y.add(resolution);

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		List<StringBuilder> X = new ArrayList<>();
		List<StringBuilder> Y = new ArrayList<>();

		for(Map.Entry<String, Tuple2<StringBuilder, List<Resolution>>> entry: entries.entrySet())
		{
			field = entry.getKey();
			tuple = entry.getValue();

			if(tuple.y.get(0).getMaxPathLen() > 0)
			{
				/*---------------------------------------------------------*/

				SchemaSingleton.FrgnKey frgnKey = tuple.y.get(0).getPaths().get(0).get(0);

				foreignKey = new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, frgnKey.fkColumn);
				primaryKey = new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn);

				/*---------------------------------------------------------*/

				StringBuilder where = Helper.isolate(
					frgnKey.pkInternalCatalog, frgnKey.pkTable, frgnKey.pkColumn,
					null, null,
					tuple.y,
					1, /* skip 1 join */
					tuple.x,
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

				X.add(new StringBuilder(Utility.textToSqlId(field)));

				Y.add(expression);
			}
			else
			{
				X.add(new StringBuilder(Utility.textToSqlId(field)));

				Y.add(tuple.x);
			}
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		for(SchemaSingleton.Column tmp: SchemaSingleton.getColumns(stdExternalCatalog, stdEntity).values())
		{
			if(tmp.created && insert) {
				X.add(new StringBuilder(Utility.textToSqlId(tmp.name))); Y.add(new StringBuilder("CURRENT_TIMESTAMP"));
			}

			if(tmp.createdBy && insert) {
				X.add(new StringBuilder(Utility.textToSqlId(tmp.name))); Y.add(new StringBuilder(Utility.textToSqlVal(AMIUser)));
			}

			if(tmp.modified) {
				X.add(new StringBuilder(Utility.textToSqlId(tmp.name))); Y.add(new StringBuilder("CURRENT_TIMESTAMP"));
			}

			if(tmp.modifiedBy) {
				X.add(new StringBuilder(Utility.textToSqlId(tmp.name))); Y.add(new StringBuilder(Utility.textToSqlVal(AMIUser)));
			}
		}

		/*-----------------------------------------------------------------*/

		return new Tuple2<List<StringBuilder>, List<StringBuilder>>(X, Y);
	}

	/*---------------------------------------------------------------------*/
}
