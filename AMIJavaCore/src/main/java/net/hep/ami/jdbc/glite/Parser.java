package net.hep.ami.jdbc.glite;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.glite.antlr.*;
import net.hep.ami.jdbc.introspection.*;

public class Parser {
	/*---------------------------------------------------------------------*/

	private Map<String, Set<String>> m_fields = new HashMap<String, Set<String>>();

	private String m_catalog;

	/*---------------------------------------------------------------------*/

	public Parser(String catalog) {

		m_catalog = catalog;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String query, String catalog) throws Exception {
		/*-------------------------------------------------------------*/
		/*                                                             */
		/*-------------------------------------------------------------*/

		GLiteLexer lexer = new GLiteLexer(new ANTLRInputStream(query));

		GLiteParser parser = new GLiteParser(new CommonTokenStream(lexer));

		/*-------------------------------------------------------------*/
		/*                                                             */
		/*-------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy() {
		});

		/*-------------------------------------------------------------*/
		/*                                                             */
		/*-------------------------------------------------------------*/

		return new Parser(catalog).visitSelectStatement(parser.selectStatement()).toString();

		/*-------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) {

		System.out.println(CatalogSingleton.listCatalogs());

		try {
			System.out.println(parse("SELECT `router_user`.*, foo.bar, `foo`.`bar` AS foobar, (1 + 1) * (1 + 1) * 333 + 4 + 4 <> 1 <> 0 AS expr WHERE foo.bar > 4 AND toto.toto LIKE 'string'", "self"));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println("done.");

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/

	private String unescapeId(String id) {

		if(id.charAt(0) == '`') {
			return id.substring(1, id.length() - 1);
		}

		return id;
	}

	/*---------------------------------------------------------------------*/

	private String escapeId(String id) {

		if(id.charAt(0) != '`') {
			return '`' + id + '`';
		}

		return id;
	}

	/*---------------------------------------------------------------------*/

	private void addColumn(String table, String column) {

		Set<String> result = m_fields.get(table);

		if(result == null) {

			result = new HashSet<String>();

			m_fields.put(table, result);
		}

		result.add(column);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSelectStatement(GLiteParser.SelectStatementContext ctx) {

		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(ctx.columns != null) {
			select.append("SELECT ");
			select.append(visitColumnList(ctx.columns));
		}

		/*-----------------------------------------------------------------*/

		if(ctx.expression != null) {
			where.append(" WHERE ");
			where.append(visitExpressionOr(ctx.expression));
		}

		/*-----------------------------------------------------------------*/

		Set<String> fields = m_fields.keySet();

		if(fields.isEmpty() == false) {
			/*-------------------------------------------------------------*/
			/* FROM PART                                                   */
			/*-------------------------------------------------------------*/

			int cnt = 0;

			from.append(" FROM ");

			for(String table: m_fields.keySet()) {

				if(cnt++ > 0) {
					from.append(", ");
				}

				from.append(escapeId(table));
			}

			/*-------------------------------------------------------------*/
			/* JOIN PART                                                   */
			/*-------------------------------------------------------------*/

			/* TODO */

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

	private StringBuilder visitColumnList(GLiteParser.ColumnListContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		int cnt = 0;

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ColumnWildcardContext) {
				if(cnt++ > 0) result.append(", ");
				result.append(visitColumnWildcard((GLiteParser.ColumnWildcardContext) child));

			} else if(child instanceof GLiteParser.ColumnExpressionContext) {
				if(cnt++ > 0) result.append(", ");
				result.append(visitColumnExpression((GLiteParser.ColumnExpressionContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnWildcard(GLiteParser.ColumnWildcardContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		try {
			/*-------------------------------------------------------------*/
			/* GET COLUMN NAMES                                            */
			/*-------------------------------------------------------------*/

			String escapeTableName = escapeId(ctx.tableName.getText());
			String unescapeTableName = unescapeId(ctx.tableName.getText());

			Set<String> columnNames = SchemaSingleton.getColumnNames(m_catalog, unescapeTableName);

			/*-------------------------------------------------------------*/
			/* WRITE COLUMN NAMES                                          */
			/*-------------------------------------------------------------*/

			int cnt = 0;

			String escapeColumnName;
			String unescapeColumnName;

			for(String columnName: columnNames) {

				escapeColumnName = escapeId(columnName);
				unescapeColumnName = unescapeId(columnName);

				if(cnt++ > 0) {
					result.append(",");
				}

				result.append(escapeTableName + "." + escapeColumnName);

				addColumn(unescapeTableName, unescapeColumnName);
			}

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			System.out.println(e.getMessage());
			/* TODO */
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnExpression(GLiteParser.ColumnExpressionContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(ctx.expression));

		/*-----------------------------------------------------------------*/

		if(ctx.alias != null) result.append(" AS " + escapeId(ctx.alias.getText()));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(GLiteParser.ExpressionOrContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionAndContext) {
				result.append(visitExpressionAnd((GLiteParser.ExpressionAndContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(" OR ");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAnd(GLiteParser.ExpressionAndContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionCompContext) {
				result.append(visitExpressionComp((GLiteParser.ExpressionCompContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(" AND ");
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private String _patchNEOperator(String operator) {

		if(operator.equals("^=")
		   ||
		   operator.equals("<>")
		 ) {
			operator = "!=";
		}

		return operator;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionComp(GLiteParser.ExpressionCompContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionAddSubContext) {
				result.append(visitExpressionAddSub((GLiteParser.ExpressionAddSubContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(_patchNEOperator(child.getText()));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAddSub(GLiteParser.ExpressionAddSubContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionMulDivContext) {
				result.append(visitExpressionMulDiv((GLiteParser.ExpressionMulDivContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(child.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionMulDiv(GLiteParser.ExpressionMulDivContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionNotPlusMinusContext) {
				result.append(visitExpressionNotPlusMinus((GLiteParser.ExpressionNotPlusMinusContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(child.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionNotPlusMinus(GLiteParser.ExpressionNotPlusMinusContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(ctx.operator != null) result.append(ctx.operator.getText());

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionGroupContext) {
				result.append(visitExpressionGroup((GLiteParser.ExpressionGroupContext) child));
			} else if(child instanceof GLiteParser.ExpressionFunctionContext) {
				result.append(visitExpressionFunction((GLiteParser.ExpressionFunctionContext) child));
			} else if(child instanceof GLiteParser.ExpressionLikeContext) {
				result.append(visitExpressionLike((GLiteParser.ExpressionLikeContext) child));
			} else if(child instanceof GLiteParser.ExpressionQIdContext) {
				result.append(visitExpressionQId((GLiteParser.ExpressionQIdContext) child));
			} else if(child instanceof GLiteParser.ExpressionLiteralContext) {
				result.append(visitExpressionLiteral((GLiteParser.ExpressionLiteralContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(GLiteParser.ExpressionGroupContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("(");
		result.append(visitExpressionOr(ctx.expression));
		result.append(")");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(GLiteParser.ExpressionFunctionContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(ctx.functionName.getText());
		result.append("(");
		result.append(visitExpressionOr(ctx.expression));
		result.append(")");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLike(GLiteParser.ExpressionLikeContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitSqlQId(ctx.qId));
		result.append(" LIKE ");
		result.append(visitSqlLiteral(ctx.literal));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(GLiteParser.ExpressionQIdContext ctx) {

		return visitSqlQId(ctx.qId);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(GLiteParser.ExpressionLiteralContext ctx) {

		return visitSqlLiteral(ctx.literal);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(GLiteParser.SqlQIdContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String tableName = ctx.tableName.getText();
		String columnName = ctx.columnName.getText();

		result.append(escapeId(tableName));
		result.append(".");
		result.append(escapeId(columnName));

		addColumn(unescapeId(tableName), unescapeId(columnName));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlLiteral(GLiteParser.SqlLiteralContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(ctx.getText());

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
