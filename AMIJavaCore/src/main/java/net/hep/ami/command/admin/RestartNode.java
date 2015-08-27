package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class RestartNode extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private final String m_path = System.getProperty("catalina.base") + "/bin";

	/*---------------------------------------------------------------------*/

	public RestartNode(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		new SimpleShell().exec(new String[] {
			"bash",
			"-c",
			m_path + "/shutdown.sh ; sleep 2 ; " + m_path + "/startup.sh",
		});

		return new StringBuilder("<info><![CDATA[restarting AMI...]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Restart AMI.";
	}

	/*---------------------------------------------------------------------*/
}
