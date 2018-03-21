package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class UpdateQuery extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public UpdateQuery(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String sql = arguments.get("sql");
		String mql = arguments.get("mql");

		if(catalog == null || (sql == null && (mql == null || entity == null)))
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		Update result;

		if(sql != null)
		{
			result = querier.executeSQLUpdate(sql);
		}
		else
		{
			result = querier.executeMQLUpdate(entity, sql);
		}

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple SQL or MQL query (update mode).";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" (-sql=\"\" | (-entity=\"\" -mql=\"\"))";
	}

	/*---------------------------------------------------------------------*/
}
