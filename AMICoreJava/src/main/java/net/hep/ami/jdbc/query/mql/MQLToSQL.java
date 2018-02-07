package net.hep.ami.jdbc.query.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.query.mql.MQLParser.AColumnContext;
import net.hep.ami.jdbc.query.mql.MQLParser.ExpressionOrContext;
import net.hep.ami.jdbc.query.mql.MQLParser.QIdContext;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.Column;
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

		/**/ if(mqlQueryContext.m_select != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitSelectStatement(mqlQueryContext.m_select).toString();
		}
		else if(mqlQueryContext.m_insert != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitInsertStatement(mqlQueryContext.m_insert).toString();
		}
		else if(mqlQueryContext.m_update != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitUpdateStatement(mqlQueryContext.m_update).toString();
		}
		else if(mqlQueryContext.m_delete != null) {
			result = new MQLToSQL(externalCatalog, internalCatalog, entity).visitDeleteStatement(mqlQueryContext.m_delete).toString();
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

		if(context.m_columns != null)
		{
			query.setDistinct(context.m_distinct != null);

			query.addSelectPart(visitColumnList(context.m_columns).toString());
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

		if(context.m_limit != null)
		{
			extra.append(" LIMIT ").append(context.m_limit.getText());

			if(context.m_offset != null)
			{
				extra.append(" OFFSET ").append(context.m_offset.getText());
			}
		}

		/*-----------------------------------------------------------------*/

		query.addFromPart(new QId(m_internalCatalog, m_entity, null).toString(QId.FLAG_ENTITY));

		for(int i = 0; i < m_from.size(); i++) 
		{
			if(!m_from.get(i).equals(m_entity))
			{
				query.addFromPart(new QId(m_internalCatalog, m_from.get(i), null).toString(QId.FLAG_ENTITY));
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

		List<String> fieldsInDefaultEntity = new ArrayList<String>();
		List<String> valuesInDefaultEntity = new ArrayList<String>();

		List<String> fieldsNotInDefaultEntity = new ArrayList<String>();
		List<String> valuesNotInDefaultEntity = new ArrayList<String>();

		List<PathList> pathListList = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		m_inInsert = true;
		List<String> fields = visitQIdTuple(context.qIdTuple(), pathListList);
		List<String> expressions = visitExpressionTuple(context.expressionTuple(), pathListList);
		m_inInsert = false;

		/*-----------------------------------------------------------------*/

		Map<String, Column> columnsInDefaultEntity = SchemaSingleton.getColumns(m_internalCatalog, m_entity);

		Map<String, FrgnKeys> frgnKeysInDefaultEntity = SchemaSingleton.getForwardFKs(m_internalCatalog, m_entity);

		/*-----------------------------------------------------------------*/

		List<String> frgnKeysAlreadyTreated = new ArrayList<>();

		for(int i = 0; i < fields.size(); i++)
		{
			QId tmpQId = new QId(fields.get(i));

			if(m_internalCatalog.equalsIgnoreCase(tmpQId.getCatalog())
			   &&
			   m_entity.equalsIgnoreCase(tmpQId.getEntity())
			   &&
			   columnsInDefaultEntity.get(tmpQId.getField()) != null
			 ) {
				if(frgnKeysInDefaultEntity.containsKey(tmpQId.getField()))
				{
					frgnKeysAlreadyTreated.add(tmpQId.getField());
				}

				fieldsInDefaultEntity.add(fields.get(i));
				valuesInDefaultEntity.add(expressions.get(i));
			}
			else
			{
				fieldsNotInDefaultEntity.add(fields.get(i));
				valuesNotInDefaultEntity.add(expressions.get(i));
			}
		}

		System.out.println("columnsInDefaultEntity: " + columnsInDefaultEntity);
		System.out.println("frgnKeysInDefaultEntity: " + frgnKeysInDefaultEntity);

		System.out.println("frgnKeysAlreadyTreated: " + frgnKeysAlreadyTreated);
		System.out.println("tableFields: " + fieldsInDefaultEntity);
		System.out.println("tableValues: " + valuesInDefaultEntity);
		System.out.println("externalFields: " + fieldsNotInDefaultEntity);
		System.out.println("externalValues: " + valuesNotInDefaultEntity);

		System.out.println("***************************");

		/*-----------------------------------------------------------------*/

		System.out.println("keys to be resolved and put in tableFields and tableValues variables: " + frgnKeysInDefaultEntity.toString());
		System.out.println("fields/values to deal with: " + fieldsNotInDefaultEntity.toString());
		for(String key: frgnKeysInDefaultEntity.keySet()) 
		{
			System.out.println("doing: " + key);
			FrgnKey tmpFrgnKey = frgnKeysInDefaultEntity.get(key).get(0);
			System.out.println("table " + tmpFrgnKey.pkTable);
			System.out.println("field " + tmpFrgnKey.pkColumn);
			List<String> tmpWhere = new ArrayList<String>();

			for(int i = 0; i < fieldsNotInDefaultEntity.size(); i++)
			{
				boolean todo = false;
				for(PathList pathList: pathListList)
				{
					for(List<FrgnKey> path: pathList.getPaths())
					{
						if(path.isEmpty() == false)
						{
							
							System.out.println(path.get(0).fkColumn + " <> " + tmpFrgnKey.fkColumn + "  ::  " + path.get(0));

							if(path.get(0).fkInternalCatalog.equals(tmpFrgnKey.fkInternalCatalog)
							   &&
							   path.get(0).fkTable.equals(tmpFrgnKey.fkTable)
							   &&
							   path.get(0).fkColumn.equals(tmpFrgnKey.fkColumn)
							 ) {
								todo = true;
								System.out.println("todo true ");
							}
						}
					}
				}
				if(todo)
				{
					tmpWhere.add(fieldsNotInDefaultEntity.get(i) + " = " + valuesNotInDefaultEntity.get(i));
				}
			}

			if(!tmpWhere.isEmpty())
			{
				String tmpMQL = "SELECT " + tmpFrgnKey.pkTable + "." + tmpFrgnKey.pkColumn + " WHERE " + String.join(" AND ", tmpWhere);

				System.out.println("MQL tmp: " + tmpMQL);

				String tmpSQL = MQLToSQL.parse(tmpFrgnKey.pkInternalCatalog, tmpFrgnKey.pkTable, tmpMQL);

				fieldsInDefaultEntity.add(tmpFrgnKey.fkColumn);

				valuesInDefaultEntity.add(tmpSQL);
			}
		}

		System.out.println("-------------");


		result.append("INSERT INTO ")
		      .append(new QId(m_internalCatalog, m_entity, null).toString(QId.FLAG_ENTITY))
		      .append(" (")
		      .append(String.join(", ", fieldsInDefaultEntity))
		      .append(") VALUES (")
		      .append(String.join(", ", valuesInDefaultEntity))
		      .append(")")
		;

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitUpdateStatement(MQLParser.UpdateStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();
		StringBuilder tmpSet = new StringBuilder();

		List<PathList> pathListList = new ArrayList<>();

		m_inUpdate = true;
		List<String> tmpFields = visitQIdTuple(context.qIdTuple(), pathListList);
		List<String> tmpExpressions = visitExpressionTuple(context.expressionTuple(), pathListList);
		m_inUpdate = false;

		for(int i = 0; i < tmpFields.size(); i++)
		{
			tmpSet.append(tmpFields.get(i) + " = " + tmpExpressions.get(i));
		}

		result.append("UPDATE ")
		      .append(new QId(m_internalCatalog, m_entity, null).toString(QId.FLAG_ENTITY))
		      .append(" SET ").append(String.join(", ", tmpSet));

		if(context.expression != null)
		{
			Query query = new Query().addWherePart("(" + visitExpressionOr(context.expression, null).toString() + ")");

			if(m_joins.isEmpty() == false)
			{
				query.addWherePart(String.join(" AND ", m_joins));
			}

			result.append(" WHERE ").append(query.getWherePart());
		}
		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitDeleteStatement(MQLParser.DeleteStatementContext context) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("DELETE FROM ").append(new QId(m_internalCatalog, m_entity, null).toString(QId.FLAG_ENTITY));

		/*-----------------------------------------------------------------*/

		if(context.m_expression != null)
		{
			Query query = new Query().addWherePart("(" + visitExpressionOr(context.m_expression, null).toString() + ")");

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
		List<String> result = new ArrayList<>();

		for(AColumnContext child: context.m_columns)
		{
			result.add(visitAColumnExpression(child).toString());
		}

		return new StringBuilder(String.join(",", result));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitAColumnExpression(MQLParser.AColumnContext context) throws Exception
	{
		/*-----------------------------------------------------------------*/

		StringBuilder result = visitExpressionOr(context.m_expression, null);

		/*-----------------------------------------------------------------*/

		if(context.m_alias != null)
		{
			result.append(" AS " + new QId(context.m_alias.getText()).toString(QId.FLAG_FIELD));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private List<String> visitQIdTuple(MQLParser.QIdTupleContext context, List<PathList> pathListList) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(QIdContext child: context.m_qIds)
		{
			result.add(visitQId(child, pathListList).toString());
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private List<String> visitExpressionTuple(MQLParser.ExpressionTupleContext context, List<PathList> pathListList) throws Exception
	{
		List<String> result = new ArrayList<>();

		for(ExpressionOrContext child: context.m_expressions)
		{
			result.add(visitExpressionOr(child, pathListList).toString());
		}

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
		StringBuilder result = new StringBuilder();

		List<PathList> pathListList = new ArrayList<>();

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
			for(PathList pathList: pathListList) 
			{
					String localTableName = pathList.getQId().getEntity();
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
					for(List<SchemaSingleton.FrgnKey> list: paths) 
					{
						List<String> localWhereList = new ArrayList<String>();
						for(SchemaSingleton.FrgnKey frgnKey: list) {
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

		if(context.m_operator != null)
		{
			result.append(context.m_operator.getText());
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
			result.append(visitExpressionLiteral((MQLParser.ExpressionLiteralContext) child, pathListList));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionGroup(MQLParser.ExpressionGroupContext context, List<PathList> pathListList) throws Exception
	{
		return new StringBuilder().append("(")
		                          .append(visitExpressionOr(context.m_expression, pathListList))
		                          .append(")")
		;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionFunction(MQLParser.ExpressionFunctionContext context, List<PathList> pathListList) throws Exception
	{
		m_inFunction = true;

		/**/		StringBuilder result = new StringBuilder(context.m_functionName.getText());
		/**/
		/**/		result.append("(");
		/**/
		/**/		if(context.m_param1 != null)
		/**/		{
		/**/			result.append( "" ).append(visitExpressionOr(context.m_param1, pathListList));
		/**/
		/**/			if(context.m_param2 != null)
		/**/			{
		/**/				result.append(", ").append(visitExpressionOr(context.m_param2, pathListList));
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
		return visitQId(context.m_qId, pathListList);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitExpressionLiteral(MQLParser.ExpressionLiteralContext context, List<PathList> pathListList) throws Exception
	{
		return visitLiteral(context.m_literal, pathListList);
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitQId(MQLParser.QIdContext context, List<PathList> pathListList) throws Exception
	{
		List<StringBuilder> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		QId qid = new QId(context.getText(), QId.FLAG_FIELD);

		/*-----------------------------------------------------------------*/

		String catalogName = (qid.getCatalog() != null) ? qid.getCatalog() : m_externalCatalog;

		String entityName = (qid.getEntity() != null) ? qid.getEntity() : /*-*/m_entity/*-*/;

		String fieldName = qid.getField();

		/*-----------------------------------------------------------------*/

		List<QId> list;

		if("*".equals(fieldName))
		{
			if(m_inFunction)
			{
				list = Arrays.asList(qid.setField(SchemaSingleton.getPrimaryKey(catalogName, entityName)));
			}
			else
			{
				list = new ArrayList<>();

				for(String fieldNAME: SchemaSingleton.getColumnNames(catalogName, entityName))
				{
					list.add(new QId(catalogName, entityName, fieldNAME, qid.getPath()));
				}
			}
		}
		else
		{
			list = Arrays.asList(qid);
		}

		/*-----------------------------------------------------------------*/

		PathList pathList;

		for(QId qId: list)
		{
			pathList = AutoJoinSingleton.resolve(m_externalCatalog, m_entity, qId, m_maxPathLength);

			result.add(pathList.getQId().toStringBuilder());

			pathListList.add(pathList);
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder(String.join(", ", result));
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder visitLiteral(MQLParser.LiteralContext context, List<PathList> pathListList) throws Exception
	{
		return new StringBuilder(context.getText());
	}

	/*---------------------------------------------------------------------*/
}
