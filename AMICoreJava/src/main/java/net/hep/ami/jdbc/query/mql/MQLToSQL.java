package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.obj.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.parser.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/

	private static final int IN_SELECT_PART = (1 << 0);
	private static final int IN_INSERT_PART = (1 << 1);
	private static final int IN_UPDATE_PART = (1 << 2);
	private static final int IN_ISO_GROUP = (1 << 3);
	private static final int IN_FUNCTION = (1 << 4);
	private static final int IS_MODIF_STM = (1 << 5);

	/*---------------------------------------------------------------------*/

	private final String m_externalCatalog;
	private final String m_internalCatalog;
	private final String m_entity;
	private final String m_primaryKey;

	/*---------------------------------------------------------------------*/

	private int m_maxPathLength = 4;

	/*---------------------------------------------------------------------*/

	private Set<QId> m_globalFromSet = new LinkedHashSet<QId>();
	private Set<String> m_globalJoinSet = new LinkedHashSet<String>();

	/*---------------------------------------------------------------------*/

	private MQLToSQL(String externalCatalog, String internalCatalog, String entity) throws Exception
	{
		SchemaSingleton.Column primaryKey = SchemaSingleton.getPrimaryKey(externalCatalog, entity);

		m_externalCatalog = primaryKey.externalCatalog;
		m_internalCatalog = primaryKey.internalCatalog;

		m_entity = primaryKey.table;

		m_primaryKey = primaryKey.name;
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
		SelectObj query = new SelectObj();

		StringBuilder extra = new StringBuilder();

		m_globalFromSet.add(new QId(m_internalCatalog, m_entity, null));

		/*-----------------------------------------------------------------*/

		if(context.m_columns != null)
		{
			query.setDistinct(context.m_distinct != null);

			query.addSelectPart(visitColumnList(context.m_columns, null, IN_SELECT_PART).toString());
		}

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			query.addWherePart(visitExpressionOr(context.m_expression, null, 0).toString());
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

		return new StringBuilder(query.addFromPart(m_globalFromSet).addWherePart(m_globalJoinSet).toString(extra));

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

		List<Resolution> resolutions = visitQIdTuple(context.m_qIds, null, IN_INSERT_PART);
		List<String> expressions = visitExpressionTuple(context.m_expressions, null, IN_INSERT_PART);

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
		UpdateObj query = new UpdateObj();

		/*-----------------------------------------------------------------*/

		query.addUpdatePart(new QId(m_internalCatalog, m_entity, null).toStringBuilder(QId.MASK_ENTITY));

		/*-----------------------------------------------------------------*/

		query.addSetFieldPart(
			visitQIdTuple(context.m_qIds, null, IN_UPDATE_PART)
					.stream().map(x -> x.getQId().toString(QId.MASK_ENTITY)).collect(Collectors.toList()),
			visitExpressionTuple(context.m_expressions, null, IN_UPDATE_PART)
		);

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			query.addWherePart(visitExpressionOr(context.m_expression, null, IS_MODIF_STM).toString());
		}

		/*-----------------------------------------------------------------*/

		return query.addWherePart(m_globalJoinSet).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitDeleteStatement(MQLParser.DeleteStatementContext context) throws Exception
	{
		DeleteObj query = new DeleteObj();

		/*-----------------------------------------------------------------*/

		query.addDeletePart(new QId(m_internalCatalog, m_entity, null).toString(QId.MASK_ENTITY));

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			query.addWherePart(visitExpressionOr(context.m_expression, null, IS_MODIF_STM).toString());
		}

		/*-----------------------------------------------------------------*/

		return query.addWherePart(m_globalJoinSet).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLParser.ColumnListContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.AColumnContext child: context.m_columns)
		{
			result.add(visitAColumnExpression(child, resolutionList, mask).toString());
		}

		return new StringBuilder(String.join(",", result));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitAColumnExpression(MQLParser.AColumnContext context, List<Resolution> resolutionList, int mask) throws Exception
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

	private List<Resolution> visitQIdTuple(MQLParser.QIdTupleContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<Resolution> result = new ArrayList<>();

		for(MQLParser.QIdContext child: context.m_qIds)
		{
			result.addAll(visitQId(child, resolutionList, mask));
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private List<String> visitExpressionTuple(MQLParser.ExpressionTupleContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.ExpressionOrContext child: context.m_expressions)
		{
			result.add(visitExpressionOr(child, resolutionList, mask).toString());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLParser.ExpressionOrContext context, List<Resolution> resolutionList, int mask) throws Exception
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

	private StringBuilder visitExpressionAnd(MQLParser.ExpressionAndContext context, List<Resolution> resolutionList, int mask) throws Exception
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
				result.append(visitExpressionComp((MQLParser.ExpressionCompContext ) child, resolutionList, mask));
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

	private StringBuilder visitExpressionComp(MQLParser.ExpressionCompContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		List<Resolution> tmpResolutionList = (mask & IN_ISO_GROUP) != 0 ? resolutionList : new ArrayList<>();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionAddSubContext)
			{
				result.append(visitExpressionAddSub((MQLParser.ExpressionAddSubContext) child, tmpResolutionList, mask & ~IS_MODIF_STM));
			}
			else if(child instanceof MQLParser.LiteralTupleContext)
			{
				result.append(visitLiteralTuple((MQLParser.LiteralTupleContext) child, tmpResolutionList, mask & ~IS_MODIF_STM));
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

		if((mask & IN_INSERT_PART) == 0 && (mask & IN_UPDATE_PART) == 0 && (mask & IN_ISO_GROUP) == 0)
		{
			result = Isolation.isolate(
				m_internalCatalog, m_entity, m_primaryKey,
				m_globalFromSet, m_globalJoinSet,
				tmpResolutionList,
				result,
				nb > 1,
				(mask & IN_SELECT_PART) != 0,
				(mask &  IS_MODIF_STM ) != 0
			);
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAddSub(MQLParser.ExpressionAddSubContext context, List<Resolution> resolutionList, int mask) throws Exception
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

	private StringBuilder visitExpressionMulDiv(MQLParser.ExpressionMulDivContext context, List<Resolution> resolutionList, int mask) throws Exception
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

	private StringBuilder visitExpressionNotPlusMinus(MQLParser.ExpressionNotPlusMinusContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.m_operator != null)
		{
			result.append(context.m_operator.getText());
		}

		/*-----------------------------------------------------------------*/

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

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(visitExpressionOr(context.m_expression, resolutionList, mask))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionIsoGroup(MQLParser.ExpressionIsoGroupContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<Resolution> tmpResolutionList = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		StringBuilder result = visitExpressionOr(context.m_isoExpression, tmpResolutionList, mask | IN_ISO_GROUP);

		/*-----------------------------------------------------------------*/

		if((mask & IN_INSERT_PART) == 0 && (mask & IN_UPDATE_PART) == 0)
		{
			result = Isolation.isolate(
				m_internalCatalog, m_entity, m_primaryKey,
				m_globalFromSet, m_globalJoinSet,
				tmpResolutionList,
				result,
				true,
				(mask & IN_SELECT_PART) != 0,
				(mask &  IS_MODIF_STM ) != 0
			);
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("(")
		                          .append(result)
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context, List<Resolution> resolutionList, int mask) throws Exception
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

	private StringBuilder visitExpressionQId(MQLParser.ExpressionQIdContext context, List<Resolution> resolutionList, int mask) throws Exception
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

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return visitLiteral(context.m_literal, resolutionList, mask);
	}

	/*---------------------------------------------------------------------*/

	private List<Resolution> visitQId(MQLParser.QIdContext context, List<Resolution> resolutionList, int mask) throws Exception
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

	private StringBuilder visitLiteralTuple(MQLParser.LiteralTupleContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(context.m_literals.stream().map(x -> x.getText()).collect(Collectors.joining(", ")))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitLiteral(MQLParser.LiteralContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
