package net.hep.ami.jdbc.query.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.structure.*;

import net.hep.ami.utility.parser.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/

	private final Islets m_islets = new Islets();

	private final String m_externalCatalog;
	private final String m_internalCatalog;
	private final String m_entity;
	private final String m_pk;

	/*---------------------------------------------------------------------*/

	public MQLToSQL(String catalog, String entity) throws Exception
	{
		m_pk = SchemaSingleton.getPrimaryKey(m_externalCatalog = catalog, m_entity = entity);

		m_internalCatalog = SchemaSingleton.externalCatalogToInternalCatalog(catalog);
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String catalog, String entity, String query) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		String result = new MQLToSQL(catalog, entity).visitSelectStatement(parser.selectStatement()).toString();

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private String unquoteId(String id)
	{
		if(id.charAt(0) == '`')
		{
			return id.substring(1, id.length() - 1).trim();
		}

		return id;
	}

	/*---------------------------------------------------------------------*/

	private String quoteId(String id)
	{
		if(id.charAt(0) != '`')
		{
			return '`' + id + '`';
		}

		return id;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSelectStatement(MQLParser.SelectStatementContext context) throws Exception
	{
		Query query = new Query();

		StringBuilder extra = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.columns != null)
		{
			query.setDistinct(context.distinct != null);

			query.addSelectPart(visitColumnList(context.columns, true).toString());
		}

		/*-----------------------------------------------------------------*/

		if(context.expression != null)
		{
			query.addWherePart(visitExpressionOr(context.expression, false).toString());
		}

		/*-----------------------------------------------------------------*/

		if(context.orderBy != null)
		{
			extra.append(" ORDER BY ").append(AutoJoinSingleton.resolveWithInnerJoins(m_islets, m_externalCatalog, m_entity, context.orderBy.getText(), null).toString());

			if(context.orderWay != null)
			{
				extra.append(" ").append(context.orderWay.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		if(context.limit != null)
		{
			extra.append(" LIMIT ").append(context.limit.getText());

			if(context.offset != null)
			{
				extra.append(" OFFSET ").append(context.offset.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		query.addWholeQuery(m_islets.toQuery());

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append(query.toString(extra.toString()));

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLParser.ColumnListContext context, boolean inSelect) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ColumnContext)
			{
				result.append(visitColumnExpression((MQLParser.ColumnContext) child, inSelect));
			}
			else if(child instanceof TerminalNode)
			{
				result.append(", ");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnExpression(MQLParser.ColumnContext context, boolean inSelect) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(context.expression, inSelect));

		/*-----------------------------------------------------------------*/

		if(context.alias != null)
		{
			result.append(" AS " + quoteId(context.alias.getText()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLParser.ExpressionOrContext context, boolean inSelect) throws Exception
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
				result.append(visitExpressionAnd((MQLParser.ExpressionAndContext) child, inSelect));
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

	private StringBuilder visitExpressionAnd(MQLParser.ExpressionAndContext context, boolean inSelect) throws Exception
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
				result.append(visitExpressionComp((MQLParser.ExpressionCompContext) child, inSelect));
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

	private StringBuilder visitExpressionComp(MQLParser.ExpressionCompContext context, boolean inSelect) throws Exception
	{
		Islets islets = (inSelect == false) ? new Islets() : m_islets;

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionAddSubContext)
			{
				result.append(visitExpressionAddSub((MQLParser.ExpressionAddSubContext) child, inSelect, islets));
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

		if(inSelect)
		{
			return result;
		}
		else
		{
			QId qid = new QId(m_internalCatalog, m_entity, m_pk);

			return new StringBuilder().append(qid.toString()).append(" IN ")
			                          .append("(")
			                          .append(new Query().addSelectPart(qid.toString()).addFromPart(qid.toString(QId.Deepness.TABLE)).addWherePart(result.toString()).addWholeQuery(islets.toQuery()).toString())
			                          .append(")")
			;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAddSub(MQLParser.ExpressionAddSubContext context, boolean inSelect, Islets islets) throws Exception
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
				result.append(visitExpressionMulDiv((MQLParser.ExpressionMulDivContext) child, inSelect, islets));
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

	private StringBuilder visitExpressionMulDiv(MQLParser.ExpressionMulDivContext context, boolean inSelect, Islets islets) throws Exception
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
				result.append(visitExpressionNotPlusMinus((MQLParser.ExpressionNotPlusMinusContext) child, inSelect, islets));
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

	private StringBuilder visitExpressionNotPlusMinus(MQLParser.ExpressionNotPlusMinusContext context, boolean inSelect, Islets islets) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.operator != null)
		{
			result.append(context.operator.getText());
		}

		/*-----------------------------------------------------------------*/

		ParseTree child = context.getChild(0);

		/**/ if(child instanceof MQLParser.ExpressionGroupContext)
		{
			result.append(visitExpressionGroup((MQLParser.ExpressionGroupContext) child, inSelect, islets));
		}
		else if(child instanceof MQLParser.ExpressionFunctionContext)
		{
			result.append(visitExpressionFunction((MQLParser.ExpressionFunctionContext) child, inSelect, islets));
		}
		else if(child instanceof MQLParser.ExpressionQIdContext)
		{
			result.append(visitExpressionQId((MQLParser.ExpressionQIdContext) child, inSelect, islets));
		}
		else if(child instanceof MQLParser.ExpressionLiteralContext)
		{
			result.append(visitExpressionLiteral((MQLParser.ExpressionLiteralContext) child, inSelect, islets));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext context, boolean inSelect, Islets islets) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(visitExpressionOr(context.expression, inSelect))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private boolean m_break = false;

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context, boolean inSelect, Islets islets) throws Exception
	{
		m_break = true;

		/**/	StringBuilder result = new StringBuilder().append(context.functionName.getText())
		/**/	                                          .append("(")
		/**/	                                          .append(context.distinct != null ? "DISTINCT " : "").append(visitExpressionOr(context.expression, inSelect))
		/**/	                                          .append(")")
		/**/	;

		m_break = false;

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLParser.ExpressionQIdContext context, boolean inSelect, Islets islets) throws Exception
	{
		return visitSqlQId(context.qId, inSelect, islets);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext context, boolean inSelect, Islets islets) throws Exception
	{
		return visitSqlLiteral(context.literal, inSelect, islets);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(MQLParser.SqlQIdContext context, boolean inSelect, Islets islets) throws Exception
	{
		List<String> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		String catalogName = (context.catalogName != null) ? unquoteId(context.catalogName.getText()) : m_externalCatalog;

		String entityName = (context.entityName != null) ? unquoteId(context.entityName.getText()) : m_entity;

		String fieldName = unquoteId(context.fieldName.getText());

		/*-----------------------------------------------------------------*/

		QId resolvedQId;

		for(String qId: "*".equals(fieldName) ? SchemaSingleton.getColumnNames(catalogName, entityName) : Arrays.asList(context.getText()))
		{
			resolvedQId = AutoJoinSingleton.resolveWithInnerJoins(
				islets,
				m_externalCatalog,
				m_entity,
				qId,
				null
			);

			result.add(resolvedQId.toString(QId.Deepness.COLUMN));

			if(m_break)
			{
				break;
			}
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder(String.join(", ", result));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlLiteral(MQLParser.SqlLiteralContext context, boolean inSelect, Islets islets)
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
