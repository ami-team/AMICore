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

	private static final int IN_SELECT_PART = (1 << 0);
	private static final int IN_INSERT_PART = (1 << 1);
	private static final int IN_UPDATE_PART = (1 << 2);
	private static final int IN_FUNCTION = (1 << 3);
	private static final int IS_MODIF_STM = (1 << 4);

	/*---------------------------------------------------------------------*/

	private final String m_externalCatalog;
	private final String m_internalCatalog;
	private final String m_entity;
	private final String m_primaryKey;

	/*---------------------------------------------------------------------*/

	private final String m_AMIUser;
	private final boolean m_isAdmin;

	/*---------------------------------------------------------------------*/

	private final List<Resolution> m_globalResolutionList = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	private MQLToSQL(String externalCatalog, String internalCatalog, String entity, String AMIUser, boolean isAdmin) throws Exception
	{
		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column primaryKey = SchemaSingleton.getPrimaryKey(externalCatalog, entity);

		m_externalCatalog = primaryKey.externalCatalog;
		m_internalCatalog = primaryKey.internalCatalog;
		m_entity = primaryKey.table;
		m_primaryKey = primaryKey.name;

		/*-----------------------------------------------------------------*/

		m_AMIUser = AMIUser;
		m_isAdmin = isAdmin;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String externalCatalog, String entity, String AMIUser, boolean isAdmin, String query) throws Exception
	{
		return parse(externalCatalog, SchemaSingleton.externalCatalogToInternalCatalog(externalCatalog), entity, AMIUser, isAdmin, query);
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String externalCatalog, String internalCatalog, String entity, String AMIUser, boolean isAdmin, String query) throws Exception
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
			result = new MQLToSQL(externalCatalog, internalCatalog, entity, AMIUser, isAdmin).visitSelectStatement(mqlQueryContext.m_select).toString();
		}
		else if(mqlQueryContext.m_insert != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity, AMIUser, isAdmin).visitInsertStatement(mqlQueryContext.m_insert).toString();
		}
		else if(mqlQueryContext.m_update != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity, AMIUser, isAdmin).visitUpdateStatement(mqlQueryContext.m_update).toString();
		}
		else if(mqlQueryContext.m_delete != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity, AMIUser, isAdmin).visitDeleteStatement(mqlQueryContext.m_delete).toString();
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
		SelectObj result = new SelectObj();

		StringBuilder extra = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.m_columns != null)
		{
			result.setDistinct(context.m_distinct != null);

			result.addSelectPart(visitColumnList(context.m_columns, m_globalResolutionList, IN_SELECT_PART));
		}

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(visitExpressionOr(context.m_expression, m_globalResolutionList, 0));
		}

		/*-----------------------------------------------------------------*/

		if(context.m_orderBy != null)
		{
			extra.append(" ORDER BY ").append(AutoJoinSingleton.resolve(m_externalCatalog, m_entity, context.m_orderBy.getText()).getInternalQId().toString(QId.MASK_CATALOG_ENTITY_FIELD));

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

		for(Resolution resolution: m_globalResolutionList)
		{
			result.addFromPart(resolution.getInternalQId().toString(QId.MASK_CATALOG_ENTITY));

			if(resolution.getMaxPathLen() > 0)
			{
				result.addWherePart("(" + resolution.getPaths().stream().map(x -> "(" + x.stream().map(y -> y.toString()).collect(Collectors.joining(" AND ")) + ")" ).collect(Collectors.joining(" OR ")) + ")");
			}
		}

		return result.toStringBuilder(extra);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitInsertStatement(MQLParser.InsertStatementContext context) throws Exception
	{
		InsertObj result = new InsertObj();

		/*-----------------------------------------------------------------*/

		Tuple2<List<String>, List<String>> tuple = Helper.resolve(
			m_externalCatalog, m_entity, m_primaryKey,
			visitQIdTuple       (context.m_qIds       , null, IN_INSERT_PART),
			visitExpressionTuple(context.m_expressions, null, IN_INSERT_PART),
			m_AMIUser,
			m_isAdmin,
			true
		);

		/*-----------------------------------------------------------------*/

		result.addInsertPart(new QId(m_internalCatalog, m_entity, null).toStringBuilder(QId.MASK_ENTITY))
		      .addFieldValuePart(tuple.x, tuple.y)
		;

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitUpdateStatement(MQLParser.UpdateStatementContext context) throws Exception
	{
		UpdateObj result = new UpdateObj();

		/*-----------------------------------------------------------------*/

		Tuple2<List<String>, List<String>> tuple = Helper.resolve(
			m_externalCatalog, m_entity, m_primaryKey,
			visitQIdTuple       (context.m_qIds       , null, IN_UPDATE_PART),
			visitExpressionTuple(context.m_expressions, null, IN_UPDATE_PART),
			m_AMIUser,
			m_isAdmin,
			false
		);

		/*-----------------------------------------------------------------*/

		result.addUpdatePart(new QId(m_internalCatalog, m_entity, null).toStringBuilder(QId.MASK_ENTITY))
		      .addFieldValuePart(tuple.x, tuple.y)
		;

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(visitExpressionOr(context.m_expression, null, IS_MODIF_STM));
		}

		/*-----------------------------------------------------------------*/

		return null; //result.addWherePart(m_globalJoinSet).toStringBuilder(); TODO

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitDeleteStatement(MQLParser.DeleteStatementContext context) throws Exception
	{
		DeleteObj result = new DeleteObj();

		/*-----------------------------------------------------------------*/

		result.addDeletePart(new QId(m_internalCatalog, m_entity, null).toString(QId.MASK_ENTITY));

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(visitExpressionOr(context.m_expression, null, IS_MODIF_STM));
		}

		/*-----------------------------------------------------------------*/

		return null; //result.addWherePart(m_globalJoinSet).toStringBuilder(); TODO

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLParser.ColumnListContext context, List<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.AColumnContext child: context.m_columns)
		{
			result.add(visitAColumn(child, resolutionList, mask).toString());
		}

		return new StringBuilder(String.join(",", result));
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

			/**/ if(child instanceof MQLParser.ExpressionAddSubContext)
			{
				result.append(visitExpressionAddSub((MQLParser.ExpressionAddSubContext) child, resolutionList, mask));
			}
			else if(child instanceof MQLParser.LiteralTupleContext)
			{
				result.append(visitLiteralTuple((MQLParser.LiteralTupleContext) child, resolutionList, mask));
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

		StringBuilder result = visitExpressionOr(context.m_isoExpression, tmpResolutionList, mask & ~IS_MODIF_STM);

		/*-----------------------------------------------------------------*/

		result = new StringBuilder(Helper.isolate(
			m_externalCatalog, m_internalCatalog, m_entity, m_primaryKey,
			tmpResolutionList,
			result,
			(mask & IS_MODIF_STM) != 0
		));

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

		QId qid = QId.visitQId(context, QId.Type.FIELD, QId.Type.FIELD);

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
			result.add(AutoJoinSingleton.resolve(m_externalCatalog, m_entity, qId, ConfigSingleton.getProperty("maxPathLength", 4)));
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
