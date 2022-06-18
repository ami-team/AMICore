package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class GetSchemas extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetSchemas(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments)
	{
		return SchemaSingleton.getDBSchemas();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the database schemas.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
