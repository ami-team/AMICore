package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_USER", secured = false)
public class GetSchemas extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetSchemas(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		return SchemaSingleton.getDBSchemas();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the database schemas.";
	}

	/*---------------------------------------------------------------------*/
}
