package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetFieldInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetFieldInfo(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		if(catalog == null
		   ||
		   entity == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(SchemaSingleton.Column column: SchemaSingleton.getColumns(catalog, entity).values())
		{
			result.append("<row>")
			      .append("<field name=\"name\">").append(column.name).append("</field>")
			      .append("<field name=\"type\">").append(column.type).append("</field>")
			      .append("<field name=\"size\">").append(column.size).append("</field>")
			      .append("<field name=\"digits\">").append(column.digits).append("</field>")
			      .append("<field name=\"def\">").append(column.def).append("</field>")
			      .append("<field name=\"rank\">").append(column.rank).append("</field>")
			      .append("<field name=\"hidden\">").append(column.hidden).append("</field>")
			      .append("<field name=\"crypted\">").append(column.crypted).append("</field>")
			      .append("<field name=\"primary\">").append(column.primary).append("</field>")
			      .append("<field name=\"created\">").append(column.created).append("</field>")
			      .append("<field name=\"createdBy\">").append(column.createdBy).append("</field>")
			      .append("<field name=\"modified\">").append(column.modified).append("</field>")
			      .append("<field name=\"modifiedBy\">").append(column.modifiedBy).append("</field>")
			      .append("<field name=\"statable\">").append(column.statable).append("</field>")
			      .append("<field name=\"groupable\">").append(column.groupable).append("</field>")
			      .append("<field name=\"description\">").append(column.description).append("</field>")
			      .append("</row>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the field info of the given catalog and entity.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
