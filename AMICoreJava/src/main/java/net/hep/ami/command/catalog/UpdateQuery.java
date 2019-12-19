package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_WRITER", visible = true, secured = false)
public class UpdateQuery extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public UpdateQuery(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
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

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*------------------------------------------------------------------------------------------------------------*/

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
			result = querier.executeMQLUpdate(entity, mql);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Execute a simple raw, SQL or MQL query (update mode).";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" (-raw\"\" | -sql=\"\" | (-entity=\"\" -mql=\"\"))";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
