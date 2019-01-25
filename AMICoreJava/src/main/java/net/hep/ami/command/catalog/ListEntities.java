package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListEntities extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListEntities(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		String catalog = arguments.get("catalog");

		if(catalog == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"entities\">");

		for(String entity: SchemaSingleton.getTableNames(catalog))
		{
			result.append("<row><field name=\"entity\"><![CDATA[").append(entity).append("]]></field></row>");
		}

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the entities of the given catalog.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
