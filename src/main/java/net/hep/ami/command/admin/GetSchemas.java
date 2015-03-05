package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

public class GetSchemas extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public GetSchemas(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		return IntrospectionSingleton.getDBSchemas();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Get database schemas.";
	}

	/*---------------------------------------------------------------------*/
}
