package net.hep.ami.command.cloud;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public class CloudDeleteServer extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_endpoint;
	private String m_identity;
	private String m_credential;
	private String m_region;
	private String m_serverID;

	/*---------------------------------------------------------------------*/

	public CloudDeleteServer(Map<String, String> arguments, int transactionID) {
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

		m_region = arguments.get("region");
		m_serverID = arguments.get("serverID");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		if(m_endpoint == null
		   ||
		   m_identity == null
		   ||
		   m_credential == null
		   ||
		   m_region == null
		   ||
		   m_serverID == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Cloud cloud = new Cloud(m_endpoint, m_identity, m_credential);

		try {
			cloud.deleteServer(m_region, m_serverID);

		} finally {
			cloud.close();
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Delete a server on the cloud.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "(-endpoint=\"value\")? (-identity=\"value\")? (-credential=\"value\")? -region=\"value\" -serverID=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
