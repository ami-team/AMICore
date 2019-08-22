package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListInterfaces extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ListInterfaces(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String group = arguments.get("group");
		String name = arguments.get("name");

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		boolean backslashEscapes = (querier.getJdbcFlags() & DriverMetadata.FLAG_BACKSLASH_ESCAPE) == DriverMetadata.FLAG_BACKSLASH_ESCAPE;

		/*------------------------------------------------------------------------------------------------------------*/

		XQLSelect xqlSelect = new XQLSelect().addSelectPart("`group`, `interface`, `json`")
		                                     .addFromPart("`router_search_interface`")
		;

		if(group != null) {
			xqlSelect.addWherePart("`group` LIKE " + Utility.textToSqlVal(group, backslashEscapes));
		}

		if(name != null) {
			xqlSelect.addWherePart("`name` LIKE " + Utility.textToSqlVal(name, backslashEscapes));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return querier.executeSQLQuery("router_search_interface", xqlSelect.toString()).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "List the available interfaces.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "(-group=\"\")? (-name=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
