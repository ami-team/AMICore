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

	/*---------------------------------------------------------------------*/

	private final String m_externalCatalog;
	private final String m_internalCatalog;
	private final String m_entity;

	/*---------------------------------------------------------------------*/

	private boolean m_inFunction = false;

	/*---------------------------------------------------------------------*/

	private MQLToSQL(String externalCatalog, String internalCatalog, String entity)
	{
		m_externalCatalog = externalCatalog;
		m_internalCatalog = internalCatalog;
		m_entity = entity;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String catalog, String entity, String query) throws Exception
	{
		return parse(catalog, SchemaSingleton.externalCatalogToInternalCatalog(catalog), entity, query);
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String externalCatalog, String internalCatalog, String entity, String query) throws Exception
	{
System.out.println(query);
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		String result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitSelectStatement(parser.selectStatement()).toString();

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
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
			query.addWherePart("(" + visitExpressionOr(context.expression).toString() + ")");
		}

		/*-----------------------------------------------------------------*/

		if(context.orderBy != null)
		{
			extra.append(" ORDER BY ").append(AutoJoinSingleton.resolve(m_islets, m_externalCatalog, m_entity, context.orderBy.getText(), null).toString());

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

		query.addFromPart(new QId(m_internalCatalog, m_entity, null).toString(QId.Deepness.TABLE)).addWholeQuery(m_islets.toQuery());

		/*-----------------------------------------------------------------*/

		return new StringBuilder(query.toString(extra.toString()));

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
			result.append(" AS " + QId.quote(context.alias.getText()));
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

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context) throws Exception
	{
		m_inFunction = true;

		/**/	StringBuilder result = new StringBuilder().append(context.functionName.getText())
		/**/	                                          .append("(")
		/**/	                                          .append(context.distinct != null ? "DISTINCT " : "").append(visitExpressionOr(context.expression))
		/**/	                                          .append(")")
		/**/	;

		m_inFunction = false;

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

		String catalogName = (context.catalogName != null) ? QId.unquote(context.catalogName.getText()) : m_externalCatalog;

		String entityName = (context.entityName != null) ? QId.unquote(context.entityName.getText()) : /*-*/m_entity/*-*/;

		String fieldName = QId.unquote(context.fieldName.getText());

		/*-----------------------------------------------------------------*/

		Collection<String> list;

		if("*".equals(fieldName) == false)
		{
			list = Arrays.asList(context.getText());
		}
		else
		{
			if(m_inFunction == false)
			{
				list = SchemaSingleton.getColumnNames(catalogName, entityName);
			}
			else
			{
				list = Arrays.asList(SchemaSingleton.getPrimaryKey(catalogName, entityName));
			}
		}

		/*-----------------------------------------------------------------*/

		QId resolvedQId;

		for(String qId: list)
		{
			resolvedQId = AutoJoinSingleton.resolve(
				m_islets,
				m_externalCatalog,
				m_entity,
				qId,
				null
			);

			result.add(resolvedQId.toString());
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
