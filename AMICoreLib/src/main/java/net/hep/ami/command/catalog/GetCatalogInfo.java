package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class GetCatalogInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetCatalogInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		if(catalog == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		SchemaSingleton.Catalog _catalog = SchemaSingleton.getCatalogInfo(catalog);

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"catalog\">");
		SchemaSingleton.appendCatalogToStringBuilder(result, _catalog);
		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"entities\">");

		for(SchemaSingleton.Table table: _catalog.tables.values())
		{
			SchemaSingleton.appendTableToStringBuilder(result, table);
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"fields\">");

		for(SchemaSingleton.Table table: _catalog.tables.values())
		 for(SchemaSingleton.Column column: table.columns.values())
		{
			SchemaSingleton.appendColumnToStringBuilder(result, column);
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"foreignKeys\">");

		for(SchemaSingleton.Table table: _catalog.tables.values())
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
		return "Get the info of the given catalog.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
