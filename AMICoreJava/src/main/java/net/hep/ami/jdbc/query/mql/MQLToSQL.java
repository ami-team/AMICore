package net.hep.ami.jdbc.query.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.reflexion.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/

	private final String m_catalog;
	private final String m_entity;

	private final Structure.Joins m_joins;

	/*---------------------------------------------------------------------*/

	private boolean m_break = false;

	/*---------------------------------------------------------------------*/

	public MQLToSQL(String catalog, String entity)
	{
		m_catalog = catalog;
		m_entity = entity;

		m_joins = new Structure.Joins(catalog);
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String catalog, String entity, String query) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy());

		/*-----------------------------------------------------------------*/

		return new MQLToSQL(catalog, entity).visitSelectStatement(parser.selectStatement()).toString();

		/*-----------------------------------------------------------------*/
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
		StringBuilder part1 = new StringBuilder();
		StringBuilder part2 = new StringBuilder();
		StringBuilder part3 = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.columns != null)
		{
			part1.append("SELECT ");

			if(context.distinct != null)
			{
				part1.append("DISTINCT ");
			}

			part1.append(visitColumnList(context.columns));
		}

		/*-----------------------------------------------------------------*/

		if(context.orderBy != null)
		{
			part3.append(" ORDER BY ").append(context.orderBy.getText());

			if(context.orderWay != null)
			{
				part3.append(context.orderWay.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		if(context.limit != null)
		{
			part3.append(" LIMIT ").append(context.limit.getText());

			if(context.offset != null)
			{
				part3.append(" OFFSET ").append(context.offset.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		part2.append(m_joins.toString());

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append(part1)
		                          .append(part2)
		                          .append(part3)
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLParser.ColumnListContext context) throws Exception
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
				result.append(visitColumnExpression((MQLParser.ColumnContext) child));
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

	private StringBuilder visitColumnExpression(MQLParser.ColumnContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(context.expression));

		/*-----------------------------------------------------------------*/

		if(context.alias != null)
		{
			result.append(" AS " + quoteId(context.alias.getText()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLParser.ExpressionOrContext context) throws Exception
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
				result.append(visitExpressionAnd((MQLParser.ExpressionAndContext) child));
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

	private StringBuilder visitExpressionAnd(MQLParser.ExpressionAndContext context) throws Exception
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
				result.append(visitExpressionComp((MQLParser.ExpressionCompContext) child));
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

	private StringBuilder visitExpressionComp(MQLParser.ExpressionCompContext context) throws Exception
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
				result.append(visitExpressionAddSub((MQLParser.ExpressionAddSubContext) child));
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

	private StringBuilder visitExpressionAddSub(MQLParser.ExpressionAddSubContext context) throws Exception
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
				result.append(visitExpressionMulDiv((MQLParser.ExpressionMulDivContext) child));
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

	private StringBuilder visitExpressionMulDiv(MQLParser.ExpressionMulDivContext context) throws Exception
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
				result.append(visitExpressionNotPlusMinus((MQLParser.ExpressionNotPlusMinusContext) child));
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

	private StringBuilder visitExpressionNotPlusMinus(MQLParser.ExpressionNotPlusMinusContext context) throws Exception
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
			result.append(visitExpressionGroup((MQLParser.ExpressionGroupContext) child));
		}
		else if(child instanceof MQLParser.ExpressionFunctionContext)
		{
			result.append(visitExpressionFunction((MQLParser.ExpressionFunctionContext) child));
		}
		else if(child instanceof MQLParser.ExpressionLikeContext)
		{
			result.append(visitExpressionLike((MQLParser.ExpressionLikeContext) child));
		}
		else if(child instanceof MQLParser.ExpressionQIdContext)
		{
			result.append(visitExpressionQId((MQLParser.ExpressionQIdContext) child));
		}
		else if(child instanceof MQLParser.ExpressionLiteralContext)
		{
			result.append(visitExpressionLiteral((MQLParser.ExpressionLiteralContext) child));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext context) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(visitExpressionOr(context.expression))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context) throws Exception
	{
		m_break = true;

		/**/	StringBuilder result = new StringBuilder().append(context.functionName.getText())
		/**/	                                          .append("(")
		/**/	                                          .append(context.distinct != null ? "DISTINCT " : "").append(visitExpressionOr(context.expression))
		/**/	                                          .append(")")
		/**/	;

		m_break = false;

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLike(MQLParser.ExpressionLikeContext context) throws Exception
	{
		return new StringBuilder().append(  visitSqlQId  (  context.qId  ))
		                          .append(" LIKE ")
		                          .append(visitSqlLiteral(context.literal))
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLParser.ExpressionQIdContext context) throws Exception
	{
		return visitSqlQId(context.qId);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext context) throws Exception
	{
		return visitSqlLiteral(context.literal);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(MQLParser.SqlQIdContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String catalogName = (context.catalogName != null) ? unquoteId(context.catalogName.getText()) : m_catalog;

		String entityName = (context.entityName != null) ? unquoteId(context.entityName.getText()) : m_entity;

		String fieldName = unquoteId(context.fieldName.getText());

		/*-----------------------------------------------------------------*/

		int cnt = 0;

		AutoJoinSingleton.SQLQId resolvedQId;

		for(String qId: "*".equals(fieldName) ? SchemaSingleton.getColumnNames(catalogName, entityName) : Arrays.asList(context.getText()))
		{
			resolvedQId = AutoJoinSingleton.resolveWithNestedSelect(
				m_joins,
				m_catalog,
				m_entity,
				qId,
				null
			);

			if(cnt++ > 0)
			{
				result.append(", ");
			}

			result.append(resolvedQId.toString());

			if(m_break)
			{
				break;
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlLiteral(MQLParser.SqlLiteralContext context)
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
