package net.hep.ami.command.catalog;

import java.sql.*;
import java.util.*;
import java.util.stream.*;

import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.command.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetElementInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetElementInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		String primaryFieldName = arguments.get("primaryFieldName");
		String primaryFieldValue = arguments.get("primaryFieldValue");

		int flags = Querier.FLAG_SHOW_LINKS;

		if(arguments.containsKey("hideBigContent"))
		{
			flags |= Querier.FLAG_HIDE_BIG_CONTENT;
		}

		if(catalog == null || entity == null || primaryFieldName == null || primaryFieldValue == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier(catalog, flags);

		/*------------------------------------------------------------------------------------------------------------*/
		/*                                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		Object primaryFieldValueObject;

		int jdbcType = SchemaSingleton.getFieldInfo(catalog, entity, primaryFieldName).jdbcType;

		if(jdbcType == Types.TINYINT || jdbcType == Types.SMALLINT || jdbcType == Types.INTEGER)
		{
			primaryFieldValueObject = Integer.parseInt(primaryFieldValue);
		}
		else
		{
			primaryFieldValueObject = /*------------*/(primaryFieldValue);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = querier.executeMQLQuery(entity, new XQLSelect().addSelectPart("*").addWherePart(new QId(catalog, entity, primaryFieldName).toString() + " = ?0").toString(), primaryFieldValueObject).toStringBuilder("element");

		/*------------------------------------------------------------------------------------------------------------*/
		/*                                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		Collection<SchemaSingleton.FrgnKeys> forwardLists = SchemaSingleton.getForwardFKs(catalog, entity).values();

		Collection<SchemaSingleton.FrgnKeys> backwardLists = SchemaSingleton.getBackwardFKs(catalog, entity).values();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"linked_elements\">");

		_getLinkedEntities(result, catalog, entity, primaryFieldName, primaryFieldValue, forwardLists, FORWARD);

		_getLinkedEntities(result, catalog, entity, primaryFieldName, primaryFieldValue, backwardLists, BACKWARD);

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private final int FORWARD = 0;
	private final int BACKWARD = 1;

	/*----------------------------------------------------------------------------------------------------------------*/

	private void _getLinkedEntities(StringBuilder result, String catalog, String entity, String primaryFieldName, String primaryFieldValue, @NotNull Collection<SchemaSingleton.FrgnKeys> list, int mode)
	{
		List<QId> constraints;

		String linkedCatalog;
		String linkedEntity;
		String direction;

		for(SchemaSingleton.FrgnKeys frgnKeys: list)
		 for(SchemaSingleton.FrgnKey frgnKey: frgnKeys)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			constraints = new ArrayList<>();

			/*--------------------------------------------------------------------------------------------------------*/

			constraints.add(new QId(frgnKey.fkExternalCatalog, frgnKey.fkEntity, frgnKey.fkField));

			/*--------------------------------------------------------------------------------------------------------*/

			switch(mode)
			{
				case FORWARD:
					/*------------------------------------------------------------------------------------------------*/

					linkedCatalog = frgnKey.pkExternalCatalog;
					linkedEntity = frgnKey.pkEntity;
					direction = "forward";

					/*------------------------------------------------------------------------------------------------*/

					break;

				case BACKWARD:
					/*------------------------------------------------------------------------------------------------*/

					linkedCatalog = frgnKey.fkExternalCatalog;
					linkedEntity = frgnKey.fkEntity;
					direction = "backward";

					/*------------------------------------------------------------------------------------------------*/

					try
					{
						SchemaSingleton.Table table = SchemaSingleton.getEntityInfo(linkedCatalog, linkedEntity);

						if(table.bridge)
						{
							constraints.clear();

							for(SchemaSingleton.FrgnKeys tmp1: table.forwardFKs.values())
							{
								constraints.add(new QId(tmp1.get(0).fkExternalCatalog, tmp1.get(0).fkEntity, tmp1.get(0).fkField));

								if(!catalog.equals(tmp1.get(0).pkExternalCatalog)
								   ||
								   !entity.equals(tmp1.get(0).pkEntity)
								 ) {
									linkedCatalog = tmp1.get(0).pkExternalCatalog;
									linkedEntity = tmp1.get(0).pkEntity;
									direction = "bridge";
								}
							}
						}
					}
					catch (Exception e)
					{
						/* IGNORE */
					}

					/*------------------------------------------------------------------------------------------------*/

					break;

				default:
					return;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			String sql;
			String mql;
			String count;

			try
			{
				boolean backslashEscapes = (CatalogSingleton.getFlags(catalog) & DriverMetadata.FLAG_BACKSLASH_ESCAPE) == DriverMetadata.FLAG_BACKSLASH_ESCAPE;

				String query = new XQLSelect().addSelectPart("COUNT(" + new QId(linkedCatalog, linkedEntity, "*").toString(QId.MASK_CATALOG_ENTITY_FIELD) + ")")
				                              .addWherePart(new QId(catalog, entity, primaryFieldName, constraints).toString(QId.MASK_CATALOG_ENTITY_FIELD, QId.MASK_CATALOG_ENTITY_FIELD) + " = " + Utility.textToSqlVal(primaryFieldValue, backslashEscapes))
				                              .toString()
				;

				RowSet rowSet = getQuerier(linkedCatalog).executeMQLQuery(linkedEntity, query);

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

			/*--------------------------------------------------------------------------------------------------------*/

			result.append("<row>")
			      .append("<field name=\"catalog\"><![CDATA[").append(linkedCatalog).append("]]></field>")
			      .append("<field name=\"entity\"><![CDATA[").append(linkedEntity).append("]]></field>")
			      .append("<field name=\"constraint\"><![CDATA[").append(constraints.stream().map(x -> x.toString(QId.MASK_CATALOG_ENTITY_FIELD)).collect(Collectors.joining(", "))).append("]]></field>")
			      .append("<field name=\"sql\"><![CDATA[").append(sql.replaceFirst("COUNT\\(([^)]+)\\)", "$1")).append("]]></field>")
			      .append("<field name=\"mql\"><![CDATA[").append(mql.replaceFirst("COUNT\\(([^)]+)\\)", "$1")).append("]]></field>")
			      .append("<field name=\"count\"><![CDATA[").append(count).append("]]></field>")
			      .append("<field name=\"direction\"><![CDATA[").append(direction).append("]]></field>")
			      .append("</row>")
			;

			/*--------------------------------------------------------------------------------------------------------*/
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the element's information.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" -primaryFieldName=\"\" -primaryFieldValue=\"\" (-hideBigContent)?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
