package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.introspection.*;

public class GetSchemes extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	public GetSchemes(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		return SchemaSingleton.getDBSchemas();
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Get database schemas.";
	}

	/*---------------------------------------------------------------------*/
}
