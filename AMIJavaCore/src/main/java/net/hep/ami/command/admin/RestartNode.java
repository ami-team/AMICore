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
		new Shell().exec(String.format("bash -c \"%s/shutdown.sh ; sleep 2 ; %s/startup.sh\"", m_path, m_path));

		return new StringBuilder("<info><![CDATA[restarting AMI...]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Restart AMI.";
	}

	/*---------------------------------------------------------------------*/
}
