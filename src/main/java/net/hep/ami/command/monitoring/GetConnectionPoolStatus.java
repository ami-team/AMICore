package net.hep.ami.command.monitoring;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.pool.*;

public class GetConnectionPoolStatus extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public GetConnectionPoolStatus(Map<String, String> arguments, int transactionID) throws Exception {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		StringBuilder result = new StringBuilder();

		result.append(ConnectionPoolSingleton.getStatus());

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Get connection pool status.";
	}

	/*---------------------------------------------------------------------*/
}
