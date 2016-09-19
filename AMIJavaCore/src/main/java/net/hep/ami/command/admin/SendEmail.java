package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class SendEmail extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public SendEmail(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String m_from = arguments.containsKey("from") ? arguments.get("from")
		                                              : ""
		;

		String m_to = arguments.containsKey("to") ? arguments.get("to")
		                                          : ""
		;

		String m_cc = arguments.containsKey("cc") ? arguments.get("cc")
		                                          : ""
		;

		String m_subject = arguments.containsKey("subject") ? arguments.get("subject")
		                                                    : ""
		;

		String m_message = arguments.containsKey("message") ? arguments.get("message")
		                                                    : ""
		;

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
