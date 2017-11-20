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

	private final String m_catalog;
	private final String m_entity;

	/*---------------------------------------------------------------------*/

	private final Islets m_islets;

	private final Set<String> m_tables;

	/*---------------------------------------------------------------------*/

	public MQLToSQL(String catalog, String entity)
	{
		m_catalog = catalog;
		m_entity = entity;

		m_islets = new Islets();

		m_tables = new LinkedHashSet<>();
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

			query.addSelectPart(visitColumnList(context.columns).toString());
		}

		/*-----------------------------------------------------------------*/

		if(context.expression != null)
		{
			query.addWherePart(visitExpressionOr(context.expression).toString());
		}

		/*-----------------------------------------------------------------*/

		if(context.orderBy != null)
		{
			extra.append(" ORDER BY ").append(AutoJoinSingleton.resolveWithInnerJoins(m_islets, m_catalog, m_entity, context.orderBy.getText(), null).toString());

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

		for(Map.Entry<String, Map<String, Query>> entry: m_islets.getJoins(Islets.DUMMY, Islets.DUMMY).entrySet())
		{
			m_tables.remove(entry.getKey());

			m_tables.removeAll(entry.getValue().keySet());
		}

		query.addFromPart(m_tables).addWholeQuery(m_islets.toQuery());

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append(query.toString(extra.toString()));

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

	private boolean m_break = false;

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
		List<String> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		String catalogName = (context.catalogName != null) ? unquoteId(context.catalogName.getText()) : m_catalog;

		String entityName = (context.entityName != null) ? unquoteId(context.entityName.getText()) : m_entity;

		String fieldName = unquoteId(context.fieldName.getText());

		/*-----------------------------------------------------------------*/

		QId resolvedQId;

		for(String qId: "*".equals(fieldName) ? SchemaSingleton.getColumnNames(catalogName, entityName) : Arrays.asList(context.getText()))
		{
			resolvedQId = AutoJoinSingleton.resolveWithInnerJoins(
				m_islets,
				m_catalog,
				m_entity,
				qId,
				null
			);

			m_tables.add(resolvedQId.toString(QId.Deepness.TABLE));

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

	private StringBuilder visitSqlLiteral(MQLParser.SqlLiteralContext context)
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
