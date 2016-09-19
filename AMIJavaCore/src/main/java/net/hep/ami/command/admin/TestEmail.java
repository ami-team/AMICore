package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class TestEmail extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public TestEmail(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String from = arguments.containsKey("from") ? arguments.get("from")
		                                            : ConfigSingleton.getProperty("log_from")
		;

		String to = arguments.containsKey("to") ? arguments.get("to")
		                                        : ConfigSingleton.getProperty("log_to")
		;

		String cc = arguments.containsKey("cc") ? arguments.get("cc")
		                                        : ConfigSingleton.getProperty("log_cc")
		;

		MailSingleton.sendMessage(from, to, cc, "This is a test", "This is a test");

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Check email.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-from=\"value\")? (-to=\"value\")? (-cc=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
