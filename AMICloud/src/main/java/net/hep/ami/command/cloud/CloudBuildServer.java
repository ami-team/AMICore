package net.hep.ami.command.cloud;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.cloud.*;
import net.hep.ami.cloud.driver.OpenStackDriver;
import net.hep.ami.command.CommandAbstractClass;

public class CloudBuildServer extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_endpoint;
	private String m_identity;
	private String m_credential;
	private String m_region;
	private String m_name;
	private String m_flavorID;
	private String m_imageID;
	private String m_keypair;
	private String m_fixedIP;
	private String m_portUUID;
	private String m_networUUID;

	/*---------------------------------------------------------------------*/

	public CloudBuildServer(Map<String, String> arguments, int transactionID) {
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
		m_flavorID = arguments.get("flavorID");
		m_imageID = arguments.get("imageID");
		m_keypair = arguments.get("keypair");
		m_fixedIP = arguments.get("fixedIP");
		m_portUUID = arguments.get("portUUID");
		m_networUUID = arguments.get("networUUID");
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
		   m_flavorID == null
		   ||
		   m_imageID == null
		   ||
		   m_keypair == null
		   ||
		   m_fixedIP == null
		   ||
		   (m_portUUID == null && m_networUUID == null || m_portUUID != null && m_networUUID != null)
		 ) {
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		OpenStackDriver cloud = new OpenStackDriver(m_endpoint, m_identity, m_credential);

		try {
			cloud.buildServer(m_region, m_name, m_flavorID, m_imageID, m_keypair, m_fixedIP, m_portUUID, m_networUUID);

		} finally {
			cloud.close();
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Build a new server on the cloud.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "(-endpoint=\"value\")? (-identity=\"value\")? (-credential=\"value\")? -region=\"value\" -name=\"value\" -flavorID=\"value\" -imageID=\"value\" -keypair=\"value\" -fixedIP=\"value\" (-portUUID=\"value\" | -networUUID=\"value\")";
	}

	/*---------------------------------------------------------------------*/
}
