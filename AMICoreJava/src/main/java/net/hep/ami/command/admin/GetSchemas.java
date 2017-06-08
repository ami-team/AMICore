package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

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
		return SchemaSingleton.getDBSchemes();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the database schemas.";
	}

	/*---------------------------------------------------------------------*/
}
