package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class SearchQuery extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public SearchQuery(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
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

		boolean count = arguments.containsKey("count");
		boolean links = arguments.containsKey("links");

		if(catalog == null || (raw == null && sql == null && (mql == null || entity == null)))
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		if(raw != null)
		{
			/*-------------------------------------------------------------*/

			for(String CATALOG: CatalogSingleton.resolve(catalog))
			{
				result.append(getQuerier(CATALOG, links).executeRawQuery(entity, raw).toStringBuilder());
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/

			Map<String, String> parts = Tokenizer.splitXQL((mql != null) ? mql.trim() : sql.trim());

			XQLSelect xqlSelect1 = new XQLSelect().addSelectPart("COUNT(*)")
				                                  .addFromPart(parts.get(Tokenizer.FROM))
				                                  .addWherePart(parts.get(Tokenizer.WHERE))
			;

			XQLSelect xqlSelect2 = new XQLSelect().addSelectPart(parts.get(Tokenizer.SELECT))
				                                  .addFromPart(parts.get(Tokenizer.FROM))
				                                  .addWherePart(parts.get(Tokenizer.WHERE))
			;

			/*-------------------------------------------------------------*/

			String groupBy2 = parts.get(Tokenizer.GROUP);
			if(groupBy == null && groupBy2 != null) {
				groupBy = groupBy2;
			}

			String orderBy2 = parts.get(Tokenizer.ORDER);
			if(orderBy == null && orderBy2 != null) {
				orderBy = orderBy2;
			}

			String orderWay2 = parts.get(Tokenizer.WAY);
			if(orderWay == null && orderWay2 != null) {
				orderWay = orderWay2;
			}

			String limit2 = parts.get(Tokenizer.LIMIT);
			if(limit == null && limit2 != null) {
				limit = limit2;
			}

			String offset2 = parts.get(Tokenizer.OFFSET);
			if(offset == null && offset2 != null) {
				offset = offset2;
			}

			/*-------------------------------------------------------------*/

			if(groupBy != null)
			{
				groupBy = QId.parseQId(groupBy, QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD);

				xqlSelect1.addExtraPart("GROUP BY " + groupBy);
				xqlSelect2.addExtraPart("GROUP BY " + groupBy);
			}

			if(orderBy != null)
			{
				orderBy = QId.parseQId(orderBy, QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD);

				xqlSelect2.addExtraPart("ORDER BY " + orderBy);

				if(orderWay != null)
				{
					xqlSelect2.addExtraPart(orderWay);
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

			/*-------------------------------------------------------------*/

			Querier querier;

			for(String CATALOG: CatalogSingleton.resolve(catalog))
			{
				/*---------------------------------------------------------*/

				querier = getQuerier(CATALOG, links);

				/*---------------------------------------------------------*/

				Integer totalNumberOfRows = null;

				if(count)
				{
					/*-------------------------------------------------------------*/

					RowSet rowSet1;

					/**/ if(sql != null)
					{
						rowSet1 = querier.executeSQLQuery(entity, xqlSelect1.toString());
					}
					else
					{
						rowSet1 = querier.executeMQLQuery(entity, xqlSelect1.toString());
					}

					/*-----------------------------------------------------*/

					totalNumberOfRows = rowSet1.getAll().get(0).getValue(0, (Integer) null);

					/*-----------------------------------------------------*/
				}

				/*---------------------------------------------------------*/

				RowSet rowSet2;

				/**/ if(sql != null)
				{
					rowSet2 = querier.executeSQLQuery(entity, xqlSelect2.toString());
				}
				else
				{
					rowSet2 = querier.executeMQLQuery(entity, xqlSelect2.toString());
				}

				/*---------------------------------------------------------*/

				result.append(rowSet2.toStringBuilder("query", totalNumberOfRows));

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple raw, SQL or MQL query (select mode). Parameter `catalog` can be a regular expression.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-raw=\"\" | -sql=\"\" | -mql=\"\") (-groupBy=\"\")? (-orderBy=\"\" (-orderWay=\"\")?)? (-limit=\"\" (-offset=\"\")?)? (-count)? (-links)?";
	}

	/*---------------------------------------------------------------------*/
}
