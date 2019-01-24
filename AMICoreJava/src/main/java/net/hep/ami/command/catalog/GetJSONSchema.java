package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetJSONSchema extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetJSONSchema(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");

		if(catalog == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		return getQuerier("self").executeSQLQuery("SELECT `custom` AS `json` FROM `router_catalog` WHERE `externalCatalog` = ?", catalog).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the JSON schema info of the given catalog.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
