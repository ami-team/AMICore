package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.jdbc.reflexion.*;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/

	private static final int NO_STAR = (1 << 0);
	private static final int STAR_TO_ID = (1 << 1);
	private static final int IS_MODIF_STM = (1 << 2);

	/*---------------------------------------------------------------------*/

	private final QId m_primaryKey;

	private final String m_catalog;
	private final String m_entity;

	private final String m_AMIUser;
	private final boolean m_isAdmin;

	/*---------------------------------------------------------------------*/

	private final List<Resolution> m_resolutionList = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	private MQLToSQL(String catalog, String entity, String AMIUser, boolean isAdmin) throws Exception
	{
		m_primaryKey = new QId(SchemaSingleton.getPrimaryKey(catalog, entity), true);

		m_catalog = catalog;
		m_entity = entity;

		m_AMIUser = AMIUser;
		m_isAdmin = isAdmin;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String externalCatalog, String entity, String AMIUser, boolean isAdmin, String query) throws Exception
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
			result = new MQLToSQL(externalCatalog, entity, AMIUser, isAdmin).visitSelectStatement(mqlQueryContext.m_select).toString();
		}
		else if(mqlQueryContext.m_insert != null) {
			result = new MQLToSQL(externalCatalog, entity, AMIUser, isAdmin).visitInsertStatement(mqlQueryContext.m_insert).toString();
		}
		else if(mqlQueryContext.m_update != null) {
			result = new MQLToSQL(externalCatalog, entity, AMIUser, isAdmin).visitUpdateStatement(mqlQueryContext.m_update).toString();
		}
		else if(mqlQueryContext.m_delete != null) {
			result = new MQLToSQL(externalCatalog, entity, AMIUser, isAdmin).visitDeleteStatement(mqlQueryContext.m_delete).toString();
		}
		else {
			result = ";";
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
		SelectObj result = new SelectObj();

		StringBuilder extra = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.setDistinct(context.m_distinct != null);

		/*-----------------------------------------------------------------*/

		if(context.m_columns != null)
		{
			result.addSelectPart(visitColumnList(context.m_columns, m_resolutionList, 0));
		}

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(visitExpressionOr(context.m_expression, m_resolutionList, 0));
		}

		/*-----------------------------------------------------------------*/

		if(context.m_groupBy != null)
		{
			extra.append(" GROUP BY ").append(visitQIdList(context.m_groupBy, m_resolutionList, 0));
		}

		/*-----------------------------------------------------------------*/

		if(context.m_orderBy != null)
		{
			extra.append(" ORDER BY ").append(visitQIdList(context.m_orderBy, m_resolutionList, 0));

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

		Tuple2<Set<String>, Set<String>> tuple = Helper.getIsolatedPath(m_catalog, m_primaryKey, m_resolutionList, 0, false);

		return result.addFromPart(tuple.x)
		             .addWherePart(tuple.y)
		             .toStringBuilder(extra)
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitInsertStatement(MQLParser.InsertStatementContext context) throws Exception
	{
		InsertObj result = new InsertObj();

		/*-----------------------------------------------------------------*/

		Tuple2<List<String>, List<String>> tuple = Helper.resolve(
			m_catalog,
			m_primaryKey,
			visitQIdTuple       (context.m_qIds       , null, IS_MODIF_STM),
			visitExpressionTuple(context.m_expressions, null, IS_MODIF_STM),
			m_AMIUser,
			m_isAdmin,
			true
		);

		/*-----------------------------------------------------------------*/

		result.addInsertPart(m_primaryKey.toStringBuilder(QId.MASK_ENTITY))
		      .addFieldValuePart(tuple.x, tuple.y)
		;

		/*-----------------------------------------------------------------*/

		System.out.println(result);

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitUpdateStatement(MQLParser.UpdateStatementContext context) throws Exception
	{
		UpdateObj result = new UpdateObj();

		/*-----------------------------------------------------------------*/

		Tuple2<List<String>, List<String>> tuple = Helper.resolve(
			m_catalog,
			m_primaryKey,
			visitQIdTuple       (context.m_qIds       , null, IS_MODIF_STM),
			visitExpressionTuple(context.m_expressions, null, IS_MODIF_STM),
			m_AMIUser,
			m_isAdmin,
			false
		);

		/*-----------------------------------------------------------------*/

		result.addUpdatePart(m_primaryKey.toStringBuilder(QId.MASK_ENTITY))
		      .addFieldValuePart(tuple.x, tuple.y)
		;

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(Helper.getIsolatedExpression(
				m_catalog,
				m_primaryKey,
				m_resolutionList,
				visitExpressionOr(context.m_expression, m_resolutionList, IS_MODIF_STM),
				0,
				false,
				true,
				true
			));
		}

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitDeleteStatement(MQLParser.DeleteStatementContext context) throws Exception
	{
		DeleteObj result = new DeleteObj();

		/*-----------------------------------------------------------------*/

		result.addDeletePart(m_primaryKey.toString(QId.MASK_ENTITY));

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(Helper.getIsolatedExpression(
				m_catalog,
				m_primaryKey,
				m_resolutionList,
				visitExpressionOr(context.m_expression, m_resolutionList, IS_MODIF_STM),
				0,
				false,
				true,
				true
			));
		}

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLParser.ColumnListContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.AColumnContext child: context.m_columns)
		{
			result.add(visitAColumn(child, resolutionList, mask).toString());
		}

		return new StringBuilder(String.join(", ", result));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitAColumn(MQLParser.AColumnContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		/*-----------------------------------------------------------------*/

		StringBuilder result = visitExpressionOr(context.m_expression, resolutionList, mask);

		/*-----------------------------------------------------------------*/

		if(context.m_alias != null)
		{
			result.append(" AS ").append(Utility.textToSqlId(context.m_alias.getText()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitQIdList(MQLParser.QIdListContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.AQIdContext child: context.m_aQIds)
		{
			result.add(visitAQId(child, resolutionList, mask).toString());
		}

		return new StringBuilder(String.join(", ", result));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitAQId(MQLParser.AQIdContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return visitQId(context.m_qId, resolutionList, mask | NO_STAR).get(0).getInternalQId().toStringBuilder(QId.MASK_CATALOG_ENTITY_FIELD);
	}

	/*---------------------------------------------------------------------*/

	private List<StringBuilder> visitExpressionTuple(MQLParser.ExpressionTupleContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<StringBuilder> result = new ArrayList<>();

		for(MQLParser.ExpressionOrContext child: context.m_expressions)
		{
			result.add(visitExpressionOr(child, resolutionList, mask));
		}

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

	private List<StringBuilder> visitLiteralTuple(MQLParser.LiteralTupleContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<StringBuilder> result = new ArrayList<>();

		for(MQLParser.LiteralContext child: context.m_literals)
		{
			result.add(visitLiteral(child, resolutionList, mask));
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

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionNotAddSubContext)
			{
				result.append(visitExpressionAddSub((MQLParser.ExpressionNotAddSubContext) child, resolutionList, mask));
			}
			else if(child instanceof MQLParser.LiteralTupleContext)
			{
				result.append("(")
				      .append(String.join(", ", visitLiteralTuple((MQLParser.LiteralTupleContext) child, resolutionList, mask)))
				      .append(")")
				;
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

	private StringBuilder visitExpressionAddSub(MQLParser.ExpressionNotAddSubContext context, List<Resolution> resolutionList, int mask) throws Exception
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

			/**/ if(child instanceof MQLParser.ExpressionPlusMinusContext)
			{
				result.append(visitExpressionNotPlusMinus((MQLParser.ExpressionPlusMinusContext) child, resolutionList, mask));
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

	private StringBuilder visitExpressionNotPlusMinus(MQLParser.ExpressionPlusMinusContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.m_operator != null)
		{
			result.append(context.m_operator.getText());
		}

		/*-----------------------------------------------------------------*/

		ParseTree child = context.getChild(0);

		/**/ if(child instanceof MQLParser.ExpressionStdGroupContext)
		{
			result.append(visitExpressionStdGroup((MQLParser.ExpressionStdGroupContext) child, resolutionList, mask));
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

	private StringBuilder visitExpressionStdGroup(MQLParser.ExpressionStdGroupContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		/*-----------------------------------------------------------------*/

		StringBuilder expression = visitExpressionOr(context.m_expression, resolutionList, mask);

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("(")
		                          .append(expression)
		                          .append(")")
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionIsoGroup(MQLParser.ExpressionIsoGroupContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		/*-----------------------------------------------------------------*/

		List<Resolution> tmpResolutionList = new ArrayList<>();

		StringBuilder expression = visitExpressionOr(context.m_expression, tmpResolutionList, mask & ~IS_MODIF_STM);

		expression = new StringBuilder(Helper.getIsolatedExpression(
			m_catalog,
			m_primaryKey,
			tmpResolutionList,
			expression,
			0,
			false,
			(mask & IS_MODIF_STM) != 0,
			false
		));

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("(")
		                          .append(expression)
		                          .append(")")
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder().append(context.m_functionName.getText())
		                          .append("(")
		                          .append(String.join(", ", visitExpressionTuple(context.m_expressions, resolutionList, mask | STAR_TO_ID)))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLParser.ExpressionQIdContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<Resolution> list;

		resolutionList.addAll(list = visitQId(context.m_qId, resolutionList, mask));

		return new StringBuilder(list.stream().map(x -> x.getInternalQId().toString((mask & IS_MODIF_STM) == 0 ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_ENTITY_FIELD)).collect(Collectors.joining(", ")));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return visitLiteral(context.m_literal, resolutionList, mask);
	}

	/*---------------------------------------------------------------------*/

	private List<Resolution> visitQId(MQLParser.QIdContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		/*-----------------------------------------------------------------*/

		System.out.println(context.getText());

		QId qid = QId.visitQId(context, QId.Type.FIELD, QId.Type.FIELD);

		/*-----------------------------------------------------------------*/

		String catalogName = (qid.getCatalog() != null) ? qid.getCatalog() : m_catalog;

		String entityName = (qid.getEntity() != null) ? qid.getEntity() : m_entity;

		String fieldName = qid.getField();

		/*-----------------------------------------------------------------*/

		List<QId> list;

		if("*".equals(fieldName))
		{
			/**/ if((mask & NO_STAR) != 0)
			{
				throw new Exception("star identifier is not authorized in `GROUP BY` or `ORDER BY` modifiers");
			}
			else if((mask & STAR_TO_ID) != 0)
			{
				list = Collections.singletonList(new QId(SchemaSingleton.getPrimaryKey(catalogName, entityName), false));
			}
			else
			{
				list = SchemaSingleton.getSortedColumnQId(catalogName, entityName, qid.getConstraints(), m_isAdmin);
			}
		}
		else
		{
			list = Collections.singletonList(qid);
		}

		/*-----------------------------------------------------------------*/

		List<Resolution> result = new ArrayList<>();

		for(QId qId: list)
		{
			System.out.println(qId.toStringBuilder(QId.MASK_CATALOG_ENTITY_FIELD, QId.MASK_CATALOG_ENTITY_FIELD));
			System.out.println(qId.getConstraints());

			result.add(AutoJoinSingleton.resolve(m_catalog, m_entity, qId, ConfigSingleton.getProperty("maxPathLength", 4)));
		}

		System.out.println("----------------");
		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitLiteral(MQLParser.LiteralContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
