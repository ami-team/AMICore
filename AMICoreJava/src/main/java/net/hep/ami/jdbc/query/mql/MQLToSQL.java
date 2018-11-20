package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.parser.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/

	private static final int IN_SELECT = (1 << 0);
	private static final int IN_INSERT = (1 << 1);
	private static final int IN_UPDATE = (1 << 2);
	private static final int IN_DELETE = (1 << 3);
	private static final int IN_GROUP = (1 << 4);
	private static final int IN_ISOGROUP = (1 << 5);
	private static final int IN_FUNCTION = (1 << 6);

	private static final int BERK = (1 << 7);

	/*---------------------------------------------------------------------*/

	private final String m_externalCatalog;
	private final String m_internalCatalog;
	private final String m_entity;
	private final String m_primaryKey;

	/*---------------------------------------------------------------------*/

	private int m_maxPathLength = 4;

	/*---------------------------------------------------------------------*/

	private List<String> m_from = new ArrayList<String>();
	private List<String> m_joins = new ArrayList<String>();

	/*---------------------------------------------------------------------*/

	private MQLToSQL(String externalCatalog, String internalCatalog, String entity) throws Exception
	{
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column primaryKey = SchemaSingleton.getPrimaryKey(externalCatalog, entity);

		m_externalCatalog = primaryKey.externalCatalog;
		m_internalCatalog = primaryKey.internalCatalog;

		m_entity = primaryKey.table;

		m_primaryKey = primaryKey.name;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String catalog, String entity, String query) throws Exception
	{
		return parse(catalog, SchemaSingleton.externalCatalogToInternalCatalog(catalog), entity, query);
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String externalCatalog, String internalCatalog, String entity, String query) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		String result;

		MQLParser.MqlQueryContext mqlQueryContext = parser.mqlQuery();

		/**/ if(mqlQueryContext.m_select != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitSelectStatement(mqlQueryContext.m_select).toString();
		}
		else if(mqlQueryContext.m_insert != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitInsertStatement(mqlQueryContext.m_insert).toString();
		}
		else if(mqlQueryContext.m_update != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitUpdateStatement(mqlQueryContext.m_update).toString();
		}
		else if(mqlQueryContext.m_delete != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitDeleteStatement(mqlQueryContext.m_delete).toString();
		}
		else {
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSelectStatement(MQLParser.SelectStatementContext context) throws Exception
	{
		Query query = new Query();

		StringBuilder extra = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.m_columns != null)
		{
			query.setDistinct(context.m_distinct != null);

			query.addSelectPart(visitColumnList(context.m_columns, null, IN_SELECT).toString());
		}

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			query.addWherePart("(" + visitExpressionOr(context.m_expression, null, 0).toString() + ")");
		}

		if(m_joins.isEmpty() == false)
		{
			query.addWherePart(String.join(" AND ", m_joins));
		}

		/*-----------------------------------------------------------------*/

		if(context.m_orderBy != null)
		{
			extra.append(" ORDER BY ").append(AutoJoinSingleton.resolve(m_externalCatalog, m_entity, context.m_orderBy.getText()).getQId().toString(QId.MASK_CATALOG_ENTITY_FIELD));

			if(context.m_orderWay != null)
			{
				extra.append(" ").append(context.m_orderWay.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		if(context.m_limit != null)
		{
			extra.append(" LIMIT ").append(context.m_limit.getText());

			if(context.m_offset != null)
			{
				extra.append(" OFFSET ").append(context.m_offset.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		// DOUBLE BERK
		// DOUBLE BERK
		// DOUBLE BERK		-> utiliser un SET
		// DOUBLE BERK
		// DOUBLE BERK

		final String catalogEntity1 = new QId(m_internalCatalog, m_entity, null).toString(QId.MASK_CATALOG_ENTITY);

		query.addFromPart(catalogEntity1);

		for(String catalogEntity2: m_from)
		{
			if(catalogEntity2.equalsIgnoreCase(catalogEntity1) == false)
			{
				query.addFromPart(catalogEntity2);
			}
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder(query.toString(extra.toString()));

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitInsertStatement(MQLParser.InsertStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		List<QId> fieldsInDefaultEntity = new ArrayList<>();
		List<String> valuesInDefaultEntity = new ArrayList<>();

		List<QId> fieldsNotInDefaultEntity = new ArrayList<>();
		List<String> valuesNotInDefaultEntity = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		List<Resolution> resolutions = visitQIdTuple(context.m_qIds, null, IN_INSERT);
		List<String> expressions = visitExpressionTuple(context.m_expressions, null, IN_INSERT);

		/*-----------------------------------------------------------------*/

		Map<String, SchemaSingleton.Column> columnsInDefaultEntity = SchemaSingleton.getColumns(m_externalCatalog, m_entity);

		Map<String, SchemaSingleton.FrgnKeys> frgnKeysInDefaultEntity = SchemaSingleton.getForwardFKs(m_externalCatalog, m_entity);

		/*-----------------------------------------------------------------*/

		List<QId> frgnKeysAlreadyTreated = new ArrayList<>();

		for(int i = 0; i < resolutions.size(); i++)
		{
			QId tmpQId = resolutions.get(i).getQId();

			if(m_internalCatalog.equalsIgnoreCase(tmpQId.getCatalog())
			   &&
			   m_entity.equalsIgnoreCase(tmpQId.getEntity())
			   &&
			   columnsInDefaultEntity.get(tmpQId.getField()) != null
			 ) {
				if(frgnKeysInDefaultEntity.containsKey(tmpQId.getField())) // A OPTIMISER
				{
					frgnKeysAlreadyTreated.add(tmpQId);
				}

				fieldsInDefaultEntity.add(resolutions.get(i).getQId());
				valuesInDefaultEntity.add(expressions.get(i));
			}
			else
			{
				fieldsNotInDefaultEntity.add(resolutions.get(i).getQId());
				valuesNotInDefaultEntity.add(expressions.get(i));
			}
		}

		/*-----------------------------------------------------------------*/

		for(SchemaSingleton.FrgnKeys frgnKeys: frgnKeysInDefaultEntity.values()) 
		{
			QId fk = new QId(frgnKeys.get(0).fkInternalCatalog, frgnKeys.get(0).fkTable, frgnKeys.get(0).fkColumn);
			QId pk = new QId(frgnKeys.get(0).pkInternalCatalog, frgnKeys.get(0).pkTable, frgnKeys.get(0).pkColumn);

			if(!frgnKeysAlreadyTreated.contains(fk))
			{
				List<String> tmpWhere = new ArrayList<String>();

				for(int i = 0; i < fieldsNotInDefaultEntity.size(); i++)
				{
					boolean pass = false;

					List<QId> tmpQIds = fieldsNotInDefaultEntity.get(i).getConstraints();
					if(tmpQIds.size() == 0)
					{
						pass = true;
					}
					else 
					{
						for(int cpt = 0; cpt < tmpQIds.size(); cpt++)
						{
							if(tmpQIds.get(cpt).matches(fk))
							{
								pass = true;
								break;
							}
						}
					}

					if(pass)
					{
						tmpWhere.add(fieldsNotInDefaultEntity.get(i).toString(QId.MASK_CATALOG_ENTITY_FIELD) + " = " + valuesNotInDefaultEntity.get(i));
					}
				}

				if(!tmpWhere.isEmpty())
				{
					String tmpMQL = "SELECT " + pk.toString() + " WHERE " + String.join(" AND ", tmpWhere);

					String tmpSQL = "(" + MQLToSQL.parse(frgnKeys.get(0).pkInternalCatalog, frgnKeys.get(0).pkTable, tmpMQL).replaceAll("`" + frgnKeys.get(0).pkInternalCatalog + "`.", "") + ")";

					fieldsInDefaultEntity.add(fk);
					valuesInDefaultEntity.add(tmpSQL);
				}
			}
		}

		StringBuilder tmpFieldsOfDefaultEntity = new StringBuilder();
		for(int i = 0; i < fieldsInDefaultEntity.size(); i++)
		{
			if(i > 0)
				tmpFieldsOfDefaultEntity.append(",");
			tmpFieldsOfDefaultEntity.append("`"+fieldsInDefaultEntity.get(i).getField()+"`");
		}
		result.append("INSERT INTO ")
		      .append(new QId(m_internalCatalog, m_entity, null).toString(QId.MASK_ENTITY))
		      .append(" (")
		      .append(tmpFieldsOfDefaultEntity)
		      .append(") VALUES (")
		      .append(String.join(", ", valuesInDefaultEntity))
		      .append(")")
		;

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitUpdateStatement(MQLParser.UpdateStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();
		StringBuilder tmpSet = new StringBuilder();

		List<Resolution> tmpFields = visitQIdTuple(context.m_qIds, null, IN_UPDATE);
		List<String> tmpExpressions = visitExpressionTuple(context.m_expressions, null, IN_UPDATE);

		for(int i = 0; i < tmpFields.size(); i++)
		{
			tmpSet.append("`" + tmpFields.get(i).getQId().getField() + "`= " + tmpExpressions.get(i));
		}

		result.append("UPDATE ")
		      .append(new QId(m_internalCatalog, m_entity, null).toStringBuilder(QId.MASK_ENTITY))
		      .append(" SET ")
		      .append(String.join(", ", tmpSet))
		;

		if(context.expression != null)
		{
			Query query = new Query().addWherePart("(" + visitExpressionOr(context.expression, null, BERK).toString() + ")");

			if(m_joins.isEmpty() == false)
			{
				query.addWherePart(String.join(" AND ", m_joins));
			}

			result.append(" WHERE ").append(query.getWherePart());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitDeleteStatement(MQLParser.DeleteStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("DELETE FROM ").append(new QId(m_internalCatalog, m_entity, null).toString(QId.MASK_ENTITY));

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			Query query = new Query().addWherePart("(" + visitExpressionOr(context.m_expression, null, IN_DELETE | BERK).toString() + ")");

			if(m_joins.isEmpty() == false)
			{
				query.addWherePart(String.join(" AND ", m_joins));
			}

			result.append(" WHERE ").append(query.getWherePart());
		}

		/*-----------------------------------------------------------------*/

		return result;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLParser.ColumnListContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.AColumnContext child: context.m_columns)
		{
			result.add(visitAColumnExpression(child, resolutionList, mask).toString());
		}

		return new StringBuilder(String.join(",", result));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitAColumnExpression(MQLParser.AColumnContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		/*-----------------------------------------------------------------*/

		StringBuilder result = visitExpressionOr(context.m_expression, resolutionList, mask);

		/*-----------------------------------------------------------------*/

		if(context.m_alias != null)
		{
			result.append(" AS " + new QId(context.m_alias.getText()).toString(QId.MASK_FIELD));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private List<Resolution> visitQIdTuple(MQLParser.QIdTupleContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		List<Resolution> result = new ArrayList<>();

		for(MQLParser.QIdContext child: context.m_qIds)
		{
			result.addAll(visitQId(child, resolutionList, mask));
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private List<String> visitExpressionTuple(MQLParser.ExpressionTupleContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.ExpressionOrContext child: context.m_expressions)
		{
			result.add(visitExpressionOr(child, resolutionList, mask).toString());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLParser.ExpressionOrContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionAndContext)
			{
				result.append(visitExpressionAnd((MQLParser.ExpressionAndContext) child, resolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" OR ");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAnd(MQLParser.ExpressionAndContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionCompContext)
			{
				result.append(visitExpressionComp((MQLParser.ExpressionCompContext) child, resolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" AND ");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionComp(MQLParser.ExpressionCompContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		Set<Resolution> tmpResolutionList = (mask & IN_ISOGROUP) == 0 ? new HashSet<>() : resolutionList ;

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionAddSubContext)
			{
				result.append(visitExpressionAddSub((MQLParser.ExpressionAddSubContext) child, tmpResolutionList, mask));
			}
			else if(child instanceof MQLParser.LiteralTupleContext)
			{
				result.append(visitLiteralTuple((MQLParser.LiteralTupleContext) child, tmpResolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" ")
				      .append(child.getText(  ))
				      .append(" ")
				;
			}
		}

		/*-----------------------------------------------------------------*/

		if(nb > 1 && (mask & IN_INSERT) == 0 && (mask & IN_UPDATE) == 0 && (mask & IN_ISOGROUP) == 0 && (mask & IN_FUNCTION) == 0)
		{
			StringBuilder localResult = new StringBuilder();
			StringBuilder localJoins = new StringBuilder();

			/*-------------------------------------------------------------*/

			QId qid1 = new QId(m_internalCatalog, m_entity, m_primaryKey);

			/*-------------------------------------------------------------*/

			if((mask & BERK) != 0)
			{
				Query query = new Query().addSelectPart(qid1.toString(QId.MASK_ENTITY_FIELD))
				                         .addFromPart(qid1.toString(QId.MASK_ENTITY))
				;

				localResult.append(qid1.toString(QId.MASK_CATALOG_ENTITY_FIELD))
				           .append(" IN (SELECT * FROM (")
				           .append(query)
				;
			}
			else
			{
				Query query = new Query().addSelectPart(qid1.toString(QId.MASK_CATALOG_ENTITY_FIELD))
				                         .addFromPart(qid1.toString(QId.MASK_CATALOG_ENTITY))
				;

				localResult.append(qid1.toString(QId.MASK_CATALOG_ENTITY_FIELD))
				           .append(" IN (")
				           .append(query)
				;
			}

			/*-------------------------------------------------------------*/

			boolean needAND = false;

			for(Resolution pathList: tmpResolutionList) 
			{
				String localCatalogName = pathList.getQId().getCatalog();
				String localTableName = pathList.getQId().getEntity();

				/*---------------------------------------------------------*/

				if(localCatalogName.equalsIgnoreCase(m_internalCatalog) == false
				   ||
				   localTableName.equalsIgnoreCase(m_entity) == false
				 ) {
					String localCatalogTable = pathList.getQId().toString(QId.MASK_CATALOG_ENTITY);

					localResult.append(", ")
					           .append(localCatalogTable)
					;

					if((mask & IN_SELECT) != 0 && m_from.contains(localCatalogTable) == false)
					{
						m_from.add(localCatalogTable);
					}

					if(needAND)
					{
						localJoins.append(" AND ");
					}

					List<String> localFromList = new ArrayList<String>();

					List<List<SchemaSingleton.FrgnKey>> paths = pathList.getPaths();

					boolean needOR = false;

					for(List<SchemaSingleton.FrgnKey> list: paths) 
					{
						List<String> localWhereList = new ArrayList<String>();

						for(SchemaSingleton.FrgnKey frgnKey: list)
						{
							if(localFromList.contains(frgnKey.fkTable) == false)
							{
								localFromList.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null).toString(QId.MASK_CATALOG_ENTITY));
							}

							if(localFromList.contains(frgnKey.pkTable) == false)
							{
								localFromList.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null).toString(QId.MASK_CATALOG_ENTITY));
							}

							localWhereList.add(frgnKey.toString());
						}

						if(!localWhereList.isEmpty())
						{
							if(needOR)
							{
								localJoins.append(" OR ");
							}

							SchemaSingleton.Column localTablePrimaryKey = SchemaSingleton.getPrimaryKey(
								SchemaSingleton.internalCatalogToExternalCatalog_noException(localCatalogName, ""), localTableName
							);

							QId qid2 = new QId(localTablePrimaryKey.internalCatalog, localTablePrimaryKey.table, localTablePrimaryKey.name);

							Query query2 = new Query().addSelectPart(qid2.toString(QId.MASK_CATALOG_ENTITY_FIELD))
							                          .addSelectPart(qid1.toString(QId.MASK_CATALOG_ENTITY_FIELD))
							                          .addFromPart(localFromList)
							                          .addWherePart(localWhereList)
							;

							localJoins.append("(")
							          .append("(")
							          .append(qid2)
							          .append(", ")
							          .append(qid1)
							          .append(") IN (")
							          .append(query2)
							          .append(")")
							          .append(")")
							;
						}

						needOR = true;
					}
					needAND = true;
				}

				/*---------------------------------------------------------*/
			}

			localResult.append(" WHERE ")
			           .append(result)
			;

			if(!localJoins.toString().isEmpty())
			{
				if((mask & IN_SELECT) != 0)
				{
					m_joins.add("(" + localJoins.toString() + ")");
				}

				localResult.append(" AND ")
				           .append("(" + localJoins + ")")
				;
			}

			if((mask & BERK) != 0)
			{
				localResult.append(") AS T)");
			}
			else
			{
				localResult.append(")");
			}

			if((mask & IN_SELECT) == 0)
			{
				result = localResult;
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAddSub(MQLParser.ExpressionAddSubContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionMulDivContext)
			{
				result.append(visitExpressionMulDiv((MQLParser.ExpressionMulDivContext) child, resolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" ")
				      .append(child.getText())
				      .append(" ")
				;
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionMulDiv(MQLParser.ExpressionMulDivContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionNotPlusMinusContext)
			{
				result.append(visitExpressionNotPlusMinus((MQLParser.ExpressionNotPlusMinusContext) child, resolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" ")
				      .append(child.getText())
				      .append(" ")
				;
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionNotPlusMinus(MQLParser.ExpressionNotPlusMinusContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.m_operator != null)
		{
			result.append(context.m_operator.getText());
		}

		/*-----------------------------------------------------------------*/

		mask &= ~BERK;

		ParseTree child = context.getChild(0);

		/**/ if(child instanceof MQLParser.ExpressionGroupContext)
		{
			result.append(visitExpressionGroup((MQLParser.ExpressionGroupContext) child, resolutionList, mask));
		}
		else if(child instanceof MQLParser.ExpressionIsoGroupContext)
		{
			result.append(visitExpressionIsoGroup((MQLParser.ExpressionIsoGroupContext) child, resolutionList, mask));
		}
		else if(child instanceof MQLParser.ExpressionFunctionContext)
		{
			result.append(visitExpressionFunction((MQLParser.ExpressionFunctionContext) child, resolutionList, mask));
		}
		else if(child instanceof MQLParser.ExpressionQIdContext)
		{
			result.append(visitExpressionQId((MQLParser.ExpressionQIdContext) child, resolutionList, mask));
		}
		else if(child instanceof MQLParser.ExpressionLiteralContext)
		{
			result.append(visitExpressionLiteral((MQLParser.ExpressionLiteralContext) child, resolutionList, mask));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(visitExpressionOr(context.m_expression, resolutionList, mask | IN_GROUP))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionIsoGroup(MQLParser.ExpressionIsoGroupContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		Set<Resolution> tmpResolutionList = new HashSet<>();

		StringBuilder isoResult = new StringBuilder().append("(")
                .append(visitExpressionOr(context.m_isoExpression, tmpResolutionList, mask | IN_ISOGROUP))
                .append(")")
                ;

		StringBuilder localResult = new StringBuilder();
		StringBuilder localJoins = new StringBuilder();
		localResult.append("`" + m_entity + "`.`" + m_primaryKey + "` IN (SELECT `" + m_entity + "`.`" + m_primaryKey + "` FROM `" + m_entity + "` ");
		boolean needAND = false;
		ArrayList<String> testCatalogTable = new ArrayList<String>();
		for(Resolution pathList: tmpResolutionList) 
		{

			String localTableName = pathList.getQId().getEntity();
			String localCatalogName = pathList.getQId().getCatalog();
			if (!testCatalogTable.contains(localCatalogName + ":" + localTableName))
			{
				testCatalogTable.add(localCatalogName + ":" + localTableName);
				if(!localTableName.equalsIgnoreCase(m_entity))
				{
					localResult.append(", `" + localTableName + "` ");
					if((mask & IN_SELECT) != 0 && !m_from.contains(localTableName))
					{
						m_from.add(localTableName);
					}

					String localTablePrimaryKey = SchemaSingleton.getPrimaryKey(
							SchemaSingleton.internalCatalogToExternalCatalog_noException(localCatalogName, ""), localTableName
							).name;

					if(needAND)
					{
						localJoins.append("  AND ");
					}
					List<String> localFromList = new ArrayList<String>();
					List<List<SchemaSingleton.FrgnKey>> paths = pathList.getPaths();
					boolean needOR = false;

					for(List<SchemaSingleton.FrgnKey> list: paths) 
					{
						List<String> localWhereList = new ArrayList<String>();
						for(SchemaSingleton.FrgnKey frgnKey: list)
						{
							if(!localFromList.contains(frgnKey.fkTable))
							{
								localFromList.add(frgnKey.fkTable);
							}
							if(!localFromList.contains(frgnKey.pkTable))
							{
								localFromList.add(frgnKey.pkTable);
							}

							localWhereList.add(frgnKey.toString());
						}
						if(!localWhereList.isEmpty())
						{
							if(needOR)
							{
								localJoins.append(" OR ");
							}

							localJoins.append("(");
							localJoins.append("(`" + localTableName + "`.`" + localTablePrimaryKey + "`, `" + m_entity + "`.`" + m_primaryKey + "`) IN ");
							localJoins.append("(SELECT `" + localTableName + "`.`" + localTablePrimaryKey + "`, " + "`" + m_entity + "`" + ".`" + m_primaryKey + "` "
									+ "FROM `"+ String.join("`,`", localFromList) + "` "
									+ "WHERE "+ String.join(" AND ", localWhereList) + ")");
							localJoins.append(")");
						}

						needOR = true;
					}
					needAND = true;
				}
			}
		}

		localResult.append(" WHERE ");

		localResult.append(isoResult.toString());

		if(!localJoins.toString().isEmpty())
		{
			if((mask & IN_SELECT) != 0)
			{
				m_joins.add("(" + localJoins.toString() + ")");
			}

			localResult.append(" AND ");
			localResult.append("(" + localJoins + ")");
		}
		localResult.append(")");
		if((mask & IN_SELECT) == 0)
		{
			result = localResult;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder(context.m_functionName.getText());

		result.append("(");

		if(context.m_param1 != null)
		{
			result.append( "" ).append(visitExpressionOr(context.m_param1, resolutionList, mask | IN_FUNCTION));

			if(context.m_param2 != null)
			{
				result.append(", ").append(visitExpressionOr(context.m_param2, resolutionList, mask | IN_FUNCTION));
			}
		}

		result.append(")");

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLParser.ExpressionQIdContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		/*----------------------------------------------------------------*/

		List<Resolution> list = visitQId(context.m_qId, resolutionList, mask);

		/*----------------------------------------------------------------*/

		resolutionList.addAll(list);

		/*----------------------------------------------------------------*/

		return new StringBuilder(list.stream().map(x -> x.getQId().toString(QId.MASK_CATALOG_ENTITY_FIELD)).collect(Collectors.joining(", ")));

		/*----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		return visitLiteral(context.m_literal, resolutionList, mask);
	}

	/*---------------------------------------------------------------------*/

	private List<Resolution> visitQId(MQLParser.QIdContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		List<Resolution> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		QId qid = new QId(context, QId.Type.FIELD, QId.Type.FIELD);

		/*-----------------------------------------------------------------*/

		String catalogName = (qid.getCatalog() != null) ? qid.getCatalog() : m_externalCatalog;

		String entityName = (qid.getEntity() != null) ? qid.getEntity() : /*-*/m_entity/*-*/;

		String fieldName = qid.getField();

		/*-----------------------------------------------------------------*/

		List<QId> list;

		if("*".equals(fieldName))
		{
			if((mask & IN_FUNCTION) != 0)
			{
				SchemaSingleton.Column primaryKey = SchemaSingleton.getPrimaryKey(catalogName, entityName);

				list = Arrays.asList(qid.setCatalog(primaryKey.internalCatalog).setEntity(primaryKey.table).setField(primaryKey.name));
			}
			else
			{
				list = new ArrayList<>();

				for(String fieldNAME: SchemaSingleton.getColumnNames(catalogName, entityName))
				{
					list.add(new QId(catalogName, entityName, fieldNAME, qid.getConstraints()));
				}
			}
		}
		else
		{
			list = Arrays.asList(qid);
		}

		/*-----------------------------------------------------------------*/

		for(QId qId: list)
		{
			result.add(AutoJoinSingleton.resolve(m_externalCatalog, m_entity, qId, m_maxPathLength));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitLiteralTuple(MQLParser.LiteralTupleContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(context.m_literals.stream().map(x -> x.getText()).collect(Collectors.joining(", ")))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitLiteral(MQLParser.LiteralContext context, Set<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
