package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public class RestartAMI extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private final String m_path = System.getProperty("catalina.base") + "/bin";

	/*---------------------------------------------------------------------*/

	public RestartAMI(Map<String, String> arguments, int transactionID) throws Exception {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		Shell.exec(new String[] {"bash", "-c", String.format("%s/shutdown.sh ; sleep 2 ; %s/startup.sh", m_path, m_path)});

		return new StringBuilder("<info><![CDATA[restarting AMI...]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Restart AMI.";
	}

	/*---------------------------------------------------------------------*/
}
