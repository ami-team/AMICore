package net.hep.ami.jdbc.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.mql.antlr.*;
import net.hep.ami.jdbc.introspection.*;

public class SelectParser {
	/*---------------------------------------------------------------------*/

	private Map<String, Set<String>> m_fields = new HashMap<String, Set<String>>();

	private String m_catalog;

	/*---------------------------------------------------------------------*/

	public SelectParser(String catalog) {

		m_catalog = catalog;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String query, String catalog) throws Exception {
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

		return new SelectParser(catalog).visitSelectStatement(parser.selectStatement()).toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) {

		CatalogSingleton.listCatalogs();

		try {
			//System.out.println(parse("SELECT `router_user`.*, foo.bar, `foo`.`bar` AS foobar, (1 + 1) * (1 + 1) * 333 + 4 + 4 <> 1 <> 0 AS expr WHERE foo.bar > 4 AND toto.toto LIKE 'string'", "self"));
			//System.out.println(parse("SELECT DISTINCT(`router_command`.`command`) WHERE (1=1) LIMIT 10 OFFSET 0", "self"));
			System.out.println(parse("SELECT `router_command`.* WHERE (`router_command`.`command`='GetSessionInfo')", "self"));

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

	private StringBuilder visitSelectStatement(MQLSelectParser.SelectStatementContext ctx) {

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

		if(ctx.limit != null) {

			where.append(" LIMIT ");
			where.append(ctx.limit.getText());

			if(ctx.offset != null) {

				where.append(" OFFSET ");
				where.append(ctx.offset.getText());
			}
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

	private StringBuilder visitColumnList(MQLSelectParser.ColumnListContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		int cnt = 0;

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof MQLSelectParser.ColumnWildcardContext) {
				if(cnt++ > 0) result.append(", ");
				result.append(visitColumnWildcard((MQLSelectParser.ColumnWildcardContext) child));

			} else if(child instanceof MQLSelectParser.ColumnExpressionContext) {
				if(cnt++ > 0) result.append(", ");
				result.append(visitColumnExpression((MQLSelectParser.ColumnExpressionContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitColumnWildcard(MQLSelectParser.ColumnWildcardContext ctx) {

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

	private StringBuilder visitColumnExpression(MQLSelectParser.ColumnExpressionContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(ctx.expression));

		/*-----------------------------------------------------------------*/

		if(ctx.alias != null) result.append(" AS " + escapeId(ctx.alias.getText()));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLSelectParser.ExpressionOrContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

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

	private StringBuilder visitExpressionAnd(MQLSelectParser.ExpressionAndContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

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

	private StringBuilder visitExpressionComp(MQLSelectParser.ExpressionCompContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

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

	private StringBuilder visitExpressionAddSub(MQLSelectParser.ExpressionAddSubContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

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

	private StringBuilder visitExpressionMulDiv(MQLSelectParser.ExpressionMulDivContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

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

	private StringBuilder visitExpressionNotPlusMinus(MQLSelectParser.ExpressionNotPlusMinusContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(ctx.operator != null) result.append(ctx.operator.getText());

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

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

	private StringBuilder visitExpressionGroup(MQLSelectParser.ExpressionGroupContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("(");
		result.append(visitExpressionOr(ctx.expression));
		result.append(")");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLSelectParser.ExpressionFunctionContext ctx) {

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

	private StringBuilder visitExpressionLike(MQLSelectParser.ExpressionLikeContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitSqlQId(ctx.qId));
		result.append(" LIKE ");
		result.append(visitSqlLiteral(ctx.literal));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLSelectParser.ExpressionQIdContext ctx) {

		return visitSqlQId(ctx.qId);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLSelectParser.ExpressionLiteralContext ctx) {

		return visitSqlLiteral(ctx.literal);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(MQLSelectParser.SqlQIdContext ctx) {

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

	private StringBuilder visitSqlLiteral(MQLSelectParser.SqlLiteralContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(ctx.getText());

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
