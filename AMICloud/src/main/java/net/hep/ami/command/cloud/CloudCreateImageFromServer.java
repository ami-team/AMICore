package net.hep.ami.command.cloud;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.cloud.*;
import net.hep.ami.cloud.driver.*;
import net.hep.ami.command.*;

public class CloudCreateImageFromServer extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_endpoint;
	private String m_identity;
	private String m_credential;
	private String m_region;
	private String m_name;
	private String m_serverID;

	/*---------------------------------------------------------------------*/

	public CloudCreateImageFromServer(Map<String, String> arguments, int transactionID) {
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
		m_name = arguments.get("name");
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
		   m_name == null
		   ||
		   m_serverID == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		DriverInterface cloud = CloudSingleton.getConnection("openstack", m_endpoint, m_identity, m_credential);

		try {
			cloud.createImageFromServer(m_region, m_name, m_serverID);

		} finally {
			cloud.close();
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Create a new image from server on the cloud.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "(-endpoint=\"value\")? (-identity=\"value\")? (-credential=\"value\")? -region=\"value\" -name=\"value\" -serverID=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
