package net.hep.ami.jdbc.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.mql.antlr.*;
import net.hep.ami.jdbc.reflexion.*;

public class parser
{
	/*---------------------------------------------------------------------*/

	private Map<String, List<String>> m_joins = new HashMap<>();

	private Set<String> m_tables = new HashSet<>();

	private DriverAbstractClass m_driver;

	private boolean m_break;

	/*---------------------------------------------------------------------*/

	public parser(DriverAbstractClass driver)
	{
		m_driver = driver;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String query, DriverAbstractClass driver) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(new ANTLRInputStream(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy() {
		});

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		return new parser(driver).visitSelectStatement(parser.selectStatement()).toString();

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

		if(ctx.alias != null) result.append(" AS " + escapeId(ctx.alias.getText()));

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

			/****/ if(child instanceof MQLParser.ExpressionAndContext) {
				result.append(visitExpressionAnd((MQLParser.ExpressionAndContext) child));
			} else if(child instanceof TerminalNode) {
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

			/****/ if(child instanceof MQLParser.ExpressionCompContext) {
				result.append(visitExpressionComp((MQLParser.ExpressionCompContext) child));
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

	private StringBuilder visitExpressionComp(MQLParser.ExpressionCompContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLParser.ExpressionAddSubContext) {
				result.append(visitExpressionAddSub((MQLParser.ExpressionAddSubContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(_patchNEOperator(child.getText()));
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

			/****/ if(child instanceof MQLParser.ExpressionMulDivContext) {
				result.append(visitExpressionMulDiv((MQLParser.ExpressionMulDivContext) child));
			} else if(child instanceof TerminalNode) {
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

			/****/ if(child instanceof MQLParser.ExpressionNotPlusMinusContext) {
				result.append(visitExpressionNotPlusMinus((MQLParser.ExpressionNotPlusMinusContext) child));
			} else if(child instanceof TerminalNode) {
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

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = ctx.getChild(i);

			/****/ if(child instanceof MQLParser.ExpressionGroupContext) {
				result.append(visitExpressionGroup((MQLParser.ExpressionGroupContext) child));
			} else if(child instanceof MQLParser.ExpressionFunctionContext) {
				result.append(visitExpressionFunction((MQLParser.ExpressionFunctionContext) child));
			} else if(child instanceof MQLParser.ExpressionLikeContext) {
				result.append(visitExpressionLike((MQLParser.ExpressionLikeContext) child));
			} else if(child instanceof MQLParser.ExpressionQIdContext) {
				result.append(visitExpressionQId((MQLParser.ExpressionQIdContext) child));
			} else if(child instanceof MQLParser.ExpressionLiteralContext) {
				result.append(visitExpressionLiteral((MQLParser.ExpressionLiteralContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext ctx)
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

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext ctx)
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

	private StringBuilder visitExpressionLike(MQLParser.ExpressionLikeContext ctx)
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

	private StringBuilder visitSqlQId(MQLParser.SqlQIdContext ctx)
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

	private StringBuilder visitSqlLiteral(MQLParser.SqlLiteralContext ctx)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(ctx.getText());

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
