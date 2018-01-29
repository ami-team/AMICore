package net.hep.ami.jdbc.query.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.FrgnKey;
import net.hep.ami.jdbc.reflexion.structure.*;

import net.hep.ami.utility.parser.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/


	/*---------------------------------------------------------------------*/

	private final String m_externalCatalog;
	private final String m_internalCatalog;
	private final String m_entity;

	/*---------------------------------------------------------------------*/

	private boolean m_inSelect = false;
	private boolean m_inFunction = false;
	
	private String m_joins = "";
	private List<String> m_from = new ArrayList<String>();
	private int m_maxPathLength = 6;

	/*---------------------------------------------------------------------*/

	private MQLToSQL(String externalCatalog, String internalCatalog, String entity)
	{
		m_externalCatalog = externalCatalog;
		m_internalCatalog = internalCatalog;
		m_entity = entity;
	}

	/*---------------------------------------------------------------------*/

	public static String parseSelect(String catalog, String entity, String query) throws Exception
	{
		return parseSelect(catalog, SchemaSingleton.externalCatalogToInternalCatalog(catalog), entity, query);
	}

	/*---------------------------------------------------------------------*/

	public static String parseSelect(String externalCatalog, String internalCatalog, String entity, String query) throws Exception
	{
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

	public static String parseInsert(String catalog, String entity, String query) throws Exception
	{
		return parseInsert(catalog, SchemaSingleton.externalCatalogToInternalCatalog(catalog), entity, query);
	}

	/*---------------------------------------------------------------------*/

	public static String parseInsert(String externalCatalog, String internalCatalog, String entity, String query) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		String result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitInsertStatement(parser.insertStatement()).toString();

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String parseUpdate(String catalog, String entity, String query) throws Exception
	{
		return parseUpdate(catalog, SchemaSingleton.externalCatalogToInternalCatalog(catalog), entity, query);
	}

	/*---------------------------------------------------------------------*/

	public static String parseUpdate(String externalCatalog, String internalCatalog, String entity, String query) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		String result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitUpdateStatement(parser.updateStatement()).toString();

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String parseDelete(String catalog, String entity, String query) throws Exception
	{
		return parseDelete(catalog, SchemaSingleton.externalCatalogToInternalCatalog(catalog), entity, query);
	}

	/*---------------------------------------------------------------------*/

	public static String parseDelete(String externalCatalog, String internalCatalog, String entity, String query) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		String result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitDeleteStatement(parser.deleteStatement()).toString();

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
		m_inSelect = true;
		/*-----------------------------------------------------------------*/

		if(context.columns != null)
		{
			query.setDistinct(context.distinct != null);

			query.addSelectPart(visitColumnList(context.columns).toString());
		}

		/*-----------------------------------------------------------------*/
		m_inSelect = false;
		/*-----------------------------------------------------------------*/

		if(context.expression != null)
		{
			query.addWherePart("(" + visitExpressionOr(context.expression, null).toString() + ")");

			if(!m_joins.equals(""))
			{
				query.addWherePart(m_joins.toString());
			}
		}

		/*-----------------------------------------------------------------*/
/*
		if(context.orderBy != null)
		{
			extra.append(" ORDER BY ").append(AutoJoinSingleton.resolve(m_islets, m_externalCatalog, m_entity, context.orderBy.getText(), null).toString());

			if(context.orderWay != null)
			{
				extra.append(" ").append(context.orderWay.getText());
			}
		}
*/
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

		query.addFromPart(new QId(m_internalCatalog, m_entity, null).toString(QId.Deepness.TABLE));
		for (int i = 0; i < m_from.size(); i++) 
		{
			if(!m_from.get(i).equals(m_entity))
			{
				query.addFromPart(new QId(m_internalCatalog, m_from.get(i), null).toString(QId.Deepness.TABLE));
			}
					
		}
		
		/*-----------------------------------------------------------------*/
		System.out.println("");
		System.out.println("query: " + query.toString(extra.toString()));
		return new StringBuilder(query.toString(extra.toString()));

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitInsertStatement(MQLParser.InsertStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		
		
		

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitUpdateStatement(MQLParser.UpdateStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitDeleteStatement(MQLParser.DeleteStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("DELETE FROM ").append(new QId(m_internalCatalog, m_entity, null).toString(QId.Deepness.TABLE));

		/*-----------------------------------------------------------------*/

		if(context.expression != null)
		{
			//todo
			Query query = new Query().addWherePart("(" + visitExpressionOr(context.expression, null).toString() + ")");

			if(m_joins.isEmpty() == false)
			{
				query.addWherePart(m_joins.toString());
			}

			result.append(" WHERE ").append(query.getWherePart());
		}

		/*-----------------------------------------------------------------*/

		return result;

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

			/**/ if(child instanceof MQLParser.AColumnContext)
			{
				result.append((visitAColumnExpression((MQLParser.AColumnContext) child).toString()));
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

	private StringBuilder visitAColumnExpression(MQLParser.AColumnContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(context.expression, null));

		/*-----------------------------------------------------------------*/

		if(context.alias != null)
		{
			result.append(" AS " + QId.quote(context.alias.getText()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitQIdList(MQLParser.QIdListContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.AQIdContext)
			{
				result.append((visitAQId((MQLParser.AQIdContext) child).toString()));
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

	private StringBuilder visitAQId(MQLParser.AQIdContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitSqlQId(context.qId, null));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionList(MQLParser.ExpressionListContext context, List<PathList> pathListList) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.AnExpressionContext)
			{
				result.append((visitAExpression((MQLParser.AnExpressionContext) child, pathListList).toString()));
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

	private StringBuilder visitAExpression(MQLParser.AnExpressionContext context, List<PathList> pathListList) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(visitExpressionOr(context.expression, pathListList));

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionOr(MQLParser.ExpressionOrContext context, @Nullable List<PathList> pathListList) throws Exception
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
		List<PathList> pathListList = new ArrayList<>();

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			/**/ if(child instanceof MQLParser.ExpressionAddSubContext)
			{
				result.append(visitExpressionAddSub((MQLParser.ExpressionAddSubContext) child, pathListList));
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
		String primaryKeyEntity = SchemaSingleton.getPrimaryKey(m_externalCatalog, m_entity);
		StringBuilder localResult = new StringBuilder();
		StringBuilder localJoins = new StringBuilder();
		localResult.append("`" + m_entity + "`.`" + primaryKeyEntity + "` IN (SELECT `" + m_entity + "`.`" + primaryKeyEntity + "` FROM `" + m_entity + "` ");
		boolean needAND = false;
		for (PathList pathList : pathListList) 
		{
				String localTableName = pathList.getQId().getTable();
				String localCatalogName = pathList.getQId().getCatalog();
				if(!localTableName.equals(m_entity))
				{
					localResult.append(", `" + localTableName + "` ");
					if(m_inSelect && !m_from.contains(localTableName))
					{
						m_from.add(localTableName);
					}
				}
				String localTablePrimaryKey = SchemaSingleton.getPrimaryKey(localCatalogName, localTableName);
				if(needAND)
				{
					localJoins.append("  AND ");
				}
				List<String> fromList = new ArrayList<String>();
				List<List<FrgnKey>> paths = pathList.getPaths();
				boolean needOR = false;
				System.out.println("");
				System.out.println("local joins: " + localTableName);
				for (List<FrgnKey> list : paths) 
				{
					List<String> localFromList = new ArrayList<String>();
					List<String> localWhereList = new ArrayList<String>();
					for (FrgnKey frgnKey : list) {
						//for order (performances), change algorithm here?
						if(!fromList.contains(frgnKey.fkTable))
						{
							fromList.add(frgnKey.fkTable);
						}
						if(!fromList.contains(frgnKey.pkTable))
						{
							fromList.add(frgnKey.pkTable);
						}
						//here as well ?
						localWhereList.add(frgnKey.toString());
					}
					if(!localWhereList.isEmpty())
					{
						if(needOR)
						{
							localJoins.append(" OR ");
						}
						localJoins.append("(");
						localJoins.append("(`" + localTableName + "`.`" + localTablePrimaryKey + "`, `" + m_entity + "`.`" + primaryKeyEntity + "`) IN ");
						// change iteration here (for joins ?)
						for (int cpt = 0; cpt < fromList.size(); cpt++) 
						{
							if(cpt > 0)
							{
								localFromList.add(",");
							}
							localFromList.add("`" + fromList.get(cpt) + "`");
						}
						localJoins.append("(SELECT `" + localTableName + "`.`" + localTablePrimaryKey + "`, " + "`" + m_entity + "`" + ".`" + primaryKeyEntity + "` "
											+ "FROM "+ String.join("", localFromList) + " "
											+ "WHERE "+ String.join(" AND ", localWhereList) + ")");
						localJoins.append(")");
						System.out.println("localWhereList: " + localWhereList);
					}
					//print
					System.out.println(localJoins.toString());
					needOR = true;
					}
				needAND = true;
		}
		localResult.append(" WHERE ");
		localResult.append(result.toString());
		if(!localJoins.toString().isEmpty())
		{
			if(m_inSelect)
			{
				if(!m_joins.isEmpty())
				{
					m_joins += " AND ";
				}
				m_joins += "(" + localJoins.toString() + ")";
				//print
				System.out.println("m_joins:" + m_joins);
			}
			localResult.append(" AND ");
			localResult.append("(" + localJoins + ")");
		}
		localResult.append(")");
		if(!m_inSelect)
		{
			result = localResult;
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionAddSub(MQLParser.ExpressionAddSubContext context, List<PathList> pathListList) throws Exception
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
				result.append(visitExpressionMulDiv((MQLParser.ExpressionMulDivContext) child, pathListList));
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

	private StringBuilder visitExpressionMulDiv(MQLParser.ExpressionMulDivContext context, List<PathList> pathListList) throws Exception
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
				result.append(visitExpressionNotPlusMinus((MQLParser.ExpressionNotPlusMinusContext) child, pathListList));
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

	private StringBuilder visitExpressionNotPlusMinus(MQLParser.ExpressionNotPlusMinusContext context, List<PathList> pathListList) throws Exception
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
			result.append(visitExpressionGroup((MQLParser.ExpressionGroupContext) child, pathListList));
		}
		else if(child instanceof MQLParser.ExpressionFunctionContext)
		{
			result.append(visitExpressionFunction((MQLParser.ExpressionFunctionContext) child, pathListList));
		}
		else if(child instanceof MQLParser.ExpressionQIdContext)
		{
			result.append(visitExpressionQId((MQLParser.ExpressionQIdContext) child, pathListList));
		}
		else if(child instanceof MQLParser.ExpressionLiteralContext)
		{
			result.append(visitExpressionLiteral((MQLParser.ExpressionLiteralContext) child));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext context, List<PathList> pathListList) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(visitExpressionOr(context.expression, pathListList))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context, List<PathList> pathListList) throws Exception
	{
		m_inFunction = true;

		/**/	StringBuilder result = new StringBuilder().append(context.functionName.getText())
		/**/	                                          .append("(")
		/**/	                                          .append(context.distinct != null ? "DISTINCT " : "").append(visitExpressionList(context.expressions, pathListList))
		/**/	                                          .append(")")
		/**/	;

		m_inFunction = false;

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionQId(MQLParser.ExpressionQIdContext context, List<PathList> pathListList) throws Exception
	{
		return visitSqlQId(context.qId, pathListList);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext context) throws Exception
	{
		return visitSqlLiteral(context.literal);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlQId(MQLParser.SqlQIdContext context, List<PathList> pathListList) throws Exception
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

		PathList pathList;

		for(String qId: list)
		{
			pathList = AutoJoinSingleton.resolve(m_externalCatalog, m_entity, qId, m_maxPathLength);
			result.add(pathList.getQId().toString());
			pathListList.add(pathList);
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
