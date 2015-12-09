package net.hep.ami.command.monitoring;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.pool.*;

public class GetConnectionPoolStatus extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public GetConnectionPoolStatus(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		return ConnectionPoolSingleton.getStatus();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get connection pool status.";
	}

	/*---------------------------------------------------------------------*/
}
