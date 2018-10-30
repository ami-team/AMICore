package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_ADMIN", visible = true, secured = false)
public class GetElementInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetElementInfo(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
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

	private void _getLinkedEntities(StringBuilder result, String catalog, String entity, String primaryFieldName, String primaryFieldValue, Collection<SchemaSingleton.FrgnKeys> list, int mode)
	{
		String linkedCatalog;
		String linkedEntity;
		String constraint;
		String sql;
		String mql;
		String count;
		String direction;

		for(SchemaSingleton.FrgnKeys frgnKeys: list)
		{
			for(SchemaSingleton.FrgnKey frgnKey: frgnKeys)
			{
				constraint = "{`" + frgnKey.fkExternalCatalog + "`.`" + frgnKey.fkTable +  "`.`" + frgnKey.fkColumn + "`}";

				switch(mode)
				{
					case FORWARD:
						linkedCatalog = frgnKey.pkExternalCatalog;
						linkedEntity = frgnKey.pkTable;
						direction = "forward";
						break;

					case BACKWARD:
						linkedCatalog = frgnKey.fkExternalCatalog;
						linkedEntity = frgnKey.fkTable;
						direction = "backward";
						break;

					default:
						return;
				}

				try
				{
					RowSet rowSet = getQuerier(linkedCatalog).executeMQLQuery(linkedEntity, "SELECT COUNT(*) WHERE `" + catalog + "`.`" + entity + "`.`" + primaryFieldName + "`" + constraint + " = '" + primaryFieldValue.replace("'", "''") + "'");

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
				      .append("<field name=\"catalog\"><![CDATA[").append(linkedCatalog).append("]]></field>")
				      .append("<field name=\"entity\"><![CDATA[").append(linkedEntity).append("]]></field>")
				      .append("<field name=\"constraint\"><![CDATA[").append(frgnKey.fkColumn).append("]]></field>")
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
