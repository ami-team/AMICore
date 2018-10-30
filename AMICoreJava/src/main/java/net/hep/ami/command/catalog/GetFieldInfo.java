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
			result.append(
				"<row>"
				+
				"<field name=\"name\">" + column.name + "</field>"
				+
				"<field name=\"type\">" + column.type + "</field>"
				+
				"<field name=\"size\">" + column.size + "</field>"
				+
				"<field name=\"digits\">" + column.digits + "</field>"
				+
				"<field name=\"def\">" + column.def + "</field>"
				+
				"<field name=\"primary\">" + column.primary + "</field>"
				+
				"</row>"
			);
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
