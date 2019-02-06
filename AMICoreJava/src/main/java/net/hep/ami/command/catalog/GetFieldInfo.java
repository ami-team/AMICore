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
		String field = arguments.get("field");

		if(catalog == null
		   ||
		   entity == null
		   ||
		   field == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(catalog, entity, field);

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"fields\">")
		      .append("<row>")
		      .append("<field name=\"name\"><![CDATA[").append(column.name).append("]]></field>")
		      .append("<field name=\"type\"><![CDATA[").append(column.type).append("]]></field>")
		      .append("<field name=\"size\"><![CDATA[").append(column.size).append("]]></field>")
		      .append("<field name=\"digits\"><![CDATA[").append(column.digits).append("]]></field>")
		      .append("<field name=\"def\"><![CDATA[").append(column.def).append("]]></field>")
		      .append("<field name=\"rank\"><![CDATA[").append(column.rank).append("]]></field>")
		      .append("<field name=\"adminOnly\"><![CDATA[").append(column.adminOnly).append("]]></field>")
		      .append("<field name=\"hidden\"><![CDATA[").append(column.hidden).append("]]></field>")
		      .append("<field name=\"crypted\"><![CDATA[").append(column.crypted).append("]]></field>")
		      .append("<field name=\"primary\"><![CDATA[").append(column.primary).append("]]></field>")
		      .append("<field name=\"created\"><![CDATA[").append(column.created).append("]]></field>")
		      .append("<field name=\"createdBy\"><![CDATA[").append(column.createdBy).append("]]></field>")
		      .append("<field name=\"modified\"><![CDATA[").append(column.modified).append("]]></field>")
		      .append("<field name=\"modifiedBy\"><![CDATA[").append(column.modifiedBy).append("]]></field>")
		      .append("<field name=\"statable\"><![CDATA[").append(column.statable).append("]]></field>")
		      .append("<field name=\"groupable\"><![CDATA[").append(column.groupable).append("]]></field>")
		      .append("<field name=\"description\"><![CDATA[").append(column.description).append("]]></field>")
		      .append("<field name=\"webLinkScript\"><![CDATA[").append(column.webLinkScript).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the field info of the given field.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" -field=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
