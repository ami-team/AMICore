package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.introspection.*;

public class ListFields extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListFields(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String m_catalog = arguments.get("catalog");
		String m_entity = arguments.get("entity");

		if(m_catalog == null
		   ||
		   m_entity == null
		 ) {
			throw new Exception("invalid usage");
		}

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(String field: SchemaSingleton.getColumnNames(m_catalog, m_entity))
		{
			result.append(
				"<row>"
				+
				"<field name=\"field\">" + field + "</field>"
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
		return "List fields.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"value\" -entity=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
