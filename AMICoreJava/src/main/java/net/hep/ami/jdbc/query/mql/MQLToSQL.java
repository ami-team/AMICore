package net.hep.ami.jdbc.query.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.FrgnKey;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.FrgnKeys;
import net.hep.ami.jdbc.reflexion.structure.*;

import net.hep.ami.utility.parser.*;

public class MQLToSQL
{
	/*---------------------------------------------------------------------*/

	private final String m_externalCatalog;
	private final String m_internalCatalog;
	private final String m_entity;

	/*---------------------------------------------------------------------*/

	private int m_maxPathLength = 4;

	/*---------------------------------------------------------------------*/

	private boolean m_inSelect = false;
	private boolean m_inInsert = false;
	private boolean m_inUpdate = false;
	private boolean m_inFunction = false;

	/*---------------------------------------------------------------------*/

	private List<String> m_from = new ArrayList<String>();
	private List<String> m_joins = new ArrayList<String>();

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
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		String result;

		MQLParser.MqlQueryContext mqlQueryContext = parser.mqlQuery();

		/**/ if(mqlQueryContext.select != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitSelectStatement(mqlQueryContext.select).toString();
		}
		else if(mqlQueryContext.insert != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitInsertStatement(mqlQueryContext.insert).toString();
		}
		else if(mqlQueryContext.update != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitUpdateStatement(mqlQueryContext.update).toString();
		}
		else if(mqlQueryContext.delete != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitDeleteStatement(mqlQueryContext.delete).toString();
		}
		else {
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

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

			if(m_joins.isEmpty() == false)
			{
				query.addWherePart(String.join(" AND ", m_joins));
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

		for(int i = 0; i < m_from.size(); i++) 
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

		List<String> tableFields = new ArrayList<String>();
		List<String> tableValues = new ArrayList<String>();

		List<String> externalFields = new ArrayList<String>();
		List<String> externalValues = new ArrayList<String>();

		List<PathList> pathListList = new ArrayList<>();

		m_inInsert = true;
		List<String> tmpFields = visitQIdTuple(context.qIdTuple(), pathListList);
		List<String> tmpExpressions = visitExpressionTuple(context.expressionTuple(), pathListList);
		m_inInsert = false;

		Map<String,FrgnKeys> tableForeignKeyFields = SchemaSingleton.getForwardFKs(m_internalCatalog, m_entity);

		System.out.println("tmpFields: " + tmpFields);
		System.out.println("tmpExpressions: " + tmpExpressions);
		System.out.println("number of fields: " + tmpFields.size());

		for (int i = 0; i < tmpFields.size(); i++) {

			QId tmpQId = new QId(tmpFields.get(i));

			if(tmpQId.getCatalog().equals(m_internalCatalog) && tmpQId.getTable().equals(m_entity) && SchemaSingleton.getColumns(m_internalCatalog, m_entity).get(tmpQId.getColumn()) != null)
			{
				tableFields.add(tmpFields.get(i));
				tableValues.add(tmpExpressions.get(i));
				tableForeignKeyFields.remove(tmpQId.getColumn().toLowerCase());
			}
			else
			{
				externalFields.add(tmpFields.get(i));
				externalValues.add(tmpExpressions.get(i));
			}
		}

		for(PathList pathList: pathListList)
		{
			System.out.println(pathList.getQId());
			System.out.println(pathList.getPaths());
		}

		System.out.println("keys to be resolved and put in tableFields and tableValues variables: " + tableForeignKeyFields.toString());
		System.out.println("fields/values to deal with: " + externalFields.toString());
		for (String key: tableForeignKeyFields.keySet()) 
		{
			System.out.println("doing: " + key);
			FrgnKey tmpFrgnKey = tableForeignKeyFields.get(key).get(0);
			System.out.println("table " + tmpFrgnKey.pkTable);
			System.out.println("field " + tmpFrgnKey.pkColumn);
			List<String> tmpWhere = new ArrayList<String>();

			for (int i = 0; i < externalFields.size(); i++)
			{
				if(true)
				{
					tmpWhere.add(externalFields.get(i) + " = " + externalValues.get(i));
				}
			}

			String tmpMQL = "SELECT " + tmpFrgnKey.pkTable + "." + tmpFrgnKey.pkColumn + " WHERE " + String.join(" AND ", tmpWhere);

			System.out.println("MQL tmp: " + tmpMQL);

			String tmpSQL = MQLToSQL.parse(tmpFrgnKey.pkInternalCatalog, tmpFrgnKey.pkTable, tmpMQL);

			tableFields.add(tmpFrgnKey.fkColumn);

			tableValues.add(tmpSQL);
		}

		System.out.println("-------------");


		result.append("INSERT INTO ")
		      .append(new QId(m_internalCatalog, m_entity, null).toString(QId.Deepness.TABLE))
		      .append(" (")
		      .append(String.join(", ", tableFields))
		      .append(") VALUES (")
		      .append(String.join(", ", tableValues))
		      .append(")")
		;

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
			Query query = new Query().addWherePart("(" + visitExpressionOr(context.expression, null).toString() + ")");

			if(m_joins.isEmpty() == false)
			{
				query.addWherePart(String.join(" AND ", m_joins));
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
		/*-----------------------------------------------------------------*/

		StringBuilder result = visitExpressionOr(context.expression, null);

		/*-----------------------------------------------------------------*/

		if(context.alias != null)
		{
			result.append(" AS " + QId.quote(context.alias.getText()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private List<String> visitQIdTuple(MQLParser.QIdTupleContext context, List<PathList> pathListList) throws Exception
	{
		List<String> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			if(child instanceof MQLParser.SqlQIdContext)
			{
				result.add((visitSqlQId((MQLParser.SqlQIdContext) child, pathListList).toString()));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private List<String> visitExpressionTuple(MQLParser.ExpressionTupleContext context, List<PathList> pathListList) throws Exception
	{
		List<String> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		ParseTree child;

		final int nb = context.getChildCount();

		for(int i = 0; i < nb; i++)
		{
			child = context.getChild(i);

			if(child instanceof MQLParser.ExpressionOrContext)
			{
				result.add((visitExpressionOr((MQLParser.ExpressionOrContext) child, pathListList).toString()));
			}
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

		if(m_inInsert == false)
		{
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
					List<String> localFromList = new ArrayList<String>();
					List<List<SchemaSingleton.FrgnKey>> paths = pathList.getPaths();
					boolean needOR = false;
					System.out.println("");
					System.out.println("local joins: " + localTableName);
					for (List<SchemaSingleton.FrgnKey> list : paths) 
					{
						List<String> localWhereList = new ArrayList<String>();
						for (SchemaSingleton.FrgnKey frgnKey : list) {
							//for order (performances), change algorithm here?
							if(!localFromList.contains(frgnKey.fkTable))
							{
								localFromList.add(frgnKey.fkTable);
							}
							if(!localFromList.contains(frgnKey.pkTable))
							{
								localFromList.add(frgnKey.pkTable);
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
							localJoins.append("(SELECT `" + localTableName + "`.`" + localTablePrimaryKey + "`, " + "`" + m_entity + "`" + ".`" + primaryKeyEntity + "` "
												+ "FROM `"+ String.join("`,`", localFromList) + "` "
												+ "WHERE "+ String.join(" AND ", localWhereList) + ")");
							localJoins.append(")");
							System.out.println("localWhereList: " + localWhereList);
						}
						System.out.println(localJoins.toString());
						needOR = true;
						}
					needAND = true;
			}
			localResult.append(" WHERE ");
			System.out.println("result: " + result.toString());
			localResult.append(result.toString());
			if(!localJoins.toString().isEmpty())
			{
				if(m_inSelect)
				{
					m_joins.add("(" + localJoins.toString() + ")");
					System.out.println("m_joins:" + m_joins.toString());
				}
				localResult.append(" AND ");
				localResult.append("(" + localJoins + ")");
			}
			localResult.append(")");
			if(!m_inSelect)
			{
				result = localResult;
			}
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

		/**/		StringBuilder result = new StringBuilder(context.functionName.getText());
		/**/
		/**/		result.append("(");
		/**/
		/**/		if(context.param1 != null)
		/**/		{
		/**/			result.append( "" ).append(visitExpressionOr(context.param1, pathListList));
		/**/
		/**/			if(context.param2 != null)
		/**/			{
		/**/				result.append(", ").append(visitExpressionOr(context.param2, pathListList));
		/**/			}
		/**/		}
		/**/
		/**/		result.append(")");

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

		String catalogName = (context.qId.catalogName != null) ? QId.unquote(context.qId.catalogName.getText()) : m_externalCatalog;

		String entityName = (context.qId.entityName != null) ? QId.unquote(context.qId.entityName.getText()) : /*-*/m_entity/*-*/;

		String fieldName = QId.unquote(context.qId.fieldName.getText());

		/*-----------------------------------------------------------------*/

		Collection<String> list;

		if("*".equals(fieldName) == false)
		{
			list = Arrays.asList(context.getText());
		}
		else
		{
			/*-------------------------------------------------------------*/

			if(m_inFunction == false)
			{
				list = SchemaSingleton.getColumnNames(catalogName, entityName);
			}
			else
			{
				list = Arrays.asList(SchemaSingleton.getPrimaryKey(catalogName, entityName));
			}

			/*-------------------------------------------------------------*/

			ParseTree child;

			final int nb = context.getChildCount();

			for(int i = 1; i < nb; i++)
			{
				child = context.getChild(i);

				if(child instanceof MQLParser.SqlBasicQIdContext)
				{
					System.out.println("{}" + visitSqlBasicQId((MQLParser.SqlBasicQIdContext) child).toString());
				}
			}

			/*-------------------------------------------------------------*/
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

	private QId visitSqlBasicQId(MQLParser.SqlBasicQIdContext context) throws Exception
	{
		return new QId(
			context.catalogName.getText(),
			context.entityName.getText(),
			context.fieldName.getText()
		);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitSqlLiteral(MQLParser.SqlLiteralContext context)
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
