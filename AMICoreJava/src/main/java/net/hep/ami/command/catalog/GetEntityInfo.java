package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetEntityInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetEntityInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
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

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Table table = SchemaSingleton.getEntityInfo(catalog, entity);

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"entity\">");
		SchemaSingleton.appendTableToStringBuilder(result, table);
		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"fields\">");

		for(SchemaSingleton.Column column: table.columns.values())
		{
			SchemaSingleton.appendColumnToStringBuilder(result, column);
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(SchemaSingleton.FrgnKeys frgnKeys: table.forwardFKs.values())
		{
			SchemaSingleton.appendFrgnKeyToStringBuilder(result, frgnKeys.get(0));
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the info of the given entity.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
