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
	private int m_maxPathLength = 5;

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
//System.out.println(query);
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
		m_inSelect = true;
		/*-----------------------------------------------------------------*/

		if(context.columns != null)
		{
			query.setDistinct(context.distinct != null);

			query.addSelectPart(visitColumnList(context.columns).toString());
			//query.addSelectPart(visitColumnList(context.columns).toString());
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
				//System.out.println(m_joins);
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

		System.out.println(query.toString(extra.toString()));
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
				result.append((visitColumnExpression((MQLParser.ColumnContext) child).toString()));
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
		//todo: modify in update and remove element key fields and key values arguments for a unique MQL statement  argument (it will allow much more possibilities, like other comparator than equality)
		StringBuilder testResult = new StringBuilder();
		String primaryKeyEntity = SchemaSingleton.getPrimaryKey(m_externalCatalog, m_entity);
		testResult.append("`" + m_entity + "`.`" +primaryKeyEntity+ "` IN (SELECT `" + m_entity + "`.`" +primaryKeyEntity+ "` FROM `" + m_entity + "` ");
		StringBuilder testJoins = new StringBuilder();
		int cpt1 = 0;
		for (PathList pathList : pathListList) 
		{
				String tmpTable = "`" +pathList.getQId().getTable()+"`" ;
				String tmpPkTable = pathList.getQId().getTable();
				String tmpPkCatalog = pathList.getQId().getCatalog();
				if(!tmpPkTable.equals(m_entity))
				{
					testResult.append(", `" +pathList.getQId().getTable() + "` ");
					if(m_inSelect && !m_from.contains(pathList.getQId().getTable()))
					{
						m_from.add(pathList.getQId().getTable());
					}
				}
				String primaryKeyTable = SchemaSingleton.getPrimaryKey(tmpPkCatalog, tmpPkTable);
				if(cpt1 > 0)
				{
					testJoins.append("  AND ");
				}
				int cpt2 = 0;
				List<String> fromList = new ArrayList<String>();
				List<List<FrgnKey>> paths= pathList.getPaths();
				for (List<FrgnKey> list : paths) 
				{
					if(list.size() <= m_maxPathLength)
					{
						String tmpFrom = "";
						String tmpWhere = "";
						int cpt3 = 0;
						for (FrgnKey frgnKey : list) {
							if(cpt3 > 0)
							{
								tmpWhere += " AND ";
							}
							//for order, change algorithm here?
							if(!fromList.contains(frgnKey.fkTable))
							{
								fromList.add(frgnKey.fkTable);
							}
							if(!fromList.contains(frgnKey.pkTable))
							{
								fromList.add(frgnKey.pkTable);
							}
							//here too ?
							tmpWhere += frgnKey.toString();
							cpt3++;
						}
						if(!tmpWhere.isEmpty())
						{
							if(cpt2 > 0)
							{
								testJoins.append(" OR ");
							}
							testJoins.append("(");
							testJoins.append("(" + tmpTable + ".`" +primaryKeyTable+ "`, `" + m_entity + "`.`" +primaryKeyEntity+ "`) IN ");
							for (int cpt4 = 0; cpt4 < fromList.size(); cpt4++) 
							{
								if(cpt4 > 0)
								{
									tmpFrom += ",";
								}
								tmpFrom += "`" + fromList.get(cpt4) + "`";
							}
							testJoins.append("(SELECT `" + tmpPkTable + "`.`" +primaryKeyTable+ "`, " + "`" + m_entity + "`" + ".`" +primaryKeyEntity+ "` FROM "+ tmpFrom + " WHERE "+tmpWhere+ ")");
							testJoins.append(")");
						}
						//print
						System.out.println(testJoins.toString());
						cpt2++;
					}
					}
			cpt1++;
		}
		testResult.append(" WHERE ");
		testResult.append(result.toString());
		if(!testJoins.toString().isEmpty())
		{
			if(m_inSelect)
			{
				if(!m_joins.isEmpty())
				{
					m_joins += " AND ";
				}
				m_joins += "("+testJoins.toString()+")";
				//print
				System.out.println(m_joins);
			}
			testResult.append(" AND ");
			testResult.append("(" + testJoins + ")");
		}
		testResult.append(")");
		if(!m_inSelect)
		{
			result = testResult;
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
		/**/	                                          .append(context.distinct != null ? "DISTINCT " : "").append(visitExpressionOr(context.expression, pathListList))
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
			pathList = AutoJoinSingleton.resolve(m_externalCatalog, m_entity, qId);
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
