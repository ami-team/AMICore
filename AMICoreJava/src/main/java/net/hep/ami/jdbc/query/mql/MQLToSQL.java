package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.reflexion.*;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

@SuppressWarnings({"unused", "SameParameterValue"})
public class MQLToSQL
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final int NO_STAR = (1 << 0);
	private static final int STAR_TO_ID = (1 << 1);
	private static final int IS_MODIF_STMT = (1 << 2);

	/*----------------------------------------------------------------------------------------------------------------*/

	private final QId m_primaryKey;

	private final String m_catalog;
	private final String m_entity;

	private final String m_AMIUser;
	private final boolean m_isAdmin;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final List<Resolution> m_resolutionList = new ArrayList<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	private MQLToSQL(@NotNull String catalog, @NotNull String entity, @NotNull String AMIUser, boolean isAdmin) throws Exception
	{
		m_primaryKey = new QId(SchemaSingleton.getPrimaryKey(catalog, entity), true);

		m_catalog = catalog;
		m_entity = entity;

		m_AMIUser = AMIUser;
		m_isAdmin = isAdmin;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String parse(@NotNull String externalCatalog, @NotNull String entity, @NotNull String AMIUser, boolean isAdmin, @NotNull String query) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitSelectStatement(@NotNull MQLParser.SelectStatementContext context) throws Exception
	{
		XQLSelect result = new XQLSelect();

		StringBuilder extra = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.setDistinct(context.m_distinct != null);

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_columns != null)
		{
			result.addSelectPart(visitColumnList(context.m_columns, m_resolutionList, 0));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(visitExpressionOr(context.m_expression, m_resolutionList, 0).insert(0, "(").append(")"));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_groupBy != null)
		{
			extra.append(" GROUP BY ").append(visitQIdList(context.m_groupBy, m_resolutionList, 0));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_orderBy != null)
		{
			extra.append(" ORDER BY ").append(visitQIdList(context.m_orderBy, m_resolutionList, 0));

			if(context.m_orderWay != null)
			{
				extra.append(" ").append(context.m_orderWay.getText());
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_limit != null)
		{
			extra.append(" LIMIT ").append(context.m_limit.getText());

			if(context.m_offset != null)
			{
				extra.append(" OFFSET ").append(context.m_offset.getText());
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple2<Set<String>, Set<String>> tuple = Helper.getIsolatedPath(m_catalog, m_primaryKey, m_resolutionList, 0, false);

		return result.addFromPart(tuple.x)
		             .addWherePart(tuple.y)
		             .addExtraPart(extra)
		             .toStringBuilder()
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitInsertStatement(@NotNull MQLParser.InsertStatementContext context) throws Exception
	{
		XQLInsert result = new XQLInsert(XQLInsert.Mode.SQL);

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple2<List<String>, List<String>> tuple = Helper.resolve(
			m_catalog,
			m_primaryKey,
			visitQIdTuple       (context.m_qIds       , null, IS_MODIF_STMT),
			visitExpressionTuple(context.m_expressions, null, IS_MODIF_STMT),
			m_AMIUser,
			m_isAdmin,
			true
		);

		/*------------------------------------------------------------------------------------------------------------*/

		result.addInsertPart(m_primaryKey.toStringBuilder(QId.MASK_ENTITY))
		      .addFieldValuePart(tuple.x, tuple.y)
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitUpdateStatement(@NotNull MQLParser.UpdateStatementContext context) throws Exception
	{
		XQLUpdate result = new XQLUpdate(XQLUpdate.Mode.SQL);

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple2<List<String>, List<String>> tuple = Helper.resolve(
			m_catalog,
			m_primaryKey,
			visitQIdTuple       (context.m_qIds       , null, IS_MODIF_STMT),
			visitExpressionTuple(context.m_expressions, null, IS_MODIF_STMT),
			m_AMIUser,
			m_isAdmin,
			false
		);

		/*------------------------------------------------------------------------------------------------------------*/

		result.addUpdatePart(m_primaryKey.toStringBuilder(QId.MASK_ENTITY))
		      .addFieldValuePart(tuple.x, tuple.y)
		;

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(Helper.getIsolatedExpression(
				m_catalog,
				m_primaryKey,
				m_resolutionList,
				visitExpressionOr(context.m_expression, m_resolutionList, IS_MODIF_STMT),
				0,
				false,
				true,
				true
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitDeleteStatement(@NotNull MQLParser.DeleteStatementContext context) throws Exception
	{
		XQLDelete result = new XQLDelete(XQLDelete.Mode.SQL);

		/*------------------------------------------------------------------------------------------------------------*/

		result.addDeletePart(m_primaryKey.toString(QId.MASK_ENTITY));

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			result.addWherePart(Helper.getIsolatedExpression(
				m_catalog,
				m_primaryKey,
				m_resolutionList,
				visitExpressionOr(context.m_expression, m_resolutionList, IS_MODIF_STMT),
				0,
				false,
				true,
				true
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract("_, _, _ -> new")
	private StringBuilder visitColumnList(@NotNull MQLParser.ColumnListContext context, @NotNull List<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.AColumnContext child: context.m_columns)
		{
			result.add(visitAColumn(child, resolutionList, mask).toString());
		}

		return new StringBuilder(String.join(", ", result));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitAColumn(@NotNull MQLParser.AColumnContext context, @NotNull List<Resolution> resolutionList, int mask) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = visitExpressionOr(context.m_expression, resolutionList, mask);

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_alias != null)
		{
			result.append(" AS ").append(Utility.textToSqlId(context.m_alias.getText()));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract("_, _, _ -> new")
	private StringBuilder visitQIdList(@NotNull MQLParser.QIdListContext context, @NotNull List<Resolution> resolutionList, int mask) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(MQLParser.AQIdContext child: context.m_aQIds)
		{
			result.add(visitAQId(child, resolutionList, mask).toString());
		}

		return new StringBuilder(String.join(", ", result));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitAQId(@NotNull MQLParser.AQIdContext context, @NotNull List<Resolution> resolutionList, int mask) throws Exception
	{
		return visitQId(context.m_qId, resolutionList, mask | NO_STAR).get(0).getInternalQId().toStringBuilder(QId.MASK_CATALOG_ENTITY_FIELD);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private List<StringBuilder> visitExpressionTuple(@NotNull MQLParser.ExpressionTupleContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		List<StringBuilder> result = new ArrayList<>();

		for(MQLParser.ExpressionOrContext child: context.m_expressions)
		{
			result.add(visitExpressionOr(child, resolutionList, mask));
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private List<Resolution> visitQIdTuple(@NotNull MQLParser.QIdTupleContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		List<Resolution> result = new ArrayList<>();

		for(MQLParser.QIdContext child: context.m_qIds)
		{
			result.addAll(visitQId(child, resolutionList, mask));
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private List<StringBuilder> visitLiteralTuple(@NotNull MQLParser.LiteralTupleContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		List<StringBuilder> result = new ArrayList<>();

		for(MQLParser.LiteralContext child: context.m_literals)
		{
			result.add(visitLiteral(child, resolutionList, mask));
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionOr(@NotNull MQLParser.ExpressionOrContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionXorContext)
			{
				result.append(visitExpressionXor((MQLParser.ExpressionXorContext) child, resolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" OR ");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionXor(@NotNull MQLParser.ExpressionXorContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

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
				result.append(" XOR ");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionAnd(@NotNull MQLParser.ExpressionAndContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionNotContext)
			{
				result.append(visitExpressionNot((MQLParser.ExpressionNotContext) child, resolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" AND ");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionNot(@NotNull MQLParser.ExpressionNotContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

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
				result.append("NOT ");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionComp(@NotNull MQLParser.ExpressionCompContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionAddSub(@NotNull MQLParser.ExpressionAddSubContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionMulDiv(@NotNull MQLParser.ExpressionMulDivContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionPlusMinusContext)
			{
				result.append(visitExpressionPlusMinus((MQLParser.ExpressionPlusMinusContext) child, resolutionList, mask));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(" ")
				      .append(child.getText())
				      .append(" ")
				;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionPlusMinus(@NotNull MQLParser.ExpressionPlusMinusContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		if(context.m_operator != null)
		{
			result.append(context.m_operator.getText());
		}

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionStdGroup(@NotNull MQLParser.ExpressionStdGroupContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder stdExpression = visitExpressionOr(context.m_expression, resolutionList, mask);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("(")
		                          .append(stdExpression)
		                          .append(")")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionIsoGroup(@NotNull MQLParser.ExpressionIsoGroupContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		List<Resolution> tmpResolutionList = new ArrayList<>();

		StringBuilder stdExpression = visitExpressionOr(context.m_expression, tmpResolutionList, mask & ~IS_MODIF_STMT);

		String isoExpression = Helper.getIsolatedExpression(
			m_catalog,
			m_primaryKey,
			tmpResolutionList,
			stdExpression,
			0,
			false,
			(mask & IS_MODIF_STMT) != 0,
			false
		);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("(")
		                          .append(isoExpression)
		                          .append(")")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionFunction(@NotNull MQLParser.ExpressionFunctionContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		return visitFunction(context.m_function, resolutionList, mask);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract("_, _, _ -> new")
	private StringBuilder visitExpressionQId(@NotNull MQLParser.ExpressionQIdContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(resolutionList == null)
		{
			throw new Exception("internal error");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Resolution> list;

		resolutionList.addAll(list = visitQId(context.m_qId, resolutionList, mask));

		return new StringBuilder(list.stream().map(x -> x.getInternalQId().toString((mask & IS_MODIF_STMT) == 0 ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_ENTITY_FIELD)).collect(Collectors.joining(", ")));

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitExpressionLiteral(@NotNull MQLParser.ExpressionLiteralContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		return visitLiteral(context.m_literal, resolutionList, mask);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract("_, _, _ -> new")
	private StringBuilder visitFunction(@NotNull MQLParser.FunctionContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		List<StringBuilder> expressions = new ArrayList<>();

		for(MQLParser.ExpressionOrContext child: context.m_expressions)
		{
			expressions.add(visitExpressionOr(child, resolutionList, STAR_TO_ID));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append(context.m_functionName.getText())
		                          .append(context.m_distinct != null ? "(DISTINCT " : "(")
		                          .append(String.join(", ", expressions))
		                          .append(")")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private List<Resolution> visitQId(@NotNull MQLParser.QIdContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		QId qid = QId.buildBasicQId(context.m_basicQId.getText(), context.m_basicQId.m_ids, QId.Type.FIELD);

		/*------------------------------------------------------------------------------------------------------------*/

		for(MQLParser.ConstraintQIdContext constraintQIdContext: context.m_constraintQIds)
		{
			qid.getConstraints().add(QId.buildBasicQId(constraintQIdContext.m_qId.m_basicQId.getText(), constraintQIdContext.m_qId.m_basicQId.m_ids, QId.Type.FIELD).setExclusion(constraintQIdContext.m_op != null));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String catalogName = (qid.getCatalog() != null) ? qid.getCatalog() : m_catalog;

		String entityName = (qid.getEntity() != null) ? qid.getEntity() : m_entity;

		String fieldName = qid.getField();

		/*------------------------------------------------------------------------------------------------------------*/

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
				list = SchemaSingleton.getSortedQIds(catalogName, entityName, qid.getConstraints());
			}
		}
		else
		{
			list = Collections.singletonList(qid);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Resolution> result = new ArrayList<>();

		int maxPathLength = ConfigSingleton.getProperty("max_path_length", 999);

		for(QId qId: list) result.add(AutoJoinSingleton.resolve(m_catalog, m_entity, qId, maxPathLength));

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract("_, _, _ -> new")
	private StringBuilder visitLiteral(@NotNull MQLParser.LiteralContext context, @Nullable List<Resolution> resolutionList, int mask) throws Exception
	{
		String literal = context.getText();

		if(!literal.isEmpty())
		{
			return new StringBuilder(literal);
		}
		else
		{
			throw new Exception("internal error");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
