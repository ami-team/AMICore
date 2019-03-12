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

		result.append("<rowset type=\"fields\">");
		SchemaSingleton.columnToStringBuilder(result, column);
		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the info of the given field.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" -field=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
