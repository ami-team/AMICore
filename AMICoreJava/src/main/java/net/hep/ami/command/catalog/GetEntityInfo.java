package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetEntityInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetEntityInfo(Set<String> userRoles, Map<String, String> arguments, long transactionId)
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

		result.append("<rowset type=\"fields\">");

		for(SchemaSingleton.Column column: SchemaSingleton.getEntityInfo(catalog, entity).values())
		{
			result.append("<row>")
			      .append("<field name=\"externalCatalog\"><![CDATA[").append(column.externalCatalog).append("]]></field>")
			      .append("<field name=\"internalCatalog\"><![CDATA[").append(column.internalCatalog).append("]]></field>")
			      .append("<field name=\"entity\"><![CDATA[").append(column.entity).append("]]></field>")
			      .append("<field name=\"name\"><![CDATA[").append(column.field).append("]]></field>")
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
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(SchemaSingleton.FrgnKeys frgnKeys: SchemaSingleton.getForwardFKs(catalog, entity).values())
		{
			SchemaSingleton.FrgnKey frgnKey = frgnKeys.get(0);

			result.append("<row>")
			      .append("<field name=\"name\"><![CDATA[").append(frgnKey.name).append("]]></field>")
			      .append("<field name=\"fkExternalCatalog\"><![CDATA[").append(frgnKey.fkExternalCatalog).append("]]></field>")
			      .append("<field name=\"fkInternalCatalog\"><![CDATA[").append(frgnKey.fkInternalCatalog).append("]]></field>")
			      .append("<field name=\"fkTable\"><![CDATA[").append(frgnKey.fkEntity).append("]]></field>")
			      .append("<field name=\"fkColumn\"><![CDATA[").append(frgnKey.fkField).append("]]></field>")
			      .append("<field name=\"pkExternalCatalog\"><![CDATA[").append(frgnKey.pkExternalCatalog).append("]]></field>")
			      .append("<field name=\"pkInternalCatalog\"><![CDATA[").append(frgnKey.pkInternalCatalog).append("]]></field>")
			      .append("<field name=\"pkTable\"><![CDATA[").append(frgnKey.pkEntity).append("]]></field>")
			      .append("<field name=\"pkColumn\"><![CDATA[").append(frgnKey.pkField).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the info of the given centity.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
