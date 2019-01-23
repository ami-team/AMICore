package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = true, secured = false)
public class UpdateQuery extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public UpdateQuery(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String raw = arguments.get("raw");
		String sql = arguments.get("sql");
		String mql = arguments.get("mql");

		if(catalog == null || (raw == null && sql == null && (mql == null || entity == null)))
		{
			throw new Exception("invalid usage");
		}

		boolean isAdmin = m_userRoles.contains("AMI_ADMIN");

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/

		Update result;

		/**/ if(raw != null)
		{
			result = querier.executeRawUpdate(raw);
		}
		else if(sql != null)
		{
			result = querier.executeSQLUpdate(sql);
		}
		else
		{
			result = querier.executeMQLUpdate(entity, m_AMIUser, isAdmin, mql);
		}

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Execute a simple raw, SQL or MQL query (update mode).";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" (-raw\"\" | -sql=\"\" | (-entity=\"\" -mql=\"\"))";
	}

	/*---------------------------------------------------------------------*/
}
