package net.hep.ami.command.cloud;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.cloud.*;
import net.hep.ami.cloud.driver.OpenStackDriver;
import net.hep.ami.command.CommandAbstractClass;

public class CloudListSecurityRules extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_endpoint;
	private String m_identity;
	private String m_credential;

	/*---------------------------------------------------------------------*/

	public CloudListSecurityRules(Map<String, String> arguments, int transactionID) {
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

		Set<OpenStackDriver.CloudSecurityRule> securityGroups;

		try {
			securityGroups = cloud.getSecurityRules();

		} finally {
			cloud.close();
		}

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(OpenStackDriver.CloudSecurityRule securityGroup: securityGroups) {

			result.append(
				"<row>"
				+
				"<field name=\"region\">" + securityGroup.region + "</field>"
				+
				"<field name=\"protocol\">" + securityGroup.protocol + "</field>"
				+
				"<field name=\"fromPort\">" + securityGroup.fromPort + "</field>"
				+
				"<field name=\"toPort\">" + securityGroup.toPort + "</field>"
				+
				"<field name=\"IPRange\">" + securityGroup.IPRange + "</field>"
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

		return "List the security rules on the cloud.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "(-endpoint=\"value\")? (-identity=\"value\")? (-credential=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
