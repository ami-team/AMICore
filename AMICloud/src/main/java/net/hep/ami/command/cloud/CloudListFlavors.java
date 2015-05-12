package net.hep.ami.command.cloud;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.cloud.*;
import net.hep.ami.cloud.driver.OpenStackDriver;
import net.hep.ami.command.CommandAbstractClass;

public class CloudListFlavors extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_endpoint;
	private String m_identity;
	private String m_credential;

	/*---------------------------------------------------------------------*/

	public CloudListFlavors(Map<String, String> arguments, int transactionID) {
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

		OpenStackDriver cloud = new OpenStackDriver(m_endpoint, m_identity, m_credential);

		Set<OpenStackDriver.CloudFlavor> flavors;

		try {
			flavors = cloud.getFlavors();

		} finally {
			cloud.close();
		}

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(OpenStackDriver.CloudFlavor flavor: flavors) {

			result.append(
				"<row>"
				+
				"<field name=\"ID\">" + flavor.ID + "</field>"
				+
				"<field name=\"name\">" + flavor.name + "</field>"
				+
				"<field name=\"region\">" + flavor.region + "</field>"
				+
				"<field name=\"cpu\">" + flavor.cpu + "</field>"
				+
				"<field name=\"ram\">" + flavor.ram + "</field>"
				+
				"<field name=\"disk\">" + flavor.disk + "</field>"
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

		return "List the flavors on the cloud.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "(-endpoint=\"value\")? (-identity=\"value\")? (-credential=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
