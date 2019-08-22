package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListFields extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ListFields(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		result.append("<rowset type=\"fields\">");

		for(String field: SchemaSingleton.getFieldNames(catalog, entity))
		{
			result.append("<row><field name=\"field\"><![CDATA[").append(field).append("]]></field></row>");
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "List the fields of the given catalog and entity.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
