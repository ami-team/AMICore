package net.hep.ami.command.cloud;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public class CloudListServers extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_endpoint;
	private String m_identity;
	private String m_credential;

	/*---------------------------------------------------------------------*/

	public CloudListServers(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_endpoint = arguments.containsKey("endpoint") ? arguments.get("endpoint")
		                                               : ConfigSingleton.getProperty("cloud_endpoint", null)
		;

		m_identity = arguments.containsKey("identity") ? arguments.get("identity")
		                                               : ConfigSingleton.getProperty("cloud_identity", null)
		;

		m_credential = arguments.containsKey("credential") ? arguments.get("credential")
		                                                   : ConfigSingleton.getProperty("cloud_credential", null)
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_endpoint == null
		   ||
		   m_identity == null
		   ||
		   m_credential == null
		 ) {
			throw new Exception("invalid usage");
		}

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		Cloud cloud = new Cloud(m_endpoint, m_identity, m_credential);

		Set<Cloud.CloudServer> servers;

		try {
			servers = cloud.getServers();

		} finally {
			cloud.close();
		}

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Cloud.CloudServer server : servers) {

			result.append(
				"<row>"
				+
				"<field name=\"region\">" + server.region + "</field>"
				+
				"<field name=\"serverID\">" + server.serverID + "</field>"
				+
				"<field name=\"serverLabel\">" + server.serverLabel + "</field>"
				+
				"<field name=\"serverIPv4\">" + server.serverIPv4 + "</field>"
				+
				"<field name=\"serverIPv6\">" + server.serverIPv6 + "</field>"
				+
				"<field name=\"serverStatus\">" + server.serverStatus + "</field>"
				+
				"</row>"
			);
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "List the servers on the cloud.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "(-endpoint=\"value\")? (-identity=\"value\")? (-credential=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
