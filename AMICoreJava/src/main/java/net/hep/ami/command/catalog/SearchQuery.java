package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.Tokenizer;

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

		Querier querier = getQuerier(catalog, links);

		/*-----------------------------------------------------------------*/

		if(raw != null)
		{
			return querier.executeRawQuery(entity, raw).toStringBuilder();
		}

		/*-----------------------------------------------------------------*/

		Map<String, String> parts = Tokenizer.splitXQL((mql != null) ? mql.trim() : sql.trim());

		XQLSelect xqlSelect1 = new XQLSelect().addSelectPart("COUNT(*)")
		                                      .addFromPart(parts.get(Tokenizer.FROM))
		                                      .addWherePart(parts.get(Tokenizer.WHERE))
		;

		XQLSelect xqlSelect2 = new XQLSelect().addSelectPart(parts.get(Tokenizer.SELECT))
		                                      .addFromPart(parts.get(Tokenizer.FROM))
		                                      .addWherePart(parts.get(Tokenizer.WHERE))
		;

		/*-----------------------------------------------------------------*/

		String groupBy = parts.get(Tokenizer.GROUP);

		if(groupBy != null)
		{
			groupBy = QId.parseQId(orderBy, QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD);

			xqlSelect1.addExtraPart(" GROUP BY " + groupBy);
			xqlSelect2.addExtraPart(" GROUP BY " + groupBy);
		}

		/*-----------------------------------------------------------------*/

		String limit2 = parts.get(Tokenizer.LIMIT);
		if(limit == null && limit2 != null) {
			limit = limit2;
		}

		String offset2 = parts.get(Tokenizer.OFFSET);
		if(offset == null && offset2 != null) {
			offset = offset2;
		}

		/*-----------------------------------------------------------------*/

		if(orderBy != null)
		{
			orderBy = QId.parseQId(orderBy, QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD);

			xqlSelect2.addExtraPart(" ORDER BY " + orderBy);

			if(orderWay != null)
			{
				xqlSelect2.addExtraPart(" " + orderWay);
			}
		}

		if(limit != null)
		{
			xqlSelect2.addExtraPart(" LIMIT " + limit);

			if(offset != null)
			{
				xqlSelect2.addExtraPart(" OFFSET " + offset);
			}
		}

		/*-----------------------------------------------------------------*/

		Integer totalNumberOfRows = null;

		if(count)
		{
			/*-------------------------------------------------------------*/

			RowSet result2;

			/**/ if(sql != null)
			{
				result2 = querier.executeSQLQuery(entity, xqlSelect1.toString());
			}
			else
			{
				result2 = querier.executeMQLQuery(entity, xqlSelect1.toString());
			}

			/*-------------------------------------------------------------*/

			totalNumberOfRows = result2.getAll().get(0).getValue(0, (Integer) null);

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		RowSet result2;

		/**/ if(sql != null)
		{
			result2 = querier.executeSQLQuery(entity, xqlSelect2.toString());
		}
		else
		{
			result2 = querier.executeMQLQuery(entity, xqlSelect2.toString());
		}

		/*-----------------------------------------------------------------*/

		return result2.toStringBuilder("query", totalNumberOfRows);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple raw, SQL or MQL query (select mode).";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" (-raw=\"\" | -sql=\"\" | (-entity=\"\" -mql=\"\")) (-orderBy=\"\" (-orderWay=\"\")?)? (-limit=\"\" (-offset=\"\")?)?";
	}

	/*---------------------------------------------------------------------*/
}
