package net.hep.ami.jdbc.glite;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.glite.antlr.*;

public class Parser extends GLiteBaseVisitor<StringBuilder> {
	/*---------------------------------------------------------------------*/

	private Map<String, Set<String>> m_fields = new HashMap<String, Set<String>>();

	/*---------------------------------------------------------------------*/

	public static String parse(String mql) {

		GLiteLexer lexer = new GLiteLexer(new ANTLRInputStream(mql));

		GLiteParser parser = new GLiteParser(new CommonTokenStream(lexer));

		ParseTree tree = parser.selectStatement();

		return new Parser().visit(tree).toString();
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) {

		System.out.println(parse("SELECT `a`.*, foo.bar, `foo`.`bar` AS foobar, (1 + 1) * (1 + 1) * 333 + 4 + 4 <> 1 <> 0 AS expr WHERE foo.bar > 4"));

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

	private Set<String> addTable(String table) {

		table = unescapeId(table);

		if(m_fields.containsKey(table) == false) {
			/*-------------------------------------------------------------*/
			/* ADD TABLE                                                   */
			/*-------------------------------------------------------------*/

			Set<String> result = new HashSet<String>();

			m_fields.put(table, result);

			return result;

			/*-------------------------------------------------------------*/
		} else {
			/*-------------------------------------------------------------*/
			/* GET TABLE                                                   */
			/*-------------------------------------------------------------*/

			return m_fields.get(table);

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	private void addColumn(String table, String column) {

		addTable(table).add(unescapeId(column));
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitSelectStatement(GLiteParser.SelectStatementContext ctx) {

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

	@Override public StringBuilder visitColumnList(GLiteParser.ColumnListContext ctx) {

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

	@Override public StringBuilder visitColumnWildcard(GLiteParser.ColumnWildcardContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String table = ctx.tableName.getText();

		result.append(escapeId(table) + ".*");

		addTable(table);

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitColumnExpression(GLiteParser.ColumnExpressionContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(ctx.expression));

		/*-----------------------------------------------------------------*/

		if(ctx.alias != null) result.append(" AS " + escapeId(ctx.alias.getText()));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitExpressionOr(GLiteParser.ExpressionOrContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionAndContext) {
				result.append(visitExpressionAnd((GLiteParser.ExpressionAndContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(child.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitExpressionAnd(GLiteParser.ExpressionAndContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionCompContext) {
				result.append(visitExpressionComp((GLiteParser.ExpressionCompContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(child.getText());
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

	@Override public StringBuilder visitExpressionComp(GLiteParser.ExpressionCompContext ctx) {

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

	@Override public StringBuilder visitExpressionAddSub(GLiteParser.ExpressionAddSubContext ctx) {

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

	@Override public StringBuilder visitExpressionMulDiv(GLiteParser.ExpressionMulDivContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = ctx.getChildCount();

		for(int i = 0; i < nb; i++) {

			child = ctx.getChild(i);

			/****/ if(child instanceof GLiteParser.ExpressionNotMinusPlusContext) {
				result.append(visitExpressionNotMinusPlus((GLiteParser.ExpressionNotMinusPlusContext) child));
			} else if(child instanceof TerminalNode) {
				result.append(child.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitExpressionNotMinusPlus(GLiteParser.ExpressionNotMinusPlusContext ctx) {

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
			} else if(child instanceof GLiteParser.ExpressionLiteralContext) {
				result.append(visitExpressionLiteral((GLiteParser.ExpressionLiteralContext) child));
			} else if(child instanceof GLiteParser.ExpressionQualifiedIdContext) {
				result.append(visitExpressionQualifiedId((GLiteParser.ExpressionQualifiedIdContext) child));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitExpressionGroup(GLiteParser.ExpressionGroupContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("(");
		result.append(visitExpressionOr(ctx.expression));
		result.append(")");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitExpressionFunction(GLiteParser.ExpressionFunctionContext ctx) {

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

	@Override public StringBuilder visitExpressionLiteral(GLiteParser.ExpressionLiteralContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(ctx.literal.getText());

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override public StringBuilder visitExpressionQualifiedId(GLiteParser.ExpressionQualifiedIdContext ctx) {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String tableName = ctx.tableName.getText();
		String columnName = ctx.columnName.getText();

		result.append(escapeId(tableName));
		result.append(".");
		result.append(escapeId(columnName));

		addColumn(tableName, columnName);

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
