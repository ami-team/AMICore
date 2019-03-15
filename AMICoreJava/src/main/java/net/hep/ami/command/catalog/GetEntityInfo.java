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

		SchemaSingleton.Table table = SchemaSingleton.getEntityInfo(catalog, entity);

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"entity\">");
		SchemaSingleton.appendTableToStringBuilder(result, table);
		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"fields\">");

		for(SchemaSingleton.Column column: table.columns.values())
		{
			SchemaSingleton.appendColumnToStringBuilder(result, column);
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(SchemaSingleton.FrgnKeys frgnKeys: table.forwardFKs.values())
		{
			SchemaSingleton.appendFrgnKeyToStringBuilder(result, frgnKeys.get(0));
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
