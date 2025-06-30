package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class SearchQuery extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SearchQuery(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String raw = arguments.get("raw");
		String sql = arguments.get("sql");
		String mql = arguments.get("mql");

		String groupBy = arguments.get("groupBy");

		String orderBy = arguments.get("orderBy");
		String orderWay = arguments.get("orderWay");

		String limit = arguments.get("limit");
		String offset = arguments.get("offset");

		int flags = 0;

		if(arguments.containsKey("hideBigContent")) {
			flags |= Querier.FLAG_HIDE_BIG_CONTENT;
		}

		if(arguments.containsKey("links")) {
			flags |= Querier.FLAG_SHOW_LINKS;
		}

		boolean count = arguments.containsKey("count");

		if(catalog == null || entity == null || (raw == null && sql == null && mql == null))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		int nbSuccess = 0;

		List<String> messages = new ArrayList<>();

		StringBuilder result = new StringBuilder();

		if(raw != null)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			for(String catalog2: CatalogSingleton.resolve(catalog))
			{
				try
				{
					result.append(getQuerier(catalog2, flags).executeRawQuery(entity, raw).toStringBuilder(catalog2, null));

					nbSuccess++;
				}
				catch(Exception e)
				{
					messages.add(e.getMessage());
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		else
		{
			/*--------------------------------------------------------------------------------------------------------*/

			Tokenizer.XQLParts parts = Tokenizer.splitXQL((mql != null) ? mql : sql);

			XQLSelect xqlSelect1 = new XQLSelect().addSelectPart(/*-----------*/ "COUNT(*)" /*-----------*/);

			XQLSelect xqlSelect2 = new XQLSelect().addSelectPart(String.join("", parts.getSelect()).trim());

			/*--------------------------------------------------------------------------------------------------------*/

			List<String> from = parts.getFrom();
			if(from != null)
			{
				String _from = String.join("", parts.getFrom()).trim();
				xqlSelect1.addFromPart(_from);
				xqlSelect2.addFromPart(_from);
			}

			List<String> where = parts.getWhere();
			if(where != null)
			{
				String _where = String.join("", parts.getWhere()).trim();

				xqlSelect1.addWherePart(_where);
				xqlSelect2.addWherePart(_where);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			List<String> groupBy2 = parts.getGroup();
			if(groupBy == null && groupBy2 != null) {
				groupBy = String.join("", groupBy2).trim();
			}

			List<String> orderBy2 = parts.getOrder();
			if(orderBy == null && orderBy2 != null) {
				orderBy = String.join("", orderBy2).trim();
			}

			List<String> orderWay2 = parts.getWay();
			if(orderWay == null && orderWay2 != null) {
				orderWay = String.join("", orderWay2).trim();
			}

			List<String> limit2 = parts.getLimit();
			if(limit == null && limit2 != null) {
				limit = String.join("", limit2).trim();
			}

			List<String> offset2 = parts.getOffset();
			if(offset == null && offset2 != null) {
				offset = String.join("", offset2).trim();
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(groupBy != null)
			{
				groupBy = QId.parseQId(groupBy, QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD);

				xqlSelect1.addExtraPart("GROUP BY " + groupBy);
				xqlSelect2.addExtraPart("GROUP BY " + groupBy);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(orderBy != null)
			{
				orderBy = QId.parseQId(orderBy, QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD);

				xqlSelect2.addExtraPart("ORDER BY " + orderBy);

				if(orderWay != null)
				{
					xqlSelect2.addExtraPart(/*----------*/ orderWay);
				}
			}

			if(limit != null)
			{
				xqlSelect2.addExtraPart("LIMIT " + limit);

				if(offset != null)
				{
					xqlSelect2.addExtraPart("OFFSET " + offset);
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			Querier querier;

			for(String catalog2: CatalogSingleton.resolve(catalog))
			{
				try
				{
					/*------------------------------------------------------------------------------------------------*/

					querier = getQuerier(catalog2, flags);

					/*------------------------------------------------------------------------------------------------*/

					Integer totalNumberOfRows = null;

					if(count)
					{
						/*--------------------------------------------------------------------------------------------*/

						RowSet rowSet1;

						/**/
						if(sql != null)
						{
							rowSet1 = querier.executeSQLQuery(entity, xqlSelect1.toString());
						}
						else
						{
							rowSet1 = querier.executeMQLQuery(entity, xqlSelect1.toString());
						}

						/*--------------------------------------------------------------------------------------------*/

						totalNumberOfRows = rowSet1.getAll().get(0).getValue(0, (Integer) null);

						/*--------------------------------------------------------------------------------------------*/
					}

					/*------------------------------------------------------------------------------------------------*/

					RowSet rowSet2;

					if(sql != null)
					{
						rowSet2 = querier.executeSQLQuery(entity, xqlSelect2.toString());
					}
					else
					{
						rowSet2 = querier.executeMQLQuery(entity, xqlSelect2.toString());
					}

					/*------------------------------------------------------------------------------------------------*/
/*
					if(count)
					{
						if(totalNumberOfRows > 0)
						{
							result.append(rowSet2.toStringBuilder(catalog2, totalNumberOfRows));
						}
					}
					else
					{
						result.append(rowSet2.toStringBuilder(catalog2, totalNumberOfRows));
					}
*/
					result.append(rowSet2.toStringBuilder(catalog2, totalNumberOfRows));

					/*------------------------------------------------------------------------------------------------*/

					nbSuccess++;

					/*------------------------------------------------------------------------------------------------*/
				}
				catch(Exception e)
				{
					messages.add(e.getMessage());
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		if(nbSuccess == 0 && messages.size() > 0)
		{
			throw new Exception(String.join(". ", messages));
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Execute a simple raw, SQL or MQL query (select mode). Parameter `catalog` can be a regular expression.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-raw=\"\" | -sql=\"\" | -mql=\"\") (-groupBy=\"\")? (-orderBy=\"\" (-orderWay=\"\")?)? (-limit=\"\" (-offset=\"\")?)? -(hideBigContent)? (-links)? (-count)?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
