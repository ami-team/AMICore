package net.hep.ami.jdbc.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.mql.antlr.*;
import net.hep.ami.jdbc.reflexion.*;

public class SelectParser
{
	/*---------------------------------------------------------------------*/

	private Map<String, List<String>> m_joins = new HashMap<String, List<String>>();

	private Set<String> m_tables = new HashSet<String>();

	private DriverAbstractClass m_driver;

	private boolean m_break;

	/*---------------------------------------------------------------------*/

	public SelectParser(DriverAbstractClass driver)
	{
		m_driver = driver;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String query, DriverAbstractClass driver) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		MQLSelectLexer lexer = new MQLSelectLexer(new ANTLRInputStream(query));

		MQLSelectParser parser = new MQLSelectParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy() {
		});

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		return new SelectParser(driver).visitSelectStatement(parser.selectStatement()).toString();

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

	private StringBuilder visitSelectStatement(MQLSelectParser.SelectStatementContext ctx)
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
			where.append(" WHERE ");
			where.append(visitExpressionOr(ctx.expression));
		}

		/*-----------------------------------------------------------------*/

		if(ctx.limit != null)
		{
			where.append(" LIMIT ");
			where.append(ctx.limit.getText());

			if(ctx.offset != null)
			{
				where.append(" OFFSET ");
				where.append(ctx.offset.getText());
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

		StringBuilder result = new StringBuilder();

		result.append(select);
		result.append(from);
		result.append(where);

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnList(MQLSelectParser.ColumnListContext ctx)
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

			result.append(visitColumnExpression((MQLSelectParser.ColumnContext) ctx.getChild(i)));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnExpression(MQLSelectParser.ColumnContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(ctx.expression));

		/*-----------------------------------------------------------------*/

		if(ctx.alias != null) result.append(" AS " + escapeId(ctx.alias.getText()));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLSelectParser.ExpressionOrContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLSelectParser.ExpressionAndContext) {
				result.append(visitExpressionAnd((MQLSelectParser.ExpressionAndContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(" OR ");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAnd(MQLSelectParser.ExpressionAndContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLSelectParser.ExpressionCompContext) {
				result.append(visitExpressionComp((MQLSelectParser.ExpressionCompContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(" AND ");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private String _patchNEOperator(String operator)
	{
		if(operator.equals("^=")
		   ||
		   operator.equals("<>")
		 ) {
			operator = "!=";
		}

		return operator;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionComp(MQLSelectParser.ExpressionCompContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLSelectParser.ExpressionAddSubContext) {
				result.append(visitExpressionAddSub((MQLSelectParser.ExpressionAddSubContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(_patchNEOperator(child.getText()));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAddSub(MQLSelectParser.ExpressionAddSubContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLSelectParser.ExpressionMulDivContext) {
				result.append(visitExpressionMulDiv((MQLSelectParser.ExpressionMulDivContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(child.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionMulDiv(MQLSelectParser.ExpressionMulDivContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLSelectParser.ExpressionNotPlusMinusContext) {
				result.append(visitExpressionNotPlusMinus((MQLSelectParser.ExpressionNotPlusMinusContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(child.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionNotPlusMinus(MQLSelectParser.ExpressionNotPlusMinusContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(ctx.operator != null) result.append(ctx.operator.getText());

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLSelectParser.ExpressionGroupContext) {
				result.append(visitExpressionGroup((MQLSelectParser.ExpressionGroupContext) child));
			} else if(child instanceof MQLSelectParser.ExpressionFunctionContext) {
				result.append(visitExpressionFunction((MQLSelectParser.ExpressionFunctionContext) child));
			} else if(child instanceof MQLSelectParser.ExpressionLikeContext) {
				result.append(visitExpressionLike((MQLSelectParser.ExpressionLikeContext) child));
			} else if(child instanceof MQLSelectParser.ExpressionQIdContext) {
				result.append(visitExpressionQId((MQLSelectParser.ExpressionQIdContext) child));
			} else if(child instanceof MQLSelectParser.ExpressionLiteralContext) {
				result.append(visitExpressionLiteral((MQLSelectParser.ExpressionLiteralContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(MQLSelectParser.ExpressionGroupContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("(");
		result.append(visitExpressionOr(ctx.expression));
		result.append(")");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLSelectParser.ExpressionFunctionContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(ctx.functionName.getText());
		result.append("(");
		m_break = true;
		if(ctx.distinct != null) result.append("DISTINCT "); result.append(visitExpressionOr(ctx.expression));
		m_break = false;
		result.append(")");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLike(MQLSelectParser.ExpressionLikeContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitSqlQId(ctx.qId));
		result.append(" LIKE ");
		result.append(visitSqlLiteral(ctx.literal));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLSelectParser.ExpressionQIdContext ctx)
	{
		return visitSqlQId(ctx.qId);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLSelectParser.ExpressionLiteralContext ctx)
	{
		return visitSqlLiteral(ctx.literal);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(MQLSelectParser.SqlQIdContext ctx)
	{
		StringBuilder result = new StringBuilder();

		try
		{
			/*-------------------------------------------------------------*/

			String tableName = ctx.tableName.getText();
			String columnName = ctx.columnName.getText();

			String escapeTableName = escapeId(tableName);
			String unescapeTableName = unescapeId(tableName);

			String escapeColumnName = escapeId(columnName);
			String unescapeColumnName = unescapeId(columnName);

			m_tables.add(unescapeTableName);

			if(unescapeColumnName.equals("*"))
			{
				int cnt = 0;

				Set<String> columnNames = SchemaSingleton.getColumnNames(m_driver.getExternalCatalog(), unescapeTableName);

				for(String x: columnNames)
				{
					escapeColumnName = escapeId(x);
					unescapeColumnName = unescapeId(x);

					AutoJoinSingleton.resolveWithNestedSelect(
						m_joins,
						m_driver.getExternalCatalog(),
						unescapeTableName,
						unescapeColumnName,
						null
					);

					if(cnt++ > 0)
					{
						result.append(",");
					}

					result.append(escapeTableName);
					result.append(".");
					result.append(escapeColumnName);

					if(m_break)
					{
						break;
					}
				}
			}
			else
			{
				AutoJoinSingleton.resolveWithNestedSelect(
					m_joins,
					m_driver.getExternalCatalog(),
					unescapeTableName,
					unescapeColumnName,
					null
				);

				result.append(escapeTableName);
				result.append(".");
				result.append(escapeColumnName);
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlLiteral(MQLSelectParser.SqlLiteralContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(ctx.getText());

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
