package net.hep.ami.command.catalog;

import java.sql.*;
import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class SetJSONSchema extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SetJSONSchema(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String json = arguments.get("json");

		if(catalog == null
		   ||
		   json == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		PreparedStatement preparedStatement = getQuerier("self").preparedStatement("UPDATE `router_catalog` SET `json` = ? WHERE `externalCatalog` = ?", false, false, null);

		preparedStatement.setString(1, json);
		preparedStatement.setString(2, catalog);

		final int nbOfUpdatedRows = preparedStatement.executeUpdate();

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			nbOfUpdatedRows > 0 ? "<info><![CDATA[done with success]]></info>"
			                    : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Set the JSON schema info of the given catalog.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -json=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
