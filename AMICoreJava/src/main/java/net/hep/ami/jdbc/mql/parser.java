package net.hep.ami.jdbc.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.mql.antlr.*;
import net.hep.ami.jdbc.reflexion.*;

public class parser
{
	/*---------------------------------------------------------------------*/

	private Map<String, List<String>> m_joins = new HashMap<>();

	private Set<String> m_tables = new HashSet<>();

	private String m_catalog;

	/*---------------------------------------------------------------------*/

	public parser(String catalog)
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

		return new parser(catalog).visitSelectStatement(parser.selectStatement()).toString();

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

	private StringBuilder visitSelectStatement(MQLParser.SelectStatementContext ctx)
	{
		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(ctx.columns != null)
		{
			select.append("SELECT ");

			if(ctx.distinct != null)
			{
				select.append("DISTINCT ");
			}

			select.append(visitColumnList(ctx.columns));
		}

		/*-----------------------------------------------------------------*/

		if(ctx.expression != null)
		{
			where.append(" WHERE ").append(visitExpressionOr(ctx.expression));
		}

		/*-----------------------------------------------------------------*/

		if(ctx.limit != null)
		{
			where.append(" LIMIT ").append(ctx.limit.getText());

			if(ctx.offset != null)
			{
				where.append(" OFFSET ").append(ctx.offset.getText());
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

				String joins = AutoJoinSingleton.joinsToSQL(m_joins).from;

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

	private StringBuilder visitColumnList(MQLParser.ColumnListContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			if(i > 0)
			{
				result.append(", ");
			}

			result.append(visitColumnExpression((MQLParser.ColumnContext) ctx.getChild(i)));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnExpression(MQLParser.ColumnContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(ctx.expression));

		/*-----------------------------------------------------------------*/

		if(ctx.alias != null)
		{
			result.append(" AS " + escapeId(ctx.alias.getText()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLParser.ExpressionOrContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

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

	private StringBuilder visitExpressionAnd(MQLParser.ExpressionAndContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

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

	private StringBuilder visitExpressionComp(MQLParser.ExpressionCompContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

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

	private StringBuilder visitExpressionAddSub(MQLParser.ExpressionAddSubContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

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

	private StringBuilder visitExpressionMulDiv(MQLParser.ExpressionMulDivContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

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

	private StringBuilder visitExpressionNotPlusMinus(MQLParser.ExpressionNotPlusMinusContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(ctx.operator != null) result.append(ctx.operator.getText());

		/*-----------------------------------------------------------------*/

		ParseTree child = ctx.getChild(0);

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

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext ctx)
	{
		return new StringBuilder().append("(")
		                          .append(visitExpressionOr(ctx.expression))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext ctx)
	{
		m_break = true;

		/**/	StringBuilder result = new StringBuilder().append(ctx.functionName.getText())
		/**/	                                          .append("(")
		/**/	                                          .append(ctx.distinct != null ? "DISTINCT " : "").append(visitExpressionOr(ctx.expression))
		/**/	                                          .append(")")
		/**/	;

		m_break = false;

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLike(MQLParser.ExpressionLikeContext ctx)
	{
		return new StringBuilder().append(  visitSqlQId  (  ctx.qId  ))
		                          .append(" LIKE ")
		                          .append(visitSqlLiteral(ctx.literal))
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLParser.ExpressionQIdContext ctx)
	{
		return visitSqlQId(ctx.qId);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext ctx)
	{
		return visitSqlLiteral(ctx.literal);
	}

	/*---------------------------------------------------------------------*/

	private boolean m_break = false;

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(MQLParser.SqlQIdContext ctx)
	{
		StringBuilder result = new StringBuilder();

		try
		{
			String tableName = ctx.tableName.getText();
			String columnName = ctx.columnName.getText();

			String escapeTableName = escapeId(tableName);
			String unescapeTableName = unescapeId(tableName);

			String escapeColumnName = escapeId(columnName);
			String unescapeColumnName = unescapeId(columnName);

			m_tables.add(unescapeTableName);

			if(unescapeColumnName.equals("*"))
			{
				/*---------------------------------------------------------*/

				int cnt = 0;

				Set<String> columnNames = SchemaSingleton.getColumnNames(m_catalog, unescapeTableName);

				for(String x: columnNames)
				{
					escapeColumnName = escapeId(x);
					unescapeColumnName = unescapeId(x);

					AutoJoinSingleton.resolveWithNestedSelect(
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

				/*---------------------------------------------------------*/
			}
			else
			{
				/*---------------------------------------------------------*/

				AutoJoinSingleton.resolveWithNestedSelect(
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

				/*---------------------------------------------------------*/
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlLiteral(MQLParser.SqlLiteralContext ctx)
	{
		return new StringBuilder(ctx.getText());
	}

	/*---------------------------------------------------------------------*/
}
