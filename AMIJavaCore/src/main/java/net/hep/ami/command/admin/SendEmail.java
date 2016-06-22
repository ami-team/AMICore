package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class SendEmail extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	private String m_from;

	private String m_to;

	private String m_cc;

	private String m_subject;

	private String m_message;

	/*---------------------------------------------------------------------*/

	public SendEmail(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);

		m_from = arguments.containsKey("from") ? arguments.get("from")
		                                       : ""
		;

		m_to = arguments.containsKey("to") ? arguments.get("to")
		                                   : ""
		;

		m_cc = arguments.containsKey("cc") ? arguments.get("cc")
		                                   : ""
		;

		m_subject = arguments.containsKey("subject") ? arguments.get("subject")
		                                             : ""
		;

		m_message = arguments.containsKey("message") ? arguments.get("message")
		                                             : ""
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
	{
		MailSingleton.sendMessage(m_from, m_to, m_cc, m_subject, m_message);

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Send email.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-from=\"value\")? (-to=\"value\")? (-cc=\"value\")? -subject=\"value\" -message=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
