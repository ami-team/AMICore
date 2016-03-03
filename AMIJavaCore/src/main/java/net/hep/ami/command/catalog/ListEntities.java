package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.introspection.*;

public class ListEntities extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_catalog;

	/*---------------------------------------------------------------------*/

	public ListEntities(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);

		m_catalog = arguments.get("catalog");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{

		if(m_catalog == null)
		{
			throw new Exception("invalid usage");
		}

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(String entity: SchemaSingleton.getTableNames(m_catalog))
		{
			result.append(
				"<row>"
				+
				"<field name=\"entity\">" + entity + "</field>"
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
		return "List entities.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
