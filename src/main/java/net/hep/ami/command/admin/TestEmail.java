package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;

public class TestEmail extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_from;

	private String m_to;

	private String m_cc;

	/*---------------------------------------------------------------------*/

	public TestEmail(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_from = arguments.containsKey("from") ? arguments.get("from")
		                                       : ConfigSingleton.getProperty("log_from")
		;

		m_to = arguments.containsKey("to") ? arguments.get("to")
		                                   : ConfigSingleton.getProperty("log_to")
		;

		m_cc = arguments.containsKey("cc") ? arguments.get("cc")
		                                   : ConfigSingleton.getProperty("log_cc")
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		MailSingleton.sendMessage(m_from, m_to, m_cc, "This is a test", "This is a test");

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Check email.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "(-from=\"value\")? (-to=\"value\")? (-cc=\"value\")?";
	}

	/*---------------------------------------------------------------------*/
}
