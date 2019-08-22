package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
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
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		return SchemaSingleton.getDBSchemas();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Get the database schemas.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
