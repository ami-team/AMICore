package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class SetJSONSchema extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public SetJSONSchema(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String json = arguments.get("json");

		if(catalog == null
		   ||
		   json == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Update update = getQuerier("self").executeSQLUpdate("UPDATE `router_catalog` SET `custom` = ? WHERE `externalCatalog` = ?", json, catalog);

		/*-----------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() == 1 ? "<info><![CDATA[done with success]]></info>"
			                                 : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Set the JSON schema info of the given catalog.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -json=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
