package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.command.*;

public class GetElementInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetElementInfo(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String primaryFieldName = arguments.get("primaryFieldName");
		String primaryFieldValue = arguments.get("primaryFieldValue");

		if(catalog == null || entity == null || primaryFieldName == null || primaryFieldValue == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		StringBuilder result = querier.executeMQLQuery(entity, "SELECT `*` WHERE `" + primaryFieldName.replace("`", "``") + "` = '" + primaryFieldValue.replace("'", "''") + "'").toStringBuilder("element");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		Collection<SchemaSingleton.FrgnKeys> forwardLists = SchemaSingleton.getForwardFKs(catalog, entity).values();

		Collection<SchemaSingleton.FrgnKeys> backwardLists = SchemaSingleton.getBackwardFKs(catalog, entity).values();

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"linked_elements\">");

		_getLinkedEntities(result, catalog, entity, primaryFieldName, primaryFieldValue, forwardLists, FORWARD);

		_getLinkedEntities(result, catalog, entity, primaryFieldName, primaryFieldValue, backwardLists, BACKWARD);

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private final int FORWARD = 0;
	private final int BACKWARD = 1;

	/*---------------------------------------------------------------------*/

	private void _getLinkedEntities(StringBuilder result, String catalog, String entity, String primaryFieldName, String primaryFieldValue, Collection<SchemaSingleton.FrgnKeys> list, int direction)
	{
		String sql;
		String mql;
		String count;

		for(SchemaSingleton.FrgnKeys frgnKeys: list)
		{
			for(SchemaSingleton.FrgnKey frgnKey: frgnKeys)
			{
				try
				{
					RowSet rowSet = getQuerier(frgnKey.pkExternalCatalog).executeMQLQuery(entity, "SELECT COUNT(*) WHERE `" + catalog + "`.`" + entity + "`.`" + primaryFieldName + "` = '" + primaryFieldValue.replace("'", "''") + "'");

					sql = rowSet.getSQL();
					mql = rowSet.getMQL();

					count = rowSet.getAll().get(0).getValue(0);
				}
				catch(Exception e)
				{
					mql = "N/A";
					sql = "N/A";

					count = "N/A";
				}

				result.append("<row>")
				      .append("<field name=\"catalog\"><![CDATA[").append(direction == FORWARD ? frgnKey.pkExternalCatalog : frgnKey.fkExternalCatalog).append("]]></field>")
				      .append("<field name=\"entity\"><![CDATA[").append(direction == FORWARD ? frgnKey.pkTable : frgnKey.fkTable).append("]]></field>")
				      .append("<field name=\"sql\"><![CDATA[").append(sql.replace("COUNT(*)", "*")).append("]]></field>")
				      .append("<field name=\"mql\"><![CDATA[").append(mql.replace("COUNT(*)", "*")).append("]]></field>")
				      .append("<field name=\"count\"><![CDATA[").append(count).append("]]></field>")
				      .append("<field name=\"direction\"><![CDATA[").append(direction).append("]]></field>")
				      .append("</row>")
				;
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the element's information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" -primaryFieldName=\"\" -primaryFieldValue=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
