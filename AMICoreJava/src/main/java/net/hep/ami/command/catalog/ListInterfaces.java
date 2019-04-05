package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.utility.parser.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListInterfaces extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListInterfaces(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String group = arguments.get("group");
		String name = arguments.get("name");

		/*-----------------------------------------------------------------*/

		XQLSelect xqlSelect = new XQLSelect().addSelectPart("`group`, `interface`, `json`")
		                                     .addFromPart("`router_interface`")
		;

		if(group != null) {
			xqlSelect.addWherePart("`group` LIKE " + Utility.textToSqlVal(group));
		}

		if(name != null) {
			xqlSelect.addWherePart("`interface` LIKE " + Utility.textToSqlVal(name));
		}

		/*-----------------------------------------------------------------*/

		return getQuerier("self").executeSQLQuery("router_interface", xqlSelect.toString()).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List the available interfaces.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-group=\"\")? (-name=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
