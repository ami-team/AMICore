package net.hep.ami.jdbc.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.mql.antlr.*;
import net.hep.ami.jdbc.reflexion.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/

	private final String m_catalog;

	private final Set<String> m_tables = new HashSet<>();

	private final AutoJoinSingleton.AMIJoins m_joins = new AutoJoinSingleton.AMIJoins();

	/*---------------------------------------------------------------------*/

	public MQLToSQL(String catalog)
	{
		m_catalog = catalog;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String query, String catalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy());

		/*-----------------------------------------------------------------*/

		return new MQLToSQL(catalog).visitSelectStatement(parser.selectStatement()).toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String unescapeId(String id)
	{
		if(id.charAt(0) == '`')
		{
			return id.substring(1, id.length() - 1);
		}

		return id;
	}

	/*---------------------------------------------------------------------*/

	private String escapeId(String id)
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
		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(context.columns != null)
		{
			select.append("SELECT ");

			if(context.distinct != null)
			{
				select.append("DISTINCT ");
			}

			select.append(visitColumnList(context.columns));
		}

		/*-----------------------------------------------------------------*/

		if(context.expression != null)
		{
			where.append(" WHERE ").append(visitExpressionOr(context.expression));
		}

		/*-----------------------------------------------------------------*/

		if(context.limit != null)
		{
			where.append(" LIMIT ").append(context.limit.getText());

			if(context.offset != null)
			{
				where.append(" OFFSET ").append(context.offset.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		if(m_tables.isEmpty() == false)
		{
			/*-------------------------------------------------------------*/
			/* FROM PART                                                   */
			/*-------------------------------------------------------------*/

			int cnt = 0;

			from.append(" FROM ");

			for(String table: m_tables)
			{
				if(cnt++ > 0)
				{
					from.append(", ");
				}

				from.append(escapeId(table));

				String joins = m_joins.toSQL().from;

				if(joins.isEmpty() == false)
				{
					from.append(joins);
				}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append(select)
		                          .append(from)
		                          .append(where)
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLParser.ColumnListContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			if(i > 0)
			{
				result.append(", ");
			}

			result.append(visitColumnExpression((MQLParser.ColumnContext) context.getChild(i)));
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
			result.append(" AS " + escapeId(context.alias.getText()));
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
				result.append(child.getText());
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
				result.append(child.getText());
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
				result.append(child.getText());
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

	private boolean m_break = false;

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(MQLParser.SqlQIdContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		String tableName = context.tableName.getText();
		String columnName = context.columnName.getText();

		String escapeTableName = escapeId(tableName);
		String unescapeTableName = unescapeId(tableName);

		String escapeColumnName = escapeId(columnName);
		String unescapeColumnName = unescapeId(columnName);

		m_tables.add(unescapeTableName);

		if("*".equals(unescapeColumnName))
		{
			/*-------------------------------------------------------------*/

			int cnt = 0;

			for(String x: SchemaSingleton.getColumnNames(m_catalog, unescapeTableName))
			{
				escapeColumnName = escapeId(x);
				unescapeColumnName = unescapeId(x);

				AutoJoinSingleton.resolveWithInnerJoins(
					m_joins,
					m_catalog,
					unescapeTableName,
					unescapeColumnName,
					null
				);

				if(cnt++ > 0)
				{
					result.append(",");
				}

				result.append(escapeTableName)
				      .append(".")
				      .append(escapeColumnName)
				;

				if(m_break)
				{
					break;
				}
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/

			AutoJoinSingleton.resolveWithInnerJoins(
				m_joins,
				m_catalog,
				unescapeTableName,
				unescapeColumnName,
				null
			);

			result.append(escapeTableName)
			      .append(".")
			      .append(escapeColumnName)
			;

			/*-------------------------------------------------------------*/
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlLiteral(MQLParser.SqlLiteralContext context)
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
